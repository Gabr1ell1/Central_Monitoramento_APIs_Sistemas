# Monitor de Requisições

Aplicação web simples, feita com Spring Boot, para registrar requisições HTTP e acompanhar problemas de erro e lentidão.

Este documento explica **o que o projeto faz**, **como ele funciona por dentro**, **como rodar no NetBeans** e **por que cada decisão foi tomada**.

---

## 1. O que o projeto faz

- Cadastra, consulta, atualiza e exclui registros de requisições (CRUD).
- Mostra serviço, endpoint, método, código HTTP, tempo de resposta e data de cada requisição.
- Destaca requisições com **erro** (HTTP 400 ou mais) e **lentas** (acima de 1000 ms).
- Permite pesquisar e organizar os registros por critérios definidos.
- Permite acompanhar a situação de cada problema: Aberto, Em análise ou Resolvido.

Os dados ficam **em memória**: ao reiniciar a aplicação, a lista volta vazia.

---

## 2. Como rodar no NetBeans

### Pré-requisitos
- **JDK 17 ou superior** (exigência do Spring Boot 3).
- **NetBeans 17 ou mais recente**, com suporte a Maven (já vem incluído).
- Internet na primeira execução, para o Maven baixar as dependências.

### Passo a passo
1. Extraia o zip em uma pasta.
2. No NetBeans: **File → Open Project** e selecione a pasta `monitor-requisicoes`.
3. Aguarde o NetBeans baixar as dependências (barra de progresso no canto inferior direito).
4. Clique com o botão direito em `MonitorApplication.java` e escolha **Run File**. O botão **Run Project** também funciona.
5. Quando o console mostrar `Started MonitorApplication`, abra **http://localhost:8080**.

### Problemas comuns
| Sintoma | O que fazer |
|---|---|
| Erro de versão do Java | Configure o JDK 17+ em **Tools → Java Platforms** e em **Project Properties → Build → Compile**. |
| `Port 8080 was already in use` | Mude `server.port` em `src/main/resources/application.properties` ou feche o programa que usa a porta. |
| Dependências não baixam | Verifique a internet e use **Clean and Build** com o botão direito no projeto. |
| Página abre com aviso amarelo "API indisponível" | O backend não respondeu e a página entrou em modo demonstração. Confira se a aplicação está rodando e se a URL é `localhost:8080`. |

---

## 3. Estrutura de pastas

```
monitor-requisicoes/
├── pom.xml
├── README.md
└── src/main/
    ├── java/br/edu/monitor/
    │   ├── MonitorApplication.java
    │   ├── Requisicao.java
    │   └── RequisicaoController.java
    └── resources/
        ├── application.properties
        └── static/
            ├── index.html
            ├── css/style.css
            └── js/app.js
```

| Arquivo | Responsabilidade |
|---|---|
| `pom.xml` | Declara a versão do Spring Boot, do Java e as dependências. |
| `MonitorApplication.java` | Ponto de entrada: inicia o servidor embutido. |
| `Requisicao.java` | Modelo de dados: o que é um registro de requisição. |
| `RequisicaoController.java` | API REST: recebe pedidos do navegador e opera sobre a lista. |
| `application.properties` | Configurações da aplicação (porta). |
| `static/index.html` | Estrutura da página. |
| `static/css/style.css` | Aparência da página. |
| `static/js/app.js` | Comportamento: chama a API, filtra, ordena e desenha a tabela. |

---

## 4. Como funciona por dentro

```
Navegador                                  Servidor (Spring Boot)
─────────                                  ──────────────────────
GET /                 ───────────────────▶ entrega static/index.html
GET /css/style.css    ───────────────────▶ entrega o CSS
GET /js/app.js        ───────────────────▶ entrega o JavaScript

app.js: fetch GET /api/requisicoes ──────▶ RequisicaoController.listar()
                      ◀─────────────────── JSON com todos os registros

app.js filtra, ordena e desenha a tabela no navegador
```

1. O Spring Boot serve automaticamente tudo o que estiver em `src/main/resources/static/`. Por isso `index.html` vira a página inicial, sem nenhum controller para ela.
2. Ao abrir, o `app.js` pede a lista em `/api/requisicoes`, guarda em uma variável e desenha a tabela.
3. Busca, filtros e ordenação rodam **no navegador**, sobre a lista já carregada.
4. Cadastrar, editar e excluir enviam `POST`, `PUT` e `DELETE` ao servidor. Depois de cada operação, a lista é recarregada.

