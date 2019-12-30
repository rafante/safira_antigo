package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.base.Persistente

class ContaBancaria extends Persistente {

    Banco banco
    String agencia
    String conta

    static constraints = {
        agencia(size: 4..10)
    }

    String toString() {
        return this.banco.toString() + "-" + this.agencia + "/" + this.conta
    }
}
