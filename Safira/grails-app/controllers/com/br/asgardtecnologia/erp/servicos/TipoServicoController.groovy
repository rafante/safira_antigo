package com.br.asgardtecnologia.erp.servicos

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class TipoServicoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def tipoServicoInstanceList = paramsToCriteria(params)
        [tipoServicoInstanceList: tipoServicoInstanceList, tipoServicoInstanceTotal: tipoServicoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [tipoServicoInstance: new TipoServico(params)]
                break
            case 'POST':
                def tipoServicoInstance = new TipoServico(params)
                setBaseData(tipoServicoInstance)
                if (!tipoServicoInstance.save(flush: true)) {
                    render view: 'create', model: [tipoServicoInstance: tipoServicoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'tipoServico.label', default: 'TipoServico'), tipoServicoInstance.id])
                redirect action: 'show', id: tipoServicoInstance.id
                break
        }
    }

    def show() {
        def tipoServicoInstance = TipoServico.read(params.id)
        if (!tipoServicoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoServico.label', default: 'TipoServico'), params.id])
            redirect action: 'list'
            return
        }

        [tipoServicoInstance: tipoServicoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def tipoServicoInstance = TipoServico.read(params.id)
                if (!tipoServicoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoServico.label', default: 'TipoServico'), params.id])
                    redirect action: 'list'
                    return
                }

                [tipoServicoInstance: tipoServicoInstance]
                break
            case 'POST':
                def tipoServicoInstance = TipoServico.get(params.id)
                setBaseData(tipoServicoInstance)
                if (!tipoServicoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoServico.label', default: 'TipoServico'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (tipoServicoInstance.version > version) {
                        tipoServicoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'tipoServico.label', default: 'TipoServico')] as Object[],
                                "Another user has updated this TipoServico while you were editing")
                        render view: 'edit', model: [tipoServicoInstance: tipoServicoInstance]
                        return
                    }
                }

                tipoServicoInstance.properties = params

                if (!tipoServicoInstance.save(flush: true)) {
                    render view: 'edit', model: [tipoServicoInstance: tipoServicoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'tipoServico.label', default: 'TipoServico'), tipoServicoInstance.id])
                redirect action: 'show', id: tipoServicoInstance.id
                break
        }
    }

    def delete() {
        def tipoServicoInstance = TipoServico.get(params.id)
        if (!tipoServicoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoServico.label', default: 'TipoServico'), params.id])
            redirect action: 'list'
            return
        }

        try {
            tipoServicoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'tipoServico.label', default: 'TipoServico'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'tipoServico.label', default: 'TipoServico'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [tipoServicoInstance: new TipoServico(params)]
                break
            case 'POST':
                def tipoServicoInstance
                if (params.id) tipoServicoInstance = TipoServico.get(params.id)
                else tipoServicoInstance = new TipoServico(params)

                setBaseData(tipoServicoInstance)
                if (!tipoServicoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [tipoServicoInstance: tipoServicoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'tipoServico.label', default: 'tipoServico'), tipoServicoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['tipoServico.id': tipoServicoInstance.id]

                break
        }
    }
}
