package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.erp.base.BaseController
import org.springframework.dao.DataIntegrityViolationException

class FormaPagamentoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def formaPagamentoInstanceList = paramsToCriteria(params)
        [formaPagamentoInstanceList: formaPagamentoInstanceList, formaPagamentoInstanceTotal: formaPagamentoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [formaPagamentoInstance: new FormaPagamento(params)]
                break
            case 'POST':
                def formaPagamentoInstance = new FormaPagamento(params)
                setBaseData(formaPagamentoInstance)
                if (!formaPagamentoInstance.save(flush: true)) {
                    render view: 'create', model: [formaPagamentoInstance: formaPagamentoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'formaPagamento.label', default: 'FormaPagamento'), formaPagamentoInstance.id])
                redirect action: 'show', id: formaPagamentoInstance.id
                break
        }
    }

    def show() {
        def formaPagamentoInstance = FormaPagamento.read(params.id)
        if (!formaPagamentoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'formaPagamento.label', default: 'FormaPagamento'), params.id])
            redirect action: 'list'
            return
        }

        [formaPagamentoInstance: formaPagamentoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def formaPagamentoInstance = FormaPagamento.read(params.id)
                if (!formaPagamentoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'formaPagamento.label', default: 'FormaPagamento'), params.id])
                    redirect action: 'list'
                    return
                }

                [formaPagamentoInstance: formaPagamentoInstance]
                break
            case 'POST':
                def formaPagamentoInstance = FormaPagamento.get(params.id)

                if(formaPagamentoInstance.descricao == "Transferência entre contas"){
                    flash.message = message(code: 'default.not.allow.delete.forma.pagamento', args: [formaPagamentoInstance.descricao])
                    redirect(action: 'show')
                }

                setBaseData(formaPagamentoInstance)
                if (!formaPagamentoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'formaPagamento.label', default: 'FormaPagamento'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (formaPagamentoInstance.version > version) {
                        formaPagamentoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'formaPagamento.label', default: 'FormaPagamento')] as Object[],
                                "Another user has updated this FormaPagamento while you were editing")
                        render view: 'edit', model: [formaPagamentoInstance: formaPagamentoInstance]
                        return
                    }
                }

                formaPagamentoInstance.properties = params

                if (!formaPagamentoInstance.save(flush: true)) {
                    render view: 'edit', model: [formaPagamentoInstance: formaPagamentoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'formaPagamento.label', default: 'FormaPagamento'), formaPagamentoInstance.id])
                redirect action: 'show', id: formaPagamentoInstance.id
                break
        }
    }

    def delete() {
        def formaPagamentoInstance = FormaPagamento.get(params.id)
        if(formaPagamentoInstance.descricao == "Transferência entre contas"){
            flash.message = message(code: 'default.not.allow.delete.forma.pagamento', args: [formaPagamentoInstance.descricao])
            redirect(action: 'show')
        }
        if (!formaPagamentoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'formaPagamento.label', default: 'FormaPagamento'), params.id])
            redirect action: 'list'
            return
        }

        try {
            formaPagamentoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'formaPagamento.label', default: 'FormaPagamento'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'formaPagamento.label', default: 'FormaPagamento'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [formaPagamentoInstance: new FormaPagamento(params)]
                break
            case 'POST':
                def formaPagamentoInstance
                if (params.id) formaPagamentoInstance = FormaPagamento.get(params.id)
                else formaPagamentoInstance = new FormaPagamento(params)

                setBaseData(formaPagamentoInstance)
                if (!formaPagamentoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [formaPagamentoInstance: formaPagamentoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'formaPagamento.label', default: 'formaPagamento'), formaPagamentoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['formaPagamento.id': formaPagamentoInstance.id]

                break
        }
    }
}
