package br.edu.monitor.estruturas;

import java.text.Normalizer;
import java.util.Locale;

/** Utilitário de texto usado pelas tabelas hash. */
public final class Texto {

    private Texto() {
    }

    /**
     * Padroniza um texto para comparação: remove espaços das pontas,
     * tira acentos e usa minúsculas. Ex.: "  São Paulo " -> "sao paulo".
     */
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String decomposto = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD);
        String sem_acento = decomposto.replaceAll("\\p{M}+", "");   // remove as marcas de acento
        return sem_acento.toLowerCase(Locale.ROOT);
    }
}
