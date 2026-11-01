package br.edu.monitor.repository;

import br.edu.monitor.estruturas.ListaLigada;
import br.edu.monitor.model.Requisicao;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Camada de acesso aos ARQUIVOS DE TEXTO (entrada.txt e arquivos de saída).
 *
 * Formato: uma requisição por linha, campos separados por ";".
 * Linhas iniciadas por "#" são comentários (leiaute, campo de ordenação etc.)
 * e são ignoradas na leitura dos dados.
 */
@Repository
public class ArquivoRequisicoes {

    public static final String ARQUIVO_ENTRADA = "entrada.txt";
    public static final String LEIAUTE =
            "LEIAUTE: id;servico;endpoint;metodo;codigoHttp;tempoRespostaMs;dataRequisicao;statusProblema";

    private static final String PREFIXO_ORDENADO_POR = "# ordenado-por=";

    private final Path pasta;

    /** A pasta vem de application.properties (monitor.pasta-dados); o padrão é "dados". */
    public ArquivoRequisicoes(@Value("${monitor.pasta-dados:dados}") String pasta_configurada) {
        this.pasta = Paths.get(pasta_configurada).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.pasta);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível criar a pasta de dados: " + this.pasta, e);
        }
    }

    public Path getPasta() {
        return pasta;
    }

    /** Monta o caminho do arquivo e impede nomes que escapem da pasta de dados (ex.: "../x"). */
    private Path caminho(String nome) {
        Path alvo = pasta.resolve(nome).normalize();
        if (!alvo.getParent().equals(pasta)) {
            throw new IllegalArgumentException("Nome de arquivo inválido: " + nome);
        }
        return alvo;
    }

    public boolean existe(String nome) {
        return Files.exists(caminho(nome));
    }

    /**
     * READ: lê o arquivo e monta uma LISTA LIGADA com um objeto por linha.
     * Linhas em branco, comentários e linhas fora do leiaute são ignorados.
     */
    public ListaLigada<Requisicao> ler(String nome) {
        ListaLigada<Requisicao> lista = new ListaLigada<>();
        Path arquivo = caminho(nome);
        if (!Files.exists(arquivo)) {
            return lista;
        }
        try (BufferedReader leitor = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (linha.isBlank() || linha.startsWith("#")) {
                    continue;
                }
                try {
                    lista.inserirFim(Requisicao.deLinha(linha));
                } catch (IllegalArgumentException e) {
                    // linha inválida: ignorada
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler " + arquivo, e);
        }
        return lista;
    }

    /** Grava (substituindo o conteúdo) os registros no arquivo, precedidos dos comentários. */
    public void gravar(String nome, Iterable<Requisicao> registros, List<String> comentarios) {
        try (BufferedWriter escritor = Files.newBufferedWriter(caminho(nome), StandardCharsets.UTF_8)) {
            for (String comentario : comentarios) {
                escritor.write("# " + comentario);
                escritor.newLine();
            }
            for (Requisicao r : registros) {
                escritor.write(r.paraLinha());
                escritor.newLine();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao gravar " + nome, e);
        }
    }

    /** Lê o cabeçalho do arquivo de saída e devolve o campo pelo qual ele foi ordenado (ou null). */
    public String ordenadoPor(String nome) {
        for (String linha : linhasBrutas(nome, 10)) {
            if (linha.startsWith(PREFIXO_ORDENADO_POR)) {
                return linha.substring(PREFIXO_ORDENADO_POR.length()).trim();
            }
        }
        return null;
    }

    /** Devolve as primeiras linhas do arquivo, exatamente como estão (para mostrar na tela). */
    public List<String> linhasBrutas(String nome, int maximo) {
        List<String> linhas = new ArrayList<>();
        Path arquivo = caminho(nome);
        if (!Files.exists(arquivo)) {
            return linhas;
        }
        try (BufferedReader leitor = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            while (linhas.size() < maximo && (linha = leitor.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler " + arquivo, e);
        }
        return linhas;
    }

    /** Lista os arquivos .txt de saída (todos menos entrada.txt), em ordem alfabética. */
    public List<String> listarArquivosDeSaida() {
        List<String> nomes = new ArrayList<>();
        try (DirectoryStream<Path> fluxo = Files.newDirectoryStream(pasta, "*.txt")) {
            for (Path p : fluxo) {
                String nome = p.getFileName().toString();
                if (!nome.equals(ARQUIVO_ENTRADA)) {
                    nomes.add(nome);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao listar " + pasta, e);
        }
        Collections.sort(nomes);
        return nomes;
    }
}
