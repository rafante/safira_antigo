package com.br.asgardtecnologia.erp.cadastros

import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.springframework.dao.DataIntegrityViolationException

class ParceiroNegociosController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def parceiroNegociosService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def parceiroNegociosInstanceList = paramsToCriteria(params)
        [parceiroNegociosInstanceList: parceiroNegociosInstanceList, parceiroNegociosInstanceTotal: parceiroNegociosInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [parceiroNegociosInstance: new ParceiroNegocios(params)]
                break
            case 'POST':
                def parceiroNegociosInstance = new ParceiroNegocios(params)
                setBaseData(parceiroNegociosInstance)

                if (!parceiroNegociosService.save(parceiroNegociosInstance)) {
                    render view: 'create', model: [parceiroNegociosInstance: parceiroNegociosInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'parceiroNegocios.label', default: 'ParceiroNegocios'), parceiroNegociosInstance.id])
                redirect action: 'show', id: parceiroNegociosInstance.id
                break
        }
    }

    def show() {
        def parceiroNegociosInstance = ParceiroNegocios.read(params.id)
        if (!parceiroNegociosInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'parceiroNegocios.label', default: 'ParceiroNegocios'), params.id])
            redirect action: 'list'
            return
        }

        [parceiroNegociosInstance: parceiroNegociosInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def parceiroNegociosInstance = ParceiroNegocios.read(params.id)
                if (!parceiroNegociosInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'parceiroNegocios.label', default: 'ParceiroNegocios'), params.id])
                    redirect action: 'list'
                    return
                }

                [parceiroNegociosInstance: parceiroNegociosInstance]
                break
            case 'POST':
                def parceiroNegociosInstance = ParceiroNegocios.get(params.id)
                setBaseData(parceiroNegociosInstance)
                if (!parceiroNegociosInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'parceiroNegocios.label', default: 'ParceiroNegocios'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (parceiroNegociosInstance.version > version) {
                        parceiroNegociosInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'parceiroNegocios.label', default: 'ParceiroNegocios')] as Object[],
                                "Another user has updated this ParceiroNegocios while you were editing")
                        render view: 'edit', model: [parceiroNegociosInstance: parceiroNegociosInstance]
                        return
                    }
                }

                parceiroNegociosInstance.properties = params

                if (!parceiroNegociosService.save(parceiroNegociosInstance)) {
                    render view: 'edit', model: [parceiroNegociosInstance: parceiroNegociosInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'parceiroNegocios.label', default: 'ParceiroNegocios'), parceiroNegociosInstance.id])
                redirect action: 'show', id: parceiroNegociosInstance.id
                break
        }
    }

    def delete() {
        def parceiroNegociosInstance = ParceiroNegocios.get(params.id)
        if (!parceiroNegociosInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'parceiroNegocios.label', default: 'ParceiroNegocios'), params.id])
            redirect action: 'list'
            return
        }

        try {
            parceiroNegociosService.delete(parceiroNegociosInstance)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'parceiroNegocios.label', default: 'ParceiroNegocios'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'parceiroNegocios.label', default: 'ParceiroNegocios'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [parceiroNegociosInstance: new ParceiroNegocios(params)]
                break
            case 'POST':
                def parceiroNegociosInstance
                if (params.id) parceiroNegociosInstance = ParceiroNegocios.get(params.id)
                else parceiroNegociosInstance = new ParceiroNegocios(params)

                setBaseData(parceiroNegociosInstance)
                if (!parceiroNegociosService.save(parceiroNegociosInstance, [validate: false])) {
                    render view: 'create', model: [parceiroNegociosInstance: parceiroNegociosInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'parceiroNegocios.label', default: 'parceiroNegocios'), parceiroNegociosInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['parceiroNegocios.id': parceiroNegociosInstance.id]

                break
        }
    }

    def cliente_analitico() {


        params._format = "PDF"
        params._name = "Relatório de Clientes"
        params._file = "clientes_analitico"
        //def clienteList = paramsToCriteria(params)
        def newOrderBy = params.orderBy
        if (params.orderBy.split("\\.").size() > 1)
            params.orderBy = "id"
        def clienteList = paramsToCriteria(params)
        if (params.orderBy != newOrderBy) {
            if (newOrderBy == 'endereco_comercial.municipio')
                clienteList.sort { it."endereco_comercial"?.municipio }
            else
                clienteList.sort { it."${params.orderBy}" }
        } else
            clienteList.sort { it."${params.orderBy}" }

        // Força o Fetch com o simples fato de chamar o getNome dos subobjetos.
        for (ParceiroNegocios cliente in clienteList) {
            cliente.endereco_comercial?.municipio?.getNome()
        }

        clienteList.sort { a, b ->
            a.nome.toString() <=> b.nome.toString()
        }

        def jasperService = new JasperService()

        if (params.extension == 'PDF') {
            def reportDef = new JasperReportDef(name: 'clientes_analitico.jasper',
                    fileFormat: JasperExportFormat.PDF_FORMAT,
                    reportData: clienteList,
                    parameters: params)
            response.contentType = "application/pdf"
            response.outputStream << jasperService.generateReport(reportDef).toByteArray()
            response.outputStream.flush()
            response.outputStream.close()

        }

        if (params.extension == 'XLS') {
            def reportDef = new JasperReportDef(name: 'clientes_analitico.jasper',
                    fileFormat: JasperExportFormat.XLS_FORMAT,
                    reportData: clienteList,
                    parameters: params)
            response.contentType = "application/xls"
            response.setHeader("Content-Disposition", "attachment;filename=\"" + params["action"] + ".xls" + "\"")
            response.outputStream << jasperService.generateReport(reportDef).toByteArray()
            response.outputStream.flush()
            response.outputStream.close()
            //.setSubtitle(reportsService.getSubtitle(params))
        }


    }
}
