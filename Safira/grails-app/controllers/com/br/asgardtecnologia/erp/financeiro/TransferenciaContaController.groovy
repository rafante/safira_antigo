package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class TransferenciaContaController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST']]

    def transferenciaContaService
    def messageService
    def sessionService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def transferenciaContaList = paramsToCriteria(params)
        [transferenciaContaList: transferenciaContaList, transferenciaContaListCount: transferenciaContaList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [transferenciaConta: new TransferenciaConta(params)]
                break
            case 'POST':
                TransferenciaConta transferenciaConta = new TransferenciaConta(params)

                setBaseData(transferenciaConta)
                if (!transferenciaContaService.save(transferenciaConta, params)) {
                    render view: 'create', model: [transferenciaConta: transferenciaConta]
                    messageService.error(transferenciaConta.errors)
                    flash.message = sessionService.getSessionAttribute("errorMessages")
//                    flash.message = message(code: 'default.created.message', args: [message(code: 'transferenciaConta.label', default: 'TransferenciaConta'), transferenciaConta.id])
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'transferenciaConta.label', default: 'TransferenciaConta'), transferenciaConta.id])
                redirect action: 'show', id: transferenciaConta.id
                break
        }
    }

    def show() {
        TransferenciaConta transferenciaConta = TransferenciaConta.read(params.id)
        if (!transferenciaConta) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'transferenciaConta.label', default: 'TransferenciaConta'), params.id])
            redirect action: 'list'
            return
        }

        [transferenciaConta: transferenciaConta, hiddenActionButtons: ['edit', 'delete']]
    }

//    def edit() {
//        switch (request.method) {
//            case 'GET':
//                def transferenciaConta = TransferenciaConta.read(params.id)
//                if (!transferenciaConta) {
//                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'transferenciaConta.label', default: 'TransferenciaConta'), params.id])
//                    redirect action: 'list'
//                    return
//                }
//                [transferenciaConta: transferenciaConta]
//                break
//            case 'POST':
//                def transferenciaConta = TransferenciaConta.get(params.id)
//
////                setBaseData(transferenciaConta)
//                if (!transferenciaConta) {
//                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'transferenciaConta.label', default: 'TransferenciaConta'), params.id])
//                    redirect action: 'list'
//                    return
//                }
//
//                if (params.version) {
//                    def version = params.version.toLong()
//                    if (transferenciaConta.version > version) {
//                        transferenciaConta.errors.rejectValue('version', 'default.optimistic.locking.failure',
//                                [message(code: 'transferenciaConta.label', default: 'TransferenciaConta')] as Object[],
//                                "Another user has updated this PlanoContas while you were editing")
//                        render view: 'edit', model: [transferenciaConta: transferenciaConta]
//                        return
//                    }
//                }
//
//                transferenciaConta.properties = params
//
//                if (!transferenciaConta.save(flush: true)) {
//                    render view: 'edit', model: [transferenciaConta: transferenciaConta]
//                    return
//                }
//
//                flash.message = message(code: 'default.updated.message', args: [message(code: 'transferenciaConta.label', default: 'TransferenciaConta'), transferenciaConta.id])
//                redirect action: 'show', id: transferenciaConta.id
//                break
//        }
//    }

//    @Secured('ROLE_ADMIN')
//    def delete() {
//        def transferenciaConta = TransferenciaConta.get(params.id)
//        if (!transferenciaConta) {
//            flash.message = message(code: 'default.not.found.message', args: [message(code: 'transferenciaConta.label', default: 'TransferenciaConta'), params.id])
//            redirect action: 'list'
//            return
//        }
//
//        try {
//            transferenciaConta.delete(flush: true)
//            flash.message = message(code: 'default.deleted.message', args: [message(code: 'transferenciaConta.label', default: 'TransferenciaConta'), params.id])
//            redirect action: 'list'
//        }
//        catch (DataIntegrityViolationException e) {
//            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'transferenciaConta.label', default: 'TransferenciaConta'), params.id])
//            redirect action: 'show', id: params.id
//        }
//    }

    def estornar() {
        def transferenciaConta = TransferenciaConta.get(params.id)
        if (!transferenciaConta) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'transferenciaConta.label', default: 'TransferenciaConta'), params.id])
            redirect action: 'list'
            return
        }

        try {
            transferenciaContaService.estornar(transferenciaConta)
            flash.message = message(code: 'default.estornado.message', args: [message(code: 'transferenciaConta.label', default: 'TransferenciaConta'), params.id])
            redirect action: 'show', id: params.id
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.estornado.message', args: [message(code: 'transferenciaConta.label', default: 'TransferenciaConta'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

