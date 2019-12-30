package com.br.asgardtecnologia.erp.ev

import com.br.asgardtecnologia.erp.base.BaseController
import org.springframework.dao.DataIntegrityViolationException

class ItemExameController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def exameService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def itemExameInstanceList = paramsToCriteria(params)
        [itemExameInstanceList: itemExameInstanceList, itemExameInstanceTotal: itemExameInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [itemExameInstance: new ItemExame(params)]
                break
            case 'POST':
                def itemExameInstance = new ItemExame(params)
                setBaseData(itemExameInstance)
                if (!exameService.save(itemExameInstance)) {
                    render view: 'create', model: [itemExameInstance: itemExameInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemExame.label', default: 'ItemExame'), itemExameInstance.id])
                redirect controller: 'material', action: 'show', id: params.material.id
                break
        }
    }

    def show() {
        def itemExameInstance = ItemExame.read(params.id)
        if (!itemExameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemExame.label', default: 'ItemExame'), params.id])
            redirect action: 'list'
            return
        }

        [itemExameInstance: itemExameInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def itemExameInstance = ItemExame.read(params.id)
                if (!itemExameInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemExame.label', default: 'ItemExame'), params.id])
                    redirect action: 'list'
                    return
                }

                [itemExameInstance: itemExameInstance]
                break
            case 'POST':
                def itemExameInstance = ItemExame.get(params.id)
                setBaseData(itemExameInstance)
                if (!itemExameInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemExame.label', default: 'ItemExame'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (itemExameInstance.version > version) {
                        itemExameInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'itemExame.label', default: 'ItemExame')] as Object[],
                                "Another user has updated this ItemExame while you were editing")
                        render view: 'edit', model: [itemExameInstance: itemExameInstance]
                        return
                    }
                }

                println(params)

                itemExameInstance.properties = params

                if (!exameService.save(itemExameInstance)) {
                    render view: 'edit', model: [itemExameInstance: itemExameInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'itemExame.label', default: 'ItemExame'), itemExameInstance.id])
                redirect action: 'show', id: itemExameInstance.id
                break
        }
    }

    def delete() {
        def itemExameInstance = ItemExame.get(params.id)
        def materialId = itemExameInstance.material.id
        if (!itemExameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemExame.label', default: 'ItemExame'), params.id])
            redirect action: 'list'
            return
        }

        try {
            exameService.delete(itemExameInstance)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'itemExame.label', default: 'ItemExame'), params.id])
            redirect controller: 'material', action: 'show', id: materialId
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'itemExame.label', default: 'ItemExame'), params.id])
            redirect controller: 'material', action: 'show', id: materialId
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [itemExameInstance: new ItemExame(params)]
                break
            case 'POST':
                def itemExameInstance
                if (params.id) itemExameInstance = ItemExame.get(params.id)
                else itemExameInstance = new ItemExame()
                itemExameInstance.properties = params

                setBaseData(itemExameInstance)
                if (!exameService.save(itemExameInstance, [validate: false])) {
                    render view: 'create', model: [itemExameInstance: itemExameInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemExame.label', default: 'itemItemExame'), itemExameInstance.id])
                redirect controller: params.controllerRedirect[0], action: 'create', params: ['itemItemExame.id': itemExameInstance.id]

                break
        }
    }

}
