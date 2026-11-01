package br.edu.monitor.repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * RELATÓRIO DE DESEMPENHO: grava em logs/desempenho.log uma linha por operação:
 *
 *   data-hora;operacao;tempo_ms;detalhe
 *
 * A unidade de medida é o TEMPO TOTAL de execução da operação, em milissegundos,
 * medido com System.nanoTime() (relógio de alta precisão).
 */
@Component
public class LogDesempenho {

    /** Uma linha lida do log (usada pela tela de desempenho). */
    public static class Entrada {
        private final String dataHora;
        private final String operacao;
        private final String tempoMs;
        private final String detalhe;

        public Entrada(String dataHora, String operacao, String tempoMs, String detalhe) {
            this.dataHora = dataHora;
            this.operacao = operacao;
            this.tempoMs = tempoMs;
            this.detalhe = detalhe;
        }

        public String getDataHora() { return dataHora; }
        public String getOperacao() { return operacao; }
        public String getTempoMs() { return tempoMs; }
        public String getDetalhe() { return detalhe; }
    }

    private final Path arquivo;

    public LogDesempenho(@Value("${monitor.pasta-logs:logs}") String pasta_configurada) {
        Path pasta = Paths.get(pasta_configurada).toAbsolutePath().normalize();
        try {
            Files.createDirectories(pasta);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível criar a pasta de logs: " + pasta, e);
        }
        this.arquivo = pasta.resolve("desempenho.log");
    }

    public Path getArquivo() {
        return arquivo;
    }

    /** Converte o instante inicial (nanosegundos) no tempo decorrido, em milissegundos. */
    public static double ms(long inicio_nanos) {
        return (System.nanoTime() - inicio_nanos) / 1_000_000.0;
    }

    /** Acrescenta uma linha ao final do log. synchronized evita linhas misturadas entre requisições. */
    public synchronized void registrar(String operacao, double tempo_ms, String detalhe) {
        String limpo = detalhe == null ? "" : detalhe.replace(";", ",").replace("\n", " ");
        String linha = String.join(";", LocalDateTime.now().withNano(0).toString(), operacao,
                String.format(Locale.ROOT, "%.3f", tempo_ms), limpo);
        try {
            Files.writeString(arquivo, linha + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Falha ao gravar o log de desempenho: " + e.getMessage());
        }
    }

    /** Devolve as últimas linhas do log, da mais recente para a mais antiga. */
    public synchronized List<Entrada> ler(int maximo) {
        List<Entrada> entradas = new ArrayList<>();
        if (!Files.exists(arquivo)) {
            return entradas;
        }
        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
            for (int i = linhas.size() - 1; i >= 0 && entradas.size() < maximo; i--) {
                String[] partes = linhas.get(i).split(";", 4);
                if (partes.length == 4) {
                    entradas.add(new Entrada(partes[0], partes[1], partes[2], partes[3]));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler o log", e);
        }
        return entradas;
    }
}
