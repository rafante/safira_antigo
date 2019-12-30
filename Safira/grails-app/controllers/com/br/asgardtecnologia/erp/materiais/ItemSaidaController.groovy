package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class ItemSaidaController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def itemSaidaInstanceList = paramsToCriteria(params)
        [itemSaidaInstanceList: itemSaidaInstanceList, itemSaidaInstanceTotal: itemSaidaInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [itemSaidaInstance: new ItemSaida(params)]
                break
            case 'POST':
                def itemSaidaInstance = new ItemSaida(params)

                if (!validaEfetivada(itemSaidaInstance)) return

                setBaseData(itemSaidaInstance)
                if (!itemSaidaInstance.save(flush: true)) {
                    render view: 'create', model: [itemSaidaInstance: itemSaidaInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemSaida.label', default: 'ItemSaida'), itemSaidaInstance.id])
                redirect controller:'movimentoMaterial', action: 'show', id: itemSaidaInstance?.saidaMaterial?.id
                break
        }
    }

    def show() {
        def itemSaidaInstance = ItemSaida.read(params.id)
        if (!itemSaidaInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemSaida.label', default: 'ItemSaida'), params.id])
            redirect action: 'list'
            return
        }

        [itemSaidaInstance: itemSaidaInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def itemSaidaInstance = ItemSaida.read(params.id)

                if (!validaEfetivada(itemSaidaInstance)) return

                if (!itemSaidaInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemSaida.label', default: 'ItemSaida'), params.id])
                    redirect action: 'list'
                    return
                }

                [itemSaidaInstance: itemSaidaInstance]
                break
            case 'POST':
                def itemSaidaInstance = ItemSaida.get(params.id)
                setBaseData(itemSaidaInstance)
                if (!itemSaidaInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemSaida.label', default: 'ItemSaida'), params.id])
                    redirect action: 'list'
                    return
                }

                if (!validaEfetivada(itemSaidaInstance)) return

                if (params.version) {
                    def version = params.version.toLong()
                    if (itemSaidaInstance.version > version) {
                        itemSaidaInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'itemSaida.label', default: 'ItemSaida')] as Object[],
                                "Another user has updated this ItemSaida while you were editing")
                        render view: 'edit', model: [itemSaidaInstance: itemSaidaInstance]
                        return
                    }
                }

                itemSaidaInstance.properties = params

                if (!itemSaidaInstance.save(flush: true)) {
                    render view: 'edit', model: [itemSaidaInstance: itemSaidaInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'itemSaida.label', default: 'ItemSaida'), itemSaidaInstance.id])
                redirect controller:'movimentoMaterial', action: 'show', id: itemSaidaInstance?.saidaMaterial?.id
                break
        }
    }

    def delete() {
        def itemSaidaInstance = ItemSaida.get(params.id)
        if (!itemSaidaInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemSaida.label', default: 'ItemSaida'), params.id])
            redirect controller:'movimentoMaterial', action: 'show', id: itemSaidaInstance?.saidaMaterial?.id
            return
        }

        if (!validaEfetivada(itemSaidaInstance)) return

        try {
            itemSaidaInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'itemSaida.label', default: 'ItemSaida'), params.id])
            redirect controller:'movimentoMaterial', action: 'show', id: itemSaidaInstance?.saidaMaterial?.id
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'itemSaida.label', default: 'ItemSaida'), params.id])
            redirect controller:'movimentoMaterial', action: 'show', id: itemSaidaInstance?.saidaMaterial?.id
        }
    }

    def validaEfetivada(ItemSaida itemSaidaInstance) {
        if (!itemSaidaInstance.saidaMaterial?.canEdit()) {
            flash.message = message(code: 'movimentoMaterial.mensagens.saidaEfetivada', args: [message(code: 'movimentoMaterial.label', default: 'Saida'), params.id])
            redirect controller:'movimentoMaterial', action: 'show', id: itemSaidaInstance?.saidaMaterial?.id

            return false
        }

        return true
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [itemSaidaInstance: new ItemSaida(params)]
                break
            case 'POST':
                def itemSaidaInstance
                if (params.id) itemSaidaInstance = ItemSaida.get(params.id)
                else itemSaidaInstance = new ItemSaida(params)

                if (!validaEfetivada(itemSaidaInstance)) return

                setBaseData(itemSaidaInstance)
                if (!itemSaidaInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [itemSaidaInstance: itemSaidaInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemSaida.label', default: 'itemSaida'), itemSaidaInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['itemSaida.id': itemSaidaInstance.id]

                break
        }
    }
}
