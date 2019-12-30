package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.base.Persistente

public class ItemPrazoPagamento extends Persistente {

    BigDecimal percentual
    Integer prazo

    static belongsTo = [prazoPagamento: PrazoPagamento]

    static constraints = {
        percentual(scale: 2)

        /**
         * N�mero de dias de prazo.
         */
        prazo()
    }

    String toString() {
        return prazo
    }
}