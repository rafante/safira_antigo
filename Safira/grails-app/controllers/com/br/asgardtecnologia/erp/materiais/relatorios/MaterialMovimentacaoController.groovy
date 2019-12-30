package com.br.asgardtecnologia.erp.materiais.relatorios

class MaterialMovimentacaoController {

    def index() {
        switch (request.method) {
            case 'GET':
                if (!params.dt_e_s_from) params.dt_e_s_from = Date.parse("dd/MM/yyyy", "01/03/2013")
                if (!params.dt_e_s_to) params.dt_e_s_to = Date.parse("dd/MM/yyyy", "31/03/2013")
                [params: params]
                break

            case 'POST':
                [params: params]
                break
        }
    }
}