### API REST

| Operação | Método e rota | Corpo | Resposta |
|---|---|---|---|
| Listar | `GET /api/requisicoes` | – | 200 com lista |
| Buscar um | `GET /api/requisicoes/{id}` | – | 200 ou 404 |
| Criar | `POST /api/requisicoes` | JSON do registro | 200 com o registro e o id gerado |
| Atualizar | `PUT /api/requisicoes/{id}` | JSON do registro | 200 ou 404 |
| Excluir | `DELETE /api/requisicoes/{id}` | – | 204 ou 404 |

Exemplo de registro:
```json
{
  "servico": "pagamentos-api",
  "endpoint": "/v1/pedidos",
  "metodo": "POST",
  "codigoHttp": 201,
  "tempoRespostaMs": 240,
  "dataRequisicao": "2026-10-30T09:12",
  "statusProblema": "ABERTO"
}
```

### Regras de negócio no front-end
- **Erro:** `codigoHttp >= 400`.
- **Lenta:** `tempoRespostaMs > 1000`. O limite é a constante `LIMITE_LENTA_MS` no início do `app.js`.
- **Situações possíveis:** `ABERTO`, `EM_ANALISE`, `RESOLVIDO`.

---

## 5. Por que cada decisão foi tomada

### Tecnologia e versões
- **Maven, e não Gradle.** O NetBeans abre projetos Maven nativamente, sem plugin extra. Também é o padrão mais comum em material de aula de Java.
- **Spring Boot 3.3.5 e Java 17.** O Spring Boot 3 exige Java 17 como mínimo. A versão 3.3.5 é estável e já tem tempo de uso, o que reduz o risco de bugs novos.
- **Só a dependência `spring-boot-starter-web`.** Ela traz o servidor embutido (Tomcat), o Spring MVC e o Jackson (conversão para JSON). Nada além disso é necessário para o escopo, e menos dependências significam download mais rápido e menos pontos de falha.
- **`exec.mainClass` no `pom.xml`.** Sem essa propriedade, o botão **Run Project** do NetBeans pode não saber qual classe executar em um projeto Spring Boot.

### Divisão de pastas
- **Pacote `br.edu.monitor`.** Segue a convenção Java de usar domínio invertido. Todas as classes ficam no mesmo pacote porque o projeto é pequeno. Separar em `controller`, `model` e `service` seria excesso para três classes.
- **A classe principal fica na raiz do pacote.** O `@SpringBootApplication` varre o pacote dele e os subpacotes. Se a classe principal ficasse em outro lugar, o controller não seria encontrado.
- **Front-end dentro de `static/`.** É a pasta que o Spring Boot serve sem configuração. Assim a página inicial é personalizada sem tocar no backend, que era o objetivo do enunciado.
- **HTML, CSS e JS em arquivos separados.** Cada arquivo tem uma função: estrutura, aparência e comportamento. Isso facilita achar e alterar cada parte, e o navegador guarda CSS e JS em cache.
- **Pastas `css/` e `js/`.** É a organização usual de projetos web e deixa claro o tipo de cada arquivo.

### Backend
- **`Requisicao` como `record`.** Um record é imutável e dispensa getters, setters e construtores escritos à mão. O Jackson (versão do Spring Boot 3) converte records para JSON e de volta sem configuração.
- **Método `comId`.** Como o record é imutável, não dá para mudar o id depois de criado. Esse método devolve uma cópia com o id definido pelo servidor.
- **Id gerado pelo servidor.** O cliente não escolhe o id, o que evita duplicidade e conflito.
- **`ConcurrentHashMap` e `AtomicLong`.** O servidor atende várias requisições ao mesmo tempo. Essas classes são seguras para uso simultâneo, então a lista e o contador de ids não ficam corrompidos.
- **Dados em memória, sem banco.** O pedido era uma aplicação simples e o foco é HTML e CSS. Um banco (JPA, H2 ou MySQL) adicionaria configuração e dependências sem ajudar nesse objetivo. A troca futura é simples: só o controller muda.
- **Data como `String`.** O campo `datetime-local` do HTML envia texto como `2026-10-30T09:12`. Manter `String` evita erros de conversão de formato. Em contrapartida, o servidor não valida a data.
- **Códigos HTTP corretos.** `404` quando o id não existe e `204` ao excluir, como pede a convenção REST.
- **Rota `/api/...`.** O prefixo separa as rotas de dados das rotas de páginas.

### Front-end
- **JavaScript puro, sem React, Bootstrap ou outra biblioteca.** O enunciado fala em HTML e CSS, e o projeto é pequeno. Sem biblioteca não há download extra, não há ferramenta de build e o código pode ser lido do começo ao fim.
- **Filtros e ordenação no navegador.** Para poucos registros, é mais rápido e mais simples do que criar parâmetros de consulta na API. Se a lista chegar a milhares de registros, o certo é mover isso para o servidor.
- **Barra de tempo em cada linha.** Compara a lentidão de relance, sem ler os números. Ela fica âmbar quando passa do limite.
- **Faixa colorida na lateral da linha.** Vermelho para erro e âmbar para lentidão. O destaque não depende só da cor do texto, o que ajuda quem tem dificuldade de distinguir cores.
- **Cores com variáveis CSS (`:root`).** As cores ficam definidas em um só lugar. O tema escuro usa a mesma estrutura, trocando só os valores via `prefers-color-scheme`.
- **Layout responsivo.** Com `grid` e regras `@media`, o formulário e os filtros se reorganizam em telas menores, e a tabela rola na horizontal em vez de quebrar a página.
- **Foco visível e `label` em cada campo.** O navegador por teclado funciona e os campos têm rótulo associado, o que melhora a acessibilidade.
- **Função `esc()` antes de inserir texto na tabela.** Os dados são montados como HTML. Sem escapar, um serviço cadastrado como `<script>...` seria executado no navegador (ataque XSS). A função converte o texto em conteúdo inofensivo.
- **Modo demonstração.** Se a API não responde, a página usa cinco registros de exemplo e avisa. Isso permite ver e testar a interface mesmo abrindo o HTML sem o servidor.
- **Botão de edição reaproveita o formulário.** Em vez de criar uma segunda tela, o mesmo formulário troca de título e de botão. Há menos código e a pessoa não perde o contexto.
- **`confirm()` antes de excluir.** Excluir não tem desfazer, então uma confirmação evita perda por clique acidental.

### Nomes e estilo de código
- **Variáveis locais em `snake_case`** (`proximo_id`, `tempo_resposta`, `modo_demo`), seguindo a orientação do PDF da disciplina. Os nomes de classes e métodos em Java seguem a convenção da linguagem (`RequisicaoController`, `listar`).
- **Campos do JSON em `camelCase`** (`codigoHttp`, `tempoRespostaMs`). É o padrão de APIs JSON e os nomes coincidem com os campos do record, o que permite a conversão automática.
- **Comentários** apenas onde explicam o motivo ou o contrato (cabeçalho do `app.js`, aviso de dados em memória no controller).

---

## 6. Limitações conhecidas

- **Sem persistência:** reiniciar a aplicação apaga os dados.
- **Sem validação no servidor:** o backend aceita qualquer valor. A validação existe só nos campos do formulário.
- **Sem autenticação.**
- **Sem testes automatizados.**
- **Não foi executado nem testado** em ambiente real ao ser gerado. Se algo falhar ao rodar, comece pela seção de problemas comuns.

---

## 7. Relação com o enunciado da disciplina

Este projeto cobre a parte de **interface e CRUD via web**. O enunciado de Estrutura de Dados também pede itens que **não estão implementados aqui**:

- arquivo `entrada.txt` com leiaute definido;
- inclusão em DEQUE (FIFO) e exclusão LIFO;
- listas ligadas nas operações de CRUD;
- quatro algoritmos de ordenação com arquivo de saída por algoritmo;
- pesquisa em arquivo ordenado e não ordenado;
- tabelas hash (países e fronteiras, cidades em ordem alfabética);
- log de desempenho com o tempo de cada operação;
- relatório teórico.

Esses itens pertencem ao backend. O caminho natural é trocar o `ConcurrentHashMap` do controller por uma estrutura própria (lista ligada ou deque) que leia e grave `entrada.txt`, sem mudar a interface.

## 8. Próximos passos sugeridos

1. Persistir os dados em arquivo (`entrada.txt`) usando uma lista ligada.
2. Adicionar validação no servidor com `spring-boot-starter-validation`.
3. Implementar as ordenações e gerar o log de tempos.
4. Trocar a memória por um banco de dados, se o projeto crescer.
