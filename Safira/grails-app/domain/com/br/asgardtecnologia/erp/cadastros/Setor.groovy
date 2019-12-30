package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.base.Persistente

class Setor extends Persistente{

    String descricao

    static defaultAutoCompleteFields = "descricao"

    static constraints = {
        descricao size:  0..60
    }

    @Override
    String toString() {
        return id + " - " + descricao
    }
}
