package com.br.asgardtecnologia.erp.base

import org.springframework.dao.DataIntegrityViolationException

class PaisController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def paisInstanceList = paramsToCriteria(params)
        [paisInstanceList: paisInstanceList, paisInstanceTotal: paisInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [paisInstance: new Pais(params)]
                break
            case 'POST':
                def paisInstance = new Pais(params)
                setBaseData(paisInstance)
                if (!paisInstance.save(flush: true)) {
                    render view: 'create', model: [paisInstance: paisInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'pais.label', default: 'Pais'), paisInstance.id])
                redirect action: 'show', id: paisInstance.id
                break
        }
    }

    def show() {
        def paisInstance = Pais.read(params.id)

        if (!paisInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'pais.label', default: 'Pais'), params.id])
            redirect action: 'list'
            return
        }

        [paisInstance: paisInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def paisInstance = Pais.read(params.id)
                if (!paisInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'pais.label', default: 'Pais'), params.id])
                    redirect action: 'list'
                    return
                }

                [paisInstance: paisInstance]
                break
            case 'POST':
                def paisInstance = Pais.get(params.id)
                setBaseData(paisInstance)
                if (!paisInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'pais.label', default: 'Pais'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (paisInstance.version > version) {
                        paisInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'pais.label', default: 'Pais')] as Object[],
                                "Another user has updated this Pais while you were editing")
                        render view: 'edit', model: [paisInstance: paisInstance]
                        return
                    }
                }

                paisInstance.properties = params

                if (!paisInstance.save(flush: true)) {
                    render view: 'edit', model: [paisInstance: paisInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'pais.label', default: 'Pais'), paisInstance.id])
                redirect action: 'show', id: paisInstance.id
                break
        }
    }

    def delete() {
        def paisInstance = Pais.get(params.id)
        if (!paisInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'pais.label', default: 'Pais'), params.id])
            redirect action: 'list'
            return
        }

        try {
            paisInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'pais.label', default: 'Pais'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'pais.label', default: 'Pais'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [paisInstance: new Pais(params)]
                break
            case 'POST':
                def paisInstance
                if (params.id) paisInstance = Pais.get(params.id)
                else paisInstance = new Pais(params)

                setBaseData(paisInstance)
                if (!paisInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [paisInstance: paisInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'pais.label', default: 'pais'), paisInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['pais.id': paisInstance.id]

                break
        }
    }
}
