package com.rboker.automation.core;

import com.rboker.automation.config.Config;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.time.Duration;

/**
 * Factory responsável por criar instâncias de WebDriver conforme configuração.
 */
public final class DriverFactory {

    private DriverFactory() {
        // Evita instanciação
    }

    public static WebDriver createDriver() {
        String browser = Config.browser().toLowerCase();
        boolean headless = Config.headless();

        try {
            // Execução remota (Grid)
            if (Config.remote()) {
                MutableCapabilities caps = buildCapabilities(browser, headless);
                WebDriver driver = new RemoteWebDriver(new URL(Config.remoteUrl()), caps);
                applyDefaults(driver);
                return driver;
            }

            // Execução local
            WebDriver driver = switch (browser) {
                case "firefox" -> new org.openqa.selenium.firefox.FirefoxDriver((FirefoxOptions) buildCapabilities("firefox", headless));
                case "edge" -> new org.openqa.selenium.edge.EdgeDriver((EdgeOptions) buildCapabilities("edge", headless));
                default -> new org.openqa.selenium.chrome.ChromeDriver((ChromeOptions) buildCapabilities("chrome", headless));
            };

            applyDefaults(driver);
            return driver;

        } catch (Exception e) {
            throw new RuntimeException("Falha ao criar WebDriver. browser=" + browser + ", remote=" + Config.remote(), e);
        }
    }

    private static MutableCapabilities buildCapabilities(String browser, boolean headless) {
        return switch (browser) {
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                yield options;
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                yield options;
            }
            default -> {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=1920,1080");
                options.addArguments("--disable-gpu");
                yield options;
            }
        };
    }

    private static void applyDefaults(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // padrão recomendado
        driver.manage().window().maximize();
    }
}
