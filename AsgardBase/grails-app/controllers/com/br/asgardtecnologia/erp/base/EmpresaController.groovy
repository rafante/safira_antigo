package com.br.asgardtecnologia.erp.base

import org.springframework.dao.DataIntegrityViolationException

class EmpresaController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def empresaService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def empresaInstanceList = paramsToCriteria(params)
        [empresaInstanceList: empresaInstanceList, empresaInstanceTotal: empresaInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [empresaInstance: new Empresa(params)]
                break
            case 'POST':
                def empresaInstance = new Empresa(params)
                //            setBasetseData(empresaInstance)
                if (!empresaService.save(empresaInstance)) {
                    render view: 'create', model: [empresaInstance: empresaInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'empresa.label', default: 'Empresa'), empresaInstance.id])
                redirect action: 'show', id: empresaInstance.id
                break
        }
    }

    def show() {
        def empresaInstance = Empresa.read(params.id)
        if (!empresaInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'empresa.label', default: 'Empresa'), params.id])
            redirect action: 'list'
            return
        }

        [empresaInstance: empresaInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def empresaInstance = Empresa.read(params.id)
                if (!empresaInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'empresa.label', default: 'Empresa'), params.id])
                    redirect action: 'list'
                    return
                }

                [empresaInstance: empresaInstance]
                break
            case 'POST':
                def empresaInstance = Empresa.get(params.id)
                //            setBaseData(empresaInstance)
                if (!empresaInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'empresa.label', default: 'Empresa'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (empresaInstance.version > version) {
                        empresaInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'empresa.label', default: 'Empresa')] as Object[],
                                "Another user has updated this Empresa while you were editing")
                        render view: 'edit', model: [empresaInstance: empresaInstance]
                        return
                    }
                }

                empresaInstance.properties = params

                if (!empresaService.save(empresaInstance)) {
                    render view: 'edit', model: [empresaInstance: empresaInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'empresa.label', default: 'Empresa'), empresaInstance.id])
                redirect action: 'show', id: empresaInstance.id
                break
        }
    }

    def delete() {
        def empresaInstance = Empresa.get(params.id)
        if (!empresaInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'empresa.label', default: 'Empresa'), params.id])
            redirect action: 'list'
            return
        }

        try {
            empresaService.delete(empresaInstance)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'empresa.label', default: 'Empresa'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'empresa.label', default: 'Empresa'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [empresaInstance: new Empresa(params)]
                break
            case 'POST':
                def empresaInstance
                if (params.id) empresaInstance = Empresa.get(params.id)
                else empresaInstance = new Empresa(params)

                //            setBaseData(empresaInstance)
                if (!empresaService.save(empresaInstance, [validate: false])) {
                    render view: 'create', model: [empresaInstance: empresaInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'empresa.label', default: 'empresa'), empresaInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['empresa.id': empresaInstance.id]

                break
        }
    }
}
