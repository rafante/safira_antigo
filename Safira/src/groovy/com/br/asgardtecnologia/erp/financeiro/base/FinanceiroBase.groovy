package com.br.asgardtecnologia.erp.financeiro.base

import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.erp.base.Empresa
import com.br.asgardtecnologia.erp.cadastros.*
import com.br.asgardtecnologia.erp.config.Periodicidade
import com.br.asgardtecnologia.erp.financeiro.ContaCorrente
import com.br.asgardtecnologia.erp.financeiro.ItemContaPagar
import com.br.asgardtecnologia.erp.financeiro.ItemContaReceber
import com.br.asgardtecnologia.erp.financeiro.PlanoContas

abstract class FinanceiroBase extends Persistente {
    Empresa empresa
    String referencia
    ParceiroNegocios parceiroNegocios //Aqui está o parceiro que vc quer portanto itemContaReceber.contaReceber.parceiroNegocios eh deste cara que vc quer o nome, endereço etc entao vamos lá na classe ParceiroNegocios pra ver o que ele tem
    CentroCusto centroCusto
    PlanoContas planoContas
    ContaCorrente contaCorrente
    String descricao
    Date dataEmissao
    Date dataDocumento
    String documento
    HistoricoPadrao historicoPadrao
    TipoDocumento tipoDocumento
    BigDecimal valorTotal

    // Info Objeto criador do documento
    String controller
    String action
    Integer lancamento_id

    static defaultValueField = "descricao"
//    static defaultFilterFields = "referencia;descricao"
    static defaultAutoCompleteFields = "referencia;descricao"

    def geraParcelas(valorTotal, numParcelas, Date primeiroVencimento, Periodicidade periodicidade, dias) {
        if (valorTotal == 0) return
        if (!this.validate()) return

        def tipoFinanceiro = this.getTipoFinanceiro()
        def items = []

        if (!periodicidade) periodicidade = new Periodicidade()

        if (numParcelas > 0 && !items) {
            // Valor das parcelas
            def valorParcela = (valorTotal as BigDecimal).divide(numParcelas, 2, BigDecimal.ROUND_FLOOR)
            def resto = valorTotal - (valorParcela * numParcelas)

            // Restante das parcelas
            def datas = periodicidade?.getDatas(numParcelas.toInteger(), primeiroVencimento, dias ? dias?.toInteger() : 0)
            for (it in datas) {
                def item
                if (tipoFinanceiro == TipoFinanceiro.RECEBER) {
                    item = new ItemContaReceber(contaReceber: this, dataVencimento: it, valor: valorParcela)
                } else if (tipoFinanceiro == TipoFinanceiro.PAGAR) {
                    item = new ItemContaPagar(contaPagar: this, dataVencimento: it, valor: valorParcela)
                }

                items.add(item)
            }

            // Primeira parcela (inclui o resto)
            if (resto > 0) items[0].valor = items[0]?.valor + resto
        }

        def itensFieldName = this.getTipoFinanceiro() == TipoFinanceiro.PAGAR ? 'itensContaPagar' : 'itensContaReceber'
        this."${itensFieldName}" = items
    }

    def geraParcelas(BigDecimal valorTotal, Date vencimentoBase, PrazoPagamento prazoPagamento) {
        def tipoFinanceiro = this.getTipoFinanceiro()
        def itens = []

        // Restante das parcelas
        def parcelas = prazoPagamento?.geraParcelas(valorTotal, vencimentoBase)
        for (it in parcelas) {
            def item
            if (tipoFinanceiro == TipoFinanceiro.RECEBER) {
                item = new ItemContaReceber(contaReceber: this, dataVencimento: it.data, valor: it.valor)
            } else if (tipoFinanceiro == TipoFinanceiro.PAGAR) {
                item = new ItemContaPagar(contaPagar: this, dataVencimento: it.data, valor: it.valor)
            }

            itens.add(item)
        }

        return itens
    }

    String toString() {
        return this.id + "-" + this.descricao
    }

    def getItens() { return this."${this.getItensName()}" }
    

    String getItensName() {
        return this.getTipoFinanceiro() == TipoFinanceiro.PAGAR ? 'itensContaPagar' : 'itensContaReceber'
    }

    Boolean hasItens() { return (this.itens != null && this.itens.size() > 0) }

    TipoFinanceiro getTipoFinanceiro() {}

    BigDecimal getValorTotalParcelas() {
        BigDecimal ret = 0
        def teste = this.itens

        for (ItemFinanceiroBase item in this.itens) {
            ret += item.valor
        }

        return ret
    }

    BigDecimal getValorTotalCompensado() {
        BigDecimal ret = 0

        for (ItemFinanceiroBase item in this.itens) {
            ret += item.valorCompensado
        }

        return ret
    }

    Boolean canDelete() { return this.valorTotalCompensado == 0 }

    Boolean isValorInconsistente() {
        return this.valorTotal != this.valorTotalParcelas
    }

}

enum TipoFinanceiro {

    PAGAR("P"),
    RECEBER("R")

    final String id

    private TipoFinanceiro(String id) {
        this.id = id
    }

    String toString() {
        name()
    }

    String value() {
        this.id
    }
}
