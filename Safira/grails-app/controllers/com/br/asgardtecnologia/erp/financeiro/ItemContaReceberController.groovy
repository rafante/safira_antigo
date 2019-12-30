package com.br.asgardtecnologia.erp.financeiro

import ar.com.fdvs.dj.core.DynamicJasperHelper
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.*
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.builders.FastReportBuilder
import ar.com.fdvs.dj.domain.builders.GroupBuilder
import ar.com.fdvs.dj.domain.constants.Border
import ar.com.fdvs.dj.domain.constants.GroupLayout
import ar.com.fdvs.dj.domain.constants.HorizontalAlign
import ar.com.fdvs.dj.domain.constants.LabelPosition
import ar.com.fdvs.dj.domain.constants.Page
import ar.com.fdvs.dj.domain.entities.DJGroup
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn
import com.br.asgardtecnologia.base.Utils
import com.br.asgardtecnologia.erp.base.BaseController
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.springframework.dao.DataIntegrityViolationException

class ItemContaReceberController extends BaseController {

    def hiddenActionButtons = ['create']

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    //aqui vc definiu um objeto do tipo FinanceiroBaseService isso aqui é um pouco complicado de entender e de explicar porque aqui ele usou um negócio
    //que chama injeção de dependencia. Tá vendo que aparece tipo um feijaozinho verde aqui sim significa que o intellij sacou que essa variavel aí
    //financeiroBaseService é um bean (bean é feijão em ingles, mas em java, bean é tipo um objeto solitário um pedaco de código que é utilizado pra todo lado
    // fazer esse def financeiroBaseService, é como se vc tivesse fazendo def financeiroBaseServcie = new FinanceiroBaseService() só que nao eh exatamente
    //a mesma coisa porque aqui uma o famoso padrão de projeto que eu tinha te falado. Aqui usa o padrão singleton
    def financeiroBaseService
    def reportsService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        redirect controller: 'contaReceber', action: 'list'
    }

    def estornar() {
        CompensacaoItemReceber compensacaoItemReceber = CompensacaoItemReceber.get(params.compensacao_id)

        def msg = financeiroBaseService.estornarParcela(compensacaoItemReceber, params);
        flash.message = resolveMessage(msg)

        redirect 'action': 'show', 'id': params.itemContaReceber.id
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [itemContaReceberInstance: new ItemContaReceber(params), hiddenActionButtons: hiddenActionButtons]
                break
            case 'POST':
                def itemContaReceberInstance = new ItemContaReceber(params)
                setBaseData(itemContaReceberInstance)
                if (!itemContaReceberInstance.save(flush: true)) {
                    render view: 'create', model: [itemContaReceberInstance: itemContaReceberInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemContaReceber.label', default: 'ItemContaReceber'), itemContaReceberInstance.id])
                redirect controller: 'contaReceber', action: 'edit', id: itemContaReceberInstance.contaReceber.id
                break
        }
    }

    def show() {
        def itemContaReceberInstance = ItemContaReceber.read(params.id)
        if (!itemContaReceberInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemContaReceber.label', default: 'ItemContaReceber'), params.id])
            redirect action: 'list'
            return
        }

        [itemContaReceberInstance: itemContaReceberInstance, hiddenActionButtons: hiddenActionButtons]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def itemContaReceberInstance = ItemContaReceber.read(params.id)
                if (!itemContaReceberInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemContaReceber.label', default: 'ItemContaReceber'), params.id])
                    redirect action: 'list'
                    return
                }

                [itemContaReceberInstance: itemContaReceberInstance, hiddenActionButtons: hiddenActionButtons]
                break
            case 'POST':
                def itemContaReceberInstance = ItemContaReceber.get(params.id)
                setBaseData(itemContaReceberInstance)
                if (!itemContaReceberInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemContaReceber.label', default: 'ItemContaReceber'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (itemContaReceberInstance.version > version) {
                        itemContaReceberInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'itemContaReceber.label', default: 'ItemContaReceber')] as Object[],
                                "Another user has updated this ItemContaReceber while you were editing")
                        render view: 'edit', model: [itemContaReceberInstance: itemContaReceberInstance]
                        return
                    }
                }

                itemContaReceberInstance.properties = params

                if (!itemContaReceberInstance.save(flush: true)) {
                    render view: 'edit', model: [itemContaReceberInstance: itemContaReceberInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'itemContaReceber.label', default: 'ItemContaReceber'), itemContaReceberInstance.id])
                redirect controller: 'contaReceber', action: 'edit', id: itemContaReceberInstance.contaReceber.id
                break
        }
    }

    def delete() {
        def itemContaReceberInstance = ItemContaReceber.get(params.id)
        if (!itemContaReceberInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemContaReceber.label', default: 'ItemContaReceber'), params.id])
            redirect action: 'list'
            return
        }

        try {
            itemContaReceberInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'itemContaReceber.label', default: 'ItemContaReceber'), params.id])
            redirect controller: 'contaReceber', action: 'edit', id: itemContaReceberInstance.contaReceber.id
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'itemContaReceber.label', default: 'ItemContaReceber'), params.id])
            redirect controller: 'contaReceber', action: 'edit', id: itemContaReceberInstance.contaReceber.id
        }
    }

    def arquivoRemessa() {
        //uai este é o controller o controller do ItemContaReceber e aqui é a action arquivoRemessa pois entao essa action pega as informaçoes do service certo ?
        //na verdade esta action pega as informacoes da view e passa pro service hmmmm o service é esse camarada aqui
        if (request.method == 'POST') { //se tiver em modo post entra dentro deste if que por sua vez
            def itensContaReceber = paramsToCriteria(params)
            File arquivoRemessa = financeiroBaseService.getArquivoRemessa(itensContaReceber)
            //entra dentro do getArquivoRemessa do FinanceiroBaseService

            //gera o arquivo
            if (arquivoRemessa.exists()) {
                response.setContentType("application/octet-stream")
                response.setHeader("Content-disposition", "filename=${arquivoRemessa.name}")
                response.outputStream << arquivoRemessa.bytes
                return
            }
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [itemContaReceberInstance: new ItemContaReceber(params)]
                break
            case 'POST':
                def itemContaReceberInstance
                if (params.id) itemContaReceberInstance = ItemContaReceber.get(params.id)
                else itemContaReceberInstance = new ItemContaReceber(params)

                setBaseData(itemContaReceberInstance)
                if (!itemContaReceberInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [itemContaReceberInstance: itemContaReceberInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemContaReceber.label', default: 'itemContaReceber'), itemContaReceberInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['itemContaReceber.id': itemContaReceberInstance.id]

                break
        }
    }

    def relatorio_analitico() {
        def agruparData = (params.agruparData == "true")
        def agruparParceiro = params.agruparParceiro == "true"

        def subtitle = ""
        if (agruparData) subtitle += "Agrupado por Data\\n"
        if (agruparParceiro) subtitle += "Agrupado por Parceiro\\n"
        if (params["dataVencimento.op"] == "eq") subtitle += "Vencimento igual a " + params["dataVencimento_holder"]
        if (params["dataVencimento.op"] == "between") subtitle += "Vencimento entre " + params["dataVencimento_holder"] + " e " + params["dataVencimento.high_holder"]

        FastReportBuilder drb = reportsService.reportBuilderBasico()

        //Estilos
        Style headerLeft = reportsService.leftHeaderStyle()
        Style headerRight = reportsService.rightHeaderStyle()
        Style rightStyle = reportsService.baseStyleRight()
        Style leftStyle = reportsService.baseStyleLeft()
        Style leftData = reportsService.dataStyleLeft()
        Style baseComDivisoria = reportsService.baseStyleLeft()
        baseComDivisoria.setHorizontalAlign(HorizontalAlign.RIGHT)
        baseComDivisoria.setBorderTop(Border.PEN_1_POINT())

        AbstractColumn columnCodigo = ColumnBuilder.getNew()
                .setColumnProperty("documentoParcela", String.class.getName())
                .setTitle("Código")
                .setWidth(new Integer(13))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnFornecedor = ColumnBuilder.getNew()
                .setColumnProperty("contaReceber.parceiroNegocios.nome", String.class.getName())
                .setTitle("Fornecedor")
                .setWidth(new Integer(60))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnDocumento = ColumnBuilder.getNew()
                .setColumnProperty("contaReceber.documento", String.class.getName())
                .setTitle("Documento")
                .setWidth(new Integer(15))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnContaDescricao = ColumnBuilder.getNew()
                .setColumnProperty("contaReceber.contaCorrente.descricao", String.class.getName())
                .setTitle("Conta")
                .setWidth(new Integer(30))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnDataDoc = ColumnBuilder.getNew()
                .setColumnProperty("contaReceber.dataDocumento", Date.class.getName())
                .setTitle("Emissão")
                .setWidth(new Integer(13))
                .setStyle(leftData)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnDataVenc = ColumnBuilder.getNew()
                .setColumnProperty("dataVencimento", Date.class.getName())
                .setTitle("Vencto.")
                .setWidth(new Integer(13))
                .setStyle(leftData)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnData = ColumnBuilder.getNew()
                .setColumnProperty("dataVencimento", String.class.getName())
                .setTitle("Vencimento:")
                .setWidth(new Integer(13))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnParceiro = ColumnBuilder.getNew()
                .setColumnProperty("contaReceber.parceiroNegocios.nome", String.class.getName())
                .setTitle("Parceiro:")
                .setWidth(new Integer(500))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
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

        DJGroupLabel glabelTotalVencimento = reportsService.rotulo('Subtotal para data:')
        if (agruparData && !agruparParceiro)
            glabelTotalVencimento.setStyle(baseComDivisoria)

        DJGroup g1 = reportsService.group(columnData, glabelTotalVencimento, [(DJCalculation.SUM): [columnValor, columnValorCompensado]], (agruparData && !agruparParceiro) ? baseComDivisoria : null)

        DJGroupLabel glabelTotalParceiro = reportsService.rotulo('Subtotal para o parceiro:', baseComDivisoria)

        glabelTotalParceiro.setStyle(baseComDivisoria)

        DJGroup g2 = reportsService.group(columnParceiro, glabelTotalParceiro, [(DJCalculation.SUM): [columnValor, columnValorCompensado]], baseComDivisoria)

        drb.addColumn(columnCodigo)
                .addColumn(columnFornecedor)
                .addColumn(columnDocumento)
                .addColumn(columnContaDescricao)
                .addColumn(columnDataDoc)
                .addColumn(columnDataVenc)
                .addColumn(columnValor)
                .addColumn(columnValorCompensado)

        if (agruparData) drb.addGroup(g1)
        if (agruparParceiro) drb.addGroup(g2)

        drb.setTitle("Relação Contas à Receber")
                .setSubtitle(reportsService.getSubtitle(params))
                .setGrandTotalLegend("TOTAL GERAL:")
                .addGlobalFooterVariable(columnValor, DJCalculation.SUM, rightStyle)
                .addGlobalFooterVariable(columnValorCompensado, DJCalculation.SUM, rightStyle)

        DynamicReport dr = drb.build()

        //def receberInstanceList = paramsToCriteria(params)
        def newOrderBy = params.orderBy
        if (params.orderBy.split("\\.").size() > 1)
            params.orderBy = "id"
        def receberInstanceList = paramsToCriteria(params)
        if (params.orderBy != newOrderBy) {
            if (newOrderBy == 'contaReceber.parceiroNegocios')
                receberInstanceList.sort { it.contaReceber.parceiroNegocios }
            else if (newOrderBy == 'contaReceber.dataEmissao')
                receberInstanceList.sort { it.contaReceber.dataEmissao }
            else
                receberInstanceList.sort { it."${params.orderBy}" }
        } else
            receberInstanceList.sort { it."${params.orderBy}" }

        if ((agruparParceiro) || ((agruparParceiro) && (agruparData))) {
            receberInstanceList.sort { a, b ->
                a.getParent().parceiroNegocios?.nome <=> b.getParent().parceiroNegocios?.nome ?:
                        a.dataVencimento <=> b.dataVencimento
            }
        } else {
            receberInstanceList.sort { a, b ->
                a.dataVencimento <=> b.dataVencimento ?:
                        a.getParent().parceiroNegocios?.nome <=> b.getParent().parceiroNegocios?.nome
            }
        }

        JRDataSource ds = new JRBeanCollectionDataSource(receberInstanceList)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }

//    def CustomExpression getCustomExpression() {
//        return new CustomExpression() {
//
//            public Object evaluate(Map fields, Map variables, Map parameters) {
//                Long item = (Long) fields.get("id")
//                String doc = (String) fields.get("contaPagar.documento")
//                return doc + '/' + item
//            }
//
//            public String getClassName() {
//                return String.class.getName();
//            }
//
//        };
//    }

}
