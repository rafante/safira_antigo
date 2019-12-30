package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class CentroCustoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def centroCustoInstanceList = paramsToCriteria(params)
        [centroCustoInstanceList: centroCustoInstanceList, centroCustoInstanceTotal: centroCustoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [centroCustoInstance: new CentroCusto(params)]
                break
            case 'POST':
                def centroCustoInstance = new CentroCusto(params)
                setBaseData(centroCustoInstance)
                if (!centroCustoInstance.save(flush: true)) {
                    render view: 'create', model: [centroCustoInstance: centroCustoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'centroCusto.label', default: 'CentroCusto'), centroCustoInstance.id])
                redirect action: 'show', id: centroCustoInstance.id
                break
        }
    }

    def show() {
        def centroCustoInstance = CentroCusto.read(params.id)
        if (!centroCustoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'centroCusto.label', default: 'CentroCusto'), params.id])
            redirect action: 'list'
            return
        }

        [centroCustoInstance: centroCustoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def centroCustoInstance = CentroCusto.read(params.id)
                if (!centroCustoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'centroCusto.label', default: 'CentroCusto'), params.id])
                    redirect action: 'list'
                    return
                }

                [centroCustoInstance: centroCustoInstance]
                break
            case 'POST':
                def centroCustoInstance = CentroCusto.get(params.id)
                setBaseData(centroCustoInstance)
                if (!centroCustoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'centroCusto.label', default: 'CentroCusto'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (centroCustoInstance.version > version) {
                        centroCustoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'centroCusto.label', default: 'CentroCusto')] as Object[],
                                "Another user has updated this CentroCusto while you were editing")
                        render view: 'edit', model: [centroCustoInstance: centroCustoInstance]
                        return
                    }
                }

                centroCustoInstance.properties = params

                if (!centroCustoInstance.save(flush: true)) {
                    render view: 'edit', model: [centroCustoInstance: centroCustoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'centroCusto.label', default: 'CentroCusto'), centroCustoInstance.id])
                redirect action: 'show', id: centroCustoInstance.id
                break
        }
    }

    def delete() {
        def centroCustoInstance = CentroCusto.get(params.id)
        if (!centroCustoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'centroCusto.label', default: 'CentroCusto'), params.id])
            redirect action: 'list'
            return
        }

        try {
            centroCustoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'centroCusto.label', default: 'CentroCusto'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'centroCusto.label', default: 'CentroCusto'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [centroCustoInstance: new CentroCusto(params)]
                break
            case 'POST':
                def centroCustoInstance
                if (params.id) centroCustoInstance = CentroCusto.get(params.id)
                else centroCustoInstance = new CentroCusto(params)

                setBaseData(centroCustoInstance)
                if (!centroCustoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [centroCustoInstance: centroCustoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'centroCusto.label', default: 'centroCusto'), centroCustoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['centroCusto.id': centroCustoInstance.id]

                break
        }
    }
}
