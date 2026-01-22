package com.rboker.automation.tests.unit;

import com.rboker.automation.core.DriverManager;
import com.rboker.automation.core.ScreenshotUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa ScreenshotUtil.capture() sem abrir navegador:
 * usa um WebDriver fake que implementa TakesScreenshot.
 */
class ScreenshotUtilCaptureUnitTest {

    @AfterEach
    void tearDown() {
        DriverManager.quitDriver();
    }

    @Test
    void shouldReturnNullWhenDriverIsNull() {
        DriverManager.quitDriver();
        assertNull(ScreenshotUtil.capture("test"));
    }

    @Test
    void shouldReturnNullWhenDriverDoesNotSupportScreenshot() {
        DriverManager.setDriver(new NoScreenshotDriver());
        assertNull(ScreenshotUtil.capture("test"));
    }

    @Test
    void shouldCreateScreenshotFileWhenDriverSupportsScreenshot() throws IOException {
        // Cria um arquivo temporário (simulando a saída do Selenium)
        Path tempImage = Files.createTempFile("fake-screenshot-", ".png");
        Files.write(tempImage, new byte[]{1, 2, 3}); // conteúdo qualquer

        DriverManager.setDriver(new FakeScreenshotDriver(tempImage));

        Path saved = ScreenshotUtil.capture("Meu teste: Selenium / Chrome?");

        // limpeza do arquivo temporário usado pelo fake
        Files.deleteIfExists(tempImage);


        assertNotNull(saved, "Deveria retornar o caminho do screenshot salvo");
        assertTrue(Files.exists(saved), "Arquivo de screenshot deveria existir");
        assertTrue(saved.toString().contains("target"), "Deveria salvar em target/");
        assertTrue(saved.toString().contains("screenshots"), "Deveria salvar em target/screenshots");
        assertTrue(saved.getFileName().toString().endsWith(".png"), "Deveria salvar .png");

        // Garante que o nome foi sanitizado (não contém caracteres proibidos como ":" "/" "?")
        String fileName = saved.getFileName().toString();
        assertFalse(fileName.contains(":"), "Filename não deveria conter ':'");
        assertFalse(fileName.contains("/"), "Filename não deveria conter '/'");
        assertFalse(fileName.contains("?"), "Filename não deveria conter '?'");
    }

    /**
     * Driver que NÃO suporta screenshot (não implementa TakesScreenshot).
     * Implementação mínima para satisfazer a interface WebDriver.
     */
    private static class NoScreenshotDriver implements WebDriver {

        @Override
        public void get(String url) { }

        @Override
        public String getCurrentUrl() { return null; }

        @Override
        public String getTitle() { return null; }

        @Override
        public List<WebElement> findElements(By by) { return List.of(); }

        @Override
        public WebElement findElement(By by) { throw new NoSuchElementException("not needed"); }

        @Override
        public String getPageSource() { return null; }

        @Override
        public void close() { }

        @Override
        public void quit() { }

        @Override
        public Set<String> getWindowHandles() { return Set.of(); }

        @Override
        public String getWindowHandle() { return "handle"; }

        @Override
        public TargetLocator switchTo() { throw new UnsupportedOperationException(); }

        @Override
        public Navigation navigate() { throw new UnsupportedOperationException(); }

        @Override
        public Options manage() { throw new UnsupportedOperationException(); }
    }

    /**
     * Driver fake que suporta screenshot retornando um arquivo temporário.
     * Isso permite testar ScreenshotUtil.capture() sem abrir navegador.
     */
    private static class FakeScreenshotDriver extends NoScreenshotDriver implements TakesScreenshot {

        private final Path fileToReturn;

        private FakeScreenshotDriver(Path fileToReturn) {
            this.fileToReturn = fileToReturn;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
            // ScreenshotUtil pede OutputType.FILE
            if (target == OutputType.FILE) {
                return (X) fileToReturn.toFile();
            }
            throw new UnsupportedOperationException("Only FILE is supported in this fake");
        }
    }
}
