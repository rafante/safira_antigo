package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.base.Persistente

class TipoConta extends Persistente {

    String descricao

    static constraints = {
    }

    @Override
    String toString() {
        return descricao
    }
}