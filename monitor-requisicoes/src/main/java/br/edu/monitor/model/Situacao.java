package br.edu.monitor.model;

/** Situação de acompanhamento de um problema. */
public enum Situacao {
    ABERTO("Aberto"),
    EM_ANALISE("Em análise"),
    RESOLVIDO("Resolvido");

    private final String rotulo;

    Situacao(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
