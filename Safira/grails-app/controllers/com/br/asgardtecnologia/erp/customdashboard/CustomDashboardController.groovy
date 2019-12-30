package com.br.asgardtecnologia.erp.customdashboard

import com.br.asgardtecnologia.erp.financeiro.ItemContaPagar

class CustomDashboardController {

    def index() {
        def contasVencidas = ItemContaPagar.findAll()

        [contasVencidasInstance: contasVencidas]
    }
}
