package com.br.asgardtecnologia.erp.ev
import com.br.asgardtecnologia.erp.base.BaseController
import com.br.asgardtecnologia.erp.financeiro.ContaCorrente
import com.br.asgardtecnologia.erp.financeiro.PlanoContas

class ParametrosEVController extends BaseController {

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
                def parametrosEVInstance = ParametrosEV.get(1)
                if (!parametrosEVInstance) {
                    parametrosEVInstance = new ParametrosEV()
                    parametrosEVInstance.id = 1
                    parametrosEVInstance.planoContas = PlanoContas.get(1)
                    parametrosEVInstance.contaCorrente = ContaCorrente.get(1)
                    parametrosEVInstance.save(flush: true)
                }

                [parametrosEVInstance: parametrosEVInstance, hiddenActionButtons: hiddenActionButtons]
                break
            case 'POST':
                def parametrosEVInstance = ParametrosEV.get(1)
                setBaseData(parametrosEVInstance)
                if (!parametrosEVInstance) {
                    parametrosEVInstance = new ParametrosEV()
                    parametrosEVInstance.id = 1
                    parametrosEVInstance.save(flush: true)
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (parametrosEVInstance.version > version) {
                        parametrosEVInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'parametrosEV.label', default: 'ParametrosEV')] as Object[],
                                "Another user has updated this ParametrosEV while you were editing")
                        render view: 'edit', model: [parametrosEVInstance: parametrosEVInstance]
                        return
                    }
                }

                parametrosEVInstance.properties = params

                if (!parametrosEVInstance.save(flush: true)) {
                    render view: 'edit', model: [parametrosEVInstance: parametrosEVInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'parametrosEV.label', default: 'ParametrosEV'), parametrosEVInstance.id])
                redirect action: 'edit', id: parametrosEVInstance.id

                break
        }
    }

}