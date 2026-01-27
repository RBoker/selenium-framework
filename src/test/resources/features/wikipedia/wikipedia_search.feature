# language: pt
@ui @wikipedia
Funcionalidade: Pesquisa na Wikipedia
  Para garantir que a busca está funcionando
  Como usuário
  Quero pesquisar um termo e validar que o resultado abriu

  Cenário: Pesquisar por "Selenium (software)" e abrir o artigo
    Dado que acesso a home da Wikipedia
    Quando eu pesquisar pelo termo "Selenium (software)"
    Então devo ver o título do artigo "Seleni (software)"
