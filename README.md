# Monitor de Requisições

Aplicação web em **Java com Spring Boot**, usando **HTML e CSS** na página inicial e **nenhum JavaScript**. Ela registra requisições HTTP e ajuda a acompanhar erros e lentidão.

Este documento explica o que o projeto faz, como ele funciona, como rodar no NetBeans e por que cada decisão foi tomada.

---

## 1. O que o projeto faz

- Cadastra, consulta, atualiza e exclui registros de requisições (CRUD).
- Mostra serviço, endpoint, método, código HTTP, tempo de resposta e data.
- Destaca requisições com **erro** (HTTP 400 ou mais) e **lentas** (acima de 1000 ms).
- Pesquisa por serviço ou endpoint, filtra por tipo de problema e por situação, e ordena por vários critérios.
- Acompanha a situação de cada problema: Aberto, Em análise ou Resolvido.

Os dados ficam **em memória**. Ao reiniciar a aplicação, voltam os 5 registros de exemplo.

---

## 2. Como rodar no NetBeans

### Pré-requisitos
- **JDK 17 ou superior** (exigência do Spring Boot 3).
- **NetBeans 17 ou mais recente**, com Maven (já incluído).
- Internet na primeira execução, para baixar as dependências.

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
| `Port 8080 was already in use` | Mude `server.port` em `application.properties` ou feche o programa que usa a porta. |
| Dependências não baixam | Verifique a internet e use **Clean and Build** com o botão direito no projeto. |
| Erro `Template might not exist` ou página em branco | Confirme que `index.html` está em `src/main/resources/templates/` e não em `static/`. |
| Alterei o CSS e nada mudou | Atualize a página com Ctrl+F5 para ignorar o cache do navegador. |

---

## 3. Estrutura de pastas

```
monitor-requisicoes/
├── pom.xml
├── README.md
└── src/main/
    ├── java/br/edu/monitor/
    │   ├── MonitorApplication.java
    │   ├── controller/
    │   │   └── RequisicaoController.java
    │   ├── model/
    │   │   ├── Requisicao.java
    │   │   └── Situacao.java
    │   └── repository/
    │       └── RequisicaoRepositorio.java
    └── resources/
        ├── application.properties
        ├── templates/
        │   └── index.html
        └── static/
            └── css/
                └── style.css
```

| Arquivo | Responsabilidade |
|---|---|
| `pom.xml` | Versão do Spring Boot, do Java e dependências. |
| `MonitorApplication` | Ponto de entrada: inicia o servidor embutido. |
| `controller/RequisicaoController` | Recebe os pedidos do navegador, aplica filtros e ordenação, e escolhe a página. |
| `model/Requisicao` | O que é um registro e as regras de erro e lentidão. |
| `model/Situacao` | As três situações possíveis, com o texto exibido em cada uma. |
| `repository/RequisicaoRepositorio` | Guarda e busca os registros (em memória). |
| `templates/index.html` | A página HTML, com espaços que o servidor preenche. |
| `static/css/style.css` | Aparência da página. |
| `application.properties` | Configurações (porta e cache do template). |

---

## 4. Como funciona

Como não há JavaScript, **o servidor monta a página inteira**. O navegador só envia pedidos e mostra o HTML recebido.

```
Navegador                                Servidor (Spring Boot)
─────────                                ──────────────────────
GET /?q=api&tipo=erro   ───────────────▶ RequisicaoController.listar()
                                           ├─ pede os registros ao Repositorio
                                           ├─ filtra e ordena
                                           ├─ calcula o resumo
                                           └─ entrega tudo ao template index.html
                        ◀─────────────── HTML pronto (Thymeleaf preencheu a página)

POST /salvar            ───────────────▶ salva e responde "redirecione para /"
GET /                   ───────────────▶ página atualizada
```

### Rotas

