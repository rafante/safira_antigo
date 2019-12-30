package com.br.asgardtecnologia.erp.ev

import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.erp.cadastros.CentroCusto
import com.br.asgardtecnologia.erp.financeiro.ContaCorrente
import com.br.asgardtecnologia.erp.financeiro.PlanoContas

class ParametrosEV extends Persistente{

    String caixaEVEncoding = "ISO-8859-1"
    String examesEncoding = "UTF-8"
    String nfeEncoding = "UTF-8"
    static belongsTo = [planoContas: PlanoContas, contaCorrente: ContaCorrente, centroCusto: CentroCusto]

    static constraints = {
        planoContas(nullable: true)
        contaCorrente(nullable: true)
        centroCusto(nullable: true)
    }
}
