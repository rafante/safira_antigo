package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.financeiro.base.ItemFinanceiroBase
import org.hibernate.collection.PersistentSet

class ItemContaPagar extends ItemFinanceiroBase {
    static tipoMovimentoFinanceiro = TipoMovimentoFinanceiro.DEBITO

    static defaultFilterFields = "contaPagar.parceiroNegocios;dataVencimento;contaPagar.dataEmissao;compensadoCompletamente"
    static defaultFilterFieldsExtra = [[name: 'agruparParceiro', type: Boolean], [name: 'agruparData', type: Boolean]]

    static belongsTo = [contaPagar: ContaPagar]

    static hasMany = [compensacaoItemPagar: CompensacaoItemPagar, prorrogacaoItemPagar: ProrrogacaoItemPagar]

    static constraints = {
        compensacaoItemPagar(cascade: "all-delete-orphan")
        prorrogacaoItemPagar(cascade: "all-delete-orphan")
    }

    ContaPagar getParent() { return contaPagar }

    PersistentSet getCompensacoes() { return compensacaoItemPagar }

    PersistentSet getProrrogacoes() { return prorrogacaoItemPagar }
}