# Selenium Framework (Java)

Framework de automação de testes **Java + Selenium**, desenhado para ser **multi‑projeto**, **configurável por YAML**, **observável (logs + evidências)** e **pronto para CI/CD**.

O objetivo é servir como **base reutilizável e profissional** para projetos de automação UI, separando claramente **framework** de **testes**, mantendo qualidade de código, estabilidade e evolução contínua.

---

## 🎯 Objetivos do framework

* Centralizar boas práticas de automação UI em Java
* Permitir múltiplos projetos (sites/sistemas) no mesmo repositório
* Facilitar manutenção com Page Objects e camadas bem definidas
* Garantir estabilidade com waits e sincronismo consistentes
* Gerar evidências automáticas (screenshots)
* Produzir relatórios ricos com Allure
* Ser simples de rodar localmente e em pipelines CI

---

## 🧰 Stack tecnológica

* **Java:** 17 (LTS)
* **Selenium:** 4.x
* **Build:** Maven
* **Testes unitários:** JUnit 5
* **Testes de UI:** TestNG + Cucumber
* **Relatórios:** Allure
* **Cobertura:** JaCoCo (gate mínimo configurável)
* **Configuração:** System Properties + YAML por projeto
* **Logs:** SLF4J + Logback

---

## 🧱 Estrutura do projeto

```
src
├── main
│   └── java
│       └── com.rboker.automation
│           ├── config        # Leitura de configurações (System + YAML)
│           ├── core          # DriverManager, WebDriver lifecycle
│           ├── wait          # WaitFactory e sincronismo
│           ├── utils         # Screenshot, helpers, utilidades
│           └── base          # BasePage e abstrações
│
├── test
│   ├── java
│   │   └── com.rboker.automation
│   │       ├── hooks         # Hooks do Cucumber/TestNG
│   │       ├── runners       # Runners TestNG
│   │       ├── steps         # Step Definitions
│   │       └── tests         # Testes unitários do framework
│   │
│   └── resources
│       ├── features         # Arquivos .feature (Cucumber)
│       ├── config
│       │   └── projects     # YAML por projeto
│       │       ├── wikipedia.yaml
│       │       └── register.yaml
│       └── allure           # environment.properties, categories.json
│
└── pom.xml
```

---

## 🔀 Conceito de multi‑projeto

O framework suporta **múltiplos projetos/sistemas** dentro do mesmo repositório.

Cada projeto possui:

* Um arquivo YAML próprio
* Base URL
* Regras de evidência (screenshots)
* Comportamentos específicos

A seleção do projeto é feita via **System Property**:

```bash
-Dproject=wikipedia
```

O framework carrega automaticamente:

```
src/test/resources/config/projects/wikipedia.yaml
```

---

## ⚙️ Configuração por YAML

Cada projeto possui um YAML dedicado.

### Exemplo: `wikipedia.yaml`

```yaml
project:
  name: Wikipedia
  baseUrl: https://www.wikipedia.org

browser:
  default: chrome
  headless: true

execution:
  timeoutSeconds: 10

screenshots:
  onStep: false
  onFailure: true
```

### Prioridade de configuração

1. System Properties (`-D`)
2. YAML do projeto
3. Valores default do framework

---

## 🧠 Leitura de configurações

O framework possui uma classe central de configuração que:

* Lê System Properties
* Lê o YAML do projeto selecionado
* Resolve overrides automaticamente

Exemplo de uso interno:

```java
Config.getBaseUrl();
Config.isHeadless();
Config.isScreenshotOnFailure();
```

---

## 🌐 Execução de testes UI

### Profile Maven

Os testes de UI são executados via profile `ui`.

```bash
mvn clean verify -Pui \
  -Dproject=wikipedia \
  -Dbrowser=chrome \
  -Dheadless=false
```

### Filtros de execução (Cucumber Tags)

```bash
-Dcucumber.filter.tags="@ui and @smoke"
```

---

## 🧪 Testes unitários do framework

Os testes unitários validam:

* Configuração
* WaitFactory
* ScreenshotUtil
* Utilitários internos

Execução padrão:

```bash
mvn test
```

Esses testes **não** abrem navegador.

---

## 🧭 DriverManager

Responsável por:

* Criar WebDriver
* Encerrar corretamente a sessão
* Evitar drivers zumbis
* Suportar execução paralela (ThreadLocal)

Uso interno:

```java
WebDriver driver = DriverManager.getDriver();
```

---

## ⏱️ WaitFactory

Centraliza toda a lógica de espera:

* WebDriverWait
* FluentWait
* Tratamento de StaleElementReferenceException

Exemplo:

```java
WaitFactory.waitForVisible(element);
```

Evita waits espalhados e inconsistentes no código.

---

## 🧩 Page Objects

O framework segue **Page Object Model** com `BasePage`.

### BasePage

Responsável por:

* Acesso ao driver
* Métodos utilitários comuns
* Integração com waits

### Exemplo de Page Object

```java
public class SearchPage extends BasePage {

    private final By searchInput = By.id("searchInput");

    public void search(String text) {
        type(searchInput, text);
        submit(searchInput);
    }
}
```

---

## 📸 Evidências (screenshots)

