package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.base.Persistente

class Banco extends Persistente{

    String descricao
    Integer codigo

    static defaultAutoCompleteFields = "codigo;descricao"

    static constraints = {
    }

    String toString(){
        descricao
    }
}
