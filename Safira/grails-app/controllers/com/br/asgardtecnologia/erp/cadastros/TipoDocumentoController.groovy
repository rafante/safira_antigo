package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class TipoDocumentoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def tipoDocumentoInstanceList = paramsToCriteria(params)
        [tipoDocumentoInstanceList: tipoDocumentoInstanceList, tipoDocumentoInstanceListTotal: tipoDocumentoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [tipoDocumentoInstance: new TipoDocumento(params)]
                break
            case 'POST':
                def tipoDocumentoInstance = new TipoDocumento(params)
                setBaseData(tipoDocumentoInstance)
                if (!tipoDocumentoInstance.save(flush: true)) {
                    render view: 'create', model: [tipoDocumentoInstance: tipoDocumentoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'tipoDocumento.label', default: 'TipoDocumento'), tipoDocumentoInstance.id])
                redirect action: 'show', id: tipoDocumentoInstance.id
                break
        }
    }

    def show() {
        def tipoDocumentoInstance = TipoDocumento.read(params.id)
        if (!tipoDocumentoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoDocumento.label', default: 'TipoDocumento'), params.id])
            redirect action: 'list'
            return
        }

        [tipoDocumentoInstance: tipoDocumentoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def tipoDocumentoInstance = TipoDocumento.read(params.id)
                if (!tipoDocumentoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoDocumento.label', default: 'TipoDocumento'), params.id])
                    redirect action: 'list'
                    return
                }

                [tipoDocumentoInstance: tipoDocumentoInstance]
                break
            case 'POST':
                def tipoDocumentoInstance = TipoDocumento.get(params.id)
                setBaseData(tipoDocumentoInstance)
                if (!tipoDocumentoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoDocumento.label', default: 'TipoDocumento'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (tipoDocumentoInstance.version > version) {
                        tipoDocumentoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'tipoDocumento.label', default: 'TipoDocumento')] as Object[],
                                "Another user has updated this TipoDocumento while you were editing")
                        render view: 'edit', model: [tipoDocumentoInstance: tipoDocumentoInstance]
                        return
                    }
                }

                tipoDocumentoInstance.properties = params

                if (!tipoDocumentoInstance.save(flush: true)) {
                    render view: 'edit', model: [tipoDocumentoInstance: tipoDocumentoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'tipoDocumento.label', default: 'TipoDocumento'), tipoDocumentoInstance.id])
                redirect action: 'show', id: tipoDocumentoInstance.id
                break
        }
    }

    def delete() {
        def tipoDocumentoInstance = tipoDocumentoInstance.get(params.id)
        if (!tipoDocumentoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoDocumento.label', default: 'TipoDocumento'), params.id])
            redirect action: 'list'
            return
        }

        try {
            tipoDocumentoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'tipoDocumento.label', default: 'TipoDocumento'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'tipoDocumento.label', default: 'TipoDocumento'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [tipoDocumentoInstance: new TipoDocumento(params)]
                break
            case 'POST':
                def tipoDocumentoInstance
                if (params.id) tipoDocumentoInstance = TipoDocumento.get(params.id)
                else tipoDocumentoInstance = new TipoDocumento(params)

                setBaseData(tipoDocumentoInstance)
                if (!tipoDocumentoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [tipoDocumentoInstance: tipoDocumentoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'tipoDocumento.label', default: 'TipoDocumento'), tipoDocumentoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['tipoDocumento.id': tipoDocumentoInstance.id]

                break
        }
    }
}
