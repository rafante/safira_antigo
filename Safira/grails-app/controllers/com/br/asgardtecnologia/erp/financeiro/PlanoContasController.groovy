package com.br.asgardtecnologia.erp.financeiro

import ar.com.fdvs.dj.core.DynamicJasperHelper
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.DJCalculation
import ar.com.fdvs.dj.domain.DJGroupLabel
import ar.com.fdvs.dj.domain.DynamicReport
import ar.com.fdvs.dj.domain.Style
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.builders.FastReportBuilder
import ar.com.fdvs.dj.domain.constants.Border
import ar.com.fdvs.dj.domain.constants.Font
import ar.com.fdvs.dj.domain.constants.GroupLayout
import ar.com.fdvs.dj.domain.constants.HorizontalAlign
import ar.com.fdvs.dj.domain.constants.Page
import ar.com.fdvs.dj.domain.constants.Transparency
import ar.com.fdvs.dj.domain.constants.VerticalAlign
import ar.com.fdvs.dj.domain.entities.DJGroup
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn
import com.br.asgardtecnologia.base.Utils
import com.br.asgardtecnologia.erp.base.BaseController
import com.br.asgardtecnologia.erp.materiais.EntradaMaterial
import com.br.asgardtecnologia.erp.materiais.ItemEntrada
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.springframework.dao.DataIntegrityViolationException

import java.awt.Color
import java.text.SimpleDateFormat

