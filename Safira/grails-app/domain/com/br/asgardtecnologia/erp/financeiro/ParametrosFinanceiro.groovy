package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.erp.base.Empresa

class ParametrosFinanceiro extends Persistente {

    Empresa empresa
    String nossoNumero
    String agencia
    String conta
    ContaCorrente contaCorrente

    static transients = ['agenciaSemDigito', 'contaSemDigito', 'agenciaDigito', 'contaDigito', 'nossoNumeroSemDigito', 'nossoNumeroDigito']
    static constraints = {
        empresa(nullable: false)
        agencia(maxSize: 3)
        conta(minSize: 3, maxSize: 8)
        nossoNumero(minSize: 3, maxSize: 10)
    }

    String getAgenciaSemDigito() {
        return agencia ? agencia.subSequence(0, 4) : ""
    }

    String getAgenciaDigito() {
        return agencia ? agencia.charAt(agencia.length() - 1) : ""
    }

    String getContaSemDigito() {
        return conta ? conta.subSequence(0, conta.length() - 2) : ""
    }

    String getContaDigito() {
        return conta ? conta.charAt(conta.length() - 1) : ""
    }

    String getNossoNumeroSemDigito() {
        return nossoNumero ? nossoNumero.subSequence(0, nossoNumero.length() - 2) : ""
    }

    String getNossoNumeroDigito() {
        return nossoNumero ? nossoNumero.charAt(nossoNumero.length() - 1) : ""
    }
}
