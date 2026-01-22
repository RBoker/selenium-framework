# Selenium Framework (Java)

Framework de automação de testes em **Java + Selenium**, estruturado para separar claramente **testes unitários do framework** e **testes de UI (end-to-end)**, com **execução por profiles**, **evidências em falha** e **gate de cobertura com JaCoCo**.

---

## 📌 Principais características

- ✅ Selenium 4
- ✅ Java 17
- ✅ Maven
- ✅ TestNG (testes de UI)
- ✅ JUnit 5 (testes unitários do framework)
- ✅ JaCoCo com **gate de cobertura (70%)**
- ✅ Execução separada por **Maven Profiles**
- ✅ Evidência automática (screenshot) em falha
- ✅ Page Objects (BasePage)
- ✅ Gerenciamento seguro de WebDriver (sem driver zumbi)

---

## 🧱 Estrutura do projeto

```

src
├── main
│   └── java
│       └── com.rboker.automation
│           ├── config        # Leitura de configs (-D)
│           └── core          # DriverManager, ScreenshotUtil, etc.
│
└── test
└── java
└── com.rboker.automation.tests
├── unit          # Testes unitários do framework (JUnit 5)
└── ui            # Testes de UI (TestNG + Selenium)

````

---

## ⚙️ Configurações via `-D`

O framework é configurado exclusivamente por **System Properties**, sem recompilar:

| Propriedade | Descrição | Default |
|------------|----------|---------|
| `baseUrl` | URL base do sistema | `https://example.com` |
| `browser` | Browser (`chrome`, `firefox`, `edge`) | `chrome` |
| `headless` | Executa browser em modo headless | `false` |
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

* Local: `com.rboker.automation.tests.unit`
* Framework: **JUnit 5**
* Executados por padrão
* Cobertos por **JaCoCo**
* **Gate de cobertura: 70%**

Execução:

```bash
  mvn test
```

Relatório:

```
target/site/jacoco/index.html
```

---

### 🔹 Testes de UI (Selenium)

* Local: `com.rboker.automation.tests.ui`
* Framework: **TestNG**
* Executados via **profile Maven**
* Isolados dos unit tests

Execução:

```bash
mvn clean verify -Pui \
  -DbaseUrl=https://www.wikipedia.org -Dbrowser=chrome
```

Suite TestNG:

```
src/test/resources/testng-ui.xml
```

---

## 📸 Evidências em falha

* Em qualquer falha de teste UI:

    * Screenshot é capturado automaticamente
    * Arquivos são salvos em:

      ```
      target/screenshots
      ```
* Nome do arquivo inclui:

    * Nome do teste (sanitizado)
    * Timestamp

---

## 🧩 Page Objects

O framework utiliza **Page Objects** com `BasePage` para separar:

* **Teste** → o que validar
* **Página** → como interagir

Exemplo:

```java
new WikipediaHomePage(driver)
    .search("Selenium")
    .heading();
```

---

## 🚦 Cobertura de código (JaCoCo)

* Cobertura aplicada **somente aos testes unitários**
* Classes fortemente acopladas ao Selenium são excluídas do gate
* Build falha automaticamente se cobertura < **70%**

Execução:

```bash
  mvn test
```

---

## 🧹 Boas práticas adotadas

* WebDriver sempre finalizado (`quit()` garantido)
* Test doubles (fakes) para Selenium em unit tests
* Sem dependência de browser nos testes unitários
* `.gitignore` preparado para Maven, IDEs e JaCoCo
* Código preparado para evolução (injeção, refactor incremental)

---

## 🛠️ Tecnologias

* Java 17
* Selenium 4
* Maven
* TestNG
* JUnit 5
* Mockito
* AssertJ
* JaCoCo
* SLF4J / Logback

---

## 🚀 Próximos passos sugeridos

* Refatorar `WaitFactory` para reduzir acoplamento
* Tornar `ElementActions` instanciável
* Reduzir gradualmente excludes do JaCoCo
* Adicionar execução paralela
* Integrar com CI (GitHub Actions / GitLab CI)

---

## 📄 Licença

Uso livre para fins educacionais e profissionais.

