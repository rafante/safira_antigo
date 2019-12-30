package com.br.asgardtecnologia.erp.base

import org.springframework.dao.DataIntegrityViolationException

class EstadoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def estadoInstanceList = paramsToCriteria(params)
        [estadoInstanceList: estadoInstanceList, estadoInstanceTotal: estadoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [estadoInstance: new Estado(params)]
                break
            case 'POST':
                def estadoInstance = new Estado(params)
                setBaseData(estadoInstance)
                if (!estadoInstance.save(flush: true)) {
                    render view: 'create', model: [estadoInstance: estadoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'estado.label', default: 'Estado'), estadoInstance.id])
                redirect action: 'show', id: estadoInstance.id
                break
        }
    }

    def show() {
        def estadoInstance = Estado.read(params.id)
        if (!estadoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'estado.label', default: 'Estado'), params.id])
            redirect action: 'list'
            return
        }

        [estadoInstance: estadoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def estadoInstance = Estado.read(params.id)
                if (!estadoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'estado.label', default: 'Estado'), params.id])
                    redirect action: 'list'
                    return
                }

                [estadoInstance: estadoInstance]
                break
            case 'POST':
                def estadoInstance = Estado.get(params.id)
                setBaseData(estadoInstance)
                if (!estadoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'estado.label', default: 'Estado'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (estadoInstance.version > version) {
                        estadoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'estado.label', default: 'Estado')] as Object[],
                                "Another user has updated this Estado while you were editing")
                        render view: 'edit', model: [estadoInstance: estadoInstance]
                        return
                    }
                }

                estadoInstance.properties = params

                if (!estadoInstance.save(flush: true)) {
                    render view: 'edit', model: [estadoInstance: estadoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'estado.label', default: 'Estado'), estadoInstance.id])
                redirect action: 'show', id: estadoInstance.id
                break
        }
    }

    def delete() {
        def estadoInstance = Estado.get(params.id)
        if (!estadoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'estado.label', default: 'Estado'), params.id])
            redirect action: 'list'
            return
        }

        try {
            estadoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'estado.label', default: 'Estado'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'estado.label', default: 'Estado'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [estadoInstance: new Estado(params)]
                break
            case 'POST':
                def estadoInstance
                if (params.id) estadoInstance = Estado.get(params.id)
                else estadoInstance = new Estado(params)

                setBaseData(estadoInstance)
                if (!estadoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [estadoInstance: estadoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'estado.label', default: 'estado'), estadoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['estado.id': estadoInstance.id]

                break
        }
    }
}
