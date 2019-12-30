package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.erp.base.BaseController
import org.springframework.dao.DataIntegrityViolationException

class HistoricoPadraoController extends BaseController{
    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def historicoPadraoInstanceList = paramsToCriteria(params)
        [historicoPadraoInstanceList: historicoPadraoInstanceList, historicoPadraoInstanceTotal: historicoPadraoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [historicoPadraoInstance: new HistoricoPadrao(params)]
                break
            case 'POST':
                def historicoPadraoInstance = new HistoricoPadrao(params)
                setBaseData(historicoPadraoInstance)
                if (!historicoPadraoInstance.save(flush: true)) {
                    render view: 'create', model: [historicoPadraoInstance: historicoPadraoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'historicoPadrao.label', default: 'HistoricoPadrao'), historicoPadraoInstance.id])
                redirect action: 'show', id: historicoPadraoInstance.id
                break
        }
    }

    def show() {
        def historicoPadraoInstance = HistoricoPadrao.read(params.id)
        if (!historicoPadraoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'historicoPadrao.label', default: 'HistoricoPadrao'), params.id])
            redirect action: 'list'
            return
        }

        [historicoPadraoInstance: historicoPadraoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def historicoPadraoInstance = HistoricoPadrao.read(params.id)
                if (!historicoPadraoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'historicoPadrao.label', default: 'HistoricoPadrao'), params.id])
                    redirect action: 'list'
                    return
                }

                [historicoPadraoInstance: historicoPadraoInstance]
                break
            case 'POST':
                def historicoPadraoInstance = HistoricoPadrao.get(params.id)
                setBaseData(historicoPadraoInstance)
                if (!historicoPadraoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'historicoPadrao.label', default: 'HistoricoPadrao'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (historicoPadraoInstance.version > version) {
                        historicoPadraoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'historicoPadrao.label', default: 'HistoricoPadrao')] as Object[],
                                "Another user has updated this HistoricoPadrao while you were editing")
                        render view: 'edit', model: [historicoPadraoInstance: historicoPadraoInstance]
                        return
                    }
                }

                historicoPadraoInstance.properties = params

                if (!historicoPadraoInstance.save(flush: true)) {
                    render view: 'edit', model: [historicoPadraoInstance: historicoPadraoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'historicoPadrao.label', default: 'HistoricoPadrao'), historicoPadraoInstance.id])
                redirect action: 'show', id: historicoPadraoInstance.id
                break
        }
    }

    def delete() {
        def historicoPadraoInstance = HistoricoPadrao.get(params.id)
        if (!historicoPadraoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'historicoPadrao.label', default: 'HistoricoPadrao'), params.id])
            redirect action: 'list'
            return
        }

        try {
            historicoPadraoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'historicoPadrao.label', default: 'HistoricoPadrao'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'historicoPadrao.label', default: 'HistoricoPadrao'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [historicoPadraoInstance: new HistoricoPadrao(params)]
                break
            case 'POST':
                def historicoPadraoInstance
                if (params.id) historicoPadraoInstance = HistoricoPadrao.get(params.id)
                else historicoPadraoInstance = new HistoricoPadrao(params)

                setBaseData(historicoPadraoInstance)
                if (!historicoPadraoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [historicoPadraoInstance: historicoPadraoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'historicoPadrao.label', default: 'historicoPadrao'), historicoPadraoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['historicoPadrao.id': historicoPadraoInstance.id]

                break
        }
    }
}
