package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.base.Persistente

class CentroCusto extends Persistente {

    String codigo
    String descricao

    static defaultValueField = "codigo;descricao"
    static defaultFilterFields = "codigo;descricao"

    static belongsTo = [centroCusto: CentroCusto]

    static hasMany = [filhos: CentroCusto]
    static transients = ['temFilhos']

    static constraints = {
        codigo()
        descricao(size: 0..60)
    }

    Boolean getTemFilhos(){
        return filhos?.size()
    }

    String toString() {

        CentroCusto almoxarifado = new CentroCusto()
        CentroCusto recepcao = new CentroCusto()


        return codigo + " - " + descricao
    }
}