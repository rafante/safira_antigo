package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.Empresa
import com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios
import org.jrimum.bopepo.BancosSuportados
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo

class CustomBoleto {

    ParceiroNegocios cliente
    Empresa cedente
    ContaCorrente contaCorrente
    String numeroDocumento
    String nossoNumero
    Date vencimento
    Date data
    BigDecimal valor
    TipoDeTitulo tipoDeTitulo
    BancosSuportados banco

    static constraints = {
    }
}
