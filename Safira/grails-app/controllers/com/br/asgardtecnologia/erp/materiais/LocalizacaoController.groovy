package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class LocalizacaoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def localizacaoInstanceList = paramsToCriteria(params)
        [localizacaoInstanceList: localizacaoInstanceList, localizacaoInstanceTotal: localizacaoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [localizacaoInstance: new Localizacao(params)]
                break
            case 'POST':
                def localizacaoInstance = new Localizacao(params)
                setBaseData(localizacaoInstance)
                if (!localizacaoInstance.save(flush: true)) {
                    render view: 'create', model: [localizacaoInstance: localizacaoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'localizacao.label', default: 'Localizacao'), localizacaoInstance.id])
                redirect action: 'show', id: localizacaoInstance.id
                break
        }
    }

    def show() {
        def localizacaoInstance = Localizacao.read(params.id)
        if (!localizacaoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'localizacao.label', default: 'Localizacao'), params.id])
            redirect action: 'list'
            return
        }

        [localizacaoInstance: localizacaoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def localizacaoInstance = Localizacao.read(params.id)
                if (!localizacaoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'localizacao.label', default: 'Localizacao'), params.id])
                    redirect action: 'list'
                    return
                }

                [localizacaoInstance: localizacaoInstance]
                break
            case 'POST':
                def localizacaoInstance = Localizacao.get(params.id)
                setBaseData(localizacaoInstance)
                if (!localizacaoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'localizacao.label', default: 'Localizacao'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (localizacaoInstance.version > version) {
                        localizacaoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'localizacao.label', default: 'Localizacao')] as Object[],
                                "Another user has updated this Localizacao while you were editing")
                        render view: 'edit', model: [localizacaoInstance: localizacaoInstance]
                        return
                    }
                }

                localizacaoInstance.properties = params

                if (!localizacaoInstance.save(flush: true)) {
                    render view: 'edit', model: [localizacaoInstance: localizacaoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'localizacao.label', default: 'Localizacao'), localizacaoInstance.id])
                redirect action: 'show', id: localizacaoInstance.id
                break
        }
    }

    def delete() {
        def localizacaoInstance = Localizacao.get(params.id)
        if (!localizacaoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'localizacao.label', default: 'Localizacao'), params.id])
            redirect action: 'list'
            return
        }

        try {
            localizacaoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'localizacao.label', default: 'Localizacao'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'localizacao.label', default: 'Localizacao'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [localizacaoInstance: new Localizacao(params)]
                break
            case 'POST':
                def localizacaoInstance
                if (params.id) localizacaoInstance = Localizacao.get(params.id)
                else localizacaoInstance = new Localizacao(params)

                setBaseData(localizacaoInstance)
                if (!localizacaoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [localizacaoInstance: localizacaoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'localizacao.label', default: 'localizacao'), localizacaoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['localizacao.id': localizacaoInstance.id]

                break
        }
    }
}