O framework suporta evidência automática configurável:

* Screenshot a cada passo
* Screenshot apenas em falha

Configurado no YAML:

```yaml
screenshots:
  onStep: false
  onFailure: true
```

As imagens são anexadas automaticamente ao Allure.

---

## 🆕 Como criar um novo projeto do zero

Esta seção descreve o **passo a passo completo** para adicionar um novo projeto (site ou sistema) ao framework.


### 1) Defina o nome do projeto (chave do YAML)

Escolha um identificador simples, sem espaços e em minúsculo, por exemplo:

- `blogagi`
- `register`
- `minhaapp`

Esse valor será usado na execução via:

```bash
-Dproject=blogagi
```

### 1️⃣ Criar o arquivo YAML do projeto

Crie um novo arquivo em:

```
src/test/resources/config/projects/
```

Exemplo: `meuprojeto.yaml`

```yaml
project:
  name: Meu Projeto
  baseUrl: https://www.meuprojeto.com

browser:
  default: chrome
  headless: true

execution:
  timeoutSeconds: 10

evidence:
  screenshot:
    # NONE = não gera evidência
    # FAILED_ONLY = gera apenas em falha
    # EACH_STEP = gera a cada passo (mais pesado)
    # EACH_SCENARIO = gera ao final de cada cenário
      mode: FAILED_ONLY
      attachToAllure: true
      saveToFile: true
```

> 🔎 O nome do arquivo **define o identificador do projeto** usado na execução (`-Dproject=meuprojeto`).

---

### 2️⃣ Criar as Features do projeto

Adicione as features em:

```
src/test/resources/features/meuprojeto/
```

Exemplo: `busca.feature`

```gherkin
@ui @meuprojeto
Feature: Busca no Meu Projeto
  @ui @meuprojeto @smoke
  Scenario: Realizar uma busca simples
    Given que acesso a página inicial
    When realizo uma busca por "exemplo"
    Then devo ver resultados relacionados
```

---

### 3️⃣ Criar Page Objects

Crie os Page Objects em:

```
src/test/java/com/rboker/automation/pages/meuprojeto/
```

Exemplo:

```java
public class HomePage extends BasePage {

    private final By searchInput = By.id("search");

    public void search(String text) {
        type(searchInput, text);
        submit(searchInput);
    }
}
```

---

### 4️⃣ Criar Step Definitions

Crie os steps em:

```
src/test/java/com/rboker/automation/steps/meuprojeto/
```

Exemplo:

```java
public class SearchSteps {

    private final HomePage homePage = new HomePage();

    @Given("que acesso a página inicial")
    public void acessarPaginaInicial() {
        homePage.open();
    }

    @When("realizo uma busca por {string}")
    public void realizarBusca(String texto) {
        homePage.search(texto);
    }
}
```

---

### 5️⃣ Executar o novo projeto

Execute informando o projeto criado:

```bash
mvn clean verify -Pui -Dproject=meuprojeto -Dcucumber.filter.tags="@meuprojeto"
```

### 6️⃣ Validar o relatório

Após a execução:

```bash
mvn allure:serve
```

Verifique:

* Projeto correto no `environment.properties`
* Evidências conforme configuração do YAML
* Steps e cenários executados corretamente

---

### ✅ Checklist rápido

* [ ] YAML criado em `config/projects`
* [ ] Features organizadas por projeto
* [ ] Page Objects isolados
* [ ] Steps específicos do projeto
* [ ] Tags configuradas
* [ ] Execução validada

Seguindo esses passos, o novo projeto já nasce **padronizado, isolado e pronto para CI** 🚀

---

## 📊 Relatórios com Allure

### Execução

```bash
mvn clean verify -Pui -Dproject=wikipedia allure:serve
```

Ou geração estática:

```bash
mvn allure:report
```

### Arquivos suportados

* `environment.properties`
* `categories.json`

Esses arquivos ficam em:

```
src/test/resources/allure
```

O relatório é **independente por execução**.

---

## 🧪 JaCoCo – Gate de cobertura

O framework aplica **gate mínimo de cobertura** para código do framework.

* Executado automaticamente no `verify`
* Falha o build se não atingir o percentual configurado

Isso garante qualidade contínua.

---

## 🧵 Execução paralela

* TestNG permite paralelismo
* Driver isolado por thread
* Seguro para CI

Configuração via TestNG XML (quando necessário).

---

## 🤖 Integração com CI/CD

O framework é compatível com:

* GitHub Actions
* GitLab CI
* Jenkins

Basta garantir:

* Java 17
* Maven
* Navegador (ou grid remoto futuramente)

---

## 🛡️ Boas práticas adotadas

* Separação total entre framework e testes
* Nenhum `Thread.sleep`
* Waits centralizados
* Configuração externa
* Código documentado
* Estrutura limpa

---

## 🚀 Próximos passos (roadmap)

* Selenium Grid
* Execução cross‑browser
* Execução em containers
* Vídeos de execução
* Integração com gerenciadores de teste (ex.: Qase)

---

## 👤 Autor

**Roberto Boker**
QA / Test Automation Engineer

---

## 📄 Licença

Uso livre para fins educacionais e profissionais.
