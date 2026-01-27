package com.rboker.automation.support;

import com.rboker.automation.config.FrameworkConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Escreve environment.properties dentro de target/allure-results
 * para o Allure exibir no painel "ENVIRONMENT".
 */
public final class AllureEnvironmentWriter {

    private static final Object LOCK = new Object();
    private static volatile boolean WRITTEN = false;

    private AllureEnvironmentWriter() {
        // utility
    }

    public static void writeOnce() {
        if (WRITTEN) return;

        synchronized (LOCK) {
            if (WRITTEN) return;

            String resultsDir = System.getProperty(
                    "allure.results.directory",
                    "target/allure-results"
            );

            Path dir = Path.of(resultsDir);
            Path file = dir.resolve("environment.properties");

            try {
                Files.createDirectories(dir);

                Properties p = new Properties();

                // Projeto selecionado via -Dproject=<id>
                String projectId = System.getProperty("project", "default").trim();
                p.setProperty("project", projectId);

                // Nome "humano" do projeto vindo do YAML (project.name).
                // Fallback: usa o próprio id.
                String projectName = FrameworkConfig.projectName();
                if (projectName == null || projectName.isBlank()) {
                    projectName = projectId;
                }
                p.setProperty("projectName", projectName);

                // Execução (YAML merge + sysprops)
                p.setProperty("baseUrl", FrameworkConfig.baseUrl());
                p.setProperty("browser", FrameworkConfig.browser());
                p.setProperty("headless", Boolean.toString(FrameworkConfig.headless()));
                p.setProperty("remote", Boolean.toString(FrameworkConfig.remote()));
                p.setProperty("remoteUrl", FrameworkConfig.remoteUrl());

                // Timeouts (útil para diagnóstico e reprodutibilidade)
                p.setProperty("timeoutSeconds", Integer.toString(FrameworkConfig.timeoutSeconds()));
                p.setProperty("uiTimeoutSeconds", Integer.toString(FrameworkConfig.uiTimeoutSeconds()));

                try (OutputStream os = Files.newOutputStream(file)) {
                    p.store(os, "Allure environment");
                }

                WRITTEN = true;
            } catch (IOException e) {
                // Não quebra a execução por falha de relatório
                WRITTEN = true;
            }
        }
    }
}
