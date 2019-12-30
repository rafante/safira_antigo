package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.BaseController
import com.br.asgardtecnologia.erp.financeiro.ContaCorrente
import org.springframework.dao.DataIntegrityViolationException

class ContaBancariaController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def contaBancariaInstanceList = paramsToCriteria(params)
        [contaBancariaInstanceList: contaBancariaInstanceList, contaBancariaInstanceTotal: contaBancariaInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [contaBancariaInstance: new ContaCorrente(params)]
                break
            case 'POST':
                def contaBancariaInstance = new ContaCorrente(params)
                setBaseData(contaBancariaInstance)
                if (!contaBancariaInstance.save(flush: true)) {
                    render view: 'create', model: [contaBancariaInstance: contaBancariaInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'contaCorrente.label', default: 'ContaBancaria'), contaBancariaInstance.id])
                redirect action: 'show', id: contaBancariaInstance.id
                break
        }
    }

    def show() {
        def contaBancariaInstance = ContaCorrente.read(params.id)
        if (!contaBancariaInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaCorrente.label', default: 'ContaBancaria'), params.id])
            redirect action: 'list'
            return
        }

        [contaBancariaInstance: contaBancariaInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def contaBancariaInstance = ContaCorrente.read(params.id)
                if (!contaBancariaInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaCorrente.label', default: 'ContaBancaria'), params.id])
                    redirect action: 'list'
                    return
                }

                [contaBancariaInstance: contaBancariaInstance]
                break
            case 'POST':
                def contaBancariaInstance = ContaCorrente.get(params.id)
                setBaseData(contaBancariaInstance)
                if (!contaBancariaInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaCorrente.label', default: 'ContaBancaria'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (contaBancariaInstance.version > version) {
                        contaBancariaInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'contaCorrente.label', default: 'ContaBancaria')] as Object[],
                                "Another user has updated this ContaBancaria while you were editing")
                        render view: 'edit', model: [contaBancariaInstance: contaBancariaInstance]
                        return
                    }
                }

                contaBancariaInstance.properties = params

                if (!contaBancariaInstance.save(flush: true)) {
                    render view: 'edit', model: [contaBancariaInstance: contaBancariaInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'contaCorrente.label', default: 'ContaBancaria'), contaBancariaInstance.id])
                redirect action: 'show', id: contaBancariaInstance.id
                break
        }
    }

    def delete() {
        def contaBancariaInstance = ContaCorrente.get(params.id)
        if (!contaBancariaInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaCorrente.label', default: 'ContaBancaria'), params.id])
            redirect action: 'list'
            return
        }

        try {
            contaBancariaInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'contaCorrente.label', default: 'ContaBancaria'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'contaBancaria.label', default: 'ContaBancaria'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [contaBancariaInstance: new ContaCorrente(params)]
                break
            case 'POST':
                def contaBancariaInstance
                if (params.id) contaBancariaInstance = ContaCorrente.get(params.id)
                else contaBancariaInstance = new ContaCorrente(params)

                setBaseData(contaBancariaInstance)
                if (!contaBancariaInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [contaBancariaInstance: contaBancariaInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'contaCorrente.label', default: 'Conta Corrente'), contaBancariaInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['contaBancaria.id': contaBancariaInstance.id]

                break
        }
    }
}
