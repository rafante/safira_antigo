package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.base.Persistente

class Cheque extends Persistente {

    String numero
    ContaCorrente contaCorrente
    BigDecimal valor

    static constraints = {
        numero(blank: false, nullable: false)
        contaCorrente(blank: false, nullable: false)
        valor(blank: false, nullable: false)
    }

    @Override
    String toString() {
        return this.numero + " | " + this.contaCorrente?.toString()
    }
}
