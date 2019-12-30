package com.br.asgardtecnologia.erp.financeiro

import ar.com.fdvs.dj.core.DynamicJasperHelper
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.DJCalculation
import ar.com.fdvs.dj.domain.DJGroupLabel
import ar.com.fdvs.dj.domain.DynamicReport
import ar.com.fdvs.dj.domain.Style
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.builders.FastReportBuilder
import ar.com.fdvs.dj.domain.constants.Page
import ar.com.fdvs.dj.domain.entities.DJGroup
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn
import com.br.asgardtecnologia.base.Utils
import com.br.asgardtecnologia.erp.base.BaseController
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.springframework.dao.DataIntegrityViolationException

class ItemContaPagarController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def financeiroBaseService
    def reportsService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        redirect controller: 'contaPagar', action: 'list'
    }

    def estornar() {
        CompensacaoItemPagar compensacaoItemPagar = CompensacaoItemPagar.get(params.compensacao_id)

        def msg = financeiroBaseService.estornarParcela(compensacaoItemPagar, params);
        flash.message = resolveMessage(msg)

        redirect 'action': 'show', 'id': params.itemContaPagar.id
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [itemContaPagarInstance: new ItemContaPagar(params)]
                break
            case 'POST':
                def itemContaPagarInstance = new ItemContaPagar(params)
                setBaseData(itemContaPagarInstance)
                if (!itemContaPagarInstance.save(flush: true)) {
                    render view: 'create', model: [itemContaPagarInstance: itemContaPagarInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemContaPagar.label', default: 'ItemContaPagar'), itemContaPagarInstance.id])
                redirect controller: 'contaPagar', action: 'edit', id: itemContaPagarInstance.contaPagar.id
                break
        }
    }

    def show() {
        def itemContaPagarInstance = ItemContaPagar.read(params.id)
        if (!itemContaPagarInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemContaPagar.label', default: 'ItemContaPagar'), params.id])
            redirect action: 'list'
            return
        }

        [itemContaPagarInstance: itemContaPagarInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def itemContaPagarInstance = ItemContaPagar.read(params.id)
                if (!itemContaPagarInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemContaPagar.label', default: 'ItemContaPagar'), params.id])
                    redirect action: 'list'
                    return
                }

                [itemContaPagarInstance: itemContaPagarInstance]
                break
            case 'POST':
                def itemContaPagarInstance = ItemContaPagar.get(params.id)
                setBaseData(itemContaPagarInstance)
                if (!itemContaPagarInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemContaPagar.label', default: 'ItemContaPagar'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (itemContaPagarInstance.version > version) {
                        itemContaPagarInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'itemContaPagar.label', default: 'ItemContaPagar')] as Object[],
                                "Another user has updated this ItemContaPagar while you were editing")
                        render view: 'edit', model: [itemContaPagarInstance: itemContaPagarInstance]
                        return
                    }
                }

                itemContaPagarInstance.properties = params

                if (!itemContaPagarInstance.save(flush: true)) {
                    render view: 'edit', model: [itemContaPagarInstance: itemContaPagarInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'itemContaPagar.label', default: 'ItemContaPagar'), itemContaPagarInstance.id])
                redirect controller: 'contaPagar', action: 'edit', id: itemContaPagarInstance.contaPagar.id
                break
        }
    }

    def delete() {
        def itemContaPagarInstance = ItemContaPagar.get(params.id)
        if (!itemContaPagarInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemContaPagar.label', default: 'ItemContaPagar'), params.id])
            redirect action: 'list'
            return
        }

        try {
            itemContaPagarInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'itemContaPagar.label', default: 'ItemContaPagar'), params.id])
            redirect controller: 'contaPagar', action: 'edit', id: itemContaPagarInstance.contaPagar.id
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'itemContaPagar.label', default: 'ItemContaPagar'), params.id])
            redirect controller: 'contaPagar', action: 'edit', id: itemContaPagarInstance.contaPagar.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [itemContaPagarInstance: new ItemContaPagar(params)]
                break
            case 'POST':
                def itemContaPagarInstance
                if (params.id) itemContaPagarInstance = ItemContaPagar.get(params.id)
                else itemContaPagarInstance = new ItemContaPagar(params)

                setBaseData(itemContaPagarInstance)
                if (!itemContaPagarInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [itemContaPagarInstance: itemContaPagarInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemContaPagar.label', default: 'itemContaPagar'), itemContaPagarInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['itemContaPagar.id': itemContaPagarInstance.id]

                break
        }
    }

    def relatorio_analitico() {
// EXEMPLO DE USO DOS PARÂMETROS ADICIONAIS!
//        def x = params.agruparParceiro
//        def z = params.agruparData
//        render x
//        render z
//        return

        def agruparData = params.agruparData
        def agruparParceiro = params.agruparParceiro

        FastReportBuilder drb = new FastReportBuilder()

        //Estilos
        Style headerLeft = reportsService.leftHeaderStyle()
        Style headerRight = reportsService.rightHeaderStyle()
        Style rightStyle = reportsService.baseStyleRight()
        Style leftStyle = reportsService.baseStyleLeft()

        //Colunas
        AbstractColumn columnCodigo = ColumnBuilder.getNew()
                .setColumnProperty("documentoParcela", String.class.getName()).setTitle("Codigo")
                .setWidth(new Integer(20))
                .setHeaderStyle(headerLeft)
                .setStyle(leftStyle)
                .build()

        AbstractColumn columnFornecedor = ColumnBuilder.getNew()
                .setColumnProperty("contaPagar.parceiroNegocios.nome", String.class.getName()).setTitle("Fornecedor")
                .setWidth(new Integer(20))
                .setHeaderStyle(headerLeft)
                .setStyle(leftStyle)
                .build()

        AbstractColumn columnDocumento = ColumnBuilder.getNew()
                .setColumnProperty("contaPagar.documento", String.class.getName()).setTitle("Documento")
                .setWidth(new Integer(30))
                .setHeaderStyle(headerLeft)
                .setStyle(leftStyle)
                .setPattern("R\$ ###,##0.00")
                .build()

        AbstractColumn columnConta = ColumnBuilder.getNew()
                .setColumnProperty("contaPagar.contaCorrente.descricaoRel", String.class.getName()).setTitle("Conta")
                .setWidth(new Integer(20))
                .setHeaderStyle(headerLeft)
                .setStyle(leftStyle)
                .build()

        AbstractColumn columnValor = ColumnBuilder.getNew()
                .setColumnProperty("valor", BigDecimal.class.getName()).setTitle("Valor")
                .setWidth(new Integer(20))
                .setStyle(rightStyle)
                .setHeaderStyle(headerRight)
                .setPattern("R\$ ###,##0.00")
                .build()

        AbstractColumn columnValorCompensado = ColumnBuilder.getNew()
                .setColumnProperty("valorCompensado", BigDecimal.class.getName()).setTitle("Compensado")
                .setWidth(new Integer(20))
                .setStyle(rightStyle)
                .setHeaderStyle(headerRight)
                .setPattern("R\$ ###,##0.00")
                .build()

        AbstractColumn columnDataDocumento = ColumnBuilder.getNew()
                .setColumnProperty("contaPagar.dataDocumento", Date.class.getName()).setTitle("Emissão")
                .setWidth(new Integer(20))
                .setHeaderStyle(headerRight)
                .setStyle(rightStyle)
                .build()

        AbstractColumn columnDataVencimento = ColumnBuilder.getNew()
                .setColumnProperty("dataVencimento", Date.class.getName()).setTitle("Vencto.")
                .setWidth(new Integer(20))
                .setHeaderStyle(headerRight)
                .setStyle(rightStyle)
                .build()

        //Agrupadores
        DJGroupLabel label = reportsService.rotulo('Subtotal')

        DJGroup g3 = reportsService.group(columnFornecedor, label, [(DJCalculation.SUM): [columnValor, columnValorCompensado]], rightStyle)

        drb.addColumn(columnCodigo)
                .addColumn(columnFornecedor)
                .addColumn(columnDocumento)
                .addColumn(columnConta)
                .addColumn(columnValor)
                .addGroup(g3)
                .addColumn(columnValorCompensado)
                .addColumn(columnDataDocumento)
                .addColumn(columnDataVencimento)
                .setTitle("Relação Contas à Pagar")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setGrandTotalLegend("TOTAL GERAL:")
                .addGlobalFooterVariable(columnValor, DJCalculation.SUM, rightStyle)
                .addGlobalFooterVariable(columnValorCompensado, DJCalculation.SUM, rightStyle)
                .setSubtitle(reportsService.getSubtitle(params))

        DynamicReport dr = drb.build()


        def newOrderBy = params.orderBy
        if (params.orderBy.split("\\.").size() > 1)
            params.orderBy = "id"
        def pagarInstanceList = paramsToCriteria(params)
        if (params.orderBy != newOrderBy) {
            if (newOrderBy == 'contaPagar.parceiroNegocios')
                pagarInstanceList.sort { it.getContaPagar().getParceiroNegocios() }
            else if (newOrderBy == 'contaPagar.dataEmissao') {
                pagarInstanceList.sort { it.getContaPagar().getDataEmissao() }
            } else
                pagarInstanceList.sort { it."${params.orderBy}" }
        } else
            pagarInstanceList.sort { it."${params.orderBy}" }

        if ((agruparParceiro) || ((agruparParceiro) && (agruparData))) {
            pagarInstanceList.sort { a, b ->
                a.getParent().parceiroNegocios?.nome <=> b.getParent().parceiroNegocios?.nome ?:
                        a.dataVencimento <=> b.dataVencimento
            }
        } else {
            pagarInstanceList.sort { a, b ->
                a.dataVencimento <=> b.dataVencimento ?:
                        a.getParent().parceiroNegocios?.nome <=> b.getParent().parceiroNegocios?.nome
            }
        }

        JRDataSource ds = new JRBeanCollectionDataSource(pagarInstanceList)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }
}
