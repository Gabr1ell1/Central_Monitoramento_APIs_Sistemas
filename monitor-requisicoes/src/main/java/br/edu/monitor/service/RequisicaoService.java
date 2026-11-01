package br.edu.monitor.service;

import br.edu.monitor.estruturas.ListaLigada;
import br.edu.monitor.estruturas.No;
import br.edu.monitor.model.Requisicao;
import br.edu.monitor.model.Situacao;
import br.edu.monitor.repository.ArquivoRequisicoes;
import br.edu.monitor.repository.LogDesempenho;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

/**
 * Regras do CRUD sobre o arquivo entrada.txt.
 *
 * Padrão de toda operação:
 *   1. lê o arquivo e monta uma LISTA LIGADA na memória;
 *   2. altera a lista (inserção em deque, busca, remoção);
 *   3. regrava o arquivo;
 *   4. registra o tempo total no log de desempenho.
 *
 * POLÍTICAS DE ACESSO:
 *   - INCLUSÃO: DEQUE, aceita novos dados no início ou no fim.
 *   - EXCLUSÃO: LIFO, remove sempre o registro inserido por último.
 */
@Service
public class RequisicaoService {

    private static final String ENTRADA = ArquivoRequisicoes.ARQUIVO_ENTRADA;
    private static final List<String> CABECALHO = List.of(ArquivoRequisicoes.LEIAUTE);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    // Dados usados para gerar registros simulados (índices alinhados: serviço <-> endpoint)
    private static final String[] SERVICOS = {"pagamentos-api", "catalogo-api", "auth-api", "estoque-api", "entregas-api", "notificacoes-api"};
    private static final String[] ENDPOINTS = {"/v1/pedidos", "/v1/produtos", "/v1/login", "/v1/itens", "/v1/entregas", "/v1/avisos"};
    private static final String[] METODOS = {"GET", "POST", "PUT", "DELETE"};
    private static final int[] CODIGOS = {200, 200, 200, 200, 201, 204, 301, 400, 401, 404, 500, 503};

    private final ArquivoRequisicoes arquivo;
    private final LogDesempenho log;
    private final Random sorteio = new Random();

    public RequisicaoService(ArquivoRequisicoes arquivo, LogDesempenho log) {
        this.arquivo = arquivo;
        this.log = log;
        if (!arquivo.existe(ENTRADA)) {
            criarArquivo();          // primeira execução: cria o arquivo com exemplos
        }
    }

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------

    /** CREATE (arquivo): cria (ou recria) o entrada.txt com o leiaute e 5 registros de exemplo. */
    public void criarArquivo() {
        long inicio = System.nanoTime();
        ListaLigada<Requisicao> exemplos = new ListaLigada<>();
        exemplos.inserirFim(exemplo(1, "pagamentos-api", "/v1/pedidos", "POST", 201, 240, "2026-10-30T09:12", Situacao.RESOLVIDO));
        exemplos.inserirFim(exemplo(2, "catalogo-api", "/v1/produtos", "GET", 200, 1850, "2026-10-30T09:40", Situacao.ABERTO));
        exemplos.inserirFim(exemplo(3, "auth-api", "/v1/login", "POST", 500, 620, "2026-10-30T10:05", Situacao.EM_ANALISE));
        exemplos.inserirFim(exemplo(4, "estoque-api", "/v1/itens/42", "GET", 404, 95, "2026-10-30T10:22", Situacao.ABERTO));
        exemplos.inserirFim(exemplo(5, "catalogo-api", "/v1/categorias", "GET", 200, 130, "2026-10-30T10:31", Situacao.RESOLVIDO));
        arquivo.gravar(ENTRADA, exemplos, CABECALHO);
        log.registrar("CREATE_ARQUIVO", LogDesempenho.ms(inicio), "entrada.txt recriado com 5 exemplos");
    }

    /** CREATE (registro): insere um novo registro no INÍCIO ou no FIM do deque e regrava o arquivo. */
    public Requisicao incluir(Requisicao nova, boolean no_inicio) {
        long inicio = System.nanoTime();
        ListaLigada<Requisicao> lista = arquivo.ler(ENTRADA);
        nova.setId(proximoId(lista));                  // o sistema gera o identificador
        if (no_inicio) {
            lista.inserirInicio(nova);
        } else {
            lista.inserirFim(nova);
        }
        arquivo.gravar(ENTRADA, lista, CABECALHO);
        log.registrar(no_inicio ? "CREATE_INICIO" : "CREATE_FIM", LogDesempenho.ms(inicio),
                "id=" + nova.getId() + "; total=" + lista.tamanho());
        return nova;
    }

    /** CREATE em volume: acrescenta registros simulados ao fim, para testar desempenho. */
    public void gerarSimulados(int quantidade) {
        long inicio = System.nanoTime();
        ListaLigada<Requisicao> lista = arquivo.ler(ENTRADA);
        long id = proximoId(lista);
        for (int i = 0; i < quantidade; i++) {
            lista.inserirFim(criarSimulado(id++));
        }
        arquivo.gravar(ENTRADA, lista, CABECALHO);
        log.registrar("GERAR_SIMULADOS", LogDesempenho.ms(inicio), "+" + quantidade + "; total=" + lista.tamanho());
    }

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------

