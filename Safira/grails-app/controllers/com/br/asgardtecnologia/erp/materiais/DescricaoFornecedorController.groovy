package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class DescricaoFornecedorController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    static TAB_DESC_FORN = "tab_desc_forn"

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def descricaoFornecedorInstanceList = paramsToCriteria(params)
        [descricaoFornecedorInstanceList: descricaoFornecedorInstanceList, descricaoFornecedorInstanceTotal: descricaoFornecedorInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [descricaoFornecedorInstance: new DescricaoFornecedor(params)]
                break
            case 'POST':
                def descricaoFornecedorInstance = new DescricaoFornecedor(params)
                setBaseData(descricaoFornecedorInstance)
                if (!descricaoFornecedorInstance.save(flush: true)) {
                    render view: 'create', model: [descricaoFornecedorInstance: descricaoFornecedorInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'descricaoFornecedor.label', default: 'DescricaoFornecedor'), descricaoFornecedorInstance.id])
                redirect controller: 'material', action: 'edit', id: descricaoFornecedorInstance.material.id, fragment: TAB_DESC_FORN
                break
        }
    }

    def show() {
        def descricaoFornecedorInstance = DescricaoFornecedor.read(params.id)
        if (!descricaoFornecedorInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'descricaoFornecedor.label', default: 'DescricaoFornecedor'), params.id])
            redirect controller: 'material', action: 'edit', id: descricaoFornecedorInstance.material.id, fragment: TAB_DESC_FORN
            return
        }

        [descricaoFornecedorInstance: descricaoFornecedorInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def descricaoFornecedorInstance = DescricaoFornecedor.read(params.id)
                if (!descricaoFornecedorInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'descricaoFornecedor.label', default: 'DescricaoFornecedor'), params.id])
                    redirect controller: 'material', action: 'edit', id: descricaoFornecedorInstance.material.id, fragment: TAB_DESC_FORN
                    return
                }

                [descricaoFornecedorInstance: descricaoFornecedorInstance]
                break
            case 'POST':
                def descricaoFornecedorInstance = DescricaoFornecedor.get(params.id)
                setBaseData(descricaoFornecedorInstance)
                if (!descricaoFornecedorInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'descricaoFornecedor.label', default: 'DescricaoFornecedor'), params.id])
                    redirect controller: 'material', action: 'edit', id: descricaoFornecedorInstance.material.id, fragment: TAB_DESC_FORN
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (descricaoFornecedorInstance.version > version) {
                        descricaoFornecedorInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'descricaoFornecedor.label', default: 'DescricaoFornecedor')] as Object[],
                                "Another user has updated this DescricaoFornecedor while you were editing")
                        render view: 'edit', model: [descricaoFornecedorInstance: descricaoFornecedorInstance]
                        return
                    }
                }

                descricaoFornecedorInstance.properties = params

                if (!descricaoFornecedorInstance.save(flush: true)) {
                    render view: 'edit', model: [descricaoFornecedorInstance: descricaoFornecedorInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'descricaoFornecedor.label', default: 'DescricaoFornecedor'), descricaoFornecedorInstance.id])
                redirect controller: 'material', action: 'edit', id: descricaoFornecedorInstance.material.id, fragment: TAB_DESC_FORN
                break
        }
    }

    def delete() {
        def descricaoFornecedorInstance = DescricaoFornecedor.get(params.id)
        if (!descricaoFornecedorInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'descricaoFornecedor.label', default: 'DescricaoFornecedor'), params.id])
            redirect controller: 'material', action: 'edit', id: descricaoFornecedorInstance.material.id, fragment: TAB_DESC_FORN
            return
        }

        try {
            descricaoFornecedorInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'descricaoFornecedor.label', default: 'DescricaoFornecedor'), params.id])
            redirect controller: 'material', action: 'edit', id: descricaoFornecedorInstance.material.id, fragment: TAB_DESC_FORN
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'descricaoFornecedor.label', default: 'DescricaoFornecedor'), params.id])
            redirect controller: 'material', action: 'edit', id: descricaoFornecedorInstance.material.id, fragment: TAB_DESC_FORN
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [descricaoFornecedorInstance: new DescricaoFornecedor(params)]
                break
            case 'POST':
                def descricaoFornecedorInstance
                if (params.id) descricaoFornecedorInstance = DescricaoFornecedor.get(params.id)
                else descricaoFornecedorInstance = new DescricaoFornecedor(params)

                setBaseData(descricaoFornecedorInstance)
                if (!descricaoFornecedorInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [descricaoFornecedorInstance: descricaoFornecedorInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'descricaoFornecedor.label', default: 'descricaoFornecedor'), descricaoFornecedorInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['descricaoFornecedor.id': descricaoFornecedorInstance.id]

                break
        }
    }
}
