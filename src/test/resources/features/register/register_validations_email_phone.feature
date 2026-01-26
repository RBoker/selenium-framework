# language: pt
@ui @register @validations @p0
Funcionalidade: Cadastro - Validações de Email e Telefone

  Esquema do Cenário: Validar formato de email
    Dado que acesso a página de cadastro
    Quando preencho o nome "Roberto" e o sobrenome "Boker"
    E preencho o email "<email>"
    E preencho o telefone "11990182878"
    E seleciono o gênero "Male"
    E seleciono o país no campo Country "Denmark"
    E informo a senha "Qa@12345" e a confirmação "Qa@12345"
    E submeto o formulário
    Então devo ver mensagem de erro para o email

    Exemplos:
      | email               |
      | qa@                 |
      | qa@dominio          |
      | qa@@dominio.com     |
      | qa dominio.com      |

  Esquema do Cenário: Validar telefone somente numérico / tamanho
    Dado que acesso a página de cadastro
    Quando preencho o nome "Roberto" e o sobrenome "Boker"
    E preencho o email com um valor único
    E preencho o telefone "<telefone>"
    E seleciono o gênero "Male"
    E seleciono o país no campo Country "Denmark"
    E informo a senha "Qa@12345" e a confirmação "Qa@12345"
    E submeto o formulário
    Então devo ver mensagem de erro para o telefone

    Exemplos:
      | telefone     |
      | abcdef       |
      | 123          |
      | 12345678901234567890 |
