package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.base.Persistente

public class NCM extends Persistente {

    String codigo
    String descricao

    static constraints = {
        codigo()
        descricao()
    }

    String toString() {
        return codigo + " - " + descricao
    }

}