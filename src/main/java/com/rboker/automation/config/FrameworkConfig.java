package com.rboker.automation.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * Centraliza a leitura de configurações do framework.
 *
 * Prioridade:
 * 1) System Properties (-D)
 * 2) YAML por projeto (se -Dproject informado): src/test/resources/config/projects/<project>.yaml
 * 3) YAML padrão: src/test/resources/config/framework.yaml
 * 4) Fallback hardcoded (segurança)
 *
 * Observação (multi-projeto):
 * - O YAML efetivo é o resultado de um MERGE:
 *   framework.yaml (base) + <project>.yaml (override).
 *   O YAML do projeto sobrescreve apenas o que declarar.
 */
public final class FrameworkConfig {

    private static final boolean YAML_ENABLED =
            Boolean.parseBoolean(System.getProperty("yaml.enabled", "true"));

    /**
     * YAML padrão (base) em src/test/resources/config/framework.yaml
     * (no classpath vira: config/framework.yaml).
     */
    private static final String DEFAULT_YAML_PATH = "config/framework.yaml";

    /**
     * Diretório dos YAMLs por projeto em src/test/resources/config/projects/
     * (no classpath vira: config/projects/<project>.yaml).
     */
    private static final String PROJECTS_YAML_DIR = "config/projects";

    /**
     * Nome do projeto selecionado via -Dproject=<nome>.
     */
    private static final String PROJECT = System.getProperty("project");

    /**
     * YAML efetivo após merge (framework.yaml + project.yaml).
     */
    private static final Map<String, Object> YAML_CONFIG = loadYamlMerged();

    private FrameworkConfig() {
        // Evita instanciação (classe utilitária)
    }

    /* ==========================================================
       Configs gerais (execução / aplicação / timeouts)
       ========================================================== */

    public static String baseUrl() {
        // Sysprop: baseUrl (legado)
        // YAML: application.baseUrl
        return getString("baseUrl",
                yamlString("ui", "baseUrl", "https://example.com"));
    }

    public static String browser() {
        // Sysprop: browser (legado)
        // YAML: execution.browser
        return getString("browser",
                yamlString("ui", "browser", "chrome"));
    }

    public static boolean headless() {
        // Sysprop: headless (legado)
        // YAML: execution.headless
        return getBoolean("headless",
                yamlBoolean("ui", "headless", false));
    }

    public static boolean remote() {
        // Sysprop: remote (legado)
        // YAML: execution.remote
        return getBoolean("remote",
                yamlBoolean("execution", "remote", false));
    }

    public static String remoteUrl() {
        // Sysprop: remoteUrl (legado)
        // YAML: execution.remoteUrl
        return getString("remoteUrl",
                yamlString("execution", "remoteUrl", "http://localhost:4444/wd/hub"));
    }

