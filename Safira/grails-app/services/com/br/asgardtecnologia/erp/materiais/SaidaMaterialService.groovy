package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseService

class SaidaMaterialService extends BaseService {

    Boolean beforeSave(SaidaMaterial saidaMaterial) {
        if (saidaMaterial.isDirty('status')) {
            switch (saidaMaterial.status) {
                case StatusSaida.PENDENTE:
                    break
                case StatusSaida.SAIDA:
                    if (!saidaMaterial.data_saida) saidaMaterial.data_saida = new Date()
                    saidaMaterial.geraMovimentoMaterial(-1, "saidaItem", "create")
                    break
            }
        }

        return super.beforeSave(saidaMaterial)
    }

}
