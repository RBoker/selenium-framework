# language: pt
@ui @register @validations @p0
Funcionalidade: Cadastro - Validações de obrigatoriedade

  Cenário: Tentar submeter sem preencher campos obrigatórios
    Dado que acesso a página de cadastro
    Quando submeto o formulário
    Então devo ver erro de obrigatoriedade para "Full Name"
    E devo ver erro de obrigatoriedade para "Email"
    E devo ver erro de obrigatoriedade para "Phone"
    E devo ver erro de obrigatoriedade para "Gender"
    E devo ver erro de obrigatoriedade para "Country"

  Cenário: Submeter sem selecionar gênero
    Dado que acesso a página de cadastro
    Quando preencho o nome "Roberto" e o sobrenome "Boker"
    E preencho o email com um valor único
    E preencho o telefone "11990182878"
    E seleciono o país no campo Country "Denmark"
    E informo a senha "Qa@12345" e a confirmação "Qa@12345"
    E submeto o formulário
    Então devo ver erro de obrigatoriedade para "Gender"

  Cenário: Submeter sem selecionar país
    Dado que acesso a página de cadastro
    Quando preencho o nome "Roberto" e o sobrenome "Boker"
    E preencho o email com um valor único
    E preencho o telefone "11990182878"
    E seleciono o gênero "Male"
    E informo a senha "Qa@12345" e a confirmação "Qa@12345"
    E submeto o formulário
    Então devo ver erro de obrigatoriedade para "Country"
