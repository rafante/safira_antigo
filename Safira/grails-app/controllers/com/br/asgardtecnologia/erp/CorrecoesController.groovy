package com.br.asgardtecnologia.erp

import groovy.sql.Sql

class CorrecoesController {

    def correcoesService

    def index() {

    }

    def corrigeCamposNulos() {
        correcoesService.correcaoDecimaisNulos()
        correcoesService.correcaoBooleanosNulos()
        flash.message = 'Correção realizada'
        redirect(action: 'index')
    }

    def correcaoColunas() {
        correcoesService.eliminaColunas([
                'compensacao_item_pagar'  : 'conta_caixa_id',
                'compensacao_item_receber': 'conta_caixa_id',
                'conta_pagar'             : 'conta_caixa_id',
                'conta_receber'           : 'conta_caixa_id',
                'entrada_material'        : 'conta_caixa_id',
                'parametrosev'            : 'conta_caixa_id',
                'plano_contas'            : 'controle_caixa',
                'cheque'                  : 'conta_bancaria_id',
                'transferencia_conta'     : 'conta_caixa_destino_id',
                'movimento_financeiro'    : 'conta_caixa_id',
        ])

        correcoesService.eliminaColunas(['transferencia_conta': 'conta_caixa_origem_id'])
        correcoesService.eliminaColunas([
                'compensacao_item_pagar'  : 'conta_bancaria_id',
                'compensacao_item_receber': 'conta_bancaria_id',
        ])
        flash.message = 'Correção realizada'
        redirect(action: 'index')
    }

    def list() {
        redirect(action: 'index')
    }
}
