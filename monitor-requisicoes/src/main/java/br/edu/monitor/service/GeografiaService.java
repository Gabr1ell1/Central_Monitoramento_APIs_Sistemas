package br.edu.monitor.service;

import br.edu.monitor.estruturas.TabelaHashCidades;
import br.edu.monitor.estruturas.TabelaHashCidades.CidadeHash;
import br.edu.monitor.estruturas.TabelaHashPaises;
import br.edu.monitor.estruturas.TabelaHashPaises.EntradaPais;
import br.edu.monitor.repository.LogDesempenho;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Módulo "Geografia": guarda as duas tabelas hash exigidas pelo enunciado
 * (países com fronteiras e cidades em ordem alfabética). É independente dos
 * registros de requisições. Os dados ficam em memória e voltam ao exemplo
 * ao reiniciar a aplicação.
 */
@Service
public class GeografiaService {

    private final TabelaHashPaises paises = new TabelaHashPaises(31);   // 31: número primo, distribui melhor
    private final TabelaHashCidades cidades = new TabelaHashCidades();
    private final LogDesempenho log;

    public GeografiaService(LogDesempenho log) {
        this.log = log;
        semear();
    }

    /** Carrega dados de exemplo (cidades fora de ordem, para mostrar que a tabela ordena sozinha). */
    private void semear() {
        paises.inserir("Brasil", List.of("Uruguai", "Argentina", "Paraguai", "Bolívia", "Peru", "Colômbia", "Venezuela", "Guiana", "Suriname", "Guiana Francesa"));
        paises.inserir("Argentina", List.of("Chile", "Bolívia", "Paraguai", "Brasil", "Uruguai"));
        paises.inserir("Uruguai", List.of("Brasil", "Argentina"));
        paises.inserir("Paraguai", List.of("Brasil", "Argentina", "Bolívia"));
        paises.inserir("Bolívia", List.of("Brasil", "Paraguai", "Argentina", "Chile", "Peru"));
        paises.inserir("Chile", List.of("Peru", "Bolívia", "Argentina"));
        paises.inserir("Peru", List.of("Equador", "Colômbia", "Brasil", "Bolívia", "Chile"));
        paises.inserir("Colômbia", List.of("Venezuela", "Brasil", "Peru", "Equador", "Panamá"));
        paises.inserir("Venezuela", List.of("Colômbia", "Brasil", "Guiana"));
        paises.inserir("Equador", List.of("Colômbia", "Peru"));
        for (String cidade : new String[]{"São Paulo", "Rio de Janeiro", "Curitiba", "Belo Horizonte", "Porto Alegre",
                "Salvador", "Recife", "Fortaleza", "Manaus", "Belém", "Brasília", "Goiânia", "Campinas", "Santos",
                "Florianópolis", "Buenos Aires", "Montevidéu", "Assunção", "Lima", "Santiago"}) {
            cidades.inserir(cidade);
        }
    }

    // ----- Países -----

    /** Cadastra um país; as fronteiras chegam como texto separado por vírgulas. */
    public void adicionarPais(String nome, String fronteiras_texto) {
        long inicio = System.nanoTime();
        List<String> fronteiras = new ArrayList<>();
        for (String parte : fronteiras_texto.split(",")) {
            if (!parte.isBlank()) {
                fronteiras.add(parte.trim());
            }
        }
        paises.inserir(nome, fronteiras);
        log.registrar("HASH_PAIS_INSERIR", LogDesempenho.ms(inicio), nome + "; balde=" + paises.hash(nome));
    }

    public EntradaPais buscarPais(String nome) {
        long inicio = System.nanoTime();
        EntradaPais achado = paises.buscar(nome);
        log.registrar("HASH_PAIS_BUSCAR", LogDesempenho.ms(inicio), nome + (achado == null ? " (não encontrado)" : ""));
        return achado;
    }

    public List<EntradaPais> listarPaises() { return paises.listar(); }
    public int getQuantidadePaises() { return paises.getQuantidade(); }
    public int getCapacidadePaises() { return paises.getCapacidade(); }

    // ----- Cidades -----

    public boolean adicionarCidade(String nome) {
        long inicio = System.nanoTime();
        boolean inseriu = cidades.inserir(nome);
        log.registrar("HASH_CIDADE_INSERIR", LogDesempenho.ms(inicio), nome + (inseriu ? "" : " (duplicada ou vazia)"));
        return inseriu;
    }

    /** Devolve o balde da cidade, ou -1 se não existir. */
    public int buscarCidade(String nome) {
        long inicio = System.nanoTime();
        int balde = cidades.buscar(nome);
        log.registrar("HASH_CIDADE_BUSCAR", LogDesempenho.ms(inicio), nome + (balde < 0 ? " (não encontrada)" : ""));
        return balde;
    }

    public List<CidadeHash> listarCidades() { return cidades.listarEmOrdem(); }
    public int getQuantidadeCidades() { return cidades.getQuantidade(); }
}
