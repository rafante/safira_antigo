package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.base.Persistente

class Grupo extends Persistente {

    static displayFields = ["descricao"]

    String descricao

    static defaultValueField = "descricao"
    static defaultFilterFields = "descricao"
    
    static constraints = {
        descricao(size: 0..60)
    }

    String toString() {
        return descricao
    }
}
