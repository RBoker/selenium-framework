# language: pt
@ui @register @password @p0
Funcionalidade: Cadastro - Senha

  Cenário: Bloquear submissão quando senhas não coincidem
    Dado que acesso a página de cadastro
    Quando preencho o nome "Roberto" e o sobrenome "Boker"
    E preencho o email com um valor único
    E preencho o telefone "19990182878"
    E seleciono o gênero "Male"
    E seleciono o país no campo Country "Denmark"
    E informo a senha "Qa@12345" e a confirmação "Qa@123456"
    E submeto o formulário
    Então devo ver mensagem de erro de confirmação de senha
