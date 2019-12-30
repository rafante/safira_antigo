package com.br.asgardtecnologia.erp.ev

import com.br.asgardtecnologia.erp.base.BaseController
import com.br.asgardtecnologia.exceptions.files.ArquivoInvalido
import groovy.xml.DOMBuilder
import org.apache.commons.io.input.XmlStreamReader
import org.springframework.dao.DataIntegrityViolationException

class MovimentoExameController extends BaseController{

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def movimentoExameService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def movimentoExameList = paramsToCriteria(params)
        [movimentoExameList: movimentoExameList]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [movimentoExameInstance: new MovimentoExame(params)]
                break
            case 'POST':
                def movimentoExameInstance = new MovimentoExame(params)
                setBaseData(movimentoExameInstance)
                if (!movimentoExameService.save(movimentoExameInstance)) {
                    render view: 'create', model: [movimentoExameInstance: movimentoExameInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'movimentoExame.label', default: 'MovimentoExame'), movimentoExameInstance.id])
                redirect action: 'show', id: movimentoExameInstance.id
                break
        }
    }

    def show() {
        def movimentoExameInstance = MovimentoExame.read(params.id)
        if (!movimentoExameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'movimentoExame.label', default: 'MovimentoExame'), params.id])
            redirect action: 'list'
            return
        }

        [movimentoExameInstance: movimentoExameInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def movimentoExameInstance = MovimentoExame.read(params.id)
                if (!movimentoExameInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'movimentoExame.label', default: 'MovimentoExame'), params.id])
                    redirect action: 'list'
                    return
                }

                [movimentoExameInstance: movimentoExameInstance]
                break
            case 'POST':
                def movimentoExameInstance = MovimentoExame.get(params.id)
                setBaseData(movimentoExameInstance)
                if (!movimentoExameInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'movimentoExame.label', default: 'MovimentoExame'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (movimentoExameInstance.version > version) {
                        movimentoExameInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'movimentoExame.label', default: 'MovimentoExame')] as Object[],
                                "Another user has updated this MovimentoExame while you were editing")
                        render view: 'edit', model: [movimentoExameInstance: movimentoExameInstance]
                        return
                    }
                }

                println(params)

                movimentoExameInstance.properties = params

                if (!movimentoExameService.save(movimentoExameInstance)) {
                    render view: 'edit', model: [movimentoExameInstance: movimentoExameInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'movimentoExame.label', default: 'MovimentoExame'), movimentoExameInstance.id])
                redirect action: 'show', id: movimentoExameInstance.id
                break
        }
    }

    def delete() {
        def movimentoExameInstance = MovimentoExame.get(params.id)
        if (!movimentoExameInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'movimentoExame.label', default: 'MovimentoExame'), params.id])
            redirect action: 'list'
            return
        }

        try {
            movimentoExameService.delete(movimentoExameInstance)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'movimentoExame.label', default: 'MovimentoExame'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'movimentoExame.label', default: 'MovimentoExame'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [movimentoExameInstance: new MovimentoExame(params)]
                break
            case 'POST':
                def movimentoExameInstance
                if (params.id) movimentoExameInstance = MovimentoExame.get(params.id)
                else movimentoExameInstance = new MovimentoExame()
                movimentoExameInstance.properties = params

                setBaseData(movimentoExameInstance)
                if (!movimentoExameService.save(movimentoExameInstance, [validate: false])) {
                    render view: 'create', model: [movimentoExameInstance: movimentoExameInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'movimentoExame.label', default: 'movimentoExame'), movimentoExameInstance.id])
                redirect controller: params.controllerRedirect[0], action: 'create', params: ['movimentoExame.id': movimentoExameInstance.id]

                break
        }
    }

    def importarMovimentos() {
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
                        def  documento = DOMBuilder.parse(new XmlStreamReader(it.inputStream))
                        def xml = new String(it.inputStream.getBytes(), documento.xmlEncoding)
                        def valida = movimentoExameService.validaXml(xml)
                        if (valida.valido) {
                            def results = movimentoExameService.movimentosFromXML(xml)
                            if (!results.movimentos) {
                                flash.message = "Falha ao salvar"
                            }
                            def resultados = movimentoExameService.saveList(results.movimentos)
                            def msg = message(code: 'importacaoMovimentoExames.xml.sucesso', args: [resultados.salvos])
                            results.errors.each { error ->
                                resultados << error
                            }
                            if (resultados.erros.size()) {
                                resultados.erros.each {
                                    msg += "<br/>" + message(code: 'importacaoMovimentoExames.xml.error', args: [it.item])
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
                redirect action: 'importarMovimentos'
                break

        }
    }
}
