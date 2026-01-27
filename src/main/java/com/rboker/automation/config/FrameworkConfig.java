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
                yamlString("application.baseUrl", "https://example.com"));
    }

    public static String browser() {
        // Sysprop: browser (legado)
        // YAML: execution.browser
        return getString("browser",
                yamlString("execution.browser", "chrome"));
    }

    public static boolean headless() {
        // Sysprop: headless (legado)
        // YAML: execution.headless
        return getBoolean("headless",
                yamlBoolean("execution.headless", false));
    }

    public static boolean remote() {
        // Sysprop: remote (legado)
        // YAML: execution.remote.enabled
        return getBoolean("remote",
                yamlBoolean("execution.remote.enabled", false));
    }

    public static String remoteUrl() {
        // Sysprop: remoteUrl (legado)
        // YAML: execution.remote.url
        return getString("remoteUrl",
                yamlString("execution.remote.url", "http://localhost:4444/wd/hub"));
    }

    public static int timeoutSeconds() {
        // Sysprop: timeout (legado)
        // YAML: timeouts.defaultSeconds
        return getInt("timeout",
                yamlInt("timeouts.defaultSeconds", 10), 10);
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
        return yamlInt("timeouts.uiSeconds", 25);
    }

    public static int pageLoadSeconds() {
        // Sysprop: pageLoadSeconds (se quiser)
        // YAML: timeouts.pageLoadSeconds
        return getInt("pageLoadSeconds",
                yamlInt("timeouts.pageLoadSeconds", 30), 30);
    }

    public static int implicitWaitSeconds() {
        // Sysprop: implicitWaitSeconds (se quiser)
        // YAML: timeouts.implicitWaitSeconds
        return getInt("implicitWaitSeconds",
                yamlInt("timeouts.implicitWaitSeconds", 0), 0);
    }

    public static int explicitWaitSeconds() {
        // Sysprop: explicitWaitSeconds (se quiser)
        // YAML: timeouts.explicitWaitSeconds
        return getInt("explicitWaitSeconds",
                yamlInt("timeouts.explicitWaitSeconds", 10), 10);
    }

    public static boolean maximizeWindow() {
        // Sysprop: windowMaximize (se quiser)
        // YAML: execution.window.maximize
        return getBoolean("windowMaximize",
                yamlBoolean("execution.window.maximize", true));
    }

    public static String loggingLevel() {
        // Sysprop: logLevel (se quiser)
        // YAML: logging.level
        return getString("logLevel",
                yamlString("logging.level", "INFO"));
    }

    /* ==========================================================
       Cucumber (contrato do YAML por projeto)
       ========================================================== */

    /**
     * YAML (recomendado):
     * cucumber:
     *   features:
     *     - "classpath:features/register"
     */
    public static List<String> cucumberFeatures() {
        String sys = System.getProperty("cucumber.features");
        if (sys != null && !sys.isBlank()) return splitCsvOrSemicolon(sys);
        return yamlStringList("cucumber.features");
    }

    /**
     * YAML (recomendado):
     * cucumber:
     *   glue:
     *     - "com.rboker.automation.tests.bdd.steps"
     *     - "com.rboker.automation.tests.bdd.hooks"
     */
    public static List<String> cucumberGlue() {
        String sys = System.getProperty("cucumber.glue");
        if (sys != null && !sys.isBlank()) return splitCsvOrSemicolon(sys);
        return yamlStringList("cucumber.glue");
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
        return yamlString("cucumber.tags", "");
    }

    /**
     * YAML:
     * cucumber:
     *   plugins:
     *     - "pretty"
     *     - "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
     */
    public static List<String> cucumberPlugins() {
        String sys = System.getProperty("cucumber.plugin");
        if (sys == null || sys.isBlank()) sys = System.getProperty("cucumber.plugins");
        if (sys != null && !sys.isBlank()) return splitCsvOrSemicolon(sys);
        return yamlStringList("cucumber.plugins");
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

    private static String yamlString(String path, String fallback) {
        Object v = yamlValue(path);
        return v == null ? fallback : v.toString();
    }

    private static boolean yamlBoolean(String path, boolean fallback) {
        Object v = yamlValue(path);
        if (v == null) return fallback;
        if (v instanceof Boolean b) return b;
        return Boolean.parseBoolean(v.toString());
    }

    private static int yamlInt(String path, int fallback) {
        Object v = yamlValue(path);
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
    private static List<String> yamlStringList(String path) {
        Object v = yamlValue(path);
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

    /**
     * Resolve um caminho com dot-notation, por exemplo:
     * "execution.remote.enabled"
     */
    @SuppressWarnings("unchecked")
    private static Object yamlValue(String path) {
        if (path == null || path.isBlank()) return null;

        String[] parts = path.split("\\.");
        Object current = YAML_CONFIG;

        for (String part : parts) {
            if (!(current instanceof Map<?, ?> map)) return null;
            Object next = ((Map<String, Object>) map).get(part);
            if (next == null) return null;
            current = next;
        }

        return current;
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

            return normalizeMapDeep(raw);
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
                        normalizeMapDeep(baseMap),
                        normalizeMapDeep(overrideMap)
                );
                result.put(key, mergedChild);
            } else {
                // Listas e valores simples: override substitui
                result.put(key, overrideVal);
            }
        }

        return result;
    }

    /**
     * Normaliza Map recursivamente, garantindo chaves String e preservando estrutura.
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> normalizeMapDeep(Map<?, ?> raw) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<?, ?> e : raw.entrySet()) {
            if (e.getKey() == null) continue;

            String key = e.getKey().toString();
            Object val = e.getValue();

            if (val instanceof Map<?, ?> childMap) {
                out.put(key, normalizeMapDeep(childMap));
            } else if (val instanceof List<?> list) {
                out.put(key, normalizeListDeep(list));
            } else {
                out.put(key, val);
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> normalizeListDeep(List<?> raw) {
        List<Object> out = new ArrayList<>();
        for (Object item : raw) {
            if (item instanceof Map<?, ?> mapItem) {
                out.add(normalizeMapDeep(mapItem));
            } else if (item instanceof List<?> listItem) {
                out.add(normalizeListDeep(listItem));
            } else {
                out.add(item);
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

        /* ==========================================================
       Projeto (metadata)
       ========================================================== */

    /**
     * Nome "humano" do projeto, vindo do YAML padronizado:
     *
     * project:
     *   name: "Wikipedia"
     */
    public static String projectName() {
        return yamlString("project.name", "");
    }

    public static String screenshotMode() {
        // Sysprop opcional: -DscreenshotMode=EACH_STEP
        return getString("screenshotMode",
                yamlString("evidence.screenshot.mode", "FAILED_ONLY"));
    }

    public static boolean screenshotAttachToAllure() {
        return getBoolean("screenshotAttachToAllure",
                yamlBoolean("evidence.screenshot.attachToAllure", true));
    }

    public static boolean screenshotSaveToFile() {
        return getBoolean("screenshotSaveToFile",
                yamlBoolean("evidence.screenshot.saveToFile", true));
    }

}
