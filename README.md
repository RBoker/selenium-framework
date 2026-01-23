# Selenium Framework (Java)

Framework de automação de testes em **Java + Selenium**, projetado para separar com clareza:

- **Testes unitários do framework**
- **Testes de UI (end-to-end)**

com **execução isolada**, **controle de logging**, **evidência automática em falha** e **gate de cobertura com JaCoCo**.

---

## 📌 Principais características

- ✅ Java 17
- ✅ Selenium 4
- ✅ Maven (Surefire + Failsafe)
- ✅ JUnit 5 (testes unitários)
- ✅ TestNG + Cucumber (testes de UI)
- ✅ JaCoCo com **gate de cobertura (70%)**
- ✅ Execução separada por **Maven Profiles**
- ✅ Evidência automática (screenshot) em falha
- ✅ Page Objects com `BasePage`
- ✅ `DriverManager` seguro (sem driver zumbi)
- ✅ `WaitFactory` centralizado
- ✅ Logging controlado (Logback + logging.properties)
- ✅ Logs limpos (sem warnings ruidosos do Selenium)

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
│       ├── ui            # Testes de UI (Selenium + TestNG)
│       └── bdd           # Steps e runners Cucumber
│
└── resources
├── features         # Arquivos .feature (Cucumber)
├── logback-test.xml # Logging SLF4J / Logback
└── logging.properties # Logging JUL (Selenium / WebDriver)

````

---

## ⚙️ Configuração via `-D`

O framework é configurado **exclusivamente por System Properties**, sem recompilar.

| Propriedade | Descrição | Default |
|------------|----------|---------|
| `baseUrl` | URL base da aplicação | `https://example.com` |
| `browser` | Browser (`chrome`, `firefox`, `edge`) | `chrome` |
| `headless` | Executa em modo headless | `false` |
| `remote` | Usa Selenium Grid | `false` |
| `remoteUrl` | URL do Grid | `http://localhost:4444/wd/hub` |
| `timeout` | Timeout padrão (segundos) | `10` |

Exemplo:

```bash
  mvn test -Dtimeout=15
````

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

* 📁 Pacotes:

    * `com.rboker.automation.tests.ui`
    * `com.rboker.automation.tests.bdd`
* 🧪 Frameworks:

    * **TestNG**
    * **Cucumber**
* ▶️ Executados pelo **maven-failsafe-plugin**
* 🔒 Isolados dos testes unitários
* 🧹 Não afetam JaCoCo

Execução:

```bash
  mvn clean verify -Pui \
    -DbaseUrl=https://www.wikipedia.org \
    -Dbrowser=chrome \
    -Dheadless=false
```

---

## 📋 Logging

O framework usa **dois níveis de logging**, corretamente separados:

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

➡️ Isso elimina warnings ruidosos como:

* `Unable to find CDP implementation`
* Logs excessivos do Selenium

---

## 📸 Evidências em falha (UI)

Em qualquer falha de teste UI:

* 📷 Screenshot automático
* 📁 Diretório:

```
target/screenshots
```

* 🧾 Nome do arquivo contém:

    * Nome do teste (sanitizado)
    * Timestamp

---

## 🧩 Page Objects

O framework utiliza **Page Objects** com `BasePage`, separando claramente:

* **Teste** → o que validar
* **Página** → como interagir

Exemplo:

```java
new WikipediaHomePage(driver)
    .search("Selenium");
```

---

## 🚦 Cobertura de código (JaCoCo)

* Aplicada **somente aos testes unitários**
* Classes fortemente acopladas ao Selenium são excluídas
* Build falha automaticamente se cobertura < **70%**

Execução:

```bash
  mvn test
```

---

## 🧹 Boas práticas adotadas

* WebDriver sempre finalizado (`quit()` garantido)
* `DriverManager` centralizado
* `WaitFactory` reutilizável
* `ElementActions` encapsula waits e interações
* Testes unitários sem dependência de browser
* `.gitignore` preparado para Maven, IDEs e JaCoCo
* Arquitetura preparada para evolução incremental

---

## 🛠️ Tecnologias

* Java 17
* Selenium 4
* Maven
* TestNG
* Cucumber
* JUnit 5
* Mockito
* AssertJ
* JaCoCo
* SLF4J / Logback

---

## 🚀 Próximos passos sugeridos

* Tornar `ElementActions` instanciável
* Reduzir gradualmente exclusões do JaCoCo
* Execução paralela (TestNG)
* Retry controlado para UI
* Integração CI (GitHub Actions / GitLab CI)

---

## 📄 Licença

Uso livre para fins educacionais e profissionais.


