package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseService

class EntradaMaterialService extends BaseService {

    static CONTROLLER_NAME = "entradaMaterial"
    def messageService

    Boolean beforeSave(EntradaMaterial entradaMaterial) {
        if (!entradaMaterial.validate()) return false

        if (entradaMaterial.status != entradaMaterial.statusOld) {
            switch (entradaMaterial.status) {
                case StatusEntrada.PEDIDO_COMPRA:
                    if (!entradaMaterial.data_pedido_compra) entradaMaterial.data_pedido_compra = new Date()
                    break
                case StatusEntrada.ENTRADA:
                    if (!entradaMaterial.validaDadosFinanceiro()) {
                        entradaMaterial.refresh()
                        entradaMaterial.errors.reject('entradaMaterial.mensagens.dadosFinanceiroIncompleto')
                        return false
                    }

                    if (!entradaMaterial.data_entrada) entradaMaterial.data_entrada = new Date()

                    // Gera Financeiro (Contas a Pagar)
                    if (!entradaMaterial.geraContaPagar(CONTROLLER_NAME, "edit")) {
                        entradaMaterial.refresh()
                        entradaMaterial.errors.reject('entradaMaterial.mensagens.erroGeracaoPagar')
                        return false
                    }

                    // Executa a Movimentação do Material
                    if (!entradaMaterial.geraMovimentoMaterial(1, true, 'entradaMaterial', "edit")) {
                        entradaMaterial.refresh()
                        entradaMaterial.errors.reject('entradaMaterial.mensagens.erroGeracaoMovimento')
                        throw new RuntimeException("entradaMaterial.mensagens.erroGeracaoMovimento")
                    }

                    break
            }
        }

        return super.beforeSave(entradaMaterial)
    }

    def corrigirEntrada(EntradaMaterial entradaMaterial, usuario, motivo) {
        return entradaMaterial.corrigirEntrada(usuario, motivo)
    }

    def deleteItem(ItemEntrada itemEntrada) {
        itemEntrada.movimento_material.removeAll(itemEntrada.movimento_material)
        itemEntrada.movimento_material.clear()
        itemEntrada.entradaMaterial.itemEntrada.remove(itemEntrada)

        return delete(itemEntrada)
    }

}
