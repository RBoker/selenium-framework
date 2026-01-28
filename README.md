# Selenium Framework (Java)

Framework de automação de testes **UI / E2E** em **Java + Selenium**, projetado para ser **simples de usar por analistas**, **robusto para CI/CD** e **flexível para múltiplos projetos** dentro do mesmo repositório.

O foco do framework é **organização**, **previsibilidade de execução**, **observabilidade (logs + evidências)** e **relatórios ricos com histórico (Allure)**.

---

## 🎯 Objetivos do framework

* Facilitar a criação e manutenção de testes UI
* Suportar **múltiplos projetos** (sites/sistemas) no mesmo framework
* Executar testes de forma **isolada por projeto**
* Gerar **relatórios Allure com histórico e trend**
* Ser facilmente integrado a pipelines de **CI/CD**
* Evitar acoplamentos frágeis e mágicas escondidas

---

## 🧰 Stack tecnológica

* **Java 17**
* **Selenium 4**
* **Maven** (Surefire + Failsafe)
* **Cucumber 7** (BDD)
* **JUnit Platform**
* **Allure Report** (com history e trend)
* **SnakeYAML** (configuração por projeto)
* **SLF4J + Logback** (logging)
* **JaCoCo** (cobertura de código)

---

## 📁 Estrutura do projeto

```
src
├── main
│   └── java
│       └── com.rboker.automation
│           ├── core            # Driver, waits, base classes
│           ├── config          # Configuração central do framework
│           └── support         # Utilidades e helpers
│
├── test
│   ├── java
│   │   └── com.rboker.automation.projects
│   │       └── wikipedia
│   │           ├── ui
│   │           │   ├── pages   # Page Objects
│   │           │   ├── steps   # Steps do Cucumber
│   │           │   └── runner  # UiTestRunner
│   │
│   └── resources
│       ├── features            # Features do Cucumber (BDD)
│       │   └── wikipedia
│       │
│       ├── config
│       │   └── projects        # YAML por projeto
│       │       └── wikipedia.yaml
│       │
│       └── allure
│           └── categories.json # Categorias do Allure
│
└── .allure-history              # Histórico persistido do Allure (por projeto)
```

---

## 🧩 Conceito de multi-projeto

O framework suporta **vários projetos** (sites ou sistemas) usando o mesmo código-base.

Cada projeto é identificado por um **ID**, passado via linha de comando:

```bash
-Dproject=wikipedia
```

Esse ID é usado para:

* Resolver o **arquivo YAML do projeto**
* Selecionar **features e cenários**
* Separar o **histórico do Allure**

---

## ⚙️ Configuração por projeto (YAML)

Cada projeto possui um arquivo YAML em:

```
src/test/resources/config/projects/<project>.yaml
```

Exemplo (`wikipedia.yaml`):

```yaml
baseUrl: https://www.wikipedia.org
browser: chrome
headless: false

cucumber:
  tags: "@ui and @wikipedia"
```

### O que pode ser configurado via YAML

* URL base do sistema
* Browser
* Headless / não headless
* Tags do Cucumber
* Outras flags específicas do projeto

O YAML é lido **antes da execução** e convertido automaticamente em configurações do JUnit Platform.

---

## 🧪 Execução dos testes

### Execução padrão (UI)

```bash
mvn clean verify -Pui -Dproject=wikipedia
```

O que acontece nesse comando:

1. Lê o YAML do projeto
2. Gera `junit-platform.properties`
3. Restaura histórico do Allure (se existir)
4. Executa os testes UI (Failsafe)
5. Gera o relatório Allure
6. Persiste o histórico para a próxima execução

---

## 🏷️ Tags e cenários

Os cenários são filtrados por **tags do Cucumber**, definidas no YAML:

```yaml
cucumber:
  tags: "@ui and @wikipedia"
```

É **normal** o Maven exibir warnings como:

```
Tests run: 17, Failures: 0, Errors: 0, Skipped: 15
```

Isso acontece porque:

* O JUnit descobre todos os cenários
* Apenas os que casam com as tags são executados

⚠️ Isso **não indica erro**.

---

## 📊 Relatórios Allure (com histórico e trend)

