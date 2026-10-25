## 💻 Central de Monitoramento de APIs e Sistemas

A Central de Monitoramento de APIs e Sistemas é uma aplicação web desenvolvida para organizar e acompanhar informações sobre requisições realizadas entre sistemas.
Imagine que um site tenha diferentes funcionalidades, como login, consulta de produtos e pagamentos. Cada uma delas depende de requisições para que as informações sejam processadas. Quando uma requisição falha ou demora muito para responder, pode prejudicar o funcionamento do sistema.

O projeto simula esse cenário, permitindo registrar e consultar informações sobre essas requisições em um único lugar.

# 📍 2. Problema que queremos resolver

Quando existem muitas requisições, pode ser difícil identificar quais apresentaram erros, quais demoraram para responder e quais serviços precisam de atenção.

Sem essas informações organizadas, encontrar possíveis problemas e acompanhar sua situação pode se tornar mais trabalhoso.

Por isso, nossa proposta é criar uma aplicação que centralize esses registros e facilite a visualização, a busca e a organização das informações.

# 🎯 3. Objetivo da aplicação

Desenvolver uma aplicação web simples que permita:

- Cadastrar, consultar, atualizar e excluir registros de requisições.

- Visualizar informações como serviço, endpoint, código HTTP, tempo de resposta e data da requisição.

- Identificar requisições com erros ou tempos de resposta elevados.

- Pesquisar e organizar os registros de acordo com critérios definidos.

- Acompanhar a situação dos problemas identificados.

Os dados utilizados poderão ser simulados, sem a necessidade de conectar a aplicação a sistemas reais.

📢 Importante: o objetivo inicial não é monitorar sistemas em tempo real, mas sim criar uma ferramenta para organizar e analisar registros de requisições.

# 4. Ferramentas e tecnologias

- Java

Linguagem de programação utilizada para desenvolver a lógica da aplicação, processar os dados e implementar as funcionalidades do sistema.

- Spring Boot

Framework utilizado para facilitar o desenvolvimento da aplicação web em Java, organizando as funcionalidades e permitindo a comunicação entre a interface e o sistema.

- HTML e CSS

Tecnologias utilizadas para construir a interface visual da aplicação.

- HTML: organiza os elementos da página, como tabelas, formulários e botões.

- CSS: define a aparência da interface, incluindo cores, espaçamentos e organização dos elementos.

A proposta é criar uma interface simples, funcional e fácil de utilizar.


- Apache NetBeans

Ambiente de desenvolvimento utilizado pelo grupo para escrever, organizar, executar e testar o código Java e a aplicação Spring Boot.

Navegador

   │  (HTTP)
   
   ▼
   
Controller ──► recebe a requisição, chama o service, escolhe a página

   │
   
   ▼
   
Service ────► regras de negócio (é erro? é lento? validações extras)

   │
   
   ▼
   
Repository ─► conversa com o banco (JPA)




# 5. Funcionamento geral

De maneira simplificada, a aplicação seguirá este fluxo:

- Os registros de requisições serão cadastrados ou gerados com dados simulados.

- As informações serão armazenadas no banco de dados H2.

- A aplicação permitirá consultar, pesquisar, filtrar e organizar os registros.

- O usuário poderá identificar erros e requisições que precisam de atenção.

- Os registros poderão ser atualizados conforme as funcionalidades implementadas.

# 6. Aprendizados esperados

Além de desenvolver uma aplicação web, o projeto permitirá praticar conceitos de programação Java, integração com banco de dados, desenvolvimento de interfaces e manipulação de informações.

Também será possível aplicar conceitos de Estruturas de Dados e Algoritmos, como pesquisa, ordenação e comparação de desempenho, contribuindo tanto para o aprendizado acadêmico quanto para a construção de um projeto de portfólio.

# 7. Escopo inicial

A primeira versão priorizará as funcionalidades essenciais: armazenamento, consulta, cadastro, atualização, exclusão e organização dos registros.

Funcionalidades adicionais poderão ser implementadas conforme o andamento do projeto e as necessidades da atividade acadêmica.
