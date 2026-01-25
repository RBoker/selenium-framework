# Selenium Framework (Java)

Framework de automação de testes em **Java + Selenium**, projetado para separar com clareza:

* **Testes unitários do framework**
* **Testes de UI (end-to-end)**

com **execução isolada**, **controle de logging**, **evidência automática em falha** e **gate de cobertura com JaCoCo**.

> 🎯 Objetivo principal: servir como **base sólida, limpa e evolutiva** para projetos de automação UI em Java, suportando **múltiplos projetos**, **relatórios independentes** e **execução previsível em ambiente local ou CI**.

---

## 📌 Principais características

* ✅ Java 17 (LTS)
* ✅ Selenium 4
* ✅ Maven (Surefire + Failsafe)
* ✅ JUnit 5 (testes unitários)
* ✅ Cucumber 7 + JUnit Platform (testes de UI)
* ✅ JaCoCo com **gate de cobertura (70%)**
* ✅ Execução separada por **Maven Profiles**
* ✅ Evidência automática (screenshot) em falha
* ✅ Page Objects com `BasePage`
* ✅ `DriverManager` seguro (sem driver zumbi)
* ✅ `WaitFactory` centralizado
* ✅ `ElementActions` para interações resilientes
* ✅ Logging controlado (Logback + logging.properties)
* ✅ Logs limpos (sem warnings ruidosos do Selenium)
* ✅ Integração com **Allure Report** (mesmo com falha de testes)

---

## 🧱 Estrutura do projeto

```
src
├── main
│   └── java
│       └── com.rboker.automation
│           ├── config        # Leitura de System Properties (-D)
│           └── core          # DriverManager, WaitFactory, ElementActions, ScreenshotUtil
│
└── test
    ├── java
    │   └── com.rboker.automation.tests
    │       ├── unit          # Testes unitários do framework (JUnit 5)
    │       └── bdd           # Steps e runners Cucumber (UI)
    │
    └── resources
        ├── features          # Arquivos .feature (Cucumber)
        ├── logback-test.xml  # Logging SLF4J / Logback
        └── logging.properties# Logging JUL (Selenium / WebDriver)
```

---

## ⚙️ Configuração via `-D`

O framework é configurado **exclusivamente por System Properties**, permitindo reutilização do mesmo binário para diferentes projetos e ambientes.

| Propriedade | Descrição                             | Default                        |
| ----------- | ------------------------------------- | ------------------------------ |
| `project`   | Identificador lógico do projeto       | `default`                      |
| `baseUrl`   | URL base da aplicação                 | `https://example.com`          |
| `browser`   | Browser (`chrome`, `firefox`, `edge`) | `chrome`                       |
| `headless`  | Executa em modo headless              | `false`                        |
| `remote`    | Usa Selenium Grid                     | `false`                        |
| `remoteUrl` | URL do Grid                           | `http://localhost:4444/wd/hub` |
| `timeout`   | Timeout padrão (segundos)             | `10`                           |

Exemplo:

```bash
mvn clean verify -Pui \
  -Dproject=wikipedia \
  -DbaseUrl=https://www.wikipedia.org \
  -Dbrowser=chrome
```

---

## 🧪 Tipos de testes

### 🔹 Testes Unitários (framework)

* 📁 Pacote: `com.rboker.automation.tests.unit`
* 🧪 Framework: **JUnit 5**
* ▶️ Executados pelo **maven-surefire-plugin**
* 📊 Cobertos por **JaCoCo**
* 🚦 Gate de cobertura: **70%**

Execução:

```bash
mvn clean test
```

Relatório de cobertura:

```
target/site/jacoco/index.html
```

---

### 🔹 Testes de UI (Selenium + Cucumber)

* 📁 Pacote: `com.rboker.automation.tests.bdd`
* 🧪 Frameworks:

  * **Cucumber 7**
  * **JUnit Platform**
* ▶️ Executados pelo **maven-failsafe-plugin**
* 🔒 Totalmente isolados dos testes unitários
* 🧹 Não afetam métricas de cobertura

Execução padrão:

```bash
mvn clean verify -Pui \
  -Dproject=wikipedia \
  -DbaseUrl=https://www.wikipedia.org
```

### ▶️ Executar e abrir relatório Allure (mesmo com falha)

```bash
mvn clean verify -Pui \
  -Dproject=wikipedia \
  -Dfailsafe.testFailureIgnore=true

mvn allure:serve
```

> ⚠️ Importante: a flag `-Dmaven.test.failure.ignore=true` **não se aplica ao Failsafe**. Para testes de UI, use sempre `failsafe.testFailureIgnore`.

---

## 📋 Logging

O framework adota **separação explícita de responsabilidades de logging**.

### 🔹 Logback (SLF4J)

Arquivo:

```
src/test/resources/logback-test.xml
```

Responsável por:

* Logs do framework
* Logs de testes
* Logs de negócio

### 🔹 logging.properties (JUL)

Arquivo:

```
src/test/resources/logging.properties
```

Responsável por:

* Selenium
* WebDriver
* ChromeDriver / GeckoDriver

➡️ Benefício direto: eliminação de warnings ruidosos como `Unable to find CDP implementation`.

---

## 📸 Evidências em falha (UI)

Em qualquer falha de teste UI:

* 📷 Screenshot automático
* 📁 Diretório:

```
target/screenshots
```

* 🧾 Nome do arquivo contém nome do cenário + timestamp

---

## 🚦 Cobertura de código (JaCoCo)

* Aplicada **somente aos testes unitários**
* Classes acopladas ao Selenium são excluídas
* Build falha automaticamente se cobertura < **70%**

---

## 🚀 Estado do projeto

✅ **Fase de suporte a múltiplos projetos e relatórios: CONCLUÍDA**

O framework está pronto para:

* Reutilização entre projetos
* Execução local e em CI
* Evolução incremental sem refatorações estruturais

---

## 🤝 Contribuição

Sugestões, issues e PRs são bem-vindos.

> Mantenha o padrão de separação entre **framework**, **testes unitários** e **testes de UI**.

---

## 📄 Licença

Uso livre para fins educacionais e profissionais.
