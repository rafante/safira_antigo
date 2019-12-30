package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.BaseController
import com.br.asgardtecnologia.erp.base.Empresa
import com.br.asgardtecnologia.erp.base.ParametrosGerais
import com.br.asgardtecnologia.erp.materiais.ItemMovimentoMaterial
import com.br.asgardtecnologia.erp.materiais.Material
import com.mrkonno.plugin.jrimum.dsl.BoletoDsl
import org.joda.time.DateTime
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeCobranca
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo
import org.jrimum.domkee.financeiro.banco.hsbc.TipoIdentificadorCNR
import org.springframework.dao.DataIntegrityViolationException

class CustomBoletoController extends BaseController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def messageService
    def customBoletoService

    def index() {
        redirect(action: "generateBoleto", params: params)
    }

    def teste() {
        customBoletoService.geraBoleto()
    }

    def generateBoleto() {
        if (request.method == 'POST') {
            def cedente = ParametrosFinanceiro.get(1)?.empresa ?: Empresa.get(1)
            def contasReceber = ContaReceber.withCriteria {
                if (params?.cliente?.id)
                    eq("parceiroNegocios", params.cliente)
            }

            if (!contasReceber.size()) {
                flash.message = "Nenhuma conta a receber encontrada "
                redirect(action: 'generateBoleto')
                return
            }

            String cNome = cedente.nome
            String cDocumento = cedente.documentoFormatado
            String cUf = cedente.uf
            String cLogradouro = cedente.logradouro
            String cLocalidade = cedente.cidade
            String cCep = cedente.cep
            String cBairro = cedente.bairro
            String cNumero = cedente.numero
            try {
                if (!cNome) {
                    throw new Exception("Não é possível prosseguir pois a empresa cedente não tem nome cadastrado")
                }
                if (!cUf || !cLogradouro || !cLocalidade || !cCep || !cNumero) {
                    throw new Exception("Não é possível prosseguir pois os dados de endereço da empresa cedente estão incompletos")
                }//
            } catch (e) {
                flash.message += e.message
                redirect(action: 'generateBoleto')
                return
            }

////            ContaCorrente conta = ContaCorrente.read(params."contaCorrente.id")
            for (ContaReceber contaReceber in contasReceber) {
                String sNome = contaReceber.parceiroNegocios.nome
                String sDocumento = contaReceber.parceiroNegocios.documentoFormatado
                String sUf = contaReceber.parceiroNegocios.uf
                String sLogradouro = contaReceber.parceiroNegocios.logradouro
                String sLocalidade = contaReceber.parceiroNegocios.cidade
                String sCep = contaReceber.parceiroNegocios.cep
                String sBairro = contaReceber.parceiroNegocios.bairro
                String sNumero = contaReceber.parceiroNegocios.numero

                try {
                    if (!sNome) {
                        throw new Exception("Não é possível gerar boleto para o documento ${contaReceber.documento}")
                    }
                    if (!sUf || !sLogradouro || !sLocalidade || !sCep || !sBairro || !sNumero) {
                        throw new Exception("Não é possível gerar boleto para o documento ${contaReceber.documento} pois há problemas no endereço do parceiro")
                    }

                    def dadosCedente = [
                            nome      : cNome,
                            documento : cDocumento,
                            uf        : cUf,
                            logradouro: cLogradouro,
                            localidade: cLocalidade,
                            cep       : cCep,
                            bairro    : cBairro,
                            numero    : cNumero
                    ]
                    def dadosSacado = [
                            nome      : sNome,
                            documento : sDocumento,
                            uf        : sUf,
                            logradouro: sLogradouro,
                            localidade: sLocalidade,
                            cep       : sCep,
                            bairro    : sBairro,
                            numero    : sNumero
                    ]
                    def parametros = [
                            "caminho"         : params.caminho, doc: contaReceber.documento,
                            "tipoDeTitulo.id": params."tipoDeTitulo.id",
                            valorTotal        : params.valorTotal,
                            nossoNumeroDigito : params.nossoNumeroDigito,
                            nossoNumero       : params.nossoNumero,
                            numDocumento      : params.documento,
                            dataVencimento    : params.dataDocumento,
                            conta             : params.conta,
                            agencia           : params.agencia,
                            "banco.id"        : params."banco.id",
                            contaDigito       : params.contaDigito,
                            agenciaDigito     : params.agenciaDigito
                    ]

                    customBoletoService.geraBoleto(dadosCedente, dadosSacado, parametros)
                    flash.message += "<br/><br/>Título ${contaReceber.documento} gerado<br/><br/>"
                } catch (e) {
                    flash.message += "<br/><br/>Título ${contaReceber.documento}<br/><br/>"
                    flash.message += e.message
                }

            }
            redirect(action: 'generateBoleto')
        }

    }


    def list(Integer max) {
        redirect(action: "generateBoleto", params: params)
    }

    def create() {
        [boletoInstance: new CustomBoleto(params)]
    }

    def save() {
        def boletoInstance = new CustomBoleto(params)
        if (!boletoInstance.save(flush: true)) {
            render(view: "create", model: [boletoInstance: boletoInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'boleto.label', default: 'CustomBoleto'), boletoInstance.id])
        redirect(action: "show", id: boletoInstance.id)
    }

    def show(Long id) {
        def boletoInstance = CustomBoleto.get(id)
        if (!boletoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'boleto.label', default: 'CustomBoleto'), id])
            redirect(action: "list")
            return
        }

        [boletoInstance: boletoInstance]
    }

    def edit(Long id) {
        def boletoInstance = CustomBoleto.get(id)
        if (!boletoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'boleto.label', default: 'CustomBoleto'), id])
            redirect(action: "list")
            return
        }

        [boletoInstance: boletoInstance]
    }

    def update(Long id, Long version) {
        def boletoInstance = CustomBoleto.get(id)
        if (!boletoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'boleto.label', default: 'CustomBoleto'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (boletoInstance.version > version) {
                boletoInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'boleto.label', default: 'CustomBoleto')] as Object[],
                        "Another user has updated this CustomBoleto while you were editing")
                render(view: "edit", model: [boletoInstance: boletoInstance])
                return
            }
        }

        boletoInstance.properties = params

        if (!boletoInstance.save(flush: true)) {
            render(view: "edit", model: [boletoInstance: boletoInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'boleto.label', default: 'CustomBoleto'), boletoInstance.id])
        redirect(action: "show", id: boletoInstance.id)
    }

    def delete(Long id) {
        def boletoInstance = CustomBoleto.get(id)
        if (!boletoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'boleto.label', default: 'CustomBoleto'), id])
            redirect(action: "list")
            return
        }

        try {
            boletoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'boleto.label', default: 'CustomBoleto'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'boleto.label', default: 'CustomBoleto'), id])
            redirect(action: "show", id: id)
        }
    }

}
