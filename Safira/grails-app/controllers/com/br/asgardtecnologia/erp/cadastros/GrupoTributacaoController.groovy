package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class GrupoTributacaoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]
    
    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def grupoTributacaoInstanceList = paramsToCriteria(params)
        [grupoTributacaoInstanceList: grupoTributacaoInstanceList, grupoTributacaoInstanceTotal: grupoTributacaoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [grupoTributacaoInstance: new GrupoTributacao(params)]
                break
            case 'POST':
                def grupoTributacaoInstance = new GrupoTributacao(params)
                setBaseData(grupoTributacaoInstance)
                if (!grupoTributacaoInstance.save(flush: true)) {
                    render view: 'create', model: [grupoTributacaoInstance: grupoTributacaoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'grupoTributacao.label', default: 'GrupoTributacao'), grupoTributacaoInstance.id])
                redirect action: 'show', id: grupoTributacaoInstance.id
                break
        }
    }

    def show() {
        def grupoTributacaoInstance = GrupoTributacao.read(params.id)
        if (!grupoTributacaoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'grupoTributacao.label', default: 'GrupoTributacao'), params.id])
            redirect action: 'list'
            return
        }

        [grupoTributacaoInstance: grupoTributacaoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def grupoTributacaoInstance = GrupoTributacao.read(params.id)
                if (!grupoTributacaoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'grupoTributacao.label', default: 'GrupoTributacao'), params.id])
                    redirect action: 'list'
                    return
                }

                [grupoTributacaoInstance: grupoTributacaoInstance]
                break
            case 'POST':
                def grupoTributacaoInstance = GrupoTributacao.get(params.id)
                setBaseData(grupoTributacaoInstance)
                if (!grupoTributacaoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'grupoTributacao.label', default: 'GrupoTributacao'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (grupoTributacaoInstance.version > version) {
                        grupoTributacaoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'grupoTributacao.label', default: 'GrupoTributacao')] as Object[],
                                "Another user has updated this GrupoTributacao while you were editing")
                        render view: 'edit', model: [grupoTributacaoInstance: grupoTributacaoInstance]
                        return
                    }
                }

                grupoTributacaoInstance.properties = params

                if (!grupoTributacaoInstance.save(flush: true)) {
                    render view: 'edit', model: [grupoTributacaoInstance: grupoTributacaoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'grupoTributacao.label', default: 'GrupoTributacao'), grupoTributacaoInstance.id])
                redirect action: 'show', id: grupoTributacaoInstance.id
                break
        }
    }

    def delete() {
        def grupoTributacaoInstance = GrupoTributacao.get(params.id)
        if (!grupoTributacaoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'grupoTributacao.label', default: 'GrupoTributacao'), params.id])
            redirect action: 'list'
            return
        }

        try {
            grupoTributacaoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'grupoTributacao.label', default: 'GrupoTributacao'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'grupoTributacao.label', default: 'GrupoTributacao'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [grupoTributacaoInstance: new GrupoTributacao(params)]
                break
            case 'POST':
                def grupoTributacaoInstance
                if (params.id) grupoTributacaoInstance = GrupoTributacao.get(params.id)
                else grupoTributacaoInstance = new GrupoTributacao(params)

                setBaseData(grupoTributacaoInstance)
                if (!grupoTributacaoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [grupoTributacaoInstance: grupoTributacaoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'grupoTributacao.label', default: 'grupoTributacao'), grupoTributacaoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['grupoTributacao.id': grupoTributacaoInstance.id]

                break
        }
    }
}
