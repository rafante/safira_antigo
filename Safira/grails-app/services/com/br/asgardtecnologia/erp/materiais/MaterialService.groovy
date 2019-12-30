package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseService

class MaterialService extends BaseService {
    Boolean beforeSave(Material material) {
        if (!material?.isNormal()) {
            material?.recalculaComposicao()
        }

        material.alimentaHistoricoCusto()

        return super.beforeSave(material)
    }
}
