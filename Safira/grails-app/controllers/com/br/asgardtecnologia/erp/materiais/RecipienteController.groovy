package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import org.springframework.dao.DataIntegrityViolationException

class RecipienteController extends BaseController{
    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def baseService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def recipienteInstanceList = paramsToCriteria(params)
        [recipienteInstanceList: recipienteInstanceList, recipienteInstanceTotal: recipienteInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [recipienteInstance: new Recipiente(params)]
                break
            case 'POST':
                def recipienteInstance = new Recipiente(params)
                setBaseData(recipienteInstance)
                if (!baseService.save(recipienteInstance)) {
                    render view: 'create', model: [recipienteInstance: recipienteInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'recipiente.label', default: 'Recipiente'), recipienteInstance.id])
                redirect action: 'show', id: recipienteInstance.id
                break
        }
    }

    def show() {
        def recipienteInstance = Recipiente.read(params.id)
        if (!recipienteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'recipiente.label', default: 'Recipiente'), params.id])
            redirect action: 'list'
            return
        }

        [recipienteInstance: recipienteInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def recipienteInstance = Recipiente.read(params.id)
                if (!recipienteInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'recipiente.label', default: 'Recipiente'), params.id])
                    redirect action: 'list'
                    return
                }

                [recipienteInstance: recipienteInstance]
                break
            case 'POST':
                def recipienteInstance = Recipiente.get(params.id)
                setBaseData(recipienteInstance)
                if (!recipienteInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'recipiente.label', default: 'Recipiente'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (recipienteInstance.version > version) {
                        recipienteInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'recipiente.label', default: 'Recipiente')] as Object[],
                                "Another user has updated this Recipiente while you were editing")
                        render view: 'edit', model: [recipienteInstance: recipienteInstance]
                        return
                    }
                }

                recipienteInstance.properties = params

                if (!baseService.save(recipienteInstance)) {
                    render view: 'edit', model: [recipienteInstance: recipienteInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'recipiente.label', default: 'Recipiente'), recipienteInstance.id])
                redirect action: 'show', id: recipienteInstance.id
                break
        }
    }

    def delete() {
        def recipienteInstance = Recipiente.get(params.id)
        if (!recipienteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'recipiente.label', default: 'Recipiente'), params.id])
            redirect action: 'list'
            return
        }

        try {
            recipienteInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'recipiente.label', default: 'Recipiente'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'recipiente.label', default: 'Recipiente'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [recipienteInstance: new Recipiente(params)]
                break
            case 'POST':
                def recipienteInstance
                if (params.id) recipienteInstance = Recipiente.get(params.id)
                else recipienteInstance = new Recipiente(params)

                setBaseData(recipienteInstance)
                if (!baseService.save(recipienteInstance)) {
                    render view: 'create', model: [recipienteInstance: recipienteInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'recipiente.label', default: 'recipiente'), recipienteInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['recipiente.id': recipienteInstance.id]

                break
        }
    }
}
