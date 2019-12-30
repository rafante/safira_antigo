package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.BaseController
import org.springframework.dao.DataIntegrityViolationException

class TipoContaController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def tipoContaInstanceList = paramsToCriteria(params)
        [tipoContaInstanceList: tipoContaInstanceList, tipoContaInstanceTotal: tipoContaInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [tipoContaInstance: new TipoConta(params)]
                break
            case 'POST':
                def tipoContaInstance = new TipoConta(params)
                setBaseData(tipoContaInstance)
                if (!tipoContaInstance.save(flush: true)) {
                    render view: 'create', model: [tipoContaInstance: tipoContaInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'tipoConta.label', default: 'TipoConta'), tipoContaInstance.id])
                redirect action: 'show', id: tipoContaInstance.id
                break
        }
    }

    def show() {
        def tipoContaInstance = TipoConta.read(params.id)
        if (!tipoContaInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoConta.label', default: 'TipoConta'), params.id])
            redirect action: 'list'
            return
        }

        [tipoContaInstance: tipoContaInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def tipoContaInstance = TipoConta.read(params.id)
                if (!tipoContaInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoConta.label', default: 'TipoConta'), params.id])
                    redirect action: 'list'
                    return
                }

                [tipoContaInstance: tipoContaInstance]
                break
            case 'POST':
                def tipoContaInstance = TipoConta.get(params.id)
                setBaseData(tipoContaInstance)
                if (!tipoContaInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoConta.label', default: 'TipoConta'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (tipoContaInstance.version > version) {
                        tipoContaInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'tipoConta.label', default: 'TipoConta')] as Object[],
                                "Another user has updated this TipoConta while you were editing")
                        render view: 'edit', model: [tipoContaInstance: tipoContaInstance]
                        return
                    }
                }

                tipoContaInstance.properties = params

                if (!tipoContaInstance.save(flush: true)) {
                    render view: 'edit', model: [tipoContaInstance: tipoContaInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'tipoConta.label', default: 'TipoConta'), tipoContaInstance.id])
                redirect action: 'show', id: tipoContaInstance.id
                break
        }
    }

    def delete() {
        def tipoContaInstance = TipoConta.get(params.id)
        if (!tipoContaInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoConta.label', default: 'TipoConta'), params.id])
            redirect action: 'list'
            return
        }

        try {
            tipoContaInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'tipoConta.label', default: 'TipoConta'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'tipoConta.label', default: 'TipoConta'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [tipoContaInstance: new TipoConta(params)]
                break
            case 'POST':
                def tipoContaInstance
                if (params.id) tipoContaInstance = TipoConta.get(params.id)
                else tipoContaInstance = new TipoConta(params)

                setBaseData(tipoContaInstance)
                if (!tipoContaInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [tipoContaInstance: tipoContaInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'tipoConta.label', default: 'tipoConta'), tipoContaInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['tipoConta.id': tipoContaInstance.id]

                break
        }
    }
}
