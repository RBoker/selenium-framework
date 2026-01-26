# language: pt
@ui @register @refresh @p1
Funcionalidade: Cadastro - Refresh

  Cenário: Limpar formulário ao clicar em Refresh
    Dado que acesso a página de cadastro
    Quando preencho o nome "Roberto" e o sobrenome "Boker"
    E preencho o endereço "Rua Exemplo, 123"
    E preencho o email "qa@exemplo.com"
    E preencho o telefone "11990182878"
    E seleciono o gênero "Male"
    E seleciono o hobby "Movies"
    E seleciono o idioma "Portuguese"
    E seleciono a skill "Java"
    E seleciono o país no campo Country "Denmark"
    E seleciono a data de nascimento "1990" "January" "10"
    E informo a senha "Qa@12345" e a confirmação "Qa@12345"
    E clico em Refresh
    Então o formulário deve estar limpo
