package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class NCMController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def NCMInstanceList = paramsToCriteria(params)
        [NCMInstanceList: NCMInstanceList, NCMInstanceTotal: NCMInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [NCMInstance: new NCM(params)]
                break
            case 'POST':
                def NCMInstance = new NCM(params)
                setBaseData(NCMInstance)
                if (!NCMInstance.save(flush: true)) {
                    render view: 'create', model: [NCMInstance: NCMInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'NCM.label', default: 'NCM'), NCMInstance.id])
                redirect action: 'show', id: NCMInstance.id
                break
        }
    }

    def show() {
        def NCMInstance = NCM.read(params.id)
        if (!NCMInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'NCM.label', default: 'NCM'), params.id])
            redirect action: 'list'
            return
        }

        [NCMInstance: NCMInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def NCMInstance = NCM.read(params.id)
                if (!NCMInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'NCM.label', default: 'NCM'), params.id])
                    redirect action: 'list'
                    return
                }

                [NCMInstance: NCMInstance]
                break
            case 'POST':
                def NCMInstance = NCM.get(params.id)
                setBaseData(NCMInstance)
                if (!NCMInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'NCM.label', default: 'NCM'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (NCMInstance.version > version) {
                        NCMInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'NCM.label', default: 'NCM')] as Object[],
                                "Another user has updated this NCM while you were editing")
                        render view: 'edit', model: [NCMInstance: NCMInstance]
                        return
                    }
                }

                NCMInstance.properties = params

                if (!NCMInstance.save(flush: true)) {
                    render view: 'edit', model: [NCMInstance: NCMInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'NCM.label', default: 'NCM'), NCMInstance.id])
                redirect action: 'show', id: NCMInstance.id
                break
        }
    }

    def delete() {
        def NCMInstance = NCM.get(params.id)
        if (!NCMInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'NCM.label', default: 'NCM'), params.id])
            redirect action: 'list'
            return
        }

        try {
            NCMInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'NCM.label', default: 'NCM'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'NCM.label', default: 'NCM'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [NCMInstance: new NCM(params)]
                break
            case 'POST':
                def NCMInstance
                if (params.id) NCMInstance = NCM.get(params.id)
                else NCMInstance = new NCM(params)

                setBaseData(NCMInstance)
                if (!NCMInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [NCMInstance: NCMInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'NCM.label', default: 'NCM'), NCMInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['NCM.id': NCMInstance.id]

                break
        }
    }
}