| Ação | Rota | Como é acionada na página |
|---|---|---|
| Listar, filtrar e ordenar | `GET /` com `q`, `tipo`, `situacao`, `ordem` | Formulário de filtros (botão Aplicar filtros) |
| Abrir para edição | `GET /?editar={id}` | Link Editar na linha |
| Cadastrar ou atualizar | `POST /salvar` | Formulário principal; com `id` atualiza, sem `id` cadastra |
| Excluir | `POST /excluir/{id}` | Botão Excluir na linha |

### Regras de negócio
- **Erro:** `codigoHttp >= 400` (método `isErro()` em `Requisicao`).
- **Lenta:** `tempoRespostaMs > 1000` (constante `LIMITE_LENTA_MS` em `Requisicao`).
- **Barra de tempo:** proporcional ao maior tempo da lista, com mínimo de 2000 ms como referência.

---

## 5. Por que cada decisão foi tomada

### Sem JavaScript: Thymeleaf
O objetivo é trabalhar com HTML e CSS dentro de um projeto Java. O **Thymeleaf** é um motor de templates: o arquivo continua sendo HTML válido, e atributos `th:` marcam onde o Java injeta dados. Com isso:
- toda a lógica (filtro, ordenação, regras) fica em Java;
- o template abre no navegador mesmo sem o servidor, com os textos de exemplo;
- não há segundo código para manter.

O custo é a página recarregar a cada ação e a ausência de confirmação ao excluir, que exigiria JavaScript.

### Tecnologia e versões
- **Maven, e não Gradle.** O NetBeans abre projetos Maven nativamente.
- **Spring Boot 3.3.5 e Java 17.** O Spring Boot 3 exige Java 17 como mínimo, e a 3.3.5 é uma versão estável.
- **Duas dependências:** `starter-web` (servidor, rotas e formulários) e `starter-thymeleaf` (templates). O Thymeleaf não inclui o web, por isso as duas.
- **`exec.mainClass` no `pom.xml`.** Faz o botão Run Project do NetBeans achar a classe principal.

### Divisão de pastas e pacotes
- **Pacote `br.edu.monitor`.** Usa o domínio invertido, como manda a convenção Java.
- **Subpacotes `controller`, `model` e `repository`.** Separam as responsabilidades: quem recebe o pedido, quem representa o dado e quem o guarda. Essa é a estrutura que a maioria dos projetos Spring segue, e facilita trocar o armazenamento depois sem mexer no controller.
- **Classe principal na raiz do pacote.** O `@SpringBootApplication` varre o pacote dele e os subpacotes. Fora dali, o controller não seria encontrado.
- **`templates/` para o HTML.** É a pasta padrão do Thymeleaf. O HTML fica aqui porque precisa ser processado pelo servidor.
- **`static/` para o CSS.** É a pasta que o Spring serve sem processar. Não pode haver `index.html` dentro dela, senão confunde com o template.

### Modelo de dados
- **`Requisicao` é uma classe com getters e setters, e não um `record`.** O `th:field` do Thymeleaf e o preenchimento automático do formulário usam o padrão JavaBean (`getX`/`setX`), que records não seguem.
- **Regras dentro do modelo** (`isErro`, `isLenta`, `getClasseHttp`, `larguraBarra`). O template só pergunta `r.erro` e `r.lenta`, sem repetir a lógica do 400 e do 1000 em lugar nenhum.
- **`Integer` e `Long` em vez de `int` e `long`.** Um formulário novo mostra o campo vazio, e não 0.
- **`Situacao` como `enum`.** Impede valores inválidos e guarda o texto de cada situação em um só lugar.
- **Data como `String`.** O campo `datetime-local` envia texto como `2026-10-30T09:12`, e guardar assim evita erros de conversão. A ordenação por data funciona porque esse formato ordena igual ao texto.

### Repositório
- **`ConcurrentHashMap` e `AtomicLong`.** O servidor atende várias pessoas ao mesmo tempo, e essas classes não corrompem a lista nem o contador de ids.
- **Em memória, sem banco.** O foco do trabalho é HTML e CSS. Trocar por banco depois mexe só no repositório.
- **Dados de exemplo no construtor.** A página não abre vazia, e dá para ver filtros e destaques funcionando de imediato.
- **`salvar` serve para criar e atualizar.** Sem id, gera um; com id, substitui.

