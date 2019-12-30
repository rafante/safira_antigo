package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class SaidaMaterialController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def saidaMaterialService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def saidaMaterialInstanceList = paramsToCriteria(params)
        [saidaMaterialInstanceList: saidaMaterialInstanceList, saidaMaterialInstanceTotal: saidaMaterialInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [saidaMaterialInstance: new SaidaMaterial(params)]
                break
            case 'POST':
                def saidaMaterialInstance = new SaidaMaterial(params)
                setBaseData(saidaMaterialInstance)
                if (!saidaMaterialService.save(saidaMaterialInstance)) {
                    render view: 'create', model: [saidaMaterialInstance: saidaMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'saidaMaterial.label', default: 'SaidaMaterial'), saidaMaterialInstance.id])
                redirect action: 'show', id: saidaMaterialInstance.id
                break
        }
    }

    def show() {
        switch (request.method) {
            case 'GET':
                def saidaMaterialInstance = SaidaMaterial.read(params.id)
                if (!saidaMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'saidaMaterial.label', default: 'SaidaMaterial'), params.id])
                    redirect action: 'list'
                    return
                }

                [saidaMaterialInstance: saidaMaterialInstance]
                break
            case 'POST':
                def saidaMaterialInstance = SaidaMaterial.get(params.id)
                setBaseData(saidaMaterialInstance)
                if (!saidaMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'saidaMaterial.label', default: 'SaidaMaterial'), params.id])
                    redirect action: 'list'
                    return
                }
                if (!validaEfetivada(saidaMaterialInstance)) return

                if (params.version) {
                    def version = params.version.toLong()
                    if (saidaMaterialInstance.version > version) {
                        saidaMaterialInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'saidaMaterial.label', default: 'SaidaMaterial')] as Object[],
                                "Another user has updated this SaidaMaterial while you were editing")
                        render view: 'edit', model: [saidaMaterialInstance: saidaMaterialInstance]
                        return
                    }
                }

                saidaMaterialInstance.status = params.status

                if (!saidaMaterialService.save(saidaMaterialInstance)) {
                    render view: 'edit', model: [saidaMaterialInstance: saidaMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'saidaMaterial.label', default: 'SaidaMaterial'), saidaMaterialInstance.id])
                redirect action: 'show', id: saidaMaterialInstance.id
                break
        }
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def saidaMaterialInstance = SaidaMaterial.read(params.id)
                if (!saidaMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'saidaMaterial.label', default: 'SaidaMaterial'), params.id])
                    redirect action: 'list'
                    return
                }
                if (!validaEfetivada(saidaMaterialInstance)) return

                [saidaMaterialInstance: saidaMaterialInstance]
                break
            case 'POST':
                def saidaMaterialInstance = SaidaMaterial.get(params.id)
                setBaseData(saidaMaterialInstance)
                if (!saidaMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'saidaMaterial.label', default: 'SaidaMaterial'), params.id])
                    redirect action: 'list'
                    return
                }
                if (!validaEfetivada(saidaMaterialInstance)) return

                if (params.version) {
                    def version = params.version.toLong()
                    if (saidaMaterialInstance.version > version) {
                        saidaMaterialInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'saidaMaterial.label', default: 'SaidaMaterial')] as Object[],
                                "Another user has updated this SaidaMaterial while you were editing")
                        render view: 'edit', model: [saidaMaterialInstance: saidaMaterialInstance]
                        return
                    }
                }

                saidaMaterialInstance.properties = params

                if (!saidaMaterialService.save(saidaMaterialInstance)) {
                    render view: 'edit', model: [saidaMaterialInstance: saidaMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'saidaMaterial.label', default: 'SaidaMaterial'), saidaMaterialInstance.id])
                redirect action: 'show', id: saidaMaterialInstance.id
                break
        }
    }

    def delete() {
        def saidaMaterialInstance = SaidaMaterial.get(params.id)
        if (!saidaMaterialInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'saidaMaterial.label', default: 'SaidaMaterial'), params.id])
            redirect action: 'list'
            return
        }

        if (!validaEfetivada(saidaMaterialInstance)) return
        try {
            saidaMaterialInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'saidaMaterial.label', default: 'SaidaMaterial'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'saidaMaterial.label', default: 'SaidaMaterial'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def corrigirSaida() {
        switch (request.method) {
            case 'GET':
                def saidaMaterialInstance = SaidaMaterial.read(params.id)
                if (!saidaMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'saidaMaterial.label', default: 'Saida'), params.id])
                    redirect action: 'list'
                    return
                }
                [saidaMaterialInstance: saidaMaterialInstance]
                break
            case 'POST':
                def saidaMaterialInstance = SaidaMaterial.get(params.id)
                if (!saidaMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'saidaMaterial.label', default: 'Saida'), params.id])
                    redirect action: 'list'
                    return
                }

                saidaMaterialInstance.corrigirSaida(Usuario.get(principal.id), params.motivo)
                if (!saidaMaterialInstance.save(flush: true)) {
                    flash.message = message(code: 'saidaMaterial.mensagens.erroCorrigir')

                    redirect action: 'show', id: saidaMaterialInstance.id
                    return
                }

                redirect action: 'edit', id: saidaMaterialInstance.id
        }
    }

    def validaEfetivada(SaidaMaterial saidaMaterialInstance) {
        if (!saidaMaterialInstance?.canEdit()) {
            flash.message = message(code: 'saidaMaterial.mensagens.saidaEfetivada', args: [message(code: 'saidaMaterial.label', default: 'Saida'), params.id])
            redirect action: 'show', id: saidaMaterialInstance.id

            return false
        }

        return true
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [saidaMaterialInstance: new SaidaMaterial(params)]
                break
            case 'POST':
                def saidaMaterialInstance
                if (params.id) saidaMaterialInstance = SaidaMaterial.get(params.id)
                else saidaMaterialInstance = new SaidaMaterial(params)

                if (!validaEfetivada(saidaMaterialInstance)) return

                setBaseData(saidaMaterialInstance)
                if (!saidaMaterialService.save(saidaMaterialInstance, [validate: false])) {
                    render view: 'create', model: [saidaMaterialInstance: saidaMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'saidaMaterial.label', default: 'saidaMaterial'), saidaMaterialInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['saidaMaterial.id': saidaMaterialInstance.id]

                break
        }
    }
}
