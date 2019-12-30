package com.br.asgardtecnologia.erp.security

import com.br.asgardtecnologia.erp.base.BaseController
import org.springframework.dao.DataIntegrityViolationException

class UsuarioController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def usuarioInstanceList = paramsToCriteria(params)
        [usuarioInstanceList: usuarioInstanceList, usuarioInstanceTotal: usuarioInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [usuarioInstance: new Usuario(params)]
                break
            case 'POST':
                def usuarioInstance = new Usuario(params)
                if (!usuarioInstance.save(flush: true)) {
                    render view: 'create', model: [usuarioInstance: usuarioInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'usuario.label', default: 'Usuario'), usuarioInstance.id])
                redirect action: 'show', id: usuarioInstance.id
                break
        }
    }

    def show() {
        def usuarioInstance = Usuario.read(params.id)
        if (!usuarioInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect action: 'list'
            return
        }

        [usuarioInstance: usuarioInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def usuarioInstance = Usuario.read(params.id)
                if (!usuarioInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
                    redirect action: 'list'
                    return
                }

                [usuarioInstance: usuarioInstance]
                break
            case 'POST':
                def usuarioInstance = Usuario.get(params.id)
                if (!usuarioInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (usuarioInstance.version > version) {
                        usuarioInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'usuario.label', default: 'Usuario')] as Object[],
                                "Another user has updated this Usuario while you were editing")
                        render view: 'edit', model: [usuarioInstance: usuarioInstance]
                        return
                    }
                }

                usuarioInstance.properties = params

                if (!usuarioInstance.save(flush: true)) {
                    render view: 'edit', model: [usuarioInstance: usuarioInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'usuario.label', default: 'Usuario'), usuarioInstance.id])
                redirect action: 'show', id: usuarioInstance.id
                break
        }
    }

    def delete() {
        def usuarioInstance = Usuario.get(params.id)
        if (!usuarioInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect action: 'list'
            return
        }

        try {
            usuarioInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [usuarioInstance: new Usuario(params)]
                break
            case 'POST':
                def usuarioInstance
                if (params.id) usuarioInstance = Usuario.get(params.id)
                else usuarioInstance = new Usuario(params)
                if (!usuarioInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [usuarioInstance: usuarioInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'usuario.label', default: 'usuario'), usuarioInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['usuario.id': usuarioInstance.id]

                break
        }
    }
}
