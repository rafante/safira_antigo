package com.br.asgardtecnologia.erp.security

import com.br.asgardtecnologia.erp.base.BaseController
import org.springframework.dao.DataIntegrityViolationException

class PerfilController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def perfilInstanceList = paramsToCriteria(params)
        [perfilInstanceList: perfilInstanceList, perfilInstanceTotal: perfilInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [perfilInstance: new Perfil(params)]
                break
            case 'POST':
                def perfilInstance = new Perfil(params)

                if (!perfilInstance.save(flush: true)) {
                    render view: 'create', model: [perfilInstance: perfilInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'perfil.label', default: 'Perfil'), perfilInstance.id])
                redirect action: 'show', id: perfilInstance.id
                break
        }
    }

    def show() {
        def perfilInstance = Perfil.read(params.id)
        if (!perfilInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'perfil.label', default: 'Perfil'), params.id])
            redirect action: 'list'
            return
        }

        [perfilInstance: perfilInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def perfilInstance = Perfil.read(params.id)
                if (!perfilInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'perfil.label', default: 'Perfil'), params.id])
                    redirect action: 'list'
                    return
                }

                [perfilInstance: perfilInstance]
                break
            case 'POST':
                def perfilInstance = Perfil.get(params.id)

                if (!perfilInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'perfil.label', default: 'Perfil'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (perfilInstance.version > version) {
                        perfilInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'perfil.label', default: 'Perfil')] as Object[],
                                "Another user has updated this Perfil while you were editing")
                        render view: 'edit', model: [perfilInstance: perfilInstance]
                        return
                    }
                }

                perfilInstance.properties = params

                if (!perfilInstance.save(flush: true)) {
                    render view: 'edit', model: [perfilInstance: perfilInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'perfil.label', default: 'Perfil'), perfilInstance.id])
                redirect action: 'show', id: perfilInstance.id
                break
        }
    }

    def delete() {
        def perfilInstance = Perfil.get(params.id)
        if (!perfilInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'perfil.label', default: 'Perfil'), params.id])
            redirect action: 'list'
            return
        }

        try {
            perfilInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'perfil.label', default: 'Perfil'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'perfil.label', default: 'Perfil'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [perfilInstance: new Perfil(params)]
                break
            case 'POST':
                def perfilInstance
                if (params.id) perfilInstance = Perfil.get(params.id)
                else perfilInstance = new Perfil(params)

                if (!perfilInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [perfilInstance: perfilInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'perfil.label', default: 'perfil'), perfilInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['perfil.id': perfilInstance.id]

                break
        }
    }
}
