package com.br.asgardtecnologia.erp.ev

import com.br.asgardtecnologia.base.Persistente

class MovimentoExame extends Persistente{

    Date dataMovimento
    BigDecimal quantidade

    static belongsTo = [exame: Exame]

    static constraints = {

    }
}