### Controller
- **Um único `GET /` faz listagem, filtro, ordenação e edição.** Os filtros vão na URL (`?q=...&tipo=...`). A página pode ser favoritada e atualizada com o filtro mantido.
- **Editar é `GET /?editar=id`.** O formulário principal é reaproveitado, trocando título e botão, sem criar uma segunda tela.
- **`POST` seguido de `redirect:/`.** Depois de salvar ou excluir, o servidor manda o navegador abrir `/` de novo. Se a pessoa apertar F5, a página só recarrega, sem reenviar o formulário e duplicar o registro.
- **Excluir é `POST`, e não link `GET`.** Uma ação que apaga dados não deve ser disparada por um simples link (por exemplo, por um robô de busca).
- **Filtros devolvidos ao template** (`q`, `tipo`, `situacao`, `ordem`). Os campos continuam preenchidos depois de filtrar.

### Visual (HTML e CSS)
- **Barra de tempo em cada linha.** Compara a lentidão de relance, sem ler os números. Fica âmbar quando passa do limite. A largura vem de `th:style`, sem JavaScript.
- **Faixa colorida na lateral da linha.** Vermelho para erro e âmbar para lentidão. O destaque não depende só da cor do texto.
- **Cores em variáveis CSS (`:root`).** Ficam definidas em um só lugar, e o tema escuro reaproveita a mesma estrutura via `prefers-color-scheme`.
- **Layout responsivo.** `grid` e `@media` reorganizam formulário e filtros em telas pequenas, e a tabela rola na horizontal.
- **Foco visível e `label` em cada campo.** Funciona por teclado e os campos têm rótulo associado.
- **Segurança contra XSS.** O `th:text` escapa o conteúdo automaticamente. Um serviço cadastrado como `<script>` aparece como texto, e não executa.

### Nomes e estilo
- **Variáveis locais em `snake_case`** (como `proximo_id`), seguindo a orientação do PDF da disciplina. Classes e métodos seguem a convenção Java (`RequisicaoController`, `listar`), e os campos do modelo seguem o padrão JavaBean (`codigoHttp`).

---

## 6. Limitações conhecidas

- **Sem persistência:** reiniciar apaga o que foi cadastrado.
- **Sem validação no servidor:** só os atributos HTML (`required`, `min`, `max`) validam os campos.
- **Sem confirmação ao excluir:** exigiria JavaScript.
- **Filtrar recarrega a página:** é o comportamento natural sem JavaScript.
- **Sem autenticação e sem testes automatizados.**
- **Não foi executado nem compilado em ambiente real** ao ser gerado. Se algo falhar ao rodar, comece pela tabela de problemas comuns.

---

## 7. Relação com o enunciado da disciplina

Este projeto cobre a parte de **interface e CRUD web**. O enunciado de Estrutura de Dados também pede itens que **não estão implementados**:

- arquivo `entrada.txt` com leiaute definido;
- inclusão em DEQUE (FIFO) e exclusão LIFO;
- listas ligadas nas operações de CRUD;
- quatro algoritmos de ordenação, com um arquivo de saída por algoritmo;
- pesquisa em arquivo ordenado e não ordenado;
- tabelas hash (países e fronteiras, cidades em ordem alfabética);
- log de desempenho com o tempo de cada operação;
- relatório teórico.

O caminho natural é trocar o `ConcurrentHashMap` do `RequisicaoRepositorio` por uma estrutura própria (lista ligada ou deque) que leia e grave `entrada.txt`. O controller e a página continuam como estão.

## 8. Próximos passos sugeridos

1. Persistir os dados em `entrada.txt` usando uma lista ligada.
2. Implementar as ordenações no pacote `service` e registrar os tempos em log.
3. Adicionar validação no servidor com `spring-boot-starter-validation`.
4. Trocar a memória por um banco de dados, se o projeto crescer.
