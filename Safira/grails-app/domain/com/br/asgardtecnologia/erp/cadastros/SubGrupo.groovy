package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.base.Persistente

class SubGrupo extends Persistente {

    String descricao

    static belongsTo = [grupo: Grupo]

    static defaultValueField = "descricao"
    static defaultFilterFields = "descricao"
    
    static constraints = {
        descricao(size: 0..60)
        grupo()
    }

    String toString() {
        return descricao
    }
}
