package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.erp.base.BaseController
import ar.com.fdvs.dj.core.DynamicJasperHelper
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.DJCalculation
import ar.com.fdvs.dj.domain.DJGroupLabel
import ar.com.fdvs.dj.domain.DynamicReport
import ar.com.fdvs.dj.domain.StringExpression
import ar.com.fdvs.dj.domain.Style
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.builders.FastReportBuilder
import ar.com.fdvs.dj.domain.builders.GroupBuilder
import ar.com.fdvs.dj.domain.constants.Border
import ar.com.fdvs.dj.domain.constants.GroupLayout
import ar.com.fdvs.dj.domain.constants.HorizontalAlign
import ar.com.fdvs.dj.domain.constants.LabelPosition
import ar.com.fdvs.dj.domain.constants.VerticalAlign
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
import net.sf.jasperreports.engine.type.OrientationEnum
import org.springframework.dao.DataIntegrityViolationException

import java.awt.*

class MovimentoMaterialController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def movimentoMaterialService

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def movimentoMaterialInstanceList = paramsToCriteria(params)
        [movimentoMaterialInstanceList: movimentoMaterialInstanceList, movimentoMaterialInstanceTotal: movimentoMaterialInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [movimentoMaterialInstance: new MovimentoMaterial(params)]
                break
            case 'POST':
                def movimentoMaterialInstance = new MovimentoMaterial(params)
                setBaseData(movimentoMaterialInstance)
                if (!movimentoMaterialService.save(movimentoMaterialInstance)) {
                    render view: 'create', model: [movimentoMaterialInstance: movimentoMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'movimentoMaterial.label', default: 'MovimentoMaterial'), movimentoMaterialInstance.id])
                redirect action: 'show', id: movimentoMaterialInstance.id
                break
        }
    }

    def show() {
        switch (request.method) {
            case 'GET':
                def movimentoMaterialInstance = MovimentoMaterial.read(params.id)
                if (!movimentoMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'movimentoMaterial.label', default: 'MovimentoMaterial'), params.id])
                    redirect action: 'list'
                    return
                }

                [movimentoMaterialInstance: movimentoMaterialInstance]
                break
            case 'POST':
                def movimentoMaterialInstance = MovimentoMaterial.get(params.id)
                setBaseData(movimentoMaterialInstance)
                if (!movimentoMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'movimentoMaterial.label', default: 'MovimentoMaterial'), params.id])
                    redirect action: 'list'
                    return
                }
                if (!validaEfetivada(movimentoMaterialInstance)) return

                if (params.version) {
                    def version = params.version.toLong()
                    if (movimentoMaterialInstance.version > version) {
                        movimentoMaterialInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'movimentoMaterial.label', default: 'MovimentoMaterial')] as Object[],
                                "Another user has updated this MovimentoMaterial while you were editing")
                        render view: 'edit', model: [movimentoMaterialInstance: movimentoMaterialInstance]
                        return
                    }
                }

                movimentoMaterialInstance.status = params.status

                if (!movimentoMaterialService.save(movimentoMaterialInstance)) {
                    render view: 'edit', model: [movimentoMaterialInstance: movimentoMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'movimentoMaterial.label', default: 'MovimentoMaterial'), movimentoMaterialInstance.id])
                redirect action: 'show', id: movimentoMaterialInstance.id
                break
        }
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def movimentoMaterialInstance = MovimentoMaterial.read(params.id)
                if (!movimentoMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'movimentoMaterial.label', default: 'MovimentoMaterial'), params.id])
                    redirect action: 'list'
                    return
                }
                if (!validaEfetivada(movimentoMaterialInstance)) return

                [movimentoMaterialInstance: movimentoMaterialInstance]
                break
            case 'POST':
                def movimentoMaterialInstance = MovimentoMaterial.get(params.id)
                setBaseData(movimentoMaterialInstance)
                if (!movimentoMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'movimentoMaterial.label', default: 'MovimentoMaterial'), params.id])
                    redirect action: 'list'
                    return
                }
                if (!validaEfetivada(movimentoMaterialInstance)) return

                if (params.version) {
                    def version = params.version.toLong()
                    if (movimentoMaterialInstance.version > version) {
                        movimentoMaterialInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'movimentoMaterial.label', default: 'MovimentoMaterial')] as Object[],
                                "Another user has updated this MovimentoMaterial while you were editing")
                        render view: 'edit', model: [movimentoMaterialInstance: movimentoMaterialInstance]
                        return
                    }
                }

                movimentoMaterialInstance.properties = params

                if (!movimentoMaterialService.save(movimentoMaterialInstance)) {
                    render view: 'edit', model: [movimentoMaterialInstance: movimentoMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'movimentoMaterial.label', default: 'MovimentoMaterial'), movimentoMaterialInstance.id])
                redirect action: 'show', id: movimentoMaterialInstance.id
                break
        }
    }

    def delete() {
        def movimentoMaterialInstance = MovimentoMaterial.get(params.id)
        if (!movimentoMaterialInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'movimentoMaterial.label', default: 'MovimentoMaterial'), params.id])
            redirect action: 'list'
            return
        }

        if (!validaEfetivada(movimentoMaterialInstance)) return
        try {
            movimentoMaterialInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'movimentoMaterial.label', default: 'MovimentoMaterial'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'movimentoMaterial.label', default: 'MovimentoMaterial'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def corrigirMovimento() {
        switch (request.method) {
            case 'GET':
                def movimentoMaterialInstance = MovimentoMaterial.read(params.id)
                if (!movimentoMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'movimentoMaterial.label', default: 'Movimento'), params.id])
                    redirect action: 'list'
                    return
                }
                [movimentoMaterialInstance: movimentoMaterialInstance]
                break
            case 'POST':
                def movimentoMaterialInstance = MovimentoMaterial.get(params.id)
                if (!movimentoMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'movimentoMaterial.label', default: 'Movimento'), params.id])
                    redirect action: 'list'
                    return
                }

                movimentoMaterialInstance.corrigirMovimento(Usuario.get(principal.id), params.motivo)
                if (!movimentoMaterialInstance.save(flush: true)) {
                    flash.message = message(code: 'movimentoMaterial.mensagens.erroCorrigir')

                    redirect action: 'show', id: movimentoMaterialInstance.id
                    return
                }

                redirect action: 'edit', id: movimentoMaterialInstance.id
        }
    }

    def validaEfetivada(MovimentoMaterial movimentoMaterialInstance) {
        if (!movimentoMaterialInstance?.canEdit()) {
            flash.message = message(code: 'movimentoMaterial.mensagens.movimentoEfetivada', args: [message(code: 'movimentoMaterial.label', default: 'Movimento'), params.id])
            redirect action: 'show', id: movimentoMaterialInstance.id

            return false
        }

        return true
    }



    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [movimentoMaterialInstance: new MovimentoMaterial(params)]
                break
            case 'POST':
                def movimentoMaterialInstance
                if (params.id) movimentoMaterialInstance = MovimentoMaterial.get(params.id)
                else movimentoMaterialInstance = new MovimentoMaterial(params)

                if (!validaEfetivada(movimentoMaterialInstance)) return

                setBaseData(movimentoMaterialInstance)
                if (!movimentoMaterialService.save(movimentoMaterialInstance, [validate: false])) {
                    render view: 'create', model: [movimentoMaterialInstance: movimentoMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'movimentoMaterial.label', default: 'movimentoMaterial'), movimentoMaterialInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['movimentoMaterial.id': movimentoMaterialInstance.id]

                break
        }
    }

    def relatorio_movimentacao() {
        //Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder()

        Style numberStyle = new Style("numberStyle");
        //numberStyle.setFont(new Font(7, Font._FONT_ARIAL, true));
        numberStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

        drb.addColumn("Mat.", "material.id", Long.class.getName(), 10, false, "00000")
        drb.addColumn("Material", "material.descricao", String.class.getName(), 70)
        drb.addColumn("Centro Custo", "movimentoMaterial.centroCusto.descricao", String.class.getName(), 15)
        drb.addColumn("Data", "movimentoMaterial.data_movimento", Date.class.getName(), 15)
        //drb.addColumn("Controle", "movimentoMaterial.controller", String.class.getName(), 20)
        //drb.addColumn("Lançamento", "lancamento_id", Integer.class.getName(), 10)
        drb.addColumn("Qde.", "quantidade", BigDecimal.class.getName(), 15, false, "0.00", numberStyle)
                .setTitle("Movimentação de Material")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)

        DynamicReport dr = drb.build()

        def newOrderBy = params.orderBy
        if (params.orderBy.split("\\.").size() > 1)
            params.orderBy = "id"
        def movimentacaoList = paramsToCriteria(params)
        movimentacaoList = ItemMovimentoMaterial.findAllByMovimentoMaterialInList(movimentacaoList)
        if (params.orderBy != newOrderBy) {
            if (newOrderBy == 'material.grupo')
                movimentacaoList.sort { it.material.grupo }
            else if (newOrderBy == 'item_exame.exame.codExame')
                movimentacaoList.sort { it."item_exame".exame.codExame }
            else
                movimentacaoList.sort { it."${params.orderBy}" }
        } else
            movimentacaoList.sort { it."${params.orderBy}" }
        JRDataSource ds = new JRBeanCollectionDataSource(movimentacaoList)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)

    }

    /**Descrição do Material / Cód / Grupo / Qtd. Entrada /
     Qtd. Saída / Saldo Atual / Estoque mínimo / Valor do Material /
     Total R$ (aqui multiplica-se saldo atual pelo valor do material)*/
    def relatorio_movimentacao_sintetico() {
        //Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder()

        Style numberStyle = new Style("numberStyle");
        numberStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

        Style footer = new Style()
        footer.setBorderTop(Border.PEN_1_POINT())

        Style columnHeader = new Style()
        columnHeader.setHorizontalAlign(HorizontalAlign.CENTER)
        columnHeader.setTextColor(Color.BLACK)
        columnHeader.setBackgroundColor(Color.LIGHT_GRAY)
        columnHeader.setVerticalAlign(VerticalAlign.MIDDLE)

        Style columnDetail = new Style()
        columnDetail.setHorizontalAlign(HorizontalAlign.LEFT)
        columnDetail.setTextColor(Color.BLACK)
        columnDetail.setVerticalAlign(VerticalAlign.MIDDLE)
        columnDetail.setPaddingTop(0)
        columnDetail.setPaddingBottom(0)

        Style footerDetail = new Style()
        footerDetail.setHorizontalAlign(HorizontalAlign.LEFT)
        footerDetail.setTextColor(Color.BLACK)
        footerDetail.setVerticalAlign(VerticalAlign.MIDDLE)
        footerDetail.setPaddingTop(0)
        footerDetail.setPaddingBottom(0)
        footerDetail.setBorderTop(Border.PEN_1_POINT())

        GroupBuilder gb1 = new GroupBuilder("group1")

        AbstractColumn columnQtdeEntrada = ColumnBuilder.getNew()
                .setColumnProperty("qtdEntrada", BigDecimal.class.getName()).setTitle("Qtde Entrada")
                .setWidth(new Integer(5))
                .setPattern("###,##0.00")
                .build()

        AbstractColumn columnQtdeSaida = ColumnBuilder.getNew()
                .setColumnProperty("qtdSaida", BigDecimal.class.getName()).setTitle("Qtde Saída")
                .setWidth(new Integer(5))
                .setPattern("###,##0.00")
                .build()

        AbstractColumn columnSaldoAtual = ColumnBuilder.getNew()
                .setColumnProperty("material.estoque", BigDecimal.class.getName()).setTitle("Saldo Atual")
                .setWidth(new Integer(5))
                .setPattern("###,##0.00")
                .build()

        AbstractColumn columnEstoqueMinimo = ColumnBuilder.getNew()
                .setColumnProperty("material.estoque_minimo", BigDecimal.class.getName()).setTitle("Estoque Mínimo")
                .setWidth(new Integer(5))
                .setPattern("###,##0.00")
                .build()

        AbstractColumn columnCustoTotal = ColumnBuilder.getNew()
                .setColumnProperty("material.custo_total", BigDecimal.class.getName()).setTitle("Valor do Material")
                .setWidth(new Integer(5))
                .setPattern("R\$ ###,##0.00")
                .build()

        AbstractColumn columnTotal = ColumnBuilder.getNew()
                .setColumnProperty("material.totalEstoqueSaldo", BigDecimal.class.getName()).setTitle("Total")
                .setWidth(new Integer(5))
                .setPattern("R\$ ###,##0.00")
                .build()

        AbstractColumn columnCentro = ColumnBuilder.getNew()
                .setColumnProperty("centroCusto.descricao", String.class.getName()).setTitle("C. Custo")
                .setWidth(new Integer(15))
                .build()

        DJGroupLabel glabelTotal = new DJGroupLabel(new StringExpression() {
            public Object evaluate(Map fields, Map variables, Map parameters) {
                return "Subtotal para Centro Custo: ";
            }
        }, null, LabelPosition.LEFT)

        glabelTotal.setStyle(footerDetail)

        DJGroup g1 = gb1.setCriteriaColumn((PropertyColumn) columnCentro)
                .setGroupLayout(GroupLayout.EMPTY)
                .setFooterLabel(glabelTotal)
                .addFooterVariable(columnQtdeEntrada, DJCalculation.SUM, footer)
                .addFooterVariable(columnQtdeSaida, DJCalculation.SUM, footer)
                .addFooterVariable(columnSaldoAtual, DJCalculation.SUM, footer)
                .addFooterVariable(columnEstoqueMinimo, DJCalculation.SUM, footer)
                .addFooterVariable(columnCustoTotal, DJCalculation.SUM, footer)
                .addFooterVariable(columnTotal, DJCalculation.SUM, footer)
                .build()

        drb.addColumn("Mat.", "material.id", Long.class.getName(), 5, false, "0")
        drb.addColumn("Material", "material.descricao", String.class.getName(), 15)
        drb.addColumn("Grupo", "material.grupo.descricao", String.class.getName(), 15)
        drb.addColumn("C. Custo", "centroCusto.descricao", String.class.getName(), 10)
        //drb.addColumn(columnCentro)
        drb.addColumn(columnQtdeEntrada)
        drb.addColumn(columnQtdeSaida)
        drb.addColumn(columnSaldoAtual)
        drb.addColumn(columnEstoqueMinimo)
        drb.addColumn(columnCustoTotal)
        drb.addColumn(columnTotal)
        drb.addGroup(g1)
                .setTitle("Movimentação de Material")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setGrandTotalLegend("Totais:")
                .setGrandTotalLegendStyle(columnDetail)
                .setDefaultStyles(null, null, null, null)



        Style footerNumberStyle = new Style()
        footerNumberStyle.setHorizontalAlign(HorizontalAlign.RIGHT)
        footerNumberStyle.setVerticalAlign(VerticalAlign.MIDDLE)
        footerNumberStyle.setBorderTop(Border.PEN_1_POINT())


        DynamicReport dr = drb.build()

        // Busca os dados
//        def movimentacaoGeralList = paramsToCriteria(params)
        def newOrderBy = params.orderBy
        if (params.orderBy.split("\\.").size() > 1)
            params.orderBy = "id"
        def movimentacaoGeralList = paramsToCriteria(params)
        if (params.orderBy != newOrderBy) {
            if (newOrderBy == 'material.grupo')
                movimentacaoGeralList.sort { it.material.grupo }
            else if (newOrderBy == 'material.sub_grupo')
                movimentacaoGeralList.sort { it.material."sub_grupo" }
            else
                movimentacaoGeralList.sort { it."${params.orderBy}" }
        } else
            movimentacaoGeralList.sort { it."${params.orderBy}" }

        // Monta o Relatório
        def relatorioList = []

        movimentacaoGeralList.each { movimentacao ->

            def row
            if (movimentacao.centroCusto) {
                row = relatorioList.find {
                    it.id == movimentacao.materialId && it.centroCusto == movimentacao.centroCusto
                }
            } else {
                row = relatorioList.find {
                    it.id == movimentacao.materialId
                }
            }

            if (row) {
                if (movimentacao.quantidade > 0)
                    row.qtdEntrada = (row.qtdEntrada ?: 0) + (movimentacao.quantidade ?: 0)
                else
                    row.qtdSaida = (row.qtdSaida ?: 0) + (movimentacao.quantidade ?: 0)
                row.centroCusto = movimentacao.centroCusto
            } else {
                row = [:]
                row.id = movimentacao.materialId
                row.material = movimentacao.material
                row.centroCusto = movimentacao.centroCusto

                if (movimentacao.quantidade > 0)
                    row.qtdEntrada = movimentacao.quantidade
                else
                    row.qtdSaida = movimentacao.quantidade

                relatorioList << row
            }

        }
        relatorioList.sort { a, b ->
            a.centroCusto?.descricao <=> b.centroCusto?.descricao
        }
        JRDataSource ds = new JRBeanCollectionDataSource(relatorioList)

        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        jp.setOrientation(OrientationEnum.LANDSCAPE)
        jp.setPageWidth(842)
        jp.setPageHeight(595)

        Utils.JasperReportOutput(response, jp, params)

    }
}
