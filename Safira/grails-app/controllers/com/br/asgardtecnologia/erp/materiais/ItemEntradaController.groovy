package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class ItemEntradaController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def entradaMaterialService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def itemEntradaInstanceList = paramsToCriteria(params)
        [itemEntradaInstanceList: itemEntradaInstanceList, itemEntradaInstanceTotal: itemEntradaInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [itemEntradaInstance: new ItemEntrada(params)]
                break
            case 'POST':
                def itemEntradaInstance = new ItemEntrada(params)

                if (!validaEfetivada(itemEntradaInstance)) return

                setBaseData(itemEntradaInstance)
                if (!itemEntradaInstance.save(flush: true)) {
                    render view: 'create', model: [itemEntradaInstance: itemEntradaInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemEntrada.label', default: 'ItemEntrada'), itemEntradaInstance.id])
                redirect controller: 'entradaMaterial', action: 'edit', id: itemEntradaInstance?.entradaMaterial?.id
                break
        }
    }

    def show() {
        def itemEntradaInstance = ItemEntrada.read(params.id)
        if (!itemEntradaInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemEntrada.label', default: 'ItemEntrada'), params.id])
            redirect controller: 'entradaMaterial', action: 'edit', id: itemEntradaInstance?.entradaMaterial?.id
            return
        }

        [itemEntradaInstance: itemEntradaInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def itemEntradaInstance = ItemEntrada.read(params.id)

                if (!validaEfetivada(itemEntradaInstance)) return

                if (!itemEntradaInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemEntrada.label', default: 'ItemEntrada'), params.id])
                    redirect controller: 'entradaMaterial', action: 'edit', id: itemEntradaInstance?.entradaMaterial?.id
                    return
                }

                [itemEntradaInstance: itemEntradaInstance]
                break
            case 'POST':
                def itemEntradaInstance = ItemEntrada.get(params.id)
                setBaseData(itemEntradaInstance)
                if (!itemEntradaInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemEntrada.label', default: 'ItemEntrada'), params.id])
                    redirect controller: 'entradaMaterial', action: 'edit', id: itemEntradaInstance?.entradaMaterial?.id
                    return
                }

                if (!validaEfetivada(itemEntradaInstance)) return

                if (params.version) {
                    def version = params.version.toLong()
                    if (itemEntradaInstance.version > version) {
                        itemEntradaInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'itemEntrada.label', default: 'ItemEntrada')] as Object[],
                                "Another user has updated this ItemEntrada while you were editing")
                        render view: 'edit', model: [itemEntradaInstance: itemEntradaInstance]
                        return
                    }
                }

                itemEntradaInstance.properties = params

                if (!itemEntradaInstance.save(flush: true)) {
                    render view: 'edit', model: [itemEntradaInstance: itemEntradaInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'itemEntrada.label', default: 'ItemEntrada'), itemEntradaInstance.id])
                redirect controller: 'entradaMaterial', action: 'edit', id: itemEntradaInstance?.entradaMaterial?.id
                break
        }
    }

    def delete() {
        def itemEntradaInstance = ItemEntrada.get(params.id)
        if (!itemEntradaInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemEntrada.label', default: 'ItemEntrada'), params.id])
            redirect controller: 'entradaMaterial', action: 'edit', id: itemEntradaInstance?.entradaMaterial?.id
            return
        }

        if (!validaEfetivada(itemEntradaInstance)) return

        try {
            entradaMaterialService.deleteItem(itemEntradaInstance)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'itemEntrada.label', default: 'ItemEntrada'), params.id])
            redirect controller: 'entradaMaterial', action: 'edit', id: itemEntradaInstance.entradaMaterial?.id
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'itemEntrada.label', default: 'ItemEntrada'), params.id])
            redirect controller: 'entradaMaterial', action: 'edit', id: itemEntradaInstance?.entradaMaterial?.id
        }
    }

    def validaEfetivada(ItemEntrada itemEntradaInstance) {
        if (!itemEntradaInstance.entradaMaterial?.canEdit()) {
            flash.message = message(code: 'entradaMaterial.mensagens.entradaEfetivada', args: [message(code: 'entradaMaterial.label', default: 'Entrada'), params.id])
            redirect controller: 'entradaMaterial', action: 'edit', id: itemEntradaInstance?.entradaMaterial?.id

            return false
        }

        return true
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [itemEntradaInstance: new ItemEntrada(params)]
                break
            case 'POST':
                def itemEntradaInstance
                if (params.id) itemEntradaInstance = ItemEntrada.get(params.id)
                else itemEntradaInstance = new ItemEntrada(params)

                if (!validaEfetivada(itemEntradaInstance)) return

                setBaseData(itemEntradaInstance)
                if (!itemEntradaInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [itemEntradaInstance: itemEntradaInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemEntrada.label', default: 'itemEntrada'), itemEntradaInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['itemEntrada.id': itemEntradaInstance.id]

                break
        }
    }
}
