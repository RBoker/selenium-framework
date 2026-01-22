package com.rboker.automation.core;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.nio.file.StandardCopyOption;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitário para captura de evidências (screenshot).
 */
public final class ScreenshotUtil {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {
        // Evita instanciação
    }

    /**
     * Captura screenshot do driver atual (se suportado) e salva em target/screenshots.
     *
     * @param testName Nome do teste para compor o nome do arquivo
     * @return caminho do arquivo salvo, ou null se não conseguiu capturar
     */
    public static Path capture(String testName) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            return null;
        }

        if (!(driver instanceof TakesScreenshot)) {
            return null;
        }

        try {
            Path dir = Path.of("target", "screenshots");
            Files.createDirectories(dir);

            String safeName = sanitize(testName);
            String fileName = safeName + "_" + LocalDateTime.now().format(TS) + ".png";
            Path destination = dir.resolve(fileName);

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);


            return destination;

        } catch (Exception e) {
            // Em framework real, aqui a gente loga o erro
            return null;
        }
    }

    private static String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "unknown_test";
        }
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
