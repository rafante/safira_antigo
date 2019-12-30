package com.br.asgardtecnologia.erp.config

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class PeriodicidadeController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def periodicidadeInstanceList = paramsToCriteria(params)
        [periodicidadeInstanceList: periodicidadeInstanceList, periodicidadeInstanceTotal: periodicidadeInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [periodicidadeInstance: new Periodicidade(params)]
                break
            case 'POST':
                def periodicidadeInstance = new Periodicidade(params)
                setBaseData(periodicidadeInstance)
                if (!periodicidadeInstance.save(flush: true)) {
                    render view: 'create', model: [periodicidadeInstance: periodicidadeInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'periodicidade.label', default: 'Periodicidade'), periodicidadeInstance.id])
                redirect action: 'show', id: periodicidadeInstance.id
                break
        }
    }

    def show() {
        def periodicidadeInstance = Periodicidade.read(params.id)
        if (!periodicidadeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'periodicidade.label', default: 'Periodicidade'), params.id])
            redirect action: 'list'
            return
        }

        [periodicidadeInstance: periodicidadeInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def periodicidadeInstance = Periodicidade.read(params.id)
                if (!periodicidadeInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'periodicidade.label', default: 'Periodicidade'), params.id])
                    redirect action: 'list'
                    return
                }

                [periodicidadeInstance: periodicidadeInstance]
                break
            case 'POST':
                def periodicidadeInstance = Periodicidade.get(params.id)
                setBaseData(periodicidadeInstance)
                if (!periodicidadeInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'periodicidade.label', default: 'Periodicidade'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (periodicidadeInstance.version > version) {
                        periodicidadeInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'periodicidade.label', default: 'Periodicidade')] as Object[],
                                "Another user has updated this Periodicidade while you were editing")
                        render view: 'edit', model: [periodicidadeInstance: periodicidadeInstance]
                        return
                    }
                }

                periodicidadeInstance.properties = params

                if (!periodicidadeInstance.save(flush: true)) {
                    render view: 'edit', model: [periodicidadeInstance: periodicidadeInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'periodicidade.label', default: 'Periodicidade'), periodicidadeInstance.id])
                redirect action: 'show', id: periodicidadeInstance.id
                break
        }
    }

    def delete() {
        def periodicidadeInstance = Periodicidade.get(params.id)
        if (!periodicidadeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'periodicidade.label', default: 'Periodicidade'), params.id])
            redirect action: 'list'
            return
        }

        try {
            periodicidadeInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'periodicidade.label', default: 'Periodicidade'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'periodicidade.label', default: 'Periodicidade'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [periodicidadeInstance: new Periodicidade(params)]
                break
            case 'POST':
                def periodicidadeInstance
                if (params.id) periodicidadeInstance = Periodicidade.get(params.id)
                else periodicidadeInstance = new Periodicidade(params)

                setBaseData(periodicidadeInstance)
                if (!periodicidadeInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [periodicidadeInstance: periodicidadeInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'periodicidade.label', default: 'periodicidade'), periodicidadeInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['periodicidade.id': periodicidadeInstance.id]

                break
        }
    }
}
