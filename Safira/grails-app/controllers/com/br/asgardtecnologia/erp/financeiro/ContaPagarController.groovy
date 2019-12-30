package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

import javax.xml.bind.ValidationException

class ContaPagarController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def financeiroBaseService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def contaPagarInstanceList = paramsToCriteria(params)
        [contaPagarInstanceList: contaPagarInstanceList, contaPagarInstanceTotal: contaPagarInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [contaPagarInstance: new ContaPagar(params), params: params]
                break
            case 'POST':
                def contaPagarInstance = new ContaPagar(params)

                if (!financeiroBaseService.save(contaPagarInstance, params)) {
                    render view: 'create', model: [contaPagarInstance: contaPagarInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'contaPagar.label', default: 'ContaPagar'), contaPagarInstance.id])
                redirect action: 'show', id: contaPagarInstance.id
                break
        }
    }

    def prorrogar() {
        def itemContaPagarInstance = ItemContaPagar.get(params.itemContaPagar.id)

        params.data = new Date().parse("d/M/yyyy", params.data_holder)

        itemContaPagarInstance.properties = params

        def msg = financeiroBaseService.prorrogarParcela(itemContaPagarInstance, 'Pagar', params)
        flash.message = resolveMessage(msg)

        redirect action: 'show', id: itemContaPagarInstance.contaPagarId
    }

    def compensar() {
        def itemContaPagarInstance = ItemContaPagar.get(params.itemContaPagar.id)
        itemContaPagarInstance.properties = params

        try {
            flash.message = resolveMessage(financeiroBaseService.compensarParcela(itemContaPagarInstance, params))
        } catch (ValidationException e) {
            flash.message = resolveMessage(e.message)
        }

        redirect controller: 'itemContaPagar', action: 'show', id: itemContaPagarInstance?.id
    }

    def show() {
        def contaPagarInstance = ContaPagar.read(params.id)
        if (!contaPagarInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaPagar.label', default: 'ContaPagar'), params.id])
            redirect action: 'list'
            return
        }

        [contaPagarInstance: contaPagarInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def contaPagarInstance = ContaPagar.read(params.id)
                if (!contaPagarInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaPagar.label', default: 'ContaPagar'), params.id])
                    redirect action: 'list'
                    return
                }

                if (!contaPagarInstance.itensContaPagar) {
                    if (!params.valorTotal) params.valorTotal = 0
                    if (!params.numParcelas) params.numParcelas = 1
                    if (!params.primeiroVencimento) params.primeiroVencimento = new Date()
                }

                [contaPagarInstance: contaPagarInstance]
                break
            case 'POST':
                def contaPagarInstance = ContaPagar.get(params.id)
                setBaseData(contaPagarInstance)
                if (!contaPagarInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaPagar.label', default: 'ContaPagar'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (contaPagarInstance.version > version) {
                        contaPagarInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'contaPagar.label', default: 'ContaPagar')] as Object[],
                                "Another user has updated this ContaPagar while you were editing")
                        render view: 'edit', model: [contaPagarInstance: contaPagarInstance]
                        return
                    }
                }

                def p = params.clone()

                contaPagarInstance.properties = params

                if (!financeiroBaseService.save(contaPagarInstance, params)) {
                    render view: 'edit', model: [contaPagarInstance: contaPagarInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'contaPagar.label', default: 'ContaPagar'), contaPagarInstance.id])
                redirect action: 'show', id: contaPagarInstance.id
                break
        }
    }

    def delete() {
        def contaPagarInstance = ContaPagar.get(params.id)
        if (!contaPagarInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaPagar.label', default: 'ContaPagar'), params.id])
            redirect action: 'list'
            return
        }

        try {
            contaPagarInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'contaPagar.label', default: 'ContaPagar'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'contaPagar.label', default: 'ContaPagar'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [contaPagarInstance: new ContaPagar(params)]
                break
            case 'POST':
                def contaPagarInstance
                if (params.id)
                    contaPagarInstance = ContaPagar.get(params.id)
                else
                    contaPagarInstance = new ContaPagar(params)

                setBaseData(contaPagarInstance)
                if (!financeiroBaseService.save(contaPagarInstance, params)) {
                    render view: 'create', model: [contaPagarInstance: contaPagarInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'contaPagar.label', default: 'contaPagar'), contaPagarInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['contaPagar.id': contaPagarInstance.id]

                break
        }
    }
}