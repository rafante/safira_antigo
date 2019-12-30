package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.erp.base.BaseController
import com.br.asgardtecnologia.erp.financeiro.Cheque
import org.springframework.dao.DataIntegrityViolationException

class ChequeController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def chequeInstanceList = paramsToCriteria(params)
        [chequeInstanceList: chequeInstanceList, chequeInstanceTotal: chequeInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [chequeInstance: new Cheque(params)]
                break
            case 'POST':
                def chequeInstance = new Cheque(params)
                setBaseData(chequeInstance)
                if (!chequeInstance.save(flush: true)) {
                    render view: 'create', model: [chequeInstance: chequeInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'cheque.label', default: 'Cheque'), chequeInstance.id])
                redirect action: 'show', id: chequeInstance.id
                break
        }
    }

    def show() {
        def chequeInstance = Cheque.read(params.id)
        if (!chequeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'cheque.label', default: 'Cheque'), params.id])
            redirect action: 'list'
            return
        }

        [chequeInstance: chequeInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def chequeInstance = Cheque.read(params.id)
                if (!chequeInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'cheque.label', default: 'Cheque'), params.id])
                    redirect action: 'list'
                    return
                }

                [chequeInstance: chequeInstance]
                break
            case 'POST':
                def chequeInstance = Cheque.get(params.id)
                setBaseData(chequeInstance)
                if (!chequeInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'cheque.label', default: 'Cheque'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (chequeInstance.version > version) {
                        chequeInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'cheque.label', default: 'Cheque')] as Object[],
                                "Another user has updated this Cheque while you were editing")
                        render view: 'edit', model: [chequeInstance: chequeInstance]
                        return
                    }
                }

                chequeInstance.properties = params

                if (!chequeInstance.save(flush: true)) {
                    render view: 'edit', model: [chequeInstance: chequeInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'cheque.label', default: 'Cheque'), chequeInstance.id])
                redirect action: 'show', id: chequeInstance.id
                break
        }
    }

    def delete() {
        def chequeInstance = Cheque.get(params.id)
        if (!chequeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'cheque.label', default: 'Cheque'), params.id])
            redirect action: 'list'
            return
        }

        try {
            chequeInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'cheque.label', default: 'Cheque'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'cheque.label', default: 'Cheque'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [chequeInstance: new Cheque(params)]
                break
            case 'POST':
                def chequeInstance
                if (params.id) chequeInstance = Cheque.get(params.id)
                else chequeInstance = new Cheque(params)

                setBaseData(chequeInstance)
                if (!chequeInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [chequeInstance: chequeInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'cheque.label', default: 'cheque'), chequeInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['cheque.id': chequeInstance.id]

                break
        }
    }
}
