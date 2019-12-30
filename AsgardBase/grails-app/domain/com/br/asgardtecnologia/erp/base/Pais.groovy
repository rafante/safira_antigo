package com.br.asgardtecnologia.erp.base

import com.br.asgardtecnologia.base.Persistente

class Pais extends Persistente {
    String cod_pais //que por sua vez tem seu código +55 no caso do brasil +1 no caso dos estados unidos
    String nome

    static defaultValueField = "nome"
    static defaultFilterFields = "nome;cod_pais"

    static constraints = {
        cod_pais(unique: true, size: 0..5)
        nome(shared: "nome")
        usuario(shared: "usuario")
    }

    String toString() {
        return nome
    }

    static mapping = {
        nome index: 'pais_nome_index'
    }
}
