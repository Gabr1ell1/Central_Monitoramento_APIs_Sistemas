package br.edu.monitor.service;

import br.edu.monitor.model.Requisicao;
import br.edu.monitor.ordenacao.CampoOrdenacao;
import br.edu.monitor.pesquisa.Busca;
import br.edu.monitor.pesquisa.ResultadoPesquisa;
import br.edu.monitor.repository.ArquivoRequisicoes;
import br.edu.monitor.repository.LogDesempenho;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Pesquisa de dados:
 *   - entrada.txt (DESORDENADO)      -> busca LINEAR, qualquer campo;
 *   - arquivos de saída (ORDENADOS)  -> busca BINÁRIA, pelo campo em que foram ordenados.
 */
@Service
public class PesquisaService {

    private final ArquivoRequisicoes arquivo;
    private final LogDesempenho log;

    public PesquisaService(ArquivoRequisicoes arquivo, LogDesempenho log) {
        this.arquivo = arquivo;
        this.log = log;
    }

    /** Arquivos disponíveis para pesquisa: o de entrada e os de saída já gerados. */
    public List<String> arquivosDisponiveis() {
        List<String> nomes = new ArrayList<>();
        nomes.add(ArquivoRequisicoes.ARQUIVO_ENTRADA);
        nomes.addAll(arquivo.listarArquivosDeSaida());
        return nomes;
    }

    public ResultadoPesquisa pesquisar(String nome_arquivo, CampoOrdenacao campo_escolhido, String valor) {
        long inicio_total = System.nanoTime();

        // Segurança: só aceita arquivos da lista (evita acesso a outros caminhos)
        if (!arquivosDisponiveis().contains(nome_arquivo)) {
            return ResultadoPesquisa.aviso(nome_arquivo, "Arquivo inexistente. Gere a ordenação primeiro.");
        }
        if (valor == null || valor.isBlank()) {
            return ResultadoPesquisa.aviso(nome_arquivo, "Informe o valor a pesquisar.");
        }

        Requisicao[] vetor = arquivo.ler(nome_arquivo).paraLista().toArray(new Requisicao[0]);
        boolean eh_entrada = nome_arquivo.equals(ArquivoRequisicoes.ARQUIVO_ENTRADA);

        // Nos arquivos de saída o campo é imposto pela ordenação que os gerou
        CampoOrdenacao campo = campo_escolhido;
        if (!eh_entrada) {
            String ordenado_por = arquivo.ordenadoPor(nome_arquivo);
            if (ordenado_por == null) {
                return ResultadoPesquisa.aviso(nome_arquivo, "Arquivo sem informação de ordenação.");
            }
            campo = CampoOrdenacao.valueOf(ordenado_por);
        }

        long[] contador = new long[1];
        List<Integer> posicoes;
        long inicio_busca = System.nanoTime();
        try {
            posicoes = eh_entrada
                    ? Busca.linear(vetor, campo, valor, contador)
                    : Busca.binaria(vetor, campo, valor, contador);
        } catch (NumberFormatException e) {
            return ResultadoPesquisa.aviso(nome_arquivo, "O campo " + campo.getRotulo() + " exige um número.");
        }
        double tempo_busca = LogDesempenho.ms(inicio_busca);

        List<Requisicao> encontrados = new ArrayList<>();
        for (int posicao : posicoes) {
            encontrados.add(vetor[posicao]);
        }

        String metodo = eh_entrada ? "Linear" : "Binária";
        log.registrar("PESQUISA_" + metodo.toUpperCase().replace("Á", "A"), LogDesempenho.ms(inicio_total),
                nome_arquivo + "; " + campo.name() + "=" + valor + "; n=" + vetor.length
                        + "; comparacoes=" + contador[0] + "; achados=" + encontrados.size());

        String mensagem = eh_entrada ? null : "Arquivo ordenado por " + campo.getRotulo() + ": a busca usou esse campo.";
        return new ResultadoPesquisa(nome_arquivo, metodo, campo, valor, contador[0], tempo_busca, encontrados, mensagem);
    }
}
