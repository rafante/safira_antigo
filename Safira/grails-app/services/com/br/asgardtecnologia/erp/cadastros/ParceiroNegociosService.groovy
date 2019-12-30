package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.erp.base.BaseService
import com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios

class ParceiroNegociosService extends BaseService {

    Boolean beforeSave(ParceiroNegocios parceiroNegocios) {
        parceiroNegocios.endereco_comercial?.save()
        parceiroNegocios.endereco_cobranca?.save()
        parceiroNegocios.endereco_entrega?.save()

        return super.beforeSave(parceiroNegocios)
    }

}
