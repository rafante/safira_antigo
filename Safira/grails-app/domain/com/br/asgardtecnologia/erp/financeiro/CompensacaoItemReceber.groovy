package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.financeiro.base.CompensacaoItemFinanceiroBase

class CompensacaoItemReceber extends CompensacaoItemFinanceiroBase {
    static belongsTo = [itemContaReceber: ItemContaReceber, movimentoFinanceiro: MovimentoFinanceiro, cheque: Cheque]

    ItemContaReceber getParent() { return itemContaReceber }
}