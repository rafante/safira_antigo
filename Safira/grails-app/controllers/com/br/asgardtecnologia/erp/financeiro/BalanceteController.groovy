package com.br.asgardtecnologia.erp.financeiro

import org.springframework.dao.DataIntegrityViolationException

class BalanceteController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [balanceteInstanceList: Balancete.list(params), balanceteInstanceTotal: Balancete.count()]
    }

    def create() {
        [balanceteInstance: new Balancete(params)]
    }

    def save() {
        def balanceteInstance = new Balancete(params)
        if (!balanceteInstance.save(flush: true)) {
            render(view: "create", model: [balanceteInstance: balanceteInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'balancete.label', default: 'Balancete'), balanceteInstance.id])
        redirect(action: "show", id: balanceteInstance.id)
    }

    def show(Long id) {
        def balanceteInstance = Balancete.get(id)
        if (!balanceteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'balancete.label', default: 'Balancete'), id])
            redirect(action: "list")
            return
        }

        [balanceteInstance: balanceteInstance]
    }

    def edit(Long id) {
        def balanceteInstance = Balancete.get(id)
        if (!balanceteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'balancete.label', default: 'Balancete'), id])
            redirect(action: "list")
            return
        }

        [balanceteInstance: balanceteInstance]
    }

    def update(Long id, Long version) {
        def balanceteInstance = Balancete.get(id)
        if (!balanceteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'balancete.label', default: 'Balancete'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (balanceteInstance.version > version) {
                balanceteInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'balancete.label', default: 'Balancete')] as Object[],
                        "Another user has updated this Balancete while you were editing")
                render(view: "edit", model: [balanceteInstance: balanceteInstance])
                return
            }
        }

        balanceteInstance.properties = params

        if (!balanceteInstance.save(flush: true)) {
            render(view: "edit", model: [balanceteInstance: balanceteInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'balancete.label', default: 'Balancete'), balanceteInstance.id])
        redirect(action: "show", id: balanceteInstance.id)
    }

    def delete(Long id) {
        def balanceteInstance = Balancete.get(id)
        if (!balanceteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'balancete.label', default: 'Balancete'), id])
            redirect(action: "list")
            return
        }

        try {
            balanceteInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'balancete.label', default: 'Balancete'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'balancete.label', default: 'Balancete'), id])
            redirect(action: "show", id: id)
        }
    }
}
