package com.br.asgardtecnologia.erp.base

import org.springframework.dao.DataIntegrityViolationException

class EnderecoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def enderecoInstanceList = paramsToCriteria(params)
        [enderecoInstanceList: enderecoInstanceList, enderecoInstanceTotal: enderecoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [enderecoInstance: new Endereco(params)]
                break
            case 'POST':
                def enderecoInstance = new Endereco(params)
                setBaseData(enderecoInstance)
                if (!enderecoInstance.save(flush: true)) {
                    render view: 'create', model: [enderecoInstance: enderecoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'endereco.label', default: 'Endereco'), enderecoInstance.id])
                redirect action: 'show', id: enderecoInstance.id
                break
        }
    }

    def show() {
        def enderecoInstance = Endereco.read(params.id)
        if (!enderecoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'endereco.label', default: 'Endereco'), params.id])
            redirect action: 'list'
            return
        }

        [enderecoInstance: enderecoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def enderecoInstance = Endereco.read(params.id)
                if (!enderecoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'endereco.label', default: 'Endereco'), params.id])
                    redirect action: 'list'
                    return
                }

                [enderecoInstance: enderecoInstance]
                break
            case 'POST':
                def enderecoInstance = Endereco.get(params.id)
                setBaseData(enderecoInstance)
                if (!enderecoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'endereco.label', default: 'Endereco'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (enderecoInstance.version > version) {
                        enderecoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'endereco.label', default: 'Endereco')] as Object[],
                                "Another user has updated this Endereco while you were editing")
                        render view: 'edit', model: [enderecoInstance: enderecoInstance]
                        return
                    }
                }

                enderecoInstance.properties = params

                if (!enderecoInstance.save(flush: true)) {
                    render view: 'edit', model: [enderecoInstance: enderecoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'endereco.label', default: 'Endereco'), enderecoInstance.id])
                redirect action: 'show', id: enderecoInstance.id
                break
        }
    }

    def delete() {
        def enderecoInstance = Endereco.get(params.id)
        if (!enderecoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'endereco.label', default: 'Endereco'), params.id])
            redirect action: 'list'
            return
        }

        try {
            enderecoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'endereco.label', default: 'Endereco'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'endereco.label', default: 'Endereco'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [enderecoInstance: new Endereco(params)]
                break
            case 'POST':
                def enderecoInstance
                if (params.id) enderecoInstance = Endereco.get(params.id)
                else enderecoInstance = new Endereco(params)

                setBaseData(enderecoInstance)
                if (!enderecoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [enderecoInstance: enderecoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'endereco.label', default: 'endereco'), enderecoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['endereco.id': enderecoInstance.id]

                break
        }
    }

}
