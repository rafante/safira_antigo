package com.br.asgardtecnologia.erp.ev

import com.br.asgardtecnologia.erp.base.BaseController
import com.br.asgardtecnologia.exceptions.files.ArquivoInvalido
import groovy.xml.DOMBuilder

//import com.br.asgardtecnologia.files.exceptions.ArquivoInvalido
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.XmlStreamReader
import org.springframework.dao.DataIntegrityViolationException

class ExameController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def exameService

    def teste() {
        def movimentos = MovimentoExame.list()
        movimentos.each { it ->
            it.delete([flush: true])
        }
        def itemExames = ItemExame.list()
        itemExames.each { it ->
            it.delete(flush: true)
        }
        def exames = Exame.list()
        exames.each { it ->
            it.delete(flush: true)
        }
        flash.message = "Tudo deletado"
        redirect(action: 'list')
    }

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def exameInstanceList = paramsToCriteria(params)
        [exameInstanceList: exameInstanceList, exameInstanceTotal: exameInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [exameInstance: new Exame(params)]
                break
            case 'POST':
                def exameInstance = new Exame(params)
                setBaseData(exameInstance)
                if (!exameService.save(exameInstance)) {
                    render view: 'create', model: [exameInstance: exameInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'exame.label', default: 'Exame'), exameInstance.id])
                redirect action: 'show', id: exameInstance.id
                break
        }
    }

    def show() {
        def exameInstance = Exame.read(params.id)
        if (!exameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'exame.label', default: 'Exame'), params.id])
            redirect action: 'list'
            return
        }

        [exameInstance: exameInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def exameInstance = Exame.read(params.id)
                if (!exameInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'exame.label', default: 'Exame'), params.id])
                    redirect action: 'list'
                    return
                }

                [exameInstance: exameInstance]
                break
            case 'POST':
                def exameInstance = Exame.get(params.id)
                setBaseData(exameInstance)
                if (!exameInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'exame.label', default: 'Exame'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (exameInstance.version > version) {
                        exameInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'exame.label', default: 'Exame')] as Object[],
                                "Another user has updated this Exame while you were editing")
                        render view: 'edit', model: [exameInstance: exameInstance]
                        return
                    }
                }

                println(params)

                exameInstance.properties = params

                if (!exameService.save(exameInstance)) {
                    render view: 'edit', model: [exameInstance: exameInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'exame.label', default: 'Exame'), exameInstance.id])
                redirect action: 'show', id: exameInstance.id
                break
        }
    }

    def delete() {
        def exameInstance = Exame.get(params.id)
        if (!exameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'exame.label', default: 'Exame'), params.id])
            redirect action: 'list'
            return
        }

        try {
            exameService.delete(exameInstance)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'exame.label', default: 'Exame'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'exame.label', default: 'Exame'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [exameInstance: new Exame(params)]
                break
            case 'POST':
                def exameInstance
                if (params.id) exameInstance = Exame.get(params.id)
                else exameInstance = new Exame()
                exameInstance.properties = params

                setBaseData(exameInstance)
                if (!exameService.save(exameInstance, [validate: false])) {
                    render view: 'create', model: [exameInstance: exameInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'exame.label', default: 'exame'), exameInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['exame.id': exameInstance.id]

                break
        }
    }

    def importarXML() {
        switch (request.method) {
            case 'GET':
                break
            case 'POST':
                def paramsEV = ParametrosEV.get(1)
                if (!paramsEV) {
                    flash.message = message(code: "parametros.ev.missing")
                    redirect(action: "edit", controller: "parametrosEV")
                    return
                }
                if (!paramsEV.examesEncoding) {
                    flash.message = message(code: "parametros.ev.encoding.exames.missing")
                    redirect(action: "edit", controller: "parametrosEV")
                    return
                }
                def xmlFiles = request.getFiles("files")
                try {
                    xmlFiles.each {
                        if (!it.contentType.equals("text/xml")) {
                            throw new ArquivoInvalido(it.fileItem.name)
                        }
                        def documento = DOMBuilder.parse(new XmlStreamReader(it.inputStream))
                        def xml = new String(it.inputStream.getBytes(), documento.xmlEncoding)
                        def valida = exameService.validaXml(xml)
                        if (valida.valido) {
                            def results = exameService.fromXML(xml)
                            if (!results.exames) {
                                flash.message = "Falha ao salvar"
                            }
                            def resultados = exameService.saveList(results.exames)
                            def msg = message(code: 'importacaoExames.xml.sucesso', args: [resultados.salvos])
                            results.errors.each { error ->
                                resultados << error
                            }
                            if (resultados.erros.size()) {
                                resultados.erros.each {
                                    msg += "<br/>" + message(code: 'importacaoExames.xml.error', args: [it.item])
                                    it.erros.each {
                                        msg += "<br/>" + mes
                                    }
                                }
                            }
                            flash.message = msg
                        } else {
                            valida.erros.each {
                                flash.message += it.toString() + "<br/>"
                            }
                        }
                    }
                } catch (ArquivoInvalido e) {
                    flash.message = message(code: 'default.invalid.file.message', args: [e.nomeArquivo])
                    return
                }
                redirect action: 'importarXML'
                break

        }

    }
}