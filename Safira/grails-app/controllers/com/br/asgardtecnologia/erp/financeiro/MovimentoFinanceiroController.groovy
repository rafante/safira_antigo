package com.br.asgardtecnologia.erp.financeiro
import ar.com.fdvs.dj.core.DynamicJasperHelper
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.*
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.builders.FastReportBuilder
import ar.com.fdvs.dj.domain.builders.GroupBuilder
import ar.com.fdvs.dj.domain.constants.*
import ar.com.fdvs.dj.domain.entities.DJGroup
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn
import com.br.asgardtecnologia.base.Utils
import com.br.asgardtecnologia.erp.base.BaseController
import com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios
import com.br.asgardtecnologia.erp.ev.ParametrosEV
import com.br.asgardtecnologia.exceptions.files.ArquivoInvalido
import com.br.asgardtecnologia.exceptions.xml.MissingTagException
import com.br.asgardtecnologia.exceptions.xml.TagException
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperService

import java.awt.*

class MovimentoFinanceiroController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]
    def movimentoFinanceiroService
    def reportsService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def movimentoFinanceiroList = paramsToCriteria(params)

        [movimentoFinanceiroList: movimentoFinanceiroList, movimentoFinanceiroListCount: movimentoFinanceiroList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [movimentoFinanceiroInstance: new MovimentoFinanceiro(params)]
                break
            case 'POST':
                MovimentoFinanceiro movimentoFinanceiroInstance = new MovimentoFinanceiro(params)
                setBaseData(movimentoFinanceiroInstance)
                if (!movimentoFinanceiroInstance.save(flush: true)) {
                    movimentoFinanceiroInstance.errors.each {
                        println it.fieldError
                    }
                    render view: 'create', model: [movimentoFinanceiroInstance: movimentoFinanceiroInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'movimentoFinanceiroInstance.label', default: 'MovimentoFinanceiro'), movimentoFinanceiroInstance.id])
                redirect action: 'show', id: movimentoFinanceiroInstance.id

                break
        }
    }

    def estornar(){
        if(request.method == "POST") {
            MovimentoFinanceiro movimentoFinanceiroInstance = MovimentoFinanceiro.read(params.id)
            movimentoFinanceiroInstance.estornar()
            flash.message = "Estorno realizado"
            redirect(action: 'show', id: params.id)
        }else{
            redirect(action: 'list')
        }
    }

    def show() {
        MovimentoFinanceiro movimentoFinanceiroInstance = MovimentoFinanceiro.read(params.id)
        if (!movimentoFinanceiroInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'movimentoFinanceiroInstance.label', default: 'MovimentoFinanceiro'), params.id])
            redirect action: 'list'
            return
        }

        [movimentoFinanceiroInstance: movimentoFinanceiroInstance]
    }

    def edit() {
        flash.message = message(code: 'movimento.messages.readonly')
        redirect action: 'show', id: params.id
    }

    def delete() {
        flash.message = message(code: 'movimento.messages.readonly')
        redirect action: 'show', id: params.id
    }

    def importarCaixaEV(){
        switch (request.method) {
            case 'GET':
                break
            case 'POST':
                def paramsEV = ParametrosEV.get(1)
                if(!paramsEV){
                    flash.message = message(code: "parametros.ev.missing")
                    redirect(action: "edit", controller: "parametrosEV")
                    return
                }
                if(!paramsEV.caixaEVEncoding){
                    flash.message = message(code: "parametros.ev.encoding.caixaev.missing")
                    redirect(action: "edit", controller: "parametrosEV")
                    return
                }
                def xmlFiles = request.getFiles("files")
                try {
                    xmlFiles.each {
                        if (!it.contentType.equals("text/xml")) {
                            throw new ArquivoInvalido(it.fileItem.name)
                        }
                        def xml = new String(((ByteArrayInputStream)it.inputStream).getBytes(), paramsEV.caixaEVEncoding)
                        def valida = movimentoFinanceiroService.validaXml(xml)
                        if(valida.valido){
                            def atendimentos = movimentoFinanceiroService.fromXML(xml)
                            if(!atendimentos){
                                flash.message = "Falha ao salvar"
                            }
                            def salvos = movimentoFinanceiroService.saveList(atendimentos)
                            flash.message = message(code: 'importacaoCaixa.xml.sucesso', args: [salvos])
                        }else{
                            valida.erros.each{
                                flash.message += it.toString() + "<br/>"
                            }
                        }
                    }
                } catch (ArquivoInvalido e) {
                    flash.message = message(code: 'default.invalid.file.message', args: [e.nomeArquivo])
                    redirect action: 'importarCaixaEV'
                    return
                }
                redirect action: 'importarCaixaEV'
                break
        }
    }

    def boleto (){

        def movimento = MovimentoFinanceiro.get(1)
        def infoBoletoList = []
        params._format = "PDF"
        params._name = "BoletoSantander" + movimento
        params._file = "boletoSantander"

        for (MovimentoFinanceiro item in infoBoletoList) {
            item.contaCorrente.agencia
            item.contaCorrente.banco.codigo
            item.dataEmissao
            item.contaCorrente.conta
            item.valor
        }

        params.VALOR = movimento.valor
        params.ARGENCIA = movimento.contaCorrente?.agencia

        def rep = 'boleto.jrxml'

        def reportDef = new JasperReportDef(
                name: rep,
                fileFormat: JasperExportFormat.PDF_FORMAT,
                reportData: infoBoletoList, //aqui são os dados de fato do report, veja que ele tá passando uma lista de itemEntrada
                parameters: params)

        response.contentType = "application/pdf"
        response.outputStream << (new JasperService()).generateReport(reportDef).toByteArray()
        response.outputStream.flush()
        response.outputStream.close()
    }


    def relatorio_movimentacao() {
        //Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder()

        Style headerLeft = reportsService.leftHeaderStyle()
        Style headerRight = reportsService.rightHeaderStyle()
        Style rightStyle = reportsService.baseStyleRight()
        Style leftStyle = reportsService.baseStyleLeft()
        /*Style baseComDivisoria = reportsService.baseStyleLeft()
        baseComDivisoria.setHorizontalAlign(HorizontalAlign.RIGHT)
        baseComDivisoria.setBorderTop(Border.PEN_1_POINT())*/



        AbstractColumn columnValorCredito = ColumnBuilder.getNew()
                .setColumnProperty("valorCredito", BigDecimal.class.getName())
                .setTitle("Crédito")
                .setWidth(new Integer(25))
                .setStyle(rightStyle)
                .setHeaderStyle(headerRight)
                .setPattern("R\$ ###,##0.00")
                .build()

        AbstractColumn columnValorDebito = ColumnBuilder.getNew()
                .setColumnProperty("valorDebito", BigDecimal.class.getName())
                .setTitle("Débito")
                .setWidth(new Integer(30))
                .setPattern("R\$ ###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(headerRight)
                .build()

        AbstractColumn columnSaldo = ColumnBuilder.getNew()
                .setColumnProperty("saldo", BigDecimal.class.getName())
                .setTitle("Saldo")
                .setWidth(new Integer(30))
                .setPattern("R\$ ###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(headerRight)
                .build()

        AbstractColumn columnDataEmissao = ColumnBuilder.getNew()
                .setColumnProperty("dataEmissao", Date.class.getName())
                .setTitle("Data   Emi.")
                .setWidth(new Integer(15))
                .setPattern("dd/MM/yy")
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnDataDocumento = ColumnBuilder.getNew()
                .setColumnProperty("dataDocumento", Date.class.getName())
                .setTitle("Data Doc.")
                .setWidth(new Integer(15))
                .setPattern("dd/MM/yy")
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnId = ColumnBuilder.getNew()
                .setColumnProperty("id", Long.class.getName())
                .setTitle("Id.")
                .setWidth(new Integer(15))
                .setFixedWidth(false)
                .setPattern("00000000")
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnConta = ColumnBuilder.getNew()
                .setColumnProperty("contasConta", String.class.getName())
                .setTitle("Conta")
                .setWidth(new Integer(15))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()
        //drb.addColumn("Conta", "contasConta.conta", String.class.getName(), 15, columnDetail, headerStyleLeft) *VERIFICAR SE ESTA CORRETO A PROPRIEDADE setColumnProperty

        AbstractColumn columnAgencia = ColumnBuilder.getNew()
                .setColumnProperty("contasAgencia.agencia", String.class.getName())
                .setTitle("Agencia")
                .setWidth(new Integer(15))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnDescricao = ColumnBuilder.getNew()
                .setColumnProperty("descricao", String.class.getName())
                .setTitle("Descriçao")
                .setWidth(new Integer(70))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnPlanoContas = ColumnBuilder.getNew()
                .setColumnProperty("planoContas.descricao", String.class.getName())
                .setTitle("Plano Contas")
                .setWidth(new Integer(30))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()




        DJGroupLabel glabelTotal = reportsService.rotulo('Subtotal:' )


        DJGroup g1 = reportsService.group(columnConta, glabelTotal, [(DJCalculation.SUM): [columnValorCredito, columnValorDebito, columnSaldo]], rightStyle)


        drb.addColumn(columnId)
                .addColumn(columnPlanoContas)
                .addColumn(columnDescricao)
                .addColumn(columnAgencia)
                .addColumn(columnConta)
                .addColumn(columnDataDocumento)
                .addColumn(columnDataEmissao)
                .addColumn(columnValorCredito)
                .addColumn(columnValorDebito)
                .addColumn(columnSaldo)
                .addGroup(g1)//
                .setTitle("Movimentação de Conta")
                .setSubtitle(reportsService.getSubtitle(params))
                .setUseFullPageWidth(true)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setGrandTotalLegend("TOTAL GERAL:")
                .addGlobalFooterVariable(columnSaldo, DJCalculation.SUM, rightStyle)


        DynamicReport dr = drb.build()

        def movimentacaoListCriteria = paramsToCriteria(params)
        movimentacaoListCriteria.sort { a, b -> a.contaCorrente?.id <=> b.contaCorrente?.id ?: a.dataEmissao <=> b.dataEmissao ?: a.id <=> b.id }

        BigDecimal saldo = 0
        def contaAnt

        def movimentacaoList = []
        for (MovimentoFinanceiro movCriteria in movimentacaoListCriteria) {
            def mov = [:]
            //mov["conta"] = "" + movCriteria.contaCorrente?.descricao
            mov["planoContas"] = movCriteria.planoContas
            mov["dataEmissao"] = movCriteria.dataEmissao
            mov["contasAgencia"] = movCriteria.contaCorrente
            mov["contasConta"] = movCriteria.contaCorrente?.conta

            if (saldo == null || contaAnt != mov.contaCorrente) {
                saldo = movCriteria.contaCorrente?.getPosicaoSaldo(movCriteria.dataEmissao) ?: 0

                def movCriteriaSaldoAnterior = mov.clone()
                movCriteriaSaldoAnterior["descricao"] = "Saldo Anterior"
                movCriteriaSaldoAnterior["saldo"] = saldo
                movimentacaoList << movCriteriaSaldoAnterior
            }

            saldo += movCriteria.valor

            mov["id"] = movCriteria.id
            mov["descricao"] = movCriteria.descricao
            mov["dataDocumento"] = movCriteria.dataDocumento
            mov["numeroDocumento"] = movCriteria.numeroDocumento
            mov["valor"] = movCriteria.valor
            mov["valorCredito"] = movCriteria.valorCredito
            mov["valorDebito"] = movCriteria.valorDebito
            mov["saldo"] = saldo
            movimentacaoList << mov

            contaAnt = mov.contaCorrente
        }

        JRDataSource ds = new JRBeanCollectionDataSource(movimentacaoList)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)

    }

}