class PlanoContasController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def reportsService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def planoContasInstanceList = paramsToCriteria(params)
        [planoContasInstanceList: planoContasInstanceList, planoContasInstanceTotal: planoContasInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [planoContasInstance: new PlanoContas(params)]
                break
            case 'POST':
                def planoContasInstance = new PlanoContas(params)
                setBaseData(planoContasInstance)
                if (!planoContasInstance.save(flush: true)) {
                    render view: 'create', model: [planoContasInstance: planoContasInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'planoContas.label', default: 'PlanoContas'), planoContasInstance.id])
                redirect action: 'show', id: planoContasInstance.id
                break
        }
    }

    def show() {
        def planoContasInstance = PlanoContas.read(params.id)
        if (!planoContasInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'planoContas.label', default: 'PlanoContas'), params.id])
            redirect action: 'list'
            return
        }

        [planoContasInstance: planoContasInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def planoContasInstance = PlanoContas.read(params.id)
                if (!planoContasInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'planoContas.label', default: 'PlanoContas'), params.id])
                    redirect action: 'list'
                    return
                }

                [planoContasInstance: planoContasInstance]
                break
            case 'POST':
                def planoContasInstance = PlanoContas.get(params.id)
                setBaseData(planoContasInstance)
                if (!planoContasInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'planoContas.label', default: 'PlanoContas'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (planoContasInstance.version > version) {
                        planoContasInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'planoContas.label', default: 'PlanoContas')] as Object[],
                                "Another user has updated this PlanoContas while you were editing")
                        render view: 'edit', model: [planoContasInstance: planoContasInstance]
                        return
                    }
                }

                planoContasInstance.properties = params

                if (!planoContasInstance.save(flush: true)) {
                    render view: 'edit', model: [planoContasInstance: planoContasInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'planoContas.label', default: 'PlanoContas'), planoContasInstance.id])
                redirect action: 'show', id: planoContasInstance.id
                break
        }
    }

    def delete() {
        def planoContasInstance = PlanoContas.get(params.id)
        if (!planoContasInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'planoContas.label', default: 'PlanoContas'), params.id])
            redirect action: 'list'
            return
        }

        try {
            planoContasInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'planoContas.label', default: 'PlanoContas'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'planoContas.label', default: 'PlanoContas'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [planoContasInstance: new PlanoContas(params)]
                break
            case 'POST':
                def planoContasInstance
                if (params.id) planoContasInstance = PlanoContas.get(params.id)
                else planoContasInstance = new PlanoContas(params)

                setBaseData(planoContasInstance)
                if (!planoContasInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [planoContasInstance: planoContasInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'planoContas.label', default: 'planoContas'), planoContasInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['planoContas.id': planoContasInstance.id]

                break
        }
    }

    def listagem() {
        //Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder()

        Style numberStyle = new Style("numberStyle");
        numberStyle.setFont(new Font(7, Font._FONT_VERDANA, true));
        numberStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

        drb.addColumn("Codigo", "codigo", String.class.getName(), 8)
        drb.addColumn("Descrição", "descricao", String.class.getName(), 60)
                .setTitle("Listagem de Plano de Contas")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setSubtitle(reportsService.getSubtitle(params))

                //.setPageSizeAndOrientation(Page.Page_A4_Landscape())

        DynamicReport dr = drb.build()

        def planoContasInstanceList = paramsToCriteria(params)

        JRDataSource ds = new JRBeanCollectionDataSource(planoContasInstanceList)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }

    def balancete() {
        def planoContasInstanceList = PlanoContas.list()

        Date inicio = params.periodo ?: Date.parse("dd/MM/yyyy", "01/01/1900")
        Date fim = params.periodo_high ?: params.periodo ?: Date.parse("dd/MM/yyyy", "31/12/2999")

        def contasPagar = ContaPagar.findAllByDataDocumentoBetween(inicio, fim)
        def contasReceber = ContaReceber.findAllByDataDocumentoBetween(inicio, fim)

        def movFinanceiros = MovimentoFinanceiro.withCriteria {
            and{
                between("dataDocumento", inicio, fim)
                isNotNull("planoContas")
                and{
                    ne("estornado", true)
                    ne("eEstorno", true)
                    not{
                        like("descricao", "Estornado%")
                    }
                }
            }
        }

        contasPagar.each { conta ->
            if (!planoContasInstanceList.contains(conta.planoContas))
                planoContasInstanceList << conta.planoContas
        }

        contasReceber.each { conta ->
            if (!planoContasInstanceList.contains(conta.planoContas))
                planoContasInstanceList << conta.planoContas
        }

        movFinanceiros.each { movimento ->
            if (!planoContasInstanceList.contains(movimento.planoContas))
                planoContasInstanceList << movimento.planoContas
        }

        contasPagar.sort { a, b ->
            a.planoContas.codigo <=> b.planoContas.codigo
        }

        contasReceber.sort { a, b ->
            a.planoContas.codigo <=> b.planoContas.codigo
        }

        movFinanceiros.sort { a, b ->
            a.planoContas.codigo <=> b.planoContas.codigo
        }

        BigDecimal valor = 0
        String codigo = ""
        contasPagar.each {
            if (codigo != it.planoContas.codigo) {
                codigo = it.planoContas.codigo
                valor = 0
            }
            valor -= it.valorTotal
            print("${it.planoContas.descricao}: ${it.planoContas.codigo} valor: ${it.valorTotal} total: ${valor}")
        }
        valor = 0
        codigo = ""
        contasReceber.each {
            if (codigo != it.planoContas.codigo) {
                codigo = it.planoContas.codigo
                valor = 0
            }
            valor += it.valorTotal
            print("${it.planoContas.descricao}: ${it.planoContas.codigo} valor: ${it.valorTotal} total: ${valor}")
        }
        valor = 0
        codigo = ""
        movFinanceiros.each {
            if (codigo != it.planoContas.codigo) {
                codigo = it.planoContas.codigo
                valor = 0
            }
            valor += it.valor
            print("${it.planoContas.descricao}: ${it.planoContas.codigo} valor: ${it.valor} total: ${valor}")
        }

        //a partir daqui
        def ret = []
        planoContasInstanceList.each { PlanoContas plano ->
            def creditos = []

            contasReceber.each { ContaReceber conta ->
                if (conta.planoContas.id == plano.id)
                    creditos << conta
            }
            BigDecimal creditosValor = 0
            creditos.each {
                creditosValor += it.valorTotal
            }

            //Movimentos financeiros
            creditos.clear()
            movFinanceiros.each { MovimentoFinanceiro movimento ->
                if (movimento.planoContas.id == plano.id && movimento.valor > 0)
                    creditos << movimento
            }
            creditos.each {
                creditosValor += it.valor
            }

            def debitos = []
            contasPagar.each { ContaPagar conta ->
                if (conta.planoContas.id == plano.id)
                    debitos << conta
            }
            BigDecimal debitosValor = 0
            debitos.each {
                debitosValor += it.valorTotal
            }

            //Movimentos financeiros
            debitos.clear()
            movFinanceiros.each { MovimentoFinanceiro movimento ->
                if (movimento.planoContas.id == plano.id && movimento.valor < 0)
                    debitos << movimento
            }
            debitos.each {
                debitosValor += it.valor
            }
            ret << [plano: plano,
                    codigo: plano.codigo,
                    descricao: plano.descricao,
                    debitos: new BigDecimal(Math.abs(debitosValor)),
                    creditos: new BigDecimal(Math.abs(creditosValor))]
        }

        ret = ret.sort { a, b ->
            a.plano.filhos.size() <=> b.plano.filhos.size()
        }

        ret.each { r ->
            BigDecimal creditosValor = 0
            BigDecimal debitosValor = 0

            def children = r.plano.allSubChildren()
            if (children.size()) {
                children.each { child ->
                    def pl = ret.find { it.codigo == child.codigo }
                    creditosValor += pl.creditos
                    debitosValor += pl.debitos
                }

                r.creditos += creditosValor
                r.debitos += debitosValor
            }
        }

        ret.each{
            if(it.creditos == 0)
                it.creditos = null
            if(it.debitos == 0)
                it.debitos = null
        }

        ret = ret.sort { a, b ->
            a.plano.codigo <=> b.plano.codigo
        }

        if (params.omiteZerados == "true") {
            ret.removeAll {
                it.debitos == null && it.creditos == null
            }
        }
        FastReportBuilder drb = new FastReportBuilder()

        Style leftStyle = reportsService.baseStyleLeft()
        Style rightStyle = reportsService.baseStyleRight()
        Style rightStyleHeader = reportsService.rightHeaderStyle()
        Style leftStyleHeader = reportsService.leftHeaderStyle()

        AbstractColumn columnValorCredito = ColumnBuilder.getNew()
                .setColumnProperty("creditos", BigDecimal.class.getName()).setTitle("Créditos")
                .setWidth(new Integer(20))
                .setPattern("R\$ ###,##0.00")
                .setHeaderStyle(leftStyleHeader)
                .setStyle(leftStyle)
                .build()

        AbstractColumn columnValorDebito = ColumnBuilder.getNew()
                .setColumnProperty("debitos", BigDecimal.class.getName()).setTitle("Débitos")
                .setWidth(new Integer(20))
                .setPattern("R\$ ###,##0.00")
                .setHeaderStyle(leftStyleHeader)
                .setStyle(leftStyle)
                .build()

       /* AbstractColumn columnSaldo = ColumnBuilder.getNew()
                .setColumnProperty("saldo", BigDecimal.class.getName()).setTitle("Saldo")
                .setWidth(new Integer(20))
                .setPattern("R\$ ###,##0.00")
                .setHeaderStyle(leftStyleHeader)
                .setStyle(leftStyle)
                .build()*/

        AbstractColumn columnCodigo = ColumnBuilder.getNew()
                .setColumnProperty("codigo", String.class.getName()).setTitle("Codigo")
                .setWidth(new Integer(20))//
                .setHeaderStyle(leftStyleHeader)
                .setStyle(leftStyle)
                .build()

        AbstractColumn columnDescricao = ColumnBuilder.getNew()
                .setColumnProperty("descricao", String.class.getName()).setTitle("Descricao")
                .setWidth(new Integer(60))//
                .setHeaderStyle(leftStyleHeader)
                .setStyle(leftStyle)
                .build()

        //DJGroupLabel glabelTotal = reportsService.rotulo('Total:', rightStyle)

        //DJGroup g1 = reportsService.group(columnCodigo, glabelTotal, [(DJCalculation.SUM): [columnValorCredito, columnValorDebito]],rightStyle)

       


        drb.addColumn(columnCodigo)
                //.addGroup(g1)
                .addColumn(columnDescricao)
                .addColumn(columnValorCredito)
                .addColumn(columnValorDebito)
        /*drb.addColumn(columnDescricao)
        drb.addColumn(columnValorCredito)
        drb.addColumn(columnValorDebito)*/
                .setTitle("Balancete")
                .setSubtitle("Período de ${new SimpleDateFormat("dd/MM/yyyy").format(inicio)} a ${new SimpleDateFormat("dd/MM/yyyy").format(fim)}")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setSubtitle(reportsService.getSubtitle(params))
                .setGrandTotalLegend("TOTAL GERAL: ")
                .addGlobalFooterVariable(columnValorCredito, DJCalculation.SUM, leftStyle)
                .addGlobalFooterVariable(columnValorDebito, DJCalculation.SUM, leftStyle)




        DynamicReport dr = drb.build()

        JRDataSource ds = new JRBeanCollectionDataSource(ret)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }
}
