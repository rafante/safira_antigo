package com.br.asgardtecnologia.erp.base

import org.springframework.dao.DataIntegrityViolationException

class MunicipioController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def municipioInstanceList = paramsToCriteria(params)
        [municipioInstanceList: municipioInstanceList, municipioInstanceTotal: municipioInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [municipioInstance: new Municipio(params)]
                break
            case 'POST':
                def municipioInstance = new Municipio(params)
                setBaseData(municipioInstance)
                if (!municipioInstance.save(flush: true)) {
                    render view: 'create', model: [municipioInstance: municipioInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'municipio.label', default: 'Municipio'), municipioInstance.id])
                redirect action: 'show', id: municipioInstance.id
                break
        }
    }

    def show() {
        def municipioInstance = Municipio.read(params.id)
        if (!municipioInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'municipio.label', default: 'Municipio'), params.id])
            redirect action: 'list'
            return
        }

        [municipioInstance: municipioInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def municipioInstance = Municipio.read(params.id)
                if (!municipioInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'municipio.label', default: 'Municipio'), params.id])
                    redirect action: 'list'
                    return
                }

                [municipioInstance: municipioInstance]
                break
            case 'POST':
                def municipioInstance = Municipio.get(params.id)
                setBaseData(municipioInstance)
                if (!municipioInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'municipio.label', default: 'Municipio'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (municipioInstance.version > version) {
                        municipioInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'municipio.label', default: 'Municipio')] as Object[],
                                "Another user has updated this Municipio while you were editing")
                        render view: 'edit', model: [municipioInstance: municipioInstance]
                        return
                    }
                }

                municipioInstance.properties = params

                if (!municipioInstance.save(flush: true)) {
                    render view: 'edit', model: [municipioInstance: municipioInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'municipio.label', default: 'Municipio'), municipioInstance.id])
                redirect action: 'show', id: municipioInstance.id
                break
        }
    }

    def delete() {
        def municipioInstance = Municipio.get(params.id)
        if (!municipioInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'municipio.label', default: 'Municipio'), params.id])
            redirect action: 'list'
            return
        }

        try {
            municipioInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'municipio.label', default: 'Municipio'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'municipio.label', default: 'Municipio'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [municipioInstance: new Municipio(params)]
                break
            case 'POST':
                def municipioInstance
                if (params.id) municipioInstance = Municipio.get(params.id)
                else municipioInstance = new Municipio(params)

                setBaseData(municipioInstance)
                if (!municipioInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [municipioInstance: municipioInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'municipio.label', default: 'municipio'), municipioInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['municipio.id': municipioInstance.id]

                break
        }
    }
}
