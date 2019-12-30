package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.base.Persistente

class Localizacao extends Persistente {

    String descricao

    static constraints = {
        descricao(size: 0..60)
    }

    String toString() {
        return descricao
    }
}
