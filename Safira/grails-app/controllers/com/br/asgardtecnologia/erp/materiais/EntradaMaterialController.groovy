package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import com.br.asgardtecnologia.erp.base.Empresa
import com.br.asgardtecnologia.erp.security.Usuario
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.springframework.dao.DataIntegrityViolationException

class EntradaMaterialController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def entradaMaterialService
    def jasperService
    def messageService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        def entradaMaterialInstanceList = paramsToCriteria(params)

        [entradaMaterialInstanceList: entradaMaterialInstanceList, entradaMaterialInstanceTotal: entradaMaterialInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [entradaMaterialInstance: new EntradaMaterial(params)]
                break
            case 'POST':
                def entradaMaterialInstance = new EntradaMaterial(params)
                setBaseData(entradaMaterialInstance)
                if (!entradaMaterialService.save(entradaMaterialInstance)) {
                    render view: 'create', model: [entradaMaterialInstance: entradaMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'entradaMaterial.label', default: 'EntradaMaterial'), entradaMaterialInstance.id])
                redirect action: 'edit', id: entradaMaterialInstance.id
                break
        }
    }

    def show() {
        switch (request.method) {
            case 'GET':
                def entradaMaterialInstance = EntradaMaterial.read(params.id)
                if (!entradaMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'entradaMaterial.label', default: 'EntradaMaterial'), params.id])
                    redirect action: 'list'
                    return
                }

                [entradaMaterialInstance: entradaMaterialInstance, customButtons: [[id: params.id, controller: "entradaMaterial", action: "print", title: "Imprimir", icon: "icon-print", target: "_blank"]]]
                break
            case 'POST':
                def entradaMaterialInstance = EntradaMaterial.get(params.id)
                setBaseData(entradaMaterialInstance)
                if (!entradaMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'entradaMaterial.label', default: 'EntradaMaterial'), params.id])
                    redirect action: 'list'
                    return
                }
                if (!validaEfetivada(entradaMaterialInstance)) return

                if (params.version) {
                    def version = params.version.toLong()
                    if (entradaMaterialInstance.version > version) {
                        entradaMaterialInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'movimentoMaterial.label', default: 'movimentoMaterial')] as Object[],
                                "Another user has updated this movimentoMaterial while you were editing")
                        render view: 'edit', model: [movimentoMaterialInstance: entradaMaterialInstance]
                        return
                    }
                }

                def oldStatus = entradaMaterialInstance.status
                entradaMaterialInstance.status = params.status

                if (!entradaMaterialService.save(entradaMaterialInstance)) {
                    entradaMaterialInstance.status = oldStatus

                    render view: 'edit', model: [entradaMaterialInstance: entradaMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'entradaMaterial.label', default: 'EntradaMaterial'), entradaMaterialInstance.id])
                redirect action: 'edit', id: entradaMaterialInstance.id
                break
        }
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def entradaMaterialInstance = EntradaMaterial.read(params.id)
                    if (!entradaMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'entradaMaterial.label', default: 'EntradaMaterial'), params.id])
                    redirect action: 'list'
                    return
                }
                if (!validaEfetivada(entradaMaterialInstance)) return

                [entradaMaterialInstance: entradaMaterialInstance, customButtons: [[id: params.id, controller: "entradaMaterial", action: "print", title: "Imprimir", icon: "icon-print", target: "_blank"]]]
                break
            case 'POST':
                def entradaMaterialInstance = EntradaMaterial.get(params.id)
                setBaseData(entradaMaterialInstance)
                if (!entradaMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'entradaMaterial.label', default: 'EntradaMaterial'), params.id])
                    redirect action: 'list'
                    return
                }
                if (!validaEfetivada(entradaMaterialInstance)) return

                if (params.version) {
                    def version = params.version.toLong()
                    if (entradaMaterialInstance.version > version) {
                        entradaMaterialInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'entradaMaterial.label', default: 'EntradaMaterial')] as Object[],
                                "Another user has updated this EntradaMaterial while you were editing")
                        render view: 'edit', model: [entradaMaterialInstance: entradaMaterialInstance]
                        return
                    }
                }

                def oldStatus = entradaMaterialInstance.status
                entradaMaterialInstance.properties = params
                entradaMaterialInstance.statusOld = oldStatus

                try {
                    entradaMaterialService.save(entradaMaterialInstance)
                } catch (e) {
                    entradaMaterialInstance.status = oldStatus
                    messageService.error(e.message)

                    if (e.class != RuntimeException) {
                        try {
                            messageService.error(e.undeclaredThrowable?.message)
                        } catch (ex1) {
                        }
                        throw e
                    } else {
                        messageService.error("data.not.saved")
                    }
                    render view: 'edit', model: [entradaMaterialInstance: entradaMaterialInstance]
                    return
                }
                flash.message = message(code: 'default.updated.message', args: [message(code: 'entradaMaterial.label', default: 'EntradaMaterial'), entradaMaterialInstance.id])
                redirect action: 'edit', id: entradaMaterialInstance.id
                break
        }
    }

    def delete() {
        def entradaMaterialInstance = EntradaMaterial.get(params.id)
        if (!entradaMaterialInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'entradaMaterial.label', default: 'EntradaMaterial'), params.id])
            redirect action: 'list'
            return
        }

        if (!validaEfetivada(entradaMaterialInstance)) return
        try {
            entradaMaterialInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'entradaMaterial.label', default: 'EntradaMaterial'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'entradaMaterial.label', default: 'EntradaMaterial'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def corrigirEntrada() {
        switch (request.method) {
            case 'GET':
                def entradaMaterialInstance = EntradaMaterial.get(params.id)
                if (!entradaMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'entradaMaterial.label', default: 'Entrada'), params.id])
                    redirect action: 'list'
                    return
                }
                [entradaMaterialInstance: entradaMaterialInstance]
                break
            case 'POST':
                def entradaMaterialInstance = EntradaMaterial.get(params.id)
                if (!entradaMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'entradaMaterial.label', default: 'Entrada'), params.id])
                    redirect action: 'list'
                    return
                }

                entradaMaterialService.corrigirEntrada(entradaMaterialInstance, Usuario.get(principal.id), params.motivo)
                if (!entradaMaterialInstance.save(flush: true)) {
                    render view: 'show', model: [entradaMaterialInstance: entradaMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'entradaMaterial.label', default: 'EntradaMaterial'), entradaMaterialInstance.id])
                redirect action: 'edit', id: entradaMaterialInstance.id
        }
    }

    def validaEfetivada(EntradaMaterial entradaMaterialInstance) {
        if (!entradaMaterialInstance?.canEdit()) {
            flash.message = message(code: 'entradaMaterial.mensagens.entradaEfetivada', args: [message(code: 'entradaMaterial.label', default: 'Entrada'), params.id])
            redirect action: 'show', id: entradaMaterialInstance.id

            return false
        }

        return true
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [entradaMaterialInstance: new EntradaMaterial(params)]
                break
            case 'POST':
                def entradaMaterialInstance
                if (params.id) entradaMaterialInstance = EntradaMaterial.get(params.id)
                else entradaMaterialInstance = new EntradaMaterial(params)

                if (!validaEfetivada(entradaMaterialInstance)) return

                setBaseData(entradaMaterialInstance)
                if (!entradaMaterialService.save(entradaMaterialInstance, [validate: false])) {
                    render view: 'create', model: [entradaMaterialInstance: entradaMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'entradaMaterial.label', default: 'entradaMaterial'), entradaMaterialInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['entradaMaterial.id': entradaMaterialInstance.id]

                break
        }
    }

    def print() {
        def reportDef = printEntradaMaterial(params, Boolean.TRUE)

        response.contentType = "application/pdf"
        response.outputStream << jasperService.generateReport(reportDef).toByteArray()
        response.outputStream.flush()
        response.outputStream.close()
    }

    def printEntradaMaterial(params, printValues) {
        params._format = "PDF"
        params._name = "Entrada" + params.id
        params._file = "guia_compra"

        EntradaMaterial entrada = EntradaMaterial.findById(params.long("id"))

        def empresa = Empresa.get(1)

        def itemEntradaList = ItemEntrada.findAllByEntradaMaterial(entrada, [fetch: "eager", sort: "material.descricao", order: "asc"])

        // For�a o Fetch com o simples fato de chamar o getNome dos subobjetos.
        /*for (ItemEntrada item in itemEntradaList) {
            // TROCAR O CAMPO EMPRESA NO JASPER PARA: Persistente.GetCurrentEmpresa()?.endereco_comercial?.municipio?.getNome()
            item.pedido?.cliente?.getNome()
            item.pedido?.getTotal()
            item.pedido?.getObservacao()
            item.pedido?.getTotalSemDesconto()
            item.pedido?.getPrazo_pagamento()
            item.pedido?.prazo_pagamento?.itemPrazoPagamento.findAll()
            item.material?.toString()
            item.material?.unidade_medida?.getUnidade()
        }*/
        params.SUBREPORT_DIR = getServletContext().getRealPath("/reports/") + System.getProperty("file.separator")
        params.PRINT_VALUES = printValues
        //params.MENSAGEM = ParametrosVendas.findById(1).mensagemDefault
        params.EMPRESA = empresa
        def rep = 'guia_compra.jrxml'
        /*switch (ped.status_pedido) {
            case StatusPedido.ORCAMENTO:
                params.STATUS_PEDIDO = "Or�amento"
                rep = 'orcamento.jrxml'
                params.MENSAGEM = ParametrosVendas.findById(1).mensagemDefaultOrcamento
                break
            case StatusPedido.PRODUCAO:
                params.STATUS_PEDIDO = "Pedido de Venda"
                break
            case StatusPedido.FATURADO:
                params.STATUS_PEDIDO = "Fatura"
                break
        }*/

        JasperReportDef reportDef = new JasperReportDef(name: rep,
                fileFormat: JasperExportFormat.PDF_FORMAT,
                reportData: itemEntradaList,
                parameters: params)

        return reportDef
    }
}
