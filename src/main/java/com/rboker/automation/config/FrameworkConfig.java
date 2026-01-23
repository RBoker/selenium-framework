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
 * 2) YAML: src/test/resources/config/framework.yaml
 * 3) Fallback hardcoded (segurança)
 *
 * Ex.: mvn verify -Pui -DbaseUrl=https://site -Dbrowser=chrome -Dheadless=true
 */
public final class FrameworkConfig {
    private static final boolean YAML_ENABLED = Boolean.parseBoolean(System.getProperty("yaml.enabled", "true"));
    private static final String YAML_PATH = "config/framework.yaml";
    private static final Map<String, Object> YAML_CONFIG = loadYaml();

    private FrameworkConfig() {
        // Evita instanciação (classe utilitária)
    }

    public static String baseUrl() {
        return getString("baseUrl",
                yamlString("application", "baseUrl", "https://example.com"));
    }

    public static String browser() {
        return getString("browser",
                yamlString("execution", "browser", "chrome")); // chrome|firefox|edge
    }

    public static boolean headless() {
        return getBoolean("headless",
                yamlBoolean("execution", "headless", false));
    }

    public static boolean remote() {
        return getBoolean("remote",
                yamlBoolean("execution", "remote", false));
    }

    public static String remoteUrl() {
        return getString("remoteUrl",
                yamlString("execution", "remoteUrl", "http://localhost:4444/wd/hub"));
    }

    public static int timeoutSeconds() {
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
        try (InputStream is = FrameworkConfig.class.getClassLoader().getResourceAsStream(YAML_PATH)) {
            if (is == null) {
                return Collections.emptyMap();
            }
            return new Yaml().load(is);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

}
