package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.financeiro.base.ProrrogacaoItemFinanceiroBase

class ProrrogacaoItemPagar extends ProrrogacaoItemFinanceiroBase {
    static belongsTo = [itemContaPagar: ItemContaPagar]
}