package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured

import java.util.regex.Pattern

class BaixaBlocoPagarController extends BaseController {

    static allowedMethods = []

    def financeiroBaseService

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        def itemContaPagarInstanceList = ItemContaPagar.createCriteria().list() {
            or {
                eq('compensadoCompletamente', false)
                isNull('compensadoCompletamente')
            }
        }

        [itemContaPagarInstanceList: itemContaPagarInstanceList]
    }

    def compensar() {
        List IDs = []

        params.each { i, obj ->
            if (((Pattern) ~/(itemContaPagar)\[[0-9]\]/).matcher(i).matches() && obj.check) {
                IDs.push(Long.valueOf(obj.id))
            }
        }

        if (IDs.size() > 0) {
            def itensContaPagar = ItemContaPagar.findAllByIdInList(IDs)

            def msg = financeiroBaseService.compensarBloco(itensContaPagar, 'Pagar', params)

            flash.message = message(code: msg)
        } else {
            flash.message = message(code: 'compensacaoItemPagar.no.items.selected')
        }

        redirect action: 'list'
    }

}