# language: pt

@ui @agi
Funcionalidade: Pesquisa no Blog do Agi
  Como usuário do Blog do Agi
  Quero pesquisar por conteúdos
  Para encontrar artigos ou ser informado quando não existirem resultados

  Cenário: Pesquisar um termo existente
    Dado que acesso o Blog do Agi
    Quando realizo a pesquisa pelo termo "empréstimo"
    Então devo visualizar resultados relevantes da pesquisa

  Cenário: Pesquisar um termo inexistente
    Dado que acesso o Blog do Agi
    Quando realizo a pesquisa pelo termo "asdasd123_inexistente"
    Então devo visualizar a mensagem "Lamentamos, mas nada foi encontrado para sua pesquisa, tente novamente com outras palavras."
