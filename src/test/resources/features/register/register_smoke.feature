# language: pt
@ui @register @smoke
Funcionalidade: Cadastro - Smoke

  Como usuário
  Quero preencher o formulário de cadastro
  Para validar que o fluxo principal funciona

  Cenário: Submeter cadastro com dados válidos (happy path)
    Dado que acesso a página de cadastro
    Quando preencho o nome "Roberto" e o sobrenome "Boker"
    E preencho o endereço "Rua Exemplo, 123"
    E preencho o email com um valor único
    E preencho o telefone "19990182878"
    E seleciono o gênero "Male"
    E seleciono o hobby "Movies"
    E seleciono o idioma "Arabic"
    E seleciono a skill "Java"
    E seleciono o país no campo Country "Denmark"
    E seleciono a data de nascimento "1990" "January" "10"
    E informo a senha "Qa@12345" e a confirmação "Qa@12345"
    E submeto o formulário
    Então o cadastro deve ser enviado com sucesso
