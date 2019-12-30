package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class UnidadeMedidaController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def unidadeMedidaInstanceList = paramsToCriteria(params)
        [unidadeMedidaInstanceList: unidadeMedidaInstanceList, unidadeMedidaInstanceTotal: unidadeMedidaInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [unidadeMedidaInstance: new UnidadeMedida(params)]
                break
            case 'POST':
                def unidadeMedidaInstance = new UnidadeMedida(params)
                setBaseData(unidadeMedidaInstance)
                if (!unidadeMedidaInstance.save(flush: true)) {
                    render view: 'create', model: [unidadeMedidaInstance: unidadeMedidaInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'unidadeMedida.label', default: 'UnidadeMedida'), unidadeMedidaInstance.id])
                redirect action: 'show', id: unidadeMedidaInstance.id
                break
        }
    }

    def show() {
        def unidadeMedidaInstance = UnidadeMedida.read(params.id)
        if (!unidadeMedidaInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'unidadeMedida.label', default: 'UnidadeMedida'), params.id])
            redirect action: 'list'
            return
        }

        [unidadeMedidaInstance: unidadeMedidaInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def unidadeMedidaInstance = UnidadeMedida.read(params.id)
                if (!unidadeMedidaInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'unidadeMedida.label', default: 'UnidadeMedida'), params.id])
                    redirect action: 'list'
                    return
                }

                [unidadeMedidaInstance: unidadeMedidaInstance]
                break
            case 'POST':
                def unidadeMedidaInstance = UnidadeMedida.get(params.id)
                setBaseData(unidadeMedidaInstance)
                if (!unidadeMedidaInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'unidadeMedida.label', default: 'UnidadeMedida'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (unidadeMedidaInstance.version > version) {
                        unidadeMedidaInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'unidadeMedida.label', default: 'UnidadeMedida')] as Object[],
                                "Another user has updated this UnidadeMedida while you were editing")
                        render view: 'edit', model: [unidadeMedidaInstance: unidadeMedidaInstance]
                        return
                    }
                }

                unidadeMedidaInstance.properties = params

                if (!unidadeMedidaInstance.save(flush: true)) {
                    render view: 'edit', model: [unidadeMedidaInstance: unidadeMedidaInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'unidadeMedida.label', default: 'UnidadeMedida'), unidadeMedidaInstance.id])
                redirect action: 'show', id: unidadeMedidaInstance.id
                break
        }
    }

    def delete() {
        def unidadeMedidaInstance = UnidadeMedida.get(params.id)
        if (!unidadeMedidaInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'unidadeMedida.label', default: 'UnidadeMedida'), params.id])
            redirect action: 'list'
            return
        }

        try {
            unidadeMedidaInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'unidadeMedida.label', default: 'UnidadeMedida'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'unidadeMedida.label', default: 'UnidadeMedida'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [unidadeMedidaInstance: new UnidadeMedida(params)]
                break
            case 'POST':
                def unidadeMedidaInstance
                if (params.id) unidadeMedidaInstance = UnidadeMedida.get(params.id)
                else unidadeMedidaInstance = new UnidadeMedida(params)

                setBaseData(unidadeMedidaInstance)
                if (!unidadeMedidaInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [unidadeMedidaInstance: unidadeMedidaInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'unidadeMedida.label', default: 'unidadeMedida'), unidadeMedidaInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['unidadeMedida.id': unidadeMedidaInstance.id]

                break
        }
    }
}
