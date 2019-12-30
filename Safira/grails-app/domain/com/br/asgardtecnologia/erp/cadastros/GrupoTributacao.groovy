package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.base.Persistente

class GrupoTributacao extends Persistente {

    String descricao

    static constraints = {
        descricao(size: 0..60)
    }

    String toString() {
        return descricao
    }
}
