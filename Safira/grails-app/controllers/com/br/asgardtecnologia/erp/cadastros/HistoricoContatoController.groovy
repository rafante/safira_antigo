package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class HistoricoContatoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    static TAB_HIST = "tab3"

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def historicoContatoInstanceList = paramsToCriteria(params)
        [historicoContatoInstanceList: historicoContatoInstanceList, historicoContatoInstanceTotal: historicoContatoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [historicoContatoInstance: new HistoricoContato(params)]
                break
            case 'POST':
                def historicoContatoInstance = new HistoricoContato(params)
                setBaseData(historicoContatoInstance)
                if (!historicoContatoInstance.save(flush: true)) {
                    render view: 'create', model: [historicoContatoInstance: historicoContatoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'historicoContato.label', default: 'HistoricoContato'), historicoContatoInstance.id])
                redirect controller: 'parceiroNegocios', action: 'edit', id: historicoContatoInstance.parceiroNegocios.id, fragment: TAB_HIST
                break
        }
    }

    def show() {
        def historicoContatoInstance = HistoricoContato.read(params.id)
        if (!historicoContatoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'historicoContato.label', default: 'HistoricoContato'), params.id])
            redirect controller: 'parceiroNegocios', action: 'edit', id: historicoContatoInstance.parceiroNegocios.id, fragment: TAB_HIST
            return
        }

        [historicoContatoInstance: historicoContatoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def historicoContatoInstance = HistoricoContato.read(params.id)
                if (!historicoContatoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'historicoContato.label', default: 'HistoricoContato'), params.id])
                    redirect controller: 'parceiroNegocios', action: 'edit', id: historicoContatoInstance.parceiroNegocios.id, fragment: TAB_HIST
                    return
                }

                [historicoContatoInstance: historicoContatoInstance]
                break
            case 'POST':
                def historicoContatoInstance = HistoricoContato.get(params.id)
                setBaseData(historicoContatoInstance)
                if (!historicoContatoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'historicoContato.label', default: 'HistoricoContato'), params.id])
                    redirect controller: 'parceiroNegocios', action: 'edit', id: historicoContatoInstance.parceiroNegocios.id, fragment: TAB_HIST
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (historicoContatoInstance.version > version) {
                        historicoContatoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'historicoContato.label', default: 'HistoricoContato')] as Object[],
                                "Another user has updated this HistoricoContato while you were editing")
                        render view: 'edit', model: [historicoContatoInstance: historicoContatoInstance]
                        return
                    }
                }

                historicoContatoInstance.properties = params

                if (!historicoContatoInstance.save(flush: true)) {
                    render view: 'edit', model: [historicoContatoInstance: historicoContatoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'historicoContato.label', default: 'HistoricoContato'), historicoContatoInstance.id])
                redirect controller: 'parceiroNegocios', action: 'edit', id: historicoContatoInstance.parceiroNegocios.id, fragment: TAB_HIST
                break
        }
    }

    def delete() {
        def historicoContatoInstance = HistoricoContato.get(params.id)
        if (!historicoContatoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'historicoContato.label', default: 'HistoricoContato'), params.id])
            redirect controller: 'parceiroNegocios', action: 'edit', id: historicoContatoInstance.parceiroNegocios.id, fragment: TAB_HIST
            return
        }

        try {
            historicoContatoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'historicoContato.label', default: 'HistoricoContato'), params.id])
            redirect controller: 'parceiroNegocios', action: 'edit', id: historicoContatoInstance.parceiroNegocios.id, fragment: TAB_HIST
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'historicoContato.label', default: 'HistoricoContato'), params.id])
            redirect controller: 'parceiroNegocios', action: 'edit', id: historicoContatoInstance.parceiroNegocios.id, fragment: TAB_HIST
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [historicoContatoInstance: new HistoricoContato(params)]
                break
            case 'POST':
                def historicoContatoInstance
                if (params.id) historicoContatoInstance = HistoricoContato.get(params.id)
                else historicoContatoInstance = new HistoricoContato(params)

                setBaseData(historicoContatoInstance)
                if (!historicoContatoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [historicoContatoInstance: historicoContatoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'historicoContato.label', default: 'historicoContato'), historicoContatoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['historicoContato.id': historicoContatoInstance.id]

                break
        }
    }
}
