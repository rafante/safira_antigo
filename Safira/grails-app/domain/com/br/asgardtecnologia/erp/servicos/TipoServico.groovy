package com.br.asgardtecnologia.erp.servicos

import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.base.Utils

class TipoServico extends Persistente {
    String cod_tipo_servico
    String descricao

    static constraints = {
        cod_tipo_servico(unique: true, blank: false, size: 1..5)
        descricao(size: 0..500)

        data_inclusao(shared: "data_inclusao")
        data_ultima_alteracao(shared: "data_ultima_alteracao")
        usuario(shared: "usuario")
    }

    String toString() {
        Utils.Substring(descricao, 50)
    }
}