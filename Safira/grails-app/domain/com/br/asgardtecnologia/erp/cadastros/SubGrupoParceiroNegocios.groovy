package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.base.Persistente

class SubGrupoParceiroNegocios extends Persistente {

    String descricao

    static belongsTo = [grupo_parceiro: GrupoParceiroNegocios]

    static defaultValueField = "descricao"
    static defaultFilterFields = "descricao"
    
    static constraints = {
        descricao(size: 0..60)
        grupo_parceiro()
    }

    String toString() {
        return descricao
    }
}
