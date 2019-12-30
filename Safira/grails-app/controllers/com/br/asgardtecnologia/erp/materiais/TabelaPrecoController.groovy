package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import com.br.asgardtecnologia.erp.cadastros.TabelaPreco
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class TabelaPrecoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def tabelaPrecoInstanceList = paramsToCriteria(params)
        [tabelaPrecoInstanceList: tabelaPrecoInstanceList, tabelaPrecoInstanceTotal: tabelaPrecoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [tabelaPrecoInstance: new TabelaPreco(params)]
                break
            case 'POST':
                def tabelaPrecoInstance = new TabelaPreco(params)
                setBaseData(tabelaPrecoInstance)
                if (!tabelaPrecoInstance.save(flush: true)) {
                    render view: 'create', model: [tabelaPrecoInstance: tabelaPrecoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'tabelaPreco.label', default: 'TabelaPreco'), tabelaPrecoInstance.id])
                redirect action: 'show', id: tabelaPrecoInstance.id
                break
        }
    }

    def show() {
        def tabelaPrecoInstance = TabelaPreco.read(params.id)
        if (!tabelaPrecoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tabelaPreco.label', default: 'TabelaPreco'), params.id])
            redirect action: 'list'
            return
        }

        [tabelaPrecoInstance: tabelaPrecoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def tabelaPrecoInstance = TabelaPreco.read(params.id)
                if (!tabelaPrecoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'tabelaPreco.label', default: 'TabelaPreco'), params.id])
                    redirect action: 'list'
                    return
                }

                [tabelaPrecoInstance: tabelaPrecoInstance]
                break
            case 'POST':
                def tabelaPrecoInstance = TabelaPreco.get(params.id)
                setBaseData(tabelaPrecoInstance)
                if (!tabelaPrecoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'tabelaPreco.label', default: 'TabelaPreco'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (tabelaPrecoInstance.version > version) {
                        tabelaPrecoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'tabelaPreco.label', default: 'TabelaPreco')] as Object[],
                                "Another user has updated this TabelaPreco while you were editing")
                        render view: 'edit', model: [tabelaPrecoInstance: tabelaPrecoInstance]
                        return
                    }
                }

                tabelaPrecoInstance.properties = params

                if (!tabelaPrecoInstance.save(flush: true)) {
                    render view: 'edit', model: [tabelaPrecoInstance: tabelaPrecoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'tabelaPreco.label', default: 'TabelaPreco'), tabelaPrecoInstance.id])
                redirect action: 'show', id: tabelaPrecoInstance.id
                break
        }
    }

    def delete() {
        def tabelaPrecoInstance = TabelaPreco.get(params.id)
        if (!tabelaPrecoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tabelaPreco.label', default: 'TabelaPreco'), params.id])
            redirect action: 'list'
            return
        }

        try {
            tabelaPrecoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'tabelaPreco.label', default: 'TabelaPreco'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'tabelaPreco.label', default: 'TabelaPreco'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [tabelaPrecoInstance: new TabelaPreco(params)]
                break
            case 'POST':
                def tabelaPrecoInstance
                if (params.id) tabelaPrecoInstance = TabelaPreco.get(params.id)
                else tabelaPrecoInstance = new TabelaPreco(params)

                setBaseData(tabelaPrecoInstance)
                if (!tabelaPrecoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [tabelaPrecoInstance: tabelaPrecoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'tabelaPreco.label', default: 'tabelaPreco'), tabelaPrecoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['tabelaPreco.id': tabelaPrecoInstance.id]

                break
        }
    }
}
