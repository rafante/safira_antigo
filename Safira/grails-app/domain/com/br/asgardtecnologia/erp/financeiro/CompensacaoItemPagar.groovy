package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.financeiro.base.CompensacaoItemFinanceiroBase

class CompensacaoItemPagar extends CompensacaoItemFinanceiroBase {
    static belongsTo = [itemContaPagar: ItemContaPagar, movimentoFinanceiro: MovimentoFinanceiro, cheque: Cheque]

    ItemContaPagar getParent() { return itemContaPagar }
    static mapping = {
        cheque cascade: 'all-delete-orphan'
    }
}