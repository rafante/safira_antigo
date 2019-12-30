package com.br.asgardtecnologia.erp.ev

import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.erp.cadastros.Setor
import com.br.asgardtecnologia.erp.materiais.Material
import com.br.asgardtecnologia.erp.materiais.Recipiente

class Exame extends Persistente{

    Long codExame
    String mnemonico
    String descricao
    BigDecimal precoCusto
    BigDecimal precoVenda
    Boolean ativo

    static defaultFilterFields = "codExame;mnemonico;descricao;precoCusto;precoVenda;ativo;setor;recipiente;material"
    static defaultAutoCompleteFields = "id;codExame;mnemonico;descricao"

    static belongsTo = [setor: Setor, recipiente: Recipiente]

    static constraints = {
        codExame(unique: true)
        mnemonico(size: 1..10, unique: true)
        descricao()
        precoCusto()
        precoVenda()
        ativo()
        setor()
        recipiente()
    }

    @Override
    String toString() {
        return id + " - " + descricao + " " + codExame + " " + mnemonico
    }
}
