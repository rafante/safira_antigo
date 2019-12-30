package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.base.Persistente

public class TabelaPreco extends Persistente {

    String descricao
    BigDecimal margem_sugerida
    BigDecimal margem_minima

    static defaultValueField = "descricao"

    static constraints = {
        /**
         * Tamanho 60
         */
        descricao()
    }

    String toString() {
        return descricao
    }

}