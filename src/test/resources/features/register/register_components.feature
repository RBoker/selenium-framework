# language: pt
@ui @register @components @p1
Funcionalidade: Cadastro - Componentes do formulário

  Cenário: Selecionar múltiplos hobbies
    Dado que acesso a página de cadastro
    Quando seleciono os hobbies "Cricket,Movies,Hockey"
    Então devo ver os hobbies selecionados corretamente

  Cenário: Selecionar múltiplos idiomas
    Dado que acesso a página de cadastro
    Quando seleciono os idiomas "Portuguese,English"
    Então devo ver os idiomas selecionados corretamente

  Cenário: Selecionar data de nascimento completa
    Dado que acesso a página de cadastro
    Quando seleciono a data de nascimento "1990" "January" "10"
    Então devo ver a data de nascimento selecionada corretamente
