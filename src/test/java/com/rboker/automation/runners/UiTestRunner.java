package com.rboker.automation.runners;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Runner Cucumber via JUnit Platform.
 *
 * IMPORTANTE (modo 100% YAML):
 * - NÃO fixa glue/plugins/tags aqui.
 * - A seleção (tags/glue/plugins) vem do junit-platform.properties gerado do YAML.
 *
 * Discovery:
 * - Precisamos de um selector para o Suite Engine descobrir algo.
 * - Selecionamos a raiz "features" e deixamos o filtro de projeto por TAGS.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public final class UiTestRunner {
    // Intencionalmente vazio.
}
