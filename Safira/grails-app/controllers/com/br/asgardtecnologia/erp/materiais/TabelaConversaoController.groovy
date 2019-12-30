package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class TabelaConversaoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    static TAB_CONV_UNID = "tab_conv_unid"

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def tabelaConversaoInstanceList = paramsToCriteria(params)
        [tabelaConversaoInstanceList: tabelaConversaoInstanceList, tabelaConversaoInstanceTotal: tabelaConversaoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [tabelaConversaoInstance: new TabelaConversao(params)]
                break
            case 'POST':
                def tabelaConversaoInstance = new TabelaConversao(params)
                setBaseData(tabelaConversaoInstance)
                if (!tabelaConversaoInstance.save(flush: true)) {
                    render view: 'create', model: [tabelaConversaoInstance: tabelaConversaoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'tabelaConversao.label', default: 'TabelaConversao'), tabelaConversaoInstance.id])
                redirect controller: 'material', action: 'edit', id: tabelaConversaoInstance.material.id, fragment: TAB_CONV_UNID
                break
        }
    }

    def show() {
        def tabelaConversaoInstance = TabelaConversao.read(params.id)
        if (!tabelaConversaoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tabelaConversao.label', default: 'TabelaConversao'), params.id])
            redirect controller: 'material', action: 'edit', id: tabelaConversaoInstance.material.id, fragment: TAB_CONV_UNID
            return
        }

        [tabelaConversaoInstance: tabelaConversaoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def tabelaConversaoInstance = TabelaConversao.read(params.id)
                if (!tabelaConversaoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'tabelaConversao.label', default: 'TabelaConversao'), params.id])
                    redirect controller: 'material', action: 'edit', id: tabelaConversaoInstance.material.id, fragment: TAB_CONV_UNID
                    return
                }

                [tabelaConversaoInstance: tabelaConversaoInstance]
                break
            case 'POST':
                def tabelaConversaoInstance = TabelaConversao.get(params.id)
                setBaseData(tabelaConversaoInstance)
                if (!tabelaConversaoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'tabelaConversao.label', default: 'TabelaConversao'), params.id])
                    redirect controller: 'material', action: 'edit', id: tabelaConversaoInstance.material.id, fragment: TAB_CONV_UNID
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (tabelaConversaoInstance.version > version) {
                        tabelaConversaoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'tabelaConversao.label', default: 'TabelaConversao')] as Object[],
                                "Another user has updated this TabelaConversao while you were editing")
                        render view: 'edit', model: [tabelaConversaoInstance: tabelaConversaoInstance]
                        return
                    }
                }

                tabelaConversaoInstance.properties = params

                if (!tabelaConversaoInstance.save(flush: true)) {
                    render view: 'edit', model: [tabelaConversaoInstance: tabelaConversaoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'tabelaConversao.label', default: 'TabelaConversao'), tabelaConversaoInstance.id])
                redirect controller: 'material', action: 'edit', id: tabelaConversaoInstance.material.id, fragment: TAB_CONV_UNID
                break
        }
    }

    def delete() {
        def tabelaConversaoInstance = TabelaConversao.get(params.id)
        if (!tabelaConversaoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tabelaConversao.label', default: 'TabelaConversao'), params.id])
            redirect controller: 'material', action: 'edit', id: tabelaConversaoInstance.material.id, fragment: TAB_CONV_UNID
            return
        }

        try {
            tabelaConversaoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'tabelaConversao.label', default: 'TabelaConversao'), params.id])
            redirect controller: 'material', action: 'edit', id: tabelaConversaoInstance.material.id, fragment: TAB_CONV_UNID
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'tabelaConversao.label', default: 'TabelaConversao'), params.id])
            redirect controller: 'material', action: 'edit', id: tabelaConversaoInstance.material.id, fragment: TAB_CONV_UNID
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [tabelaConversaoInstance: new TabelaConversao(params)]
                break
            case 'POST':
                def tabelaConversaoInstance
                if (params.id) tabelaConversaoInstance = TabelaConversao.get(params.id)
                else tabelaConversaoInstance = new TabelaConversao(params)

                setBaseData(tabelaConversaoInstance)
                if (!tabelaConversaoInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [tabelaConversaoInstance: tabelaConversaoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'tabelaConversao.label', default: 'tabelaConversao'), tabelaConversaoInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['tabelaConversao.id': tabelaConversaoInstance.id]

                break
        }
    }
}
