package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class PrazoPagamentoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def baseService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def prazoPagamentoInstanceList = paramsToCriteria(params)
        [prazoPagamentoInstanceList: prazoPagamentoInstanceList, prazoPagamentoInstanceTotal: prazoPagamentoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [prazoPagamentoInstance: new PrazoPagamento(params)]
                break
            case 'POST':
                def prazoPagamentoInstance = new PrazoPagamento(params)
                setBaseData(prazoPagamentoInstance)
                if (!baseService.save(prazoPagamentoInstance)) {
                    render view: 'create', model: [prazoPagamentoInstance: prazoPagamentoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'prazoPagamento.label', default: 'PrazoPagamento'), prazoPagamentoInstance.id])
                redirect action: 'show', id: prazoPagamentoInstance.id
                break
        }
    }

    def show() {
        def prazoPagamentoInstance = PrazoPagamento.read(params.id)
        if (!prazoPagamentoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'prazoPagamento.label', default: 'PrazoPagamento'), params.id])
            redirect action: 'list'
            return
        }

        [prazoPagamentoInstance: prazoPagamentoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def prazoPagamentoInstance = PrazoPagamento.read(params.id)
                if (!prazoPagamentoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'prazoPagamento.label', default: 'PrazoPagamento'), params.id])
                    redirect action: 'list'
                    return
                }

                [prazoPagamentoInstance: prazoPagamentoInstance]
                break
            case 'POST':
                def prazoPagamentoInstance = PrazoPagamento.get(params.id)
                setBaseData(prazoPagamentoInstance)
                if (!prazoPagamentoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'prazoPagamento.label', default: 'PrazoPagamento'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (prazoPagamentoInstance.version > version) {
                        prazoPagamentoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'prazoPagamento.label', default: 'PrazoPagamento')] as Object[],
                                "Another user has updated this PrazoPagamento while you were editing")
                        render view: 'edit', model: [prazoPagamentoInstance: prazoPagamentoInstance]
                        return
                    }
                }

                prazoPagamentoInstance.properties = params

                if (!baseService.save(prazoPagamentoInstance)) {
                    render view: 'edit', model: [prazoPagamentoInstance: prazoPagamentoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'prazoPagamento.label', default: 'PrazoPagamento'), prazoPagamentoInstance.id])
                redirect action: 'show', id: prazoPagamentoInstance.id
                break
        }
    }

    def delete() {
        def prazoPagamentoInstance = PrazoPagamento.get(params.id)
        if (!prazoPagamentoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'prazoPagamento.label', default: 'PrazoPagamento'), params.id])
            redirect action: 'list'
            return
        }

        try {
            prazoPagamentoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'prazoPagamento.label', default: 'PrazoPagamento'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'prazoPagamento.label', default: 'PrazoPagamento'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [prazoPagamentoInstance: new PrazoPagamento(params)]
                break
            case 'POST':
                def prazoPagamentoInstance
                if (params.id) prazoPagamentoInstance = PrazoPagamento.get(params.id)
                else prazoPagamentoInstance = new PrazoPagamento(params)

                setBaseData(prazoPagamentoInstance)
                if (!baseService.save(prazoPagamentoInstance)) {
                    render view: 'create', model: [prazoPagamentoInstance: prazoPagamentoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'prazoPagamento.label', default: 'prazoPagamento'), prazoPagamentoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['prazoPagamento.id': prazoPagamentoInstance.id]

                break
        }
    }
}