    /** READ: lê o entrada.txt inteiro e devolve a lista ligada. */
    public ListaLigada<Requisicao> listar() {
        long inicio = System.nanoTime();
        ListaLigada<Requisicao> lista = arquivo.ler(ENTRADA);
        log.registrar("READ", LogDesempenho.ms(inicio), lista.tamanho() + " registros");
        return lista;
    }

    /** Quantidade de registros do arquivo (consulta auxiliar, sem registro no log). */
    public int quantidade() {
        return arquivo.ler(ENTRADA).tamanho();
    }

    /** Primeiras linhas do arquivo, exatamente como gravadas (exibidas na tela). */
    public List<String> linhasBrutas(int maximo) {
        return arquivo.linhasBrutas(ENTRADA, maximo);
    }

    public String caminhoDaPasta() {
        return arquivo.getPasta().toString();
    }

    // ------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------

    /** UPDATE: busca o registro na lista ligada pelo id, troca seus dados e regrava o arquivo. */
    public boolean atualizar(Requisicao alterada) {
        long inicio = System.nanoTime();
        ListaLigada<Requisicao> lista = arquivo.ler(ENTRADA);
        No<Requisicao> no = lista.buscarNo(r -> r.getId().equals(alterada.getId()));
        boolean achou = no != null;
        if (achou) {
            no.setDado(alterada);                      // troca o dado dentro do nó
            arquivo.gravar(ENTRADA, lista, CABECALHO);
        }
        log.registrar("UPDATE", LogDesempenho.ms(inicio), "id=" + alterada.getId() + (achou ? "" : " (não encontrado)"));
        return achou;
    }

    // ------------------------------------------------------------------
    // DELETE (LIFO)
    // ------------------------------------------------------------------

    /**
     * DELETE com política LIFO (Last In, First Out): remove o registro inserido por último.
     * O id cresce a cada inclusão, então o "último a entrar" é o de MAIOR id,
     * esteja ele no início ou no fim do deque. Remove um registro por execução.
     * Devolve o registro removido, ou null se o arquivo estiver vazio.
     */
    public Requisicao excluirUltimo() {
        long inicio = System.nanoTime();
        ListaLigada<Requisicao> lista = arquivo.ler(ENTRADA);
        No<Requisicao> alvo = null;
        for (No<Requisicao> atual = lista.getPrimeiro(); atual != null; atual = atual.getProximo()) {
            if (alvo == null || atual.getDado().getId() > alvo.getDado().getId()) {
                alvo = atual;                          // guarda o de maior id visto até agora
            }
        }
        Requisicao removida = null;
        if (alvo != null) {
            removida = lista.remover(alvo);
            arquivo.gravar(ENTRADA, lista, CABECALHO); // só os dados saem; o arquivo permanece
        }
        log.registrar("DELETE_LIFO", LogDesempenho.ms(inicio),
                removida == null ? "arquivo vazio" : "id=" + removida.getId());
        return removida;
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    /** Próximo id = maior id existente + 1 (assim os ids nunca se repetem). */
    private long proximoId(ListaLigada<Requisicao> lista) {
        long maior = 0;
        for (Requisicao r : lista) {
            if (r.getId() != null && r.getId() > maior) {
                maior = r.getId();
            }
        }
        return maior + 1;
    }

    private Requisicao exemplo(long id, String servico, String endpoint, String metodo, int codigo,
                               long tempo, String data, Situacao situacao) {
        Requisicao r = new Requisicao(servico, endpoint, metodo, codigo, tempo, data, situacao);
        r.setId(id);
        return r;
    }

    /** Gera uma requisição aleatória com tempos e códigos realistas (maioria 2xx, poucas lentas). */
    private Requisicao criarSimulado(long id) {
        int servico = sorteio.nextInt(SERVICOS.length);
        long tempo = 30 + sorteio.nextInt(450);
        if (sorteio.nextInt(10) == 0) {
            tempo += 800 + sorteio.nextInt(3000);        // ~10% ficam lentas
        }
        String data = LocalDateTime.of(2026, 10, 1, 0, 0)
                .plusMinutes(sorteio.nextInt(30 * 24 * 60)).format(FORMATO_DATA);
        Requisicao r = new Requisicao(SERVICOS[servico], ENDPOINTS[servico],
                METODOS[sorteio.nextInt(METODOS.length)], CODIGOS[sorteio.nextInt(CODIGOS.length)],
                tempo, data, Situacao.RESOLVIDO);
        r.setId(id);
        if (r.isErro() || r.isLenta()) {
            r.setStatusProblema(Situacao.values()[sorteio.nextInt(Situacao.values().length)]);
        }
        return r;
    }
}
