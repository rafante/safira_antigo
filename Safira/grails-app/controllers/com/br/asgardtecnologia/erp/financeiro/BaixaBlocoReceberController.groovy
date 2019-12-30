package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured

import java.util.regex.Pattern

class BaixaBlocoReceberController extends BaseController {

    static allowedMethods = []

    def financeiroBaseService

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        def itemContaReceberInstanceList = ItemContaReceber.createCriteria().list() {
            or {
                eq('compensadoCompletamente', false)
                isNull('compensadoCompletamente')
            }
        }

        [itemContaReceberInstanceList: itemContaReceberInstanceList]
    }

    def compensar() {
        List IDs = []

        params.each { i, obj ->
            if (((Pattern) ~/(itemContaReceber)\[[0-9]\]/).matcher(i).matches() && obj.check) {
                IDs.push(Long.valueOf(obj.id))
            }
        }

        if (IDs.size() > 0) {
            def itensContaReceber = ItemContaReceber.findAllByIdInList(IDs)

            def msg = financeiroBaseService.compensarBloco(itensContaReceber, 'Receber', params)

            flash.message = message(code: msg)
        } else {
            flash.message = message(code: 'compensacaoItemReceber.no.items.selected')
        }

        redirect action: 'list'
    }

}