package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.financeiro.base.ProrrogacaoItemFinanceiroBase

class ProrrogacaoItemReceber extends ProrrogacaoItemFinanceiroBase {
    static belongsTo = [itemContaReceber: ItemContaReceber]
}