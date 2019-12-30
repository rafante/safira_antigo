package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.erp.base.BaseController
import org.springframework.dao.DataIntegrityViolationException

class SetorController extends BaseController{

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def baseService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def setorInstanceList = paramsToCriteria(params)
        [setorInstanceList: setorInstanceList, setorInstanceTotal: setorInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [setorInstance: new Setor(params)]
                break
            case 'POST':
                def setorInstance = new Setor(params)
                setBaseData(setorInstance)
                if (!baseService.save(setorInstance)) {
                    render view: 'create', model: [setorInstance: setorInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'setor.label', default: 'Setor'), setorInstance.id])
                redirect action: 'show', id: setorInstance.id
                break
        }
    }

    def show() {
        def setorInstance = Setor.read(params.id)
        if (!setorInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'setor.label', default: 'Setor'), params.id])
            redirect action: 'list'
            return
        }

        [setorInstance: setorInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def setorInstance = Setor.read(params.id)
                if (!setorInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'setor.label', default: 'Setor'), params.id])
                    redirect action: 'list'
                    return
                }

                [setorInstance: setorInstance]
                break
            case 'POST':
                def setorInstance = Setor.get(params.id)
                setBaseData(setorInstance)
                if (!setorInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'setor.label', default: 'Setor'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (setorInstance.version > version) {
                        setorInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'setor.label', default: 'Setor')] as Object[],
                                "Another user has updated this Setor while you were editing")
                        render view: 'edit', model: [setorInstance: setorInstance]
                        return
                    }
                }

                setorInstance.properties = params

                if (!baseService.save(setorInstance)) {
                    render view: 'edit', model: [setorInstance: setorInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'setor.label', default: 'Setor'), setorInstance.id])
                redirect action: 'show', id: setorInstance.id
                break
        }
    }

    def delete() {
        def setorInstance = Setor.get(params.id)
        if (!setorInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'setor.label', default: 'Setor'), params.id])
            redirect action: 'list'
            return
        }

        try {
            setorInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'setor.label', default: 'Setor'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'setor.label', default: 'Setor'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [setorInstance: new Setor(params)]
                break
            case 'POST':
                def setorInstance
                if (params.id) setorInstance = Setor.get(params.id)
                else setorInstance = new Setor(params)

                setBaseData(setorInstance)
                if (!baseService.save(setorInstance)) {
                    render view: 'create', model: [setorInstance: setorInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'setor.label', default: 'setor'), setorInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['setor.id': setorInstance.id]

                break
        }
    }
}