    public static int timeoutSeconds() {
        // Sysprop: timeout (legado)
        // YAML: timeouts.seconds
        return getInt("timeout",
                yamlInt("ui", "timeoutSeconds", 10), 10);
    }

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
        return yamlInt("ui", "uiTimeoutSeconds", 25);
    }

    /* ==========================================================
       Cucumber (contrato do YAML por projeto)
       ========================================================== */

    /**
     * YAML (recomendado):
     * cucumber:
     *   features:
     *     - classpath:features/register
     */
    public static List<String> cucumberFeatures() {
        String sys = System.getProperty("cucumber.features");
        if (sys != null && !sys.isBlank()) return splitCsvOrSemicolon(sys);
        return yamlStringList("cucumber", "features");
    }

    /**
     * YAML (recomendado):
     * cucumber:
     *   glue:
     *     - com.rboker.automation.tests.bdd.steps
     *     - com.rboker.automation.tests.bdd.hooks
     */
    public static List<String> cucumberGlue() {
        String sys = System.getProperty("cucumber.glue");
        if (sys != null && !sys.isBlank()) return splitCsvOrSemicolon(sys);
        return yamlStringList("cucumber", "glue");
    }

    /**
     * YAML:
     * cucumber:
     *   tags: "@ui and @register"
     */
    public static String cucumberTags() {
        String sys = System.getProperty("cucumber.filter.tags");
        if (sys == null || sys.isBlank()) sys = System.getProperty("cucumber.tags");
        if (sys != null && !sys.isBlank()) return sys.trim();
        return yamlString("cucumber", "tags", "");
    }

    /**
     * YAML:
     * cucumber:
     *   plugins:
     *     - pretty
     *     - io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm
     */
    public static List<String> cucumberPlugins() {
        String sys = System.getProperty("cucumber.plugin");
        if (sys == null || sys.isBlank()) sys = System.getProperty("cucumber.plugins");
        if (sys != null && !sys.isBlank()) return splitCsvOrSemicolon(sys);
        return yamlStringList("cucumber", "plugins");
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

    /**
     * Lê lista de strings do YAML aceitando:
     * - lista YAML
     * - string única
     */
    private static List<String> yamlStringList(String section, String key) {
        Object v = yamlValue(section, key);
        if (v == null) return Collections.emptyList();

        if (v instanceof List<?> list) {
            List<String> out = new ArrayList<>();
            for (Object item : list) {
                if (item != null) {
                    String s = item.toString().trim();
                    if (!s.isBlank()) out.add(s);
                }
            }
            return Collections.unmodifiableList(out);
        }

        String s = v.toString().trim();
        if (s.isBlank()) return Collections.emptyList();
        return Collections.unmodifiableList(List.of(s));
    }

    @SuppressWarnings("unchecked")
    private static Object yamlValue(String section, String key) {
        Object sec = YAML_CONFIG.get(section);
        if (!(sec instanceof Map<?, ?> map)) return null;
        return ((Map<String, Object>) map).get(key);
    }

    /* ==========================================================
       YAML loading + merge
       ========================================================== */

    private static Map<String, Object> loadYamlMerged() {
        if (!YAML_ENABLED) {
            return Collections.emptyMap();
        }

        // 1) Base: framework.yaml
        Map<String, Object> base = tryLoadYaml(DEFAULT_YAML_PATH);

        // 2) Override: config/projects/<project>.yaml
        if (PROJECT != null && !PROJECT.isBlank()) {
            String projectPath = PROJECTS_YAML_DIR + "/" + PROJECT.trim() + ".yaml";
            Map<String, Object> override = tryLoadYaml(projectPath);

            if (!override.isEmpty()) {
                Map<String, Object> merged = deepMergeMaps(base, override);
                return Collections.unmodifiableMap(merged);
            }
        }

        return Collections.unmodifiableMap(base);
    }

    private static Map<String, Object> tryLoadYaml(String path) {
        try (InputStream is = FrameworkConfig.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) return Collections.emptyMap();

            Object loaded = new Yaml().load(is);
            if (!(loaded instanceof Map<?, ?> raw)) return Collections.emptyMap();

            return normalizeMap(raw);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    /**
     * Merge profundo:
     * - Map + Map => merge recursivo
     * - Listas e valores simples => override substitui base
     */
    private static Map<String, Object> deepMergeMaps(Map<String, Object> base, Map<String, Object> override) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (base != null) result.putAll(base);
        if (override == null) return result;

        for (Map.Entry<String, Object> e : override.entrySet()) {
            String key = e.getKey();
            Object overrideVal = e.getValue();
            Object baseVal = result.get(key);

            if (baseVal instanceof Map<?, ?> baseMap && overrideVal instanceof Map<?, ?> overrideMap) {
                Map<String, Object> mergedChild = deepMergeMaps(
                        normalizeMap(baseMap),
                        normalizeMap(overrideMap)
                );
                result.put(key, mergedChild);
            } else {
                // Listas e valores simples: override substitui
                result.put(key, overrideVal);
            }
        }

        return result;
    }

    private static Map<String, Object> normalizeMap(Map<?, ?> raw) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<?, ?> e : raw.entrySet()) {
            if (e.getKey() != null) {
                out.put(e.getKey().toString(), e.getValue());
            }
        }
        return out;
    }

    private static List<String> splitCsvOrSemicolon(String raw) {
        if (raw == null) return Collections.emptyList();
        String normalized = raw.trim();
        if (normalized.isBlank()) return Collections.emptyList();

        String[] parts = normalized.split("[,;]");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            if (p != null) {
                String s = p.trim();
                if (!s.isBlank()) out.add(s);
            }
        }
        return Collections.unmodifiableList(out);
    }
}
