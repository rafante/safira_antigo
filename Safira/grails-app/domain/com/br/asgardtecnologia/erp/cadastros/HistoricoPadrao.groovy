package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.erp.financeiro.ContaCorrente
import com.br.asgardtecnologia.erp.financeiro.PlanoContas

class HistoricoPadrao extends Persistente {

    static displayFields = ["descricao"]

    String descricao
    PlanoContas planoContas
    CentroCusto centroCusto
    ContaCorrente contaCorrente

    static defaultValueField = "descricao"
    static defaultFilterFields = "descricao"
    static defaultAutoCompleteFields = "descricao"

    String toString() {
        return descricao
    }
}
