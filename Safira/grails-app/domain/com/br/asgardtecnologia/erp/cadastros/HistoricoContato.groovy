package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.base.Persistente

public class HistoricoContato extends Persistente {

    String contato
    Date data = new Date()
    String historico

    static belongsTo = [parceiroNegocios: ParceiroNegocios]

    static constraints = {
        parceiroNegocios(asgDefaultFilter: [cliente: true])

        /**
         * Data do contato
         */
        data()
        /**
         * Descri��o do contato.
         * Tamanho 255.
         */
        historico()
        /**
         * Nome de contato.
         */
        contato()
    }

}