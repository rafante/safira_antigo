package com.br.asgardtecnologia.erp.financeiro
import com.br.asgardtecnologia.erp.base.BaseController
import com.br.asgardtecnologia.erp.ev.ParametrosEV
import com.br.asgardtecnologia.exceptions.files.ArquivoInvalido
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.validation.FieldError

import javax.xml.bind.ValidationException

class ContaReceberController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def financeiroBaseService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def contaReceberInstanceList = paramsToCriteria(params)
        [contaReceberInstanceList: contaReceberInstanceList, contaReceberInstanceTotal: contaReceberInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [contaReceberInstance: new ContaReceber(params), params: params]
                break
            case 'POST':
                def contaReceberInstance = new ContaReceber(params)

                if (!financeiroBaseService.save(contaReceberInstance, params)) {
                    render view: 'create', model: [contaReceberInstance: contaReceberInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'contaReceber.label', default: 'ContaReceber'), contaReceberInstance.id])
                redirect action: 'show', id: contaReceberInstance.id
                break
        }
    }

    def prorrogar() {
        def itemContaReceberInstance = ItemContaReceber.get(params.itemContaReceber.id)

        itemContaReceberInstance.properties = params

        def msg = financeiroBaseService.prorrogarParcela(itemContaReceberInstance, 'Receber', params)
        flash.message = resolveMessage(msg)

        redirect action: 'show', id: itemContaReceberInstance.contaReceberId
    }

    def compensar() {
        def itemContaReceberInstance = ItemContaReceber.get(params.itemContaReceber.id)
        itemContaReceberInstance.properties = params

        try {
            flash.message = resolveMessage(financeiroBaseService.compensarParcela(itemContaReceberInstance, params))
        } catch (ValidationException e) {
            flash.message = resolveMessage(e.message)
        }

        redirect controller: 'itemContaReceber', action: 'show', id: itemContaReceberInstance?.id
    }

    def show() {
        def contaReceberInstance = ContaReceber.read(params.id)
        if (!contaReceberInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaReceber.label', default: 'ContaReceber'), params.id])
            redirect action: 'list'
            return
        }

        [contaReceberInstance: contaReceberInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def contaReceberInstance = ContaReceber.read(params.id)
                if (!contaReceberInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaReceber.label', default: 'ContaReceber'), params.id])
                    redirect action: 'list'
                    return
                }

                [contaReceberInstance: contaReceberInstance]
                break
            case 'POST':
                def contaReceberInstance = ContaReceber.get(params.id)
                setBaseData(contaReceberInstance)
                if (!contaReceberInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaReceber.label', default: 'ContaReceber'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (contaReceberInstance.version > version) {
                        contaReceberInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'contaReceber.label', default: 'ContaReceber')] as Object[],
                                "Another user has updated this ContaReceber while you were editing")
                        render view: 'edit', model: [contaReceberInstance: contaReceberInstance]
                        return
                    }
                }

                contaReceberInstance.properties = params

                if (!financeiroBaseService.save(contaReceberInstance, params)) {
                    render view: 'edit', model: [contaReceberInstance: contaReceberInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'contaReceber.label', default: 'ContaReceber'), contaReceberInstance.id])
                redirect action: 'show', id: contaReceberInstance.id
                break
        }
    }

    def delete() {
        def contaReceberInstance = ContaReceber.get(params.id)
        if (!contaReceberInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'contaReceber.label', default: 'ContaReceber'), params.id])
            redirect action: 'list'
            return
        }

        try {
            contaReceberInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'contaReceber.label', default: 'ContaReceber'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'contaReceber.label', default: 'ContaReceber'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [contaReceberInstance: new ContaReceber(params)]
                break
            case 'POST':
                def contaReceberInstance
                if (params.id) contaReceberInstance = ContaReceber.get(params.id)
                else contaReceberInstance = new ContaReceber(params)

                setBaseData(contaReceberInstance)
                if (!financeiroBaseService.save(contaReceberInstance, params)) {
                    render view: 'create', model: [contaReceberInstance: contaReceberInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'contaReceber.label', default: 'contaReceber'), contaReceberInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['contaReceber.id': contaReceberInstance.id]

                break
        }
    }

    def importarNFe() {
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
                if (!paramsEV.nfeEncoding) {
                    flash.message = message(code: "parametros.ev.encoding.exames.missing")
                    redirect(action: "edit", controller: "parametrosEV")
                    return
                }
                def xmlFiles = request.getFiles("files")
                def arquivoLog = ""
                try {
                    xmlFiles.each {
                        if (!it.contentType.equals("text/xml")) {
                            throw new ArquivoInvalido(it.fileItem.name)
                        }
                        def xml = new String(it.inputStream.getBytes(), paramsEV.nfeEncoding)
//                        def xml = new String(IOUtils.toString(it.inputStream).getBytes("UTF-8"), "ISO-8859-1")
                        def valida = financeiroBaseService.validaXml(xml, params.referencia)
                        if (valida.valido) {
                            def salvos = financeiroBaseService.fromXML(xml, params.referencia, params.dataVencimento)
                            arquivoLog += message(code: 'nfe.xml.sucesso', args: [salvos])
                        } else {
                            valida.erros.each {
                                arquivoLog += it.toString() + "//\n"
                            }
                        }
                    }
                } catch (ArquivoInvalido e) {
                    arquivoLog = message(code: 'default.invalid.file.message', args: [e.nomeArquivo])
                    redirect action: 'importarNFe'
                    return
                } catch (UnsupportedEncodingException ex) {
                    arquivoLog = message(code: 'nfe.xml.unsupportedencoding', args: [ex.message])
                    redirect(action: "edit", controller: "parametrosEV")
                    return
                }
                File arquivo = new File('log.txt')
                arquivo.write(arquivoLog)
                response.setContentType("application/octet-stream")
                response.setHeader("Content-disposition", "filename=${arquivo.name}")
                response.outputStream << arquivo.bytes

                render action: 'importarNFe'
                break
        }
    }

    def importarFaturas() {
        switch (request.method) {
            case 'GET': //aqui é quando a pagina é carregada
                break
            case 'POST': //aqui é quando o usua'rio clica pra importar
                def paramsEV = ParametrosEV.get(1)
                def arquivoLog = ""
                if (!paramsEV) {
                    arquivoLog += message(code: "parametros.ev.missing")
                    redirect(action: "edit", controller: "parametrosEV")
                    return
                }
                if (!paramsEV.nfeEncoding) {
                    arquivoLog += message(code: "parametros.ev.encoding.exames.missing")
                    redirect(action: "edit", controller: "parametrosEV")
                    return
                }
                def xmlFiles = request.getFiles("files") //aqui le o arquivo pra dentro desse xmlFiles aí
                try {
                    xmlFiles.each {
                        if (!it.contentType.equals("text/xml")) { //aqui eu to verificando se o tipo de arquivo que o usuario mandou é xml mesmo
                            throw new ArquivoInvalido(it.fileItem.name)
                        }
                        def xml = new String(it.inputStream.getBytes(), paramsEV.nfeEncoding) //aqui eu to montando uma string do xml passando o encoding dele
//                        def xml = new String(IOUtils.toString(it.inputStream).getBytes("UTF-8"), "ISO-8859-1")
                        def valida = financeiroBaseService.validaXmlFatura(xml, "") //valida
                        if (valida.valido) {
                            def retorno = financeiroBaseService.fromXMLFatura(xml, "") //cria
                            arquivoLog += message(code: 'nfe.xml.sucesso', args: [retorno.salvos])
                            retorno.itens.each{ item ->
                                if(!item.conta.hasErrors()) { //se a conta salvou sem hasErros (sem ter erros)
                                    arquivoLog += (char) 10 //adiciona ela no log
                                    arquivoLog += "Conta: ${item.conta.id}"
                                }else{
                                    //senão, fala no log que deu errado e mostra a mensagem de erro para aquela conta
                                    def numeroItem = item.numero
                                    ((ContaReceber)item.conta).errors.fieldErrors.each {erro ->
                                        arquivoLog += (char)10
                                        arquivoLog += "Erro ao gerar a conta ${numeroItem} ${(char)10}"
                                        arquivoLog += " -> campo: ${erro.field} rejeitou valor ${erro.rejectedValue}"

                                    }
                                }
                            }
                        } else {
                            valida.erros.each {
                                char lf = (char)10
                                arquivoLog += it.toString() + lf
                            }
                        }
                    }
                } catch (ArquivoInvalido e) {
                    arquivoLog += message(code: 'default.invalid.file.message', args: [e.nomeArquivo])
                    redirect action: 'importarFaturas'
                    return
                } catch (UnsupportedEncodingException ex) {
                    arquivoLog += message(code: 'nfe.xml.unsupportedencoding', args: [ex.message])
                    redirect(action: "edit", controller: "parametrosEV")
                    return
                }

                File arquivo = new File('log.txt')
                arquivo.write(arquivoLog)
                response.setContentType("application/octet-stream")
                response.setHeader("Content-disposition", "filename=${arquivo.name}")
                response.outputStream << arquivo.bytes

                redirect action: 'importarFaturas'
                break
        }
    }
}