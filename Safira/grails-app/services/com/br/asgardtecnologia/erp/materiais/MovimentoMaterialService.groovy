package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseService

class MovimentoMaterialService extends BaseService {

    Boolean beforeSave(MovimentoMaterial movimentoMaterial) {
        if (movimentoMaterial.isDirty('status')) {
            switch (movimentoMaterial.status) {
                case StatusMovimento.SOLICITACAO:
                    break
                case StatusMovimento.CONCLUIDO:
                    if (!movimentoMaterial.data_movimento) movimentoMaterial.data_movimento = new Date()
                    movimentoMaterial.geraMovimento(movimentoMaterial.entrada_saida == TipoMovimentoMaterial.TIPO_SAIDA ? -1 : 1)
                    break
            }
        }

        return super.beforeSave(movimentoMaterial)
    }

}