### Geração automática

O relatório Allure é gerado automaticamente ao final da execução:

```
target/site/allure-maven-plugin/index.html
```

### Histórico e Trend

O framework mantém histórico persistido fora do `target`:

```
.allure-history/<project>/history
```

Fluxo:

1. Antes dos testes: histórico é restaurado para `allure-results`
2. Após o report: novo histórico é salvo
3. A partir da **segunda execução**, o **Trend** aparece no Allure

---

## 🧾 Categories (Allure)

O arquivo de categorias fica em:

```
src/test/resources/allure/categories.json
```

Ele permite agrupar falhas no relatório por tipo (ex.: timeout, assertion, etc).

---

## 📸 Evidências (Screenshots)

* Screenshots são gerados automaticamente em **falha**
* Integrados ao Allure
* Associados ao cenário que falhou

---

## 🧠 Logging

* Logging via **SLF4J + Logback**
* Logs limpos (sem ruído excessivo do Selenium)
* Fácil ajuste de nível (`INFO`, `DEBUG`, etc.)

---

## 🔬 Testes unitários do framework

* Testes unitários rodam com **Surefire**
* Testes UI rodam com **Failsafe**
* No profile `ui`, os testes unitários são automaticamente desabilitados

---

## 📈 Cobertura de código (JaCoCo)

* JaCoCo integrado
* Coleta cobertura do código do framework
* Pronto para ser usado como **gate de qualidade** no CI

---

## 🧱 Boas práticas adotadas

* Page Object Model
* Separação clara entre framework e testes
* Configuração explícita (nada escondido)
* Paths previsíveis
* Build reproduzível

---

## 🚀 Criando um novo projeto

Checklist rápido:

1. Criar `src/test/resources/config/projects/<project>.yaml`
2. Criar features em `src/test/resources/features/<project>/`
3. Criar steps/pages em `com.rboker.automation.projects.<project>`
4. Executar:

   ```bash
   mvn clean verify -Pui -Dproject=<project>
   ```

---

## 🧯 Troubleshooting (Problemas comuns)

### ❌ Nenhum teste executa

**Sintoma**:

* Build passa, mas nenhum cenário roda

**Possíveis causas**:

* Tags configuradas no YAML não correspondem às tags das features
* `-Dproject` não informado ou incorreto

**Como verificar**:

* Confira o YAML em `src/test/resources/config/projects/<project>.yaml`
* Valide as tags com as features

---

### ⚠️ Muitos testes aparecem como *Skipped*

**Exemplo**:

```
Tests run: 17, Failures: 0, Errors: 0, Skipped: 15
```

**Explicação**:

* O JUnit Platform descobre todos os cenários
* Apenas os que casam com as tags do projeto são executados

✔️ **Comportamento esperado com Cucumber + JUnit Platform**

---

### 📉 Trend não aparece no Allure

**Importante**:

* O Trend só aparece a partir da **segunda execução**

**Checklist**:

1. Execute o mesmo comando duas vezes
2. Verifique se existe:

   ```
   .allure-history/<project>/history
   ```
3. Confirme que o restore ocorre antes do report

---

### 📂 categories.json não é reconhecido

**Caminho esperado**:

```
src/test/resources/allure/categories.json
```

O arquivo é copiado automaticamente para `allure-results` antes da execução.

---

## 📌 Observações finais

* O Trend do Allure aparece a partir da **2ª execução**
* O diretório `.allure-history` **não deve ser apagado** entre execuções
* O framework foi desenhado para evoluir sem quebrar contratos

---

## 🧑‍💻 Público-alvo do framework

Este framework foi desenhado para:

* Analistas de QA
* Engenheiros de Automação
* Times que precisam executar testes UI com rapidez e previsibilidade

Sem exigir conhecimento profundo de Maven ou JUnit para o uso básico.

---

## 🤝 Contribuição e evolução

Este framework é uma base sólida para:

* automação UI corporativa
* aprendizado
* entrevistas técnicas
* projetos reais

Evoluções são bem-vindas, desde que mantenham:

* clareza
* simplicidade
* previsibilidade

---

**Autor:** Roberto Boker
