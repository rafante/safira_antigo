package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.base.Persistente

public class FormaPagamento extends Persistente {

    String descricao
    Boolean controle_cheque

    static defaultValueField = "descricao"
    static defaultFilterFields = "descricao"

    static constraints = {
        /**
         * Tamanho 60.
         */
        descricao(size: 0..60, unique: true)

        controle_cheque()
    }

    String toString() {
        return descricao
    }

}