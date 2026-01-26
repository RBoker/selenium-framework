package com.rboker.automation.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Centraliza a leitura de configurações do framework.
 *
 * Prioridade:
 * 1) System Properties (-D)
 * 2) YAML por projeto (se -Dproject informado): src/test/resources/config/projects/<project>.yaml
 * 3) YAML padrão: src/test/resources/config/framework.yaml
 * 4) Fallback hardcoded (segurança)
 *
 * Ex. (single-project):
 * mvn verify -Pui -DbaseUrl=https://site -Dbrowser=chrome -Dheadless=true
 *
 * Ex. (multi-project):
 * mvn verify -Pui -Dproject=blog-agi
 */
public final class FrameworkConfig {

    private static final boolean YAML_ENABLED = Boolean.parseBoolean(System.getProperty("yaml.enabled", "true"));

    public static int uiTimeoutSeconds() {
        // Sysprop: uiTimeout (novo) ou uiTimeoutSeconds (compat)
        // YAML: timeouts.uiSeconds
        String raw = System.getProperty("uiTimeout");
        if (raw == null || raw.isBlank()) {
            raw = System.getProperty("uiTimeoutSeconds");
        }
        if (raw != null && !raw.isBlank()) {
            try {
                return Integer.parseInt(raw.trim());
            } catch (NumberFormatException ignored) {
                return 25;
            }
        }
        return yamlInt("timeouts", "uiSeconds", 25);
    }


    /**
     * YAML padrão (legado / single-project).
     */
    private static final String DEFAULT_YAML_PATH = "config/framework.yaml";

    /**
     * Diretório de YAMLs por projeto (multi-projeto).
     * Ex.: config/projects/blog-agi.yaml
     */
    private static final String PROJECTS_YAML_DIR = "config/projects";

    /**
     * Nome do projeto selecionado via -Dproject=<nome>.
     * Se não informado, permanece no comportamento legado.
     */
    private static final String PROJECT = System.getProperty("project");

    /**
     * Carrega o YAML na inicialização (mantém comportamento atual de "config estática").
     * Ordem:
     * - se project informado e o arquivo existir -> usa YAML do projeto
     * - caso contrário -> usa YAML padrão
     */
    private static final Map<String, Object> YAML_CONFIG = loadYaml();

    private FrameworkConfig() {
        // Evita instanciação (classe utilitária)
    }

    public static String baseUrl() {
        // Mantém sysprop: baseUrl (legado)
        // YAML: application.baseUrl
        return getString("baseUrl",
                yamlString("application", "baseUrl", "https://example.com"));
    }

    public static String browser() {
        // Mantém sysprop: browser (legado)
        // YAML: execution.browser
        return getString("browser",
                yamlString("execution", "browser", "chrome")); // chrome|firefox|edge
    }

    public static boolean headless() {
        // Mantém sysprop: headless (legado)
        // YAML: execution.headless
        return getBoolean("headless",
                yamlBoolean("execution", "headless", false));
    }

    public static boolean remote() {
        // Mantém sysprop: remote (legado)
        // YAML: execution.remote
        return getBoolean("remote",
                yamlBoolean("execution", "remote", false));
    }

    public static String remoteUrl() {
        // Mantém sysprop: remoteUrl (legado)
        // YAML: execution.remoteUrl
        return getString("remoteUrl",
                yamlString("execution", "remoteUrl", "http://localhost:4444/wd/hub"));
    }

    public static int timeoutSeconds() {
        // Mantém sysprop: timeout (legado)
        // YAML: timeouts.seconds
        return getInt("timeout",
                yamlInt("timeouts", "seconds", 10), 10);
    }

    /* ==========================================================
       Helpers: prioridade -D > YAML > fallback
       ========================================================== */

    private static String getString(String sysProp, String defaultValue) {
        return System.getProperty(sysProp, defaultValue);
    }

    private static boolean getBoolean(String sysProp, boolean defaultValue) {
        return Boolean.parseBoolean(System.getProperty(sysProp, Boolean.toString(defaultValue)));
    }

    private static int getInt(String sysProp, int defaultValue, int safeFallback) {
        String raw = System.getProperty(sysProp);
        if (raw == null || raw.isBlank()) return defaultValue;

        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return safeFallback;
        }
    }

    @SuppressWarnings("unchecked")
    private static String yamlString(String section, String key, String fallback) {
        Object v = yamlValue(section, key);
        return v == null ? fallback : v.toString();
    }

    private static boolean yamlBoolean(String section, String key, boolean fallback) {
        Object v = yamlValue(section, key);
        if (v == null) return fallback;
        if (v instanceof Boolean b) return b;
        return Boolean.parseBoolean(v.toString());
    }

    private static int yamlInt(String section, String key, int fallback) {
        Object v = yamlValue(section, key);
        if (v == null) return fallback;
        if (v instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(v.toString().trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    @SuppressWarnings("unchecked")
    private static Object yamlValue(String section, String key) {
        Object sec = YAML_CONFIG.get(section);
        if (!(sec instanceof Map<?, ?> map)) return null;
        return ((Map<String, Object>) map).get(key);
    }

    private static Map<String, Object> loadYaml() {
        if (!YAML_ENABLED) {
            return Collections.emptyMap();
        }

        // 1) tenta YAML do projeto (se -Dproject informado)
        if (PROJECT != null && !PROJECT.isBlank()) {
            String projectPath = PROJECTS_YAML_DIR + "/" + PROJECT.trim() + ".yaml";
            Map<String, Object> projectYaml = tryLoadYaml(projectPath);
            if (!projectYaml.isEmpty()) {
                return projectYaml;
            }
            // Se não existir ou falhar, segue com padrão (não quebra nada)
        }

        // 2) YAML padrão (legado)
        return tryLoadYaml(DEFAULT_YAML_PATH);
    }

    private static Map<String, Object> tryLoadYaml(String path) {
        try (InputStream is = FrameworkConfig.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                return Collections.emptyMap();
            }
            Map<String, Object> loaded = new Yaml().load(is);
            return loaded == null ? Collections.emptyMap() : loaded;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
