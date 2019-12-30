package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.BaseController
import org.springframework.dao.DataIntegrityViolationException

class ContaCorrenteController extends BaseController{

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def contaCorrenteInstanceList = paramsToCriteria(params)
        [contaCorrenteInstanceList: contaCorrenteInstanceList, contaCorrenteInstanceTotal: contaCorrenteInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [contaCorrenteInstance: new ContaCorrente(params)]
                break
            case 'POST':
                def contaCorrenteInstance = new ContaCorrente(params)
                setBaseData(contaCorrenteInstance)
                if (!contaCorrenteInstance.save(flush: true)) {
                    render view: 'create', model: [contaCorrenteInstance: contaCorrenteInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'contaCorrente.label', default: 'ContaCorrente'), contaCorrenteInstance.id])
                redirect action: 'show', id: contaCorrenteInstance.id
                break
        }
    }

    def show() {
        def contaCorrenteInstance = ContaCorrente.read(params.id)
        if (!contaCorrenteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaCorrente.label', default: 'ContaCorrente'), params.id])
            redirect action: 'list'
            return
        }

        [contaCorrenteInstance: contaCorrenteInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def contaCorrenteInstance = ContaCorrente.read(params.id)
                if (!contaCorrenteInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaCorrente.label', default: 'ContaCorrente'), params.id])
                    redirect action: 'list'
                    return
                }

                [contaCorrenteInstance: contaCorrenteInstance]
                break
            case 'POST':
                def contaCorrenteInstance = ContaCorrente.get(params.id)
                setBaseData(contaCorrenteInstance)
                if (!contaCorrenteInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaCorrente.label', default: 'ContaCorrente'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (contaCorrenteInstance.version > version) {
                        contaCorrenteInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'contaCorrente.label', default: 'ContaCorrente')] as Object[],
                                "Another user has updated this ContaCorrente while you were editing")
                        render view: 'edit', model: [contaCorrenteInstance: contaCorrenteInstance]
                        return
                    }
                }

                contaCorrenteInstance.properties = params

                if (!contaCorrenteInstance.save(flush: true)) {
                    render view: 'edit', model: [contaCorrenteInstance: contaCorrenteInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'contaCorrente.label', default: 'ContaCorrente'), contaCorrenteInstance.id])
                redirect action: 'show', id: contaCorrenteInstance.id
                break
        }
    }

    def delete() {
        def contaCorrenteInstance = ContaCorrente.get(params.id)
        if (!contaCorrenteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaCorrente.label', default: 'ContaCorrente'), params.id])
            redirect action: 'list'
            return
        }

        try {
            contaCorrenteInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'contaCorrente.label', default: 'ContaCorrente'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'contaCorrente.label', default: 'ContaCorrente'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [contaCorrenteInstance: new ContaCorrente(params)]
                break
            case 'POST':
                def contaCorrenteInstance
                if (params.id) contaCorrenteInstance = ContaCorrente.get(params.id)
                else contaCorrenteInstance = new ContaCorrente(params)

                setBaseData(contaCorrenteInstance)
                if (!contaCorrenteInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [contaCorrenteInstance: contaCorrenteInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'contaCorrente.label', default: 'contaCorrente'), contaCorrenteInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['contaCorrente.id': contaCorrenteInstance.id]

                break
        }
    }
}
