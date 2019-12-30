package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.BaseController
import org.springframework.dao.DataIntegrityViolationException

class BancoController extends BaseController{

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def bancoInstanceList = paramsToCriteria(params)
        [bancoInstanceList: bancoInstanceList, bancoInstanceTotal: bancoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [bancoInstance: new Banco(params)]
                break
            case 'POST':
                def bancoInstance = new Banco(params)
                setBaseData(bancoInstance)
                if (!bancoInstance.save(flush: true)) {
                    render view: 'create', model: [bancoInstance: bancoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'banco.label', default: 'Banco'), bancoInstance.id])
                redirect action: 'show', id: bancoInstance.id
                break
        }
    }

    def show() {
        def bancoInstance = Banco.read(params.id)
        if (!bancoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'banco.label', default: 'Banco'), params.id])
            redirect action: 'list'
            return
        }

        [bancoInstance: bancoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def bancoInstance = Banco.read(params.id)
                if (!bancoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'banco.label', default: 'Banco'), params.id])
                    redirect action: 'list'
                    return
                }

                [bancoInstance: bancoInstance]
                break
            case 'POST':
                def bancoInstance = Banco.get(params.id)
                setBaseData(bancoInstance)
                if (!bancoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'banco.label', default: 'Banco'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (bancoInstance.version > version) {
                        bancoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'banco.label', default: 'Banco')] as Object[],
                                "Another user has updated this Banco while you were editing")
                        render view: 'edit', model: [bancoInstance: bancoInstance]
                        return
                    }
                }

                bancoInstance.properties = params

                if (!bancoInstance.save(flush: true)) {
                    render view: 'edit', model: [bancoInstance: bancoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'banco.label', default: 'Banco'), bancoInstance.id])
                redirect action: 'show', id: bancoInstance.id
                break
        }
    }

    def delete() {
        def bancoInstance = Banco.get(params.id)
        if (!bancoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'banco.label', default: 'Banco'), params.id])
            redirect action: 'list'
            return
        }

        try {
            bancoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'banco.label', default: 'Banco'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'banco.label', default: 'Banco'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [bancoInstance: new Banco(params)]
                break
            case 'POST':
                def bancoInstance
                if (params.id) bancoInstance = Banco.get(params.id)
                else bancoInstance = new Banco(params)

                setBaseData(bancoInstance)
                if (!bancoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [bancoInstance: bancoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'banco.label', default: 'banco'), bancoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['banco.id': bancoInstance.id]

                break
        }
    }
}
