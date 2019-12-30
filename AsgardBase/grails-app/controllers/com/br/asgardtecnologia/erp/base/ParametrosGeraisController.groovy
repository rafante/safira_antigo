package com.br.asgardtecnologia.erp.base

class ParametrosGeraisController extends BaseController {

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
                def parametrosGeraisInstance = ParametrosGerais.get(1)
                if (!parametrosGeraisInstance) {
                    parametrosGeraisInstance = new ParametrosGerais()
                    parametrosGeraisInstance.id = 1
                    parametrosGeraisInstance.save(flush: true)
                }

                [parametrosGeraisInstance: parametrosGeraisInstance, hiddenActionButtons: hiddenActionButtons]
                break
            case 'POST':
                def parametrosGeraisInstance = ParametrosGerais.get(1)
                setBaseData(parametrosGeraisInstance)
                if (!parametrosGeraisInstance) {
                    parametrosGeraisInstance = new ParametrosGerais()
                    parametrosGeraisInstance.id = 1
                    parametrosGeraisInstance.save(flush: true)
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (parametrosGeraisInstance.version > version) {
                        parametrosGeraisInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'parametrosGerais.label', default: 'ParametrosGerais')] as Object[],
                                "Another user has updated this ParametrosGerais while you were editing")
                        render view: 'edit', model: [parametrosGeraisInstance: parametrosGeraisInstance]
                        return
                    }
                }

                parametrosGeraisInstance.properties = params

                if (!parametrosGeraisInstance.save(flush: true)) {
                    render view: 'edit', model: [parametrosGeraisInstance: parametrosGeraisInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'parametrosGerais.label', default: 'ParametrosGerais'), parametrosGeraisInstance.id])
                redirect action: 'edit', id: parametrosGeraisInstance.id

                break
        }
    }

}