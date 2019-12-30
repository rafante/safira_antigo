package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.BaseService
import com.br.asgardtecnologia.erp.cadastros.FormaPagamento

class TransferenciaContaService extends BaseService {

    def save(TransferenciaConta transferenciaConta, Map params) {
        // Cria a movimentacao para a conta origem
        FormaPagamento transfBanc = FormaPagamento.findByDescricao("Transferência entre contas")
        MovimentoFinanceiro movimentoOrigem = new MovimentoFinanceiro(
                dataDocumento: transferenciaConta.data,
                debito_credito: TipoMovimentoFinanceiro.DEBITO,
                planoContas: transferenciaConta.historicoPadrao?.planoContas,
                contaCorrente: transferenciaConta.contaCorrenteOrigem,
                formaPagamento: transfBanc,
                valor: (transferenciaConta.valor * -1),
                descricao: "ORIGEM: " + transferenciaConta.descricao
        )

        // Cria a movimentacao para a conta destino
        MovimentoFinanceiro movimentoDestino = new MovimentoFinanceiro(
                dataDocumento: transferenciaConta.data,
                dataEmissao: transferenciaConta.data,
                debito_credito: TipoMovimentoFinanceiro.CREDITO,
                planoContas: transferenciaConta.historicoPadrao?.planoContas,
                contaCorrente: transferenciaConta.contaCorrenteDestino,
                formaPagamento: transfBanc,
                valor: transferenciaConta.valor,
                descricao: "DESTINO: " + transferenciaConta.descricao

        )

        movimentoOrigem.save(flush: true)
        movimentoDestino.save(flush: true)

        transferenciaConta.data = transferenciaConta.data ?: new Date()
        transferenciaConta.addToMovimentoFinanceiro(movimentoOrigem)
        transferenciaConta.addToMovimentoFinanceiro(movimentoDestino)

        return transferenciaConta.save(flush: true)
    }

    def estornar(TransferenciaConta transferenciaConta) {
        transferenciaConta.estornar()
        return this.save(transferenciaConta)
    }
}