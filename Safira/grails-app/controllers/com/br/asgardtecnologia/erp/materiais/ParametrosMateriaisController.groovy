package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured

class ParametrosMateriaisController extends BaseController {

    def hiddenActionButtons = ['create', 'show', 'delete']

    def index() {
        redirect action: 'edit', id: 1
    }

    def list() {
        redirect action: 'edit', id: 1
    }

    def create() {
        redirect action: 'edit', id: 1
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def parametrosMateriaisInstance = ParametrosMateriais.get(1)
                if (!parametrosMateriaisInstance) {
                    parametrosMateriaisInstance = new ParametrosMateriais()
                    parametrosMateriaisInstance.id = 1
                    parametrosMateriaisInstance.save(flush: true)
                }

                [parametrosMateriaisInstance: parametrosMateriaisInstance, hiddenActionButtons: hiddenActionButtons]
                break
            case 'POST':
                def parametrosMateriaisInstance = ParametrosMateriais.get(1)
                setBaseData(parametrosMateriaisInstance)
                if (!parametrosMateriaisInstance) {
                    parametrosMateriaisInstance = new ParametrosMateriais()
                    parametrosMateriaisInstance.id = 1
                    parametrosMateriaisInstance.save(flush: true)
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (parametrosMateriaisInstance.version > version) {
                        parametrosMateriaisInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'parametrosMateriais.label', default: 'ParametrosMateriais')] as Object[],
                                "Another user has updated this ParametrosMateriais while you were editing")
                        render view: 'edit', model: [parametrosMateriaisInstance: parametrosMateriaisInstance]
                        return
                    }
                }

                parametrosMateriaisInstance.properties = params

                if (!parametrosMateriaisInstance.save(flush: true)) {
                    render view: 'edit', model: [parametrosMateriaisInstance: parametrosMateriaisInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'parametrosMateriais.label', default: 'ParametrosMateriais'), parametrosMateriaisInstance.id])
                redirect action: 'edit', id: parametrosMateriaisInstance.id

                break
        }
    }

}