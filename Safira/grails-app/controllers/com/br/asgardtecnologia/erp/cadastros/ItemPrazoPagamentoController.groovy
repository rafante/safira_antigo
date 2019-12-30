package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class ItemPrazoPagamentoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def itemPrazoPagamentoInstanceList = paramsToCriteria(params)
        [itemPrazoPagamentoInstanceList: itemPrazoPagamentoInstanceList, itemPrazoPagamentoInstanceTotal: itemPrazoPagamentoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [itemPrazoPagamentoInstance: new ItemPrazoPagamento(params)]
                break
            case 'POST':
                def itemPrazoPagamentoInstance = new ItemPrazoPagamento(params)
                setBaseData(itemPrazoPagamentoInstance)
                if (!itemPrazoPagamentoInstance.save(flush: true)) {
                    render view: 'create', model: [itemPrazoPagamentoInstance: itemPrazoPagamentoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemPrazoPagamento.label', default: 'ItemPrazoPagamento'), itemPrazoPagamentoInstance.id])
                redirect controller:'prazoPagamento', action: 'edit', id: itemPrazoPagamentoInstance.prazoPagamento?.id
                break
        }
    }

    def show() {
        def itemPrazoPagamentoInstance = ItemPrazoPagamento.read(params.id)
        if (!itemPrazoPagamentoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemPrazoPagamento.label', default: 'ItemPrazoPagamento'), params.id])
            redirect action: 'list'
            return
        }

        [itemPrazoPagamentoInstance: itemPrazoPagamentoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def itemPrazoPagamentoInstance = ItemPrazoPagamento.read(params.id)
                if (!itemPrazoPagamentoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemPrazoPagamento.label', default: 'ItemPrazoPagamento'), params.id])
                    redirect action: 'list'
                    return
                }

                [itemPrazoPagamentoInstance: itemPrazoPagamentoInstance]
                break
            case 'POST':
                def itemPrazoPagamentoInstance = ItemPrazoPagamento.get(params.id)
                setBaseData(itemPrazoPagamentoInstance)
                if (!itemPrazoPagamentoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemPrazoPagamento.label', default: 'ItemPrazoPagamento'), params.id])
                    redirect controller:'prazoPagamento', action: 'edit', id: itemPrazoPagamentoInstance.prazoPagamento.id
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (itemPrazoPagamentoInstance.version > version) {
                        itemPrazoPagamentoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'itemPrazoPagamento.label', default: 'ItemPrazoPagamento')] as Object[],
                                "Another user has updated this ItemPrazoPagamento while you were editing")
                        redirect controller:'prazoPagamento', action: 'edit', id: itemPrazoPagamentoInstance.prazoPagamento.id
                        return
                    }
                }

                itemPrazoPagamentoInstance.properties = params

                if (!itemPrazoPagamentoInstance.save(flush: true)) {
                    redirect controller:'prazoPagamento', action: 'edit', id: itemPrazoPagamentoInstance.prazoPagamento.id
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'itemPrazoPagamento.label', default: 'ItemPrazoPagamento'), itemPrazoPagamentoInstance.id])
                redirect controller:'prazoPagamento', action: 'edit', id: itemPrazoPagamentoInstance.prazoPagamento.id
                break
        }
    }

    def delete() {
        def itemPrazoPagamentoInstance = ItemPrazoPagamento.get(params.id)
        if (!itemPrazoPagamentoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemPrazoPagamento.label', default: 'ItemPrazoPagamento'), params.id])
            redirect controller:'prazoPagamento', action: 'edit', id: itemPrazoPagamentoInstance.prazoPagamento.id
            return
        }

        try {
            itemPrazoPagamentoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'itemPrazoPagamento.label', default: 'ItemPrazoPagamento'), params.id])
            redirect controller:'prazoPagamento', action: 'edit', id: itemPrazoPagamentoInstance.prazoPagamento.id
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'itemPrazoPagamento.label', default: 'ItemPrazoPagamento'), params.id])
            redirect controller:'prazoPagamento', action: 'edit', id: itemPrazoPagamentoInstance.prazoPagamento.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [itemPrazoPagamentoInstance: new ItemPrazoPagamento(params)]
                break
            case 'POST':
                def itemPrazoPagamentoInstance
                if (params.id) itemPrazoPagamentoInstance = ItemPrazoPagamento.get(params.id)
                else itemPrazoPagamentoInstance = new ItemPrazoPagamento(params)

                setBaseData(itemPrazoPagamentoInstance)
                if (!itemPrazoPagamentoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [itemPrazoPagamentoInstance: itemPrazoPagamentoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemPrazoPagamento.label', default: 'itemPrazoPagamento'), itemPrazoPagamentoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['itemPrazoPagamento.id': itemPrazoPagamentoInstance.id]

                break
        }
    }
}
