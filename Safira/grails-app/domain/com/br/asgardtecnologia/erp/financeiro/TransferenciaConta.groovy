package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.erp.cadastros.HistoricoPadrao

class TransferenciaConta extends Persistente {
    Date data
    String descricao
    HistoricoPadrao historicoPadrao
    ContaCorrente contaCorrenteDestino
    ContaCorrente contaCorrenteOrigem
    BigDecimal valor
    Boolean estornado

    static hasMany = [
            movimentoFinanceiro: MovimentoFinanceiro
    ]

    static constraints = {
        valor(scale: 2)
        contaCorrenteDestino(nullable: false, validator: {val, obj ->
            return val.id != obj.contaCorrenteOrigem.id
        })
        contaCorrenteOrigem(nullable: false, validator: { val, obj ->
            return val.id != obj.contaCorrenteDestino.id
        })
        movimentoFinanceiro(cascade: 'all-delete-orphan')
    }

    def estornar() {
        for (MovimentoFinanceiro mov in movimentoFinanceiro) {
            mov.estornar()
        }

        this.estornado = true
    }
}