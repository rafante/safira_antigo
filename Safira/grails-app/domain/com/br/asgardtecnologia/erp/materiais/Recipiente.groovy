package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.base.Persistente

class Recipiente extends Persistente {

    Long codEv
    String descricao

    static defaultAutoCompleteFields = "codEv;descricao"

    static constraints = {
        descricao size: 0..60
        codEv unique: true
    }

    @Override
    String toString() {
        return id + " - " + descricao
    }
}
