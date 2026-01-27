package com.rboker.automation.support;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Lê o YAML do projeto (src/test/resources/config/projects/{project}.yaml) e
 * gera o arquivo junit-platform.properties dentro de target/test-classes,
 * antes do JUnit/Cucumber fazer discovery.
 *
 * Assim, features/glue/tags/plugins ficam 100% no YAML.
 */
public final class ProjectYamlConfigurator {

    private ProjectYamlConfigurator() {
        // utilitário
    }

    public static void main(String[] args) throws IOException {
        String project = System.getProperty("project");
        if (isBlank(project)) {
            project = "register"; // default seguro (ou ajuste para "default")
        }

        String yamlPath = "config/projects/" + project;
        Map<String, Object> root;

        try {
            root = loadYamlFromTestResources(yamlPath + ".yaml");
        } catch (IllegalStateException ex) {
            root = loadYamlFromTestResources(yamlPath + ".yml");
        }

        Map<String, Object> cucumber = asMap(root.get("cucumber"), "cucumber");

        // features agora pode ser lista (recomendado) ou string (compat)
        List<String> features = asStringList(cucumber.get("features"), "cucumber.features");
        List<String> glue = asStringList(cucumber.get("glue"), "cucumber.glue");
        String tags = asNullableString(cucumber.get("tags"));
        List<String> plugins = asStringList(cucumber.get("plugins"), "cucumber.plugins");

        // Normalização básica (evita espaços e itens vazios)
        features = normalizeStringList(features);
        glue = normalizeStringList(glue);
        plugins = normalizeStringList(plugins);
        tags = (tags == null) ? null : tags.trim();

        if (features.isEmpty()) {
            throw new IllegalStateException("YAML inválido: cucumber.features está vazio em " + yamlPath);
        }
        if (glue.isEmpty()) {
            throw new IllegalStateException("YAML inválido: cucumber.glue está vazio em " + yamlPath);
        }

        Properties props = new Properties();

        // Cucumber JUnit Platform Engine properties
        // IMPORTANTE: cucumber.features não aceita a string "[...]" (lista toString).
        // Precisa ser CSV: "classpath:features/a,classpath:features/b"
        props.setProperty("cucumber.features", String.join(",", features));
        props.setProperty("cucumber.glue", String.join(",", glue));

        if (!isBlank(tags)) {
            props.setProperty("cucumber.filter.tags", tags);
        }

        if (!plugins.isEmpty()) {
            props.setProperty("cucumber.plugin", String.join(",", plugins));
        }

        // (Opcional) deixa registrado qual projeto está rodando
        props.setProperty("framework.project", project);

        writeToTargetTestClasses(props);
        System.out.println("[ProjectYamlConfigurator] Gerado junit-platform.properties para project=" + project);
    }

    private static Map<String, Object> loadYamlFromTestResources(String resourcePath) throws IOException {
        try (InputStream in = ProjectYamlConfigurator.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Não encontrei o YAML em src/test/resources/" + resourcePath);
            }

            LoaderOptions options = new LoaderOptions();
            Yaml yaml = new Yaml(new SafeConstructor(options));
            Object obj = yaml.load(in);

            if (obj == null) {
                throw new IllegalStateException("YAML inválido: arquivo vazio em " + resourcePath);
            }
            if (!(obj instanceof Map<?, ?> map)) {
                throw new IllegalStateException("YAML inválido: raiz deve ser um objeto/map em " + resourcePath);
            }

            return toStringObjectMap(map, "root");
        }
    }

    private static void writeToTargetTestClasses(Properties props) throws IOException {
        Path outDir = Path.of("target", "test-classes");
        Files.createDirectories(outDir);

        Path outFile = outDir.resolve("junit-platform.properties");

        // Grava no formato padrão .properties (determinístico)
        StringBuilder sb = new StringBuilder();
        List<String> keys = props.stringPropertyNames().stream().sorted().collect(Collectors.toList());
        for (String k : keys) {
            sb.append(k).append("=").append(props.getProperty(k)).append("\n");
        }

        Files.writeString(outFile, sb.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Converte um Map<?, ?> em Map<String, Object> validando que todas as chaves são String.
     */
    private static Map<String, Object> toStringObjectMap(Map<?, ?> map, String path) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<?, ?> e : map.entrySet()) {
            Object k = e.getKey();
            if (!(k instanceof String ks)) {
                throw new IllegalStateException("YAML inválido: chave não-string em '" + path + "': " + k);
            }
            out.put(ks, e.getValue());
        }
        return out;
    }

    private static Map<String, Object> asMap(Object v, String path) {
        if (v == null) return Collections.emptyMap();
        if (!(v instanceof Map<?, ?> map)) {
            throw new IllegalStateException("YAML inválido: '" + path + "' deve ser um objeto/map.");
        }
        return toStringObjectMap(map, path);
    }

    private static String asNullableString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private static List<String> asStringList(Object v, String path) {
        if (v == null) return new ArrayList<>();

        // Lista YAML
        if (v instanceof List<?> list) {
            List<String> out = new ArrayList<>();
            for (Object item : list) {
                if (item == null) {
                    throw new IllegalStateException("YAML inválido: item nulo em '" + path + "'.");
                }
                out.add(String.valueOf(item));
            }
            return out;
        }

        // Permite também string única no YAML (atalho)
        return List.of(String.valueOf(v));
    }

    /**
     * Normaliza lista de strings:
     * - trim
     * - remove vazios
     */
    private static List<String> normalizeStringList(List<String> raw) {
        if (raw == null || raw.isEmpty()) return Collections.emptyList();

        List<String> out = new ArrayList<>();
        for (String s : raw) {
            if (s == null) continue;
            String v = s.trim();
            if (!v.isBlank()) out.add(v);
        }
        return out;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
