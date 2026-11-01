package br.edu.monitor.model;

/**
 * Registro de uma requisição HTTP monitorada.
 *
 * ESTRUTURA DE DADOS NÃO-HOMOGÊNEA: a classe reúne, em um único registro,
 * campos de tipos diferentes (texto, inteiro, inteiro longo, enumeração),
 * ao contrário de um vetor, que guarda apenas elementos do mesmo tipo.
 *
 * Cada objeto vira UMA LINHA do arquivo entrada.txt, com campos separados por ";":
 * id;servico;endpoint;metodo;codigoHttp;tempoRespostaMs;dataRequisicao;statusProblema
 */
public class Requisicao {

    /** Acima deste tempo (ms) a requisição é considerada lenta. */
    public static final long LIMITE_LENTA_MS = 1000;

    /** Separador de campos usado no arquivo de texto. */
    public static final String SEPARADOR = ";";

    private Long id;                    // identificador (gerado pelo sistema)
    private String servico;             // nome do serviço (ex.: pagamentos-api)
    private String endpoint;            // rota chamada (ex.: /v1/pedidos)
    private String metodo = "GET";      // método HTTP
    private Integer codigoHttp;         // código de resposta (200, 404, 500...)
    private Long tempoRespostaMs;       // tempo de resposta em milissegundos
    private String dataRequisicao;      // data e hora no formato yyyy-MM-ddTHH:mm
    private Situacao statusProblema = Situacao.ABERTO;

    /** Construtor vazio: exigido pelo Spring para preencher o formulário. */
    public Requisicao() {
    }

    public Requisicao(String servico, String endpoint, String metodo, Integer codigoHttp,
                      Long tempoRespostaMs, String dataRequisicao, Situacao statusProblema) {
        this.servico = servico;
        this.endpoint = endpoint;
        this.metodo = metodo;
        this.codigoHttp = codigoHttp;
        this.tempoRespostaMs = tempoRespostaMs;
        this.dataRequisicao = dataRequisicao;
        this.statusProblema = statusProblema;
    }

    // ------------------------------------------------------------------
    // Conversão de/para linha do arquivo de texto
    // ------------------------------------------------------------------

    /** Transforma o objeto em uma linha de texto para gravar no arquivo. */
    public String paraLinha() {
        return String.join(SEPARADOR,
                texto(id), texto(servico), texto(endpoint), texto(metodo),
                texto(codigoHttp), texto(tempoRespostaMs), texto(dataRequisicao),
                statusProblema == null ? Situacao.ABERTO.name() : statusProblema.name());
    }

    /**
     * Lê uma linha do arquivo e devolve o objeto correspondente.
     * Lança IllegalArgumentException se a linha estiver fora do leiaute.
     */
    public static Requisicao deLinha(String linha) {
        String[] campos = linha.split(SEPARADOR, -1);   // -1 mantém campos vazios no fim
        if (campos.length != 8) {
            throw new IllegalArgumentException("Linha com " + campos.length + " campos (esperado 8): " + linha);
        }
        try {
            Requisicao r = new Requisicao(campos[1], campos[2], campos[3],
                    Integer.valueOf(campos[4].trim()), Long.valueOf(campos[5].trim()),
                    campos[6], Situacao.valueOf(campos[7].trim()));
            r.setId(Long.valueOf(campos[0].trim()));
            return r;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inválido na linha: " + linha);
        }
    }

    /** Converte para texto e troca o separador, para o texto digitado não quebrar o leiaute. */
    private static String texto(Object valor) {
        return valor == null ? "" : valor.toString().replace(SEPARADOR, ",").replace("\n", " ").replace("\r", " ");
    }

    // ------------------------------------------------------------------
    // Regras usadas pela página
    // ------------------------------------------------------------------

    /** Erro: código HTTP 400 ou maior. */
    public boolean isErro() {
        return codigoHttp != null && codigoHttp >= 400;
    }

    /** Lenta: tempo de resposta acima do limite. */
    public boolean isLenta() {
        return tempoRespostaMs != null && tempoRespostaMs > LIMITE_LENTA_MS;
    }

    /** Classe CSS conforme a família do código: e2, e3, e4 ou e5. */
    public String getClasseHttp() {
        return codigoHttp == null ? "" : "e" + String.valueOf(codigoHttp).charAt(0);
    }

    /** Largura (0 a 100) da barra de tempo em relação ao maior tempo da lista. */
    public int larguraBarra(long maior) {
        if (tempoRespostaMs == null || maior <= 0) {
            return 0;
        }
        return (int) Math.min(100, tempoRespostaMs * 100 / maior);
    }

    // ------------------------------------------------------------------
    // Getters e setters (padrão JavaBean, exigido pelo Thymeleaf)
    // ------------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public Integer getCodigoHttp() { return codigoHttp; }
    public void setCodigoHttp(Integer codigoHttp) { this.codigoHttp = codigoHttp; }

    public Long getTempoRespostaMs() { return tempoRespostaMs; }
    public void setTempoRespostaMs(Long tempoRespostaMs) { this.tempoRespostaMs = tempoRespostaMs; }

    public String getDataRequisicao() { return dataRequisicao; }
    public void setDataRequisicao(String dataRequisicao) { this.dataRequisicao = dataRequisicao; }

    public Situacao getStatusProblema() { return statusProblema; }
    public void setStatusProblema(Situacao statusProblema) { this.statusProblema = statusProblema; }
}
