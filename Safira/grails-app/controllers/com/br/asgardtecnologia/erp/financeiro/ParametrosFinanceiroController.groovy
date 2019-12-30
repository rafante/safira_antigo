package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.BaseController
import org.springframework.dao.DataIntegrityViolationException

class ParametrosFinanceiroController extends BaseController {
    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        redirect(action: 'edit')
    }

    def show() {
        def parametrosFinanceiroInstance = ParametrosFinanceiro.read(1)
        if (!parametrosFinanceiroInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'parametrosFinanceiro.label', default: 'ParametrosFinanceiro'), params.id])
            redirect action: 'list'
            return
        }

        [parametrosFinanceiroInstance: parametrosFinanceiroInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def parametrosFinanceiroInstance = ParametrosFinanceiro.read(1)
                if(!parametrosFinanceiroInstance) {
                    parametrosFinanceiroInstance = new ParametrosFinanceiro()
                    parametrosFinanceiroInstance.save(flush: true)
                }
                if (!parametrosFinanceiroInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'parametrosFinanceiro.label', default: 'ParametrosFinanceiro'), params.id])
                    redirect action: 'list'
                    return
                }

                [parametrosFinanceiroInstance: parametrosFinanceiroInstance]
                break
            case 'POST':
                def parametrosFinanceiroInstance = ParametrosFinanceiro.get(1)
                setBaseData(parametrosFinanceiroInstance)
                if (!parametrosFinanceiroInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'parametrosFinanceiro.label', default: 'ParametrosFinanceiro'), params.id])
                    redirect action: 'edit'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (parametrosFinanceiroInstance.version > version) {
                        parametrosFinanceiroInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'parametrosFinanceiro.label', default: 'ParametrosFinanceiro')] as Object[],
                                "Another user has updated this ParametrosFinanceiro while you were editing")
                        render view: 'edit', model: [parametrosFinanceiroInstance: parametrosFinanceiroInstance]
                        return
                    }
                }

                parametrosFinanceiroInstance.properties = params

                if (!parametrosFinanceiroInstance.save(flush: true)) {
                    render view: 'edit', model: [parametrosFinanceiroInstance: parametrosFinanceiroInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'parametrosFinanceiro.label', default: 'ParametrosFinanceiro'), parametrosFinanceiroInstance.id])
                redirect action: 'edit', id: parametrosFinanceiroInstance.id
                break
        }
    }

    def delete() {
        def parametrosFinanceiroInstance = ParametrosFinanceiro.get(params.id)
        if (!parametrosFinanceiroInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'parametrosFinanceiro.label', default: 'ParametrosFinanceiro'), params.id])
            redirect action: 'list'
            return
        }

        try {
            parametrosFinanceiroInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'parametrosFinanceiro.label', default: 'ParametrosFinanceiro'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'parametrosFinanceiro.label', default: 'ParametrosFinanceiro'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
