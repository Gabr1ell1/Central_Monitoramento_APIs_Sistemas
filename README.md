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
## 5. Limitações conhecidas

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
