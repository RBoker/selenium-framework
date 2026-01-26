package com.rboker.automation.tests.bdd.runner;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;

/**
 * Runner Cucumber via JUnit Platform.
 *
 * IMPORTANTE (modo 100% YAML):
 * - NÃO fixa features/glue/plugins/tags aqui.
 * - A seleção é feita via 'junit-platform.properties' no classpath de teste,
 *   gerado dinamicamente a partir do YAML do projeto (ex.: src/test/resources/projects/{project}.yaml).
 *
 * Vantagens:
 * - Um único comando para rodar: mvn clean verify -Pui -Dproject=register
 * - Evita mexer em POM por projeto
 * - Evita hardcode de paths/pacotes no runner
 */
@Suite
@IncludeEngines("cucumber")
public class RunCucumberTest {
}
