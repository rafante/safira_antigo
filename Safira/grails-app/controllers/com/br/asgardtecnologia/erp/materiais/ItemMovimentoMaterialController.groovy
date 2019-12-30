package com.br.asgardtecnologia.erp.materiais

import ar.com.fdvs.dj.core.DynamicJasperHelper
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.CustomExpression
import ar.com.fdvs.dj.domain.DJCalculation
import ar.com.fdvs.dj.domain.DJGroupLabel
import ar.com.fdvs.dj.domain.DynamicReport
import ar.com.fdvs.dj.domain.StringExpression
import ar.com.fdvs.dj.domain.Style
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.builders.FastReportBuilder
import ar.com.fdvs.dj.domain.builders.GroupBuilder
import ar.com.fdvs.dj.domain.constants.Border
import ar.com.fdvs.dj.domain.constants.Font
import ar.com.fdvs.dj.domain.constants.GroupLayout
import ar.com.fdvs.dj.domain.constants.HorizontalAlign
import ar.com.fdvs.dj.domain.constants.LabelPosition
import ar.com.fdvs.dj.domain.constants.Page
import ar.com.fdvs.dj.domain.constants.Transparency
import ar.com.fdvs.dj.domain.constants.VerticalAlign
import ar.com.fdvs.dj.domain.entities.DJGroup
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn
import com.br.asgardtecnologia.base.Utils
import com.br.asgardtecnologia.erp.base.BaseController
import com.br.asgardtecnologia.erp.ev.MovimentoExame
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.hibernate.criterion.CriteriaSpecification
import org.hibernate.transform.Transformers
import org.springframework.dao.DataIntegrityViolationException

import java.awt.Color
import java.text.DateFormat
import java.text.SimpleDateFormat

class ItemMovimentoMaterialController extends BaseController {

    def reportsService

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def itemMovimentoMaterialInstanceList = paramsToCriteria(params)
        [itemMovimentoMaterialInstanceList: itemMovimentoMaterialInstanceList, itemMovimentoMaterialInstanceTotal: itemMovimentoMaterialInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [itemMovimentoMaterialInstance: new ItemMovimentoMaterial(params)]
                break
            case 'POST':
                def itemMovimentoMaterialInstance = new ItemMovimentoMaterial(params)

                if (!validaEfetivada(itemMovimentoMaterialInstance)) return

                setBaseData(itemMovimentoMaterialInstance)
                if (!itemMovimentoMaterialInstance.save(flush: true)) {
                    render view: 'create', model: [itemMovimentoMaterialInstance: itemMovimentoMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemMovimentoMaterial.label', default: 'ItemMovimentoMaterial'), itemMovimentoMaterialInstance.id])
                redirect controller: 'movimentoMaterial', action: 'show', id: itemMovimentoMaterialInstance?.movimentoMaterial?.id
                break
        }
    }

    def show() {
        def itemMovimentoMaterialInstance = ItemMovimentoMaterial.read(params.id)
        if (!itemMovimentoMaterialInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemMovimentoMaterial.label', default: 'ItemMovimentoMaterial'), params.id])
            redirect action: 'list'
            return
        }

        [itemMovimentoMaterialInstance: itemMovimentoMaterialInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def itemMovimentoMaterialInstance = ItemMovimentoMaterial.read(params.id)

                if (!validaEfetivada(itemMovimentoMaterialInstance)) return

                if (!itemMovimentoMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemMovimentoMaterial.label', default: 'ItemMovimentoMaterial'), params.id])
                    redirect action: 'list'
                    return
                }

                [itemMovimentoMaterialInstance: itemMovimentoMaterialInstance]
                break
            case 'POST':
                def itemMovimentoMaterialInstance = ItemMovimentoMaterial.get(params.id)
                setBaseData(itemMovimentoMaterialInstance)
                if (!itemMovimentoMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemMovimentoMaterial.label', default: 'ItemMovimentoMaterial'), params.id])
                    redirect action: 'list'
                    return
                }

                if (!validaEfetivada(itemMovimentoMaterialInstance)) return

                if (params.version) {
                    def version = params.version.toLong()
                    if (itemMovimentoMaterialInstance.version > version) {
                        itemMovimentoMaterialInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'itemMovimentoMaterial.label', default: 'ItemMovimentoMaterial')] as Object[],
                                "Another user has updated this ItemMovimentoMaterial while you were editing")
                        render view: 'edit', model: [itemMovimentoMaterialInstance: itemMovimentoMaterialInstance]
                        return
                    }
                }

                itemMovimentoMaterialInstance.properties = params

                if (!itemMovimentoMaterialInstance.save(flush: true)) {
                    render view: 'edit', model: [itemMovimentoMaterialInstance: itemMovimentoMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'itemMovimentoMaterial.label', default: 'ItemMovimentoMaterial'), itemMovimentoMaterialInstance.id])
                redirect controller: 'movimentoMaterial', action: 'show', id: itemMovimentoMaterialInstance?.movimentoMaterial?.id
                break
        }
    }

    def delete() {
        def itemMovimentoMaterialInstance = ItemMovimentoMaterial.get(params.id)
        if (!itemMovimentoMaterialInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'itemMovimentoMaterial.label', default: 'ItemMovimentoMaterial'), params.id])
            redirect controller: 'movimentoMaterial', action: 'show', id: itemMovimentoMaterialInstance?.movimentoMaterial?.id
            return
        }

        if (!validaEfetivada(itemMovimentoMaterialInstance)) return

        try {
            itemMovimentoMaterialInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'itemMovimentoMaterial.label', default: 'ItemMovimentoMaterial'), params.id])
            redirect controller: 'movimentoMaterial', action: 'show', id: itemMovimentoMaterialInstance?.movimentoMaterial?.id
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'itemMovimentoMaterial.label', default: 'ItemMovimentoMaterial'), params.id])
            redirect controller: 'movimentoMaterial', action: 'show', id: itemMovimentoMaterialInstance?.movimentoMaterial?.id
        }
    }

    def estoquePorCentroCusto() {
        ///Instancia o FastReportBuilder
        FastReportBuilder drb = new FastReportBuilder()

        //Cria os estilos
        Style leftStyle = reportsService.baseStyleLeft()
        Style rightStyle = reportsService.baseStyleRight()
        Style rightStyleHeader = reportsService.rightHeaderStyle()
        Style leftStyleHeader = reportsService.leftHeaderStyle()
        Style leftStyleData = reportsService.dataStyleLeft()

        //busca a lista de entidades com os filtros informados pelo usuário
        def lista = paramsToCriteria(params)
        lista.sort { ItemMovimentoMaterial a, ItemMovimentoMaterial b ->
            a.movimentoMaterial?.centroCusto?.descricao <=> b?.movimentoMaterial?.centroCusto?.descricao ?:
                    a?.material?.descricao <=> b?.material?.descricao ?:
                            a?.movimentoMaterial?.data_movimento <=> b?.movimentoMaterial?.data_movimento ?:
                                    a."${params.orderBy}" <=> b."${params.orderBy}"
        }

        //Colunas
        AbstractColumn columnCentroCusto = ColumnBuilder.getNew()
                .setColumnProperty("movimentoMaterial.centroCusto.descricao", String.class.getName()).setTitle("Centro de Custo")
                .setWidth(new Integer(20))
                .setStyle(leftStyle)
                .setHeaderStyle(leftStyleHeader)
                .build()

        AbstractColumn columnMateriais = ColumnBuilder.getNew()
                .setColumnProperty("material.descricao", String.class.getName())
                .setTitle("Materiais")
                .setWidth(new Integer(60))
                .setHeaderStyle(leftStyleHeader)
                .setStyle(leftStyle)
                .build()

        AbstractColumn columnDataMovimentacao = ColumnBuilder.getNew()
                .setColumnProperty("movimentoMaterial.data_movimento", Date.class.getName())
                .setTitle("Data da Movimentação")
                .setWidth(new Integer(20))
                .setStyle(leftStyleData)
                .setHeaderStyle(leftStyleHeader)
                .build()

        AbstractColumn colunaQuantidade = ColumnBuilder.getNew()
                .setWidth(22)
                .setPattern("###,##0.000")
                .setTitle("Qtd")
                .setColumnProperty("qtdRelatorio", BigDecimal.class.getName())
                .setStyle(leftStyle)
                .setHeaderStyle(leftStyleHeader)
                .build()

        AbstractColumn colunaSaldo = ColumnBuilder.getNew()
                .setWidth(22)
                .setPattern("###,##0.000")
                .setTitle("Saldo Atual")
                .setColumnProperty("material.estoque", BigDecimal.class.getName())
                .setStyle(rightStyle)
                .setHeaderStyle(rightStyleHeader)
                .build()

        AbstractColumn colunaValor = ColumnBuilder.getNew()
                .setWidth(22)
                .setPattern("R\$ ###,##0.00")
                .setTitle("Valor do Material")
                .setColumnProperty("material.custo_total", BigDecimal.class.getName())
                .setStyle(rightStyle)
                .setHeaderStyle(rightStyleHeader)
                .build()

        AbstractColumn colunaValorTotal = ColumnBuilder.getNew()
                .setCustomExpression(new CustomExpression() {
                    @Override
                    Object evaluate(Map campos, Map variaveis, Map parametros) {
                        def quantidade = campos.get("qtdRelatorio")
                        def valor = campos.get("material.custo_total")
                        return quantidade * valor
//                        return (BigDecimal)(Math.abs(quantidade * valor))
                    }

                    @Override
                    String getClassName() {
                        return BigDecimal.class.getName()
                    }
                })
                .setWidth(22)
                .setTitle("Valor Total")
                .setPattern("R\$ ###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(rightStyleHeader)
                .build()

        //Agrupadores
        DJGroupLabel rotulo = reportsService.rotulo('Subtotal')

        DJGroup g1 = reportsService.group(columnCentroCusto, rotulo, [(DJCalculation.SUM): [ colunaValorTotal]])

        //Monta relatório
        drb.addColumn(columnCentroCusto)
                .addColumn(columnMateriais)
                .addColumn(columnDataMovimentacao)
                .addColumn(colunaQuantidade)
                .addColumn(colunaSaldo)
                .addColumn(colunaValor)
                .addColumn(colunaValorTotal)
                .addGroup(g1)
                .setTitle("Estoque por Centro de Custo")
                .setSubtitle(reportsService.getSubtitle(params))
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setGrandTotalLegend("TOTAL GERAL:")
                .addGlobalFooterVariable(colunaValorTotal, DJCalculation.SUM, rightStyle)
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)

        DynamicReport dr = drb.build()

        JRDataSource ds = new JRBeanCollectionDataSource(lista)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }

    def validaEfetivada(ItemMovimentoMaterial itemMovimentoMaterialInstance) {
        if (!itemMovimentoMaterialInstance.movimentoMaterial?.canEdit()) {
            flash.message = message(code: 'movimentoMaterial.mensagens.saidaEfetivada', args: [message(code: 'movimentoMaterial.label', default: 'Saida'), params.id])
            redirect controller: 'movimentoMaterial', action: 'show', id: itemMovimentoMaterialInstance?.movimentoMaterial?.id

            return false
        }

        return true
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [itemMovimentoMaterialInstance: new ItemMovimentoMaterial(params)]
                break
            case 'POST':
                def itemMovimentoMaterialInstance
                if (params.id) itemMovimentoMaterialInstance = ItemMovimentoMaterial.get(params.id)
                else itemMovimentoMaterialInstance = new ItemMovimentoMaterial(params)

                if (!validaEfetivada(itemMovimentoMaterialInstance)) return

                setBaseData(itemMovimentoMaterialInstance)
                if (!itemMovimentoMaterialInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [itemMovimentoMaterialInstance: itemMovimentoMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'itemMovimentoMaterial.label', default: 'itemMovimentoMaterial'), itemMovimentoMaterialInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['itemMovimentoMaterial.id': itemMovimentoMaterialInstance.id]

                break
        }
    }

    def usedMaterials() {
        def list = paramsToCriteria(params)
        def materials = []

        list.each { ItemMovimentoMaterial item ->
            if (item.material?.item_exame?.size() && item.material?.item_exame?.first()?.exame?.ativo)
                materials << item.material
        }

        def parametros = ItemMovimentoMaterial.ObterParametros(params, getDomainClass())

        materials = materials.unique()
        def dataIni = new Date(0)
        def dataFim = new Date()

        if (parametros.params."movimentoMaterial.data_movimento") {
            dataIni = parametros.params."movimentoMaterial.data_movimento".low
            dataFim = parametros.params."movimentoMaterial.data_movimento".high
        }

        def movimentosExame = MovimentoExame.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            if (parametros.params?.'movimentoMaterial.data_movimento'?.op) {
                if (dataIni != null && dataFim == null)
                    "${parametros.params.'movimentoMaterial.data_movimento'.op}"("dataMovimento", dataIni)
                else if (dataIni == null && dataFim != null)
                    "${parametros.params.'movimentoMaterial.data_movimento'.op}"("dataMovimento", dataFim)
                else if (dataIni != null && dataFim != null)
                    "${parametros.params.'movimentoMaterial.data_movimento'.op}"("dataMovimento", dataIni, dataFim)
            }
            projections {
                property("exame", "exame")
                //property("quantidade", "quantidade")
                sum("quantidade", "quantidade")
            }
            groupProperty("exame")
        }

        def movimentacaoListCriteria = materials?.size() ? ItemMovimentoMaterial.withCriteria {
            'in'('material', materials)
            resultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
            projections {
                property("material.id", "materialId")
                property("material", "material")
                property("quantidade", "quantidade")
                property("valor", "valor")
                movimentoMaterial {
                    property("data_movimento", "data")
                    property("entrada_saida", "entradaSaida")
//                    groupProperty("entrada_saida")
                }
//                groupProperty("material")
            }
        } : []

        BigDecimal saldo = 0
        def materialAnt

        def movimentacaoList = []

        for (def mc in movimentacaoListCriteria) {
            def contained = true
            def mov = movimentacaoList.find { it.materialId == mc.materialId }
            if (!mov) {
                mov = [entradas: BigDecimal.ZERO, saidas: BigDecimal.ZERO]
                contained = false
            }

            mov["materialId"] = mc.materialId
            mov["material"] = mc.material
            mov["testesPorKit"] = mc.material.getPesoCalc()
            mov["saldoAnterior"] = (BigDecimal) ((params.data_movimento ? mc.material.getPosicaoEstoque(params.data_movimento) : 0) * mov["testesPorKit"])

            def quantidade = mc.quantidade * mov["testesPorKit"]
            if (mc.entradaSaida == TipoMovimentoMaterial.TIPO_ENTRADA) {
                mov["entradas"] += quantidade
            } else {
                mov["saidas"] += quantidade
            }
            def exame = mc.material.item_exame.first().exame
            mov["qtdExames"] = (BigDecimal) (movimentosExame.find { movEx -> movEx.exame.id == exame.id }?.quantidade ?: 0f)
            mov["custo"] = (BigDecimal) (mov["testesPorKit"] ? mc.material.getCustoTotalCalc() / mov["testesPorKit"] : 0)
            mov["saldoAtual"] = (BigDecimal) (mov["saldoAnterior"] + (mov["entradas"] ?: 0) - (mov["saidas"] ?: 0))
            mov["saldoAtualMoney"] = (BigDecimal) ((mov["saldoAtual"] ?: 0) * (mov["custo"] ?: 0))

            if (!contained)
                movimentacaoList << mov
        }

        ///Instancia o FastReportBuilder
        FastReportBuilder drb = new FastReportBuilder()

        Style numberStyle = new Style("numberStyle")
        numberStyle.setFont(new Font(8, Font._FONT_VERDANA, true))
        numberStyle.setVerticalAlign(VerticalAlign.TOP)
        numberStyle.setHorizontalAlign(HorizontalAlign.RIGHT)//

        Style numberStyle2 = new Style("numberStyle2")
        numberStyle2.setFont(new Font(8, Font._FONT_VERDANA, true))
        numberStyle2.setVerticalAlign(VerticalAlign.TOP)
        numberStyle2.setHorizontalAlign(HorizontalAlign.LEFT)

        Style footerDetail = new Style()
        footerDetail.setFont(new Font(8, Font._FONT_ARIAL, false))
        footerDetail.setHorizontalAlign(HorizontalAlign.LEFT)
        footerDetail.setTextColor(Color.BLACK)
        footerDetail.setVerticalAlign(VerticalAlign.MIDDLE)
        footerDetail.setPaddingTop(0)
        footerDetail.setPaddingBottom(0)
        footerDetail.setBorderTop(Border.PEN_1_POINT())

        Style footerNumberStyle = new Style()
        footerNumberStyle.setFont(new Font(8, Font._FONT_ARIAL, false))
        footerNumberStyle.setHorizontalAlign(HorizontalAlign.LEFT)
        footerNumberStyle.setVerticalAlign(VerticalAlign.MIDDLE)
        footerNumberStyle.setBorderTop(Border.PEN_1_POINT())

        DJGroupLabel glabelTotalMaterial = new DJGroupLabel(new StringExpression() {
            public Object evaluate(Map fields, Map variables, Map parameters) {
                return "Subtotal para o material: ";
            }
        }, null, LabelPosition.LEFT)

        glabelTotalMaterial.setStyle(footerDetail)

        AbstractColumn columnMaterial = ColumnBuilder.getNew()
                .setColumnProperty("material.id", Long.class.getName()).setTitle("Cód.")
                .setWidth(new Integer(10))
                .build()

        AbstractColumn columnMaterialDesc = ColumnBuilder.getNew()
                .setColumnProperty("material.descricao", String.class.getName()).setTitle("Descrição")
        //.setHeaderStyle(headerStyle)
                .setStyle(numberStyle2)
                .setWidth(new Integer(75))
                .build()

        AbstractColumn columnTestesPorKit = ColumnBuilder.getNew()
        //.setHeaderStyle(headerStyle)
                .setColumnProperty("testesPorKit", BigDecimal.class.getName()).setTitle("Testes/kit")
                .setStyle(numberStyle)
                .setWidth(new Integer(16))
                .setPattern("#,###,###,###")
                .build()
//
        AbstractColumn columnEntradas = ColumnBuilder.getNew()
        //.setHeaderStyle(headerStyle)
                .setStyle(numberStyle)
                .setColumnProperty("entradas", BigDecimal.class.getName()).setTitle("Entradas")
                .setStyle(numberStyle)
                .setWidth(new Integer(20))
                .setPattern("#,###,###,###")
                .build()

        AbstractColumn columnExamesRealizados = ColumnBuilder.getNew()
        //.setHeaderStyle(headerStyle)
                .setStyle(numberStyle)
                .setColumnProperty("qtdExames", BigDecimal.class.getName()).setTitle("Qt.Ex.Realizados")
                .setStyle(numberStyle)
                .setWidth(new Integer(27))
                .setPattern("#,###,###,###")
                .build()

        AbstractColumn columnSaidas = ColumnBuilder.getNew()
        //.setHeaderStyle(headerStyle)
                .setStyle(numberStyle)
                .setColumnProperty("saidas", BigDecimal.class.getName()).setTitle("Saídas")
                .setWidth(new Integer(20))
                .setPattern("#,###,###,###")
                .build()


        AbstractColumn columnData = ColumnBuilder.getNew()
        //.setHeaderStyle(headerStyle)
                .setStyle(numberStyle)
                .setColumnProperty("data", Date.class.getName()).setTitle("Data")
                .setWidth(new Integer(20))
                .setPattern("dd/MM/yy")
                .build()

        AbstractColumn columnCustoPorTeste = ColumnBuilder.getNew()
        //.setHeaderStyle(headerStyle)
                .setStyle(numberStyle)
                .setColumnProperty("custo", BigDecimal.class.getName()).setTitle("Custo/teste")
                .setWidth(new Integer(25))
                .setPattern("R\$ ###,#####0.00000")
                .build()

        AbstractColumn columnValor = ColumnBuilder.getNew()
        //.setHeaderStyle(headerStyle)
                .setStyle(numberStyle)
                .setColumnProperty("valor", BigDecimal.class.getName()).setTitle("Valor")
                .setWidth(new Integer(25))
                .setPattern("R\$ ###,##0.00")
                .build()

        AbstractColumn columnSaldoAnterior = ColumnBuilder.getNew()
        //.setHeaderStyle(headerStyle)
                .setStyle(numberStyle)
                .setColumnProperty("saldoAnterior", BigDecimal.class.getName()).setTitle("Sld. Anterior")
                .setWidth(new Integer(20))
                .setPattern("#,###,###,###")
                .build()

        AbstractColumn columnSaldoAtual = ColumnBuilder.getNew()
        //.setHeaderStyle(headerStyle)
                .setStyle(numberStyle)
                .setColumnProperty("saldoAtual", BigDecimal.class.getName()).setTitle("Sld. Atual")
                .setWidth(new Integer(20))
                .setPattern("#,###,###,###")
                .build()

        AbstractColumn columnSaldoAtualMoney = ColumnBuilder.getNew()
        //.setHeaderStyle(headerStyle)
                .setStyle(numberStyle)                            \
                .setColumnProperty("saldoAtualMoney", BigDecimal.class.getName()).setTitle("Sld. Atual \$")
                .setWidth(new Integer(20))
                .setPattern("R\$ ###,##0.00")
                .build()

        GroupBuilder gbMaterial = new GroupBuilder("groupMaterial")
        DJGroup g1 = gbMaterial.setCriteriaColumn((PropertyColumn) columnMaterial)
                .setGroupLayout(GroupLayout.EMPTY)
                .setFooterLabel(glabelTotalMaterial)
                .addFooterVariable(columnEntradas, DJCalculation.SUM, footerNumberStyle)
                .addFooterVariable(columnValor, DJCalculation.SUM, footerNumberStyle)
                .addFooterVariable(columnSaldoAnterior, DJCalculation.SUM, footerNumberStyle)
                .build()

        def periodo = "Período: "
        if (dataIni)
            periodo += new SimpleDateFormat("dd/MM/yyyy").format(dataIni)
        if (dataFim)
            periodo += " a " + new SimpleDateFormat("dd/MM/yyyy").format(dataFim)
        //drb.addColumn("Cód.", "material.id", Long.class.getName(), 10)
        drb.addColumn(columnMaterialDesc)
                .addColumn(columnTestesPorKit)
                .addColumn(columnSaldoAnterior)
                .addColumn(columnEntradas)
                .addColumn(columnSaidas)
                .addColumn(columnExamesRealizados)
                .addColumn(columnSaldoAtual)
                .addColumn(columnCustoPorTeste)
                .addColumn(columnSaldoAtualMoney)
                .setSubtitle(reportsService.getSubtitle(params) + (periodo))
                .setTitle("MOVIMENTAÇÃO ESTOQUE - POR QUANTIDADE DE TESTES")
                //.setSubtitle(periodo)
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
//                .addGroup(g1)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())

        DynamicReport dr = drb.build()

        JRDataSource ds = new JRBeanCollectionDataSource(movimentacaoList)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }

    def relatorio_movimentacao_sintetico() {
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
                .setSubtitle(reportsService.getSubtitle(params))

        DynamicReport dr = drb.build()

        def newOrderBy = params.orderBy
        if (params.orderBy.split("\\.").size() > 1)
            params.orderBy = "id"
//        params.groupBy = ["material"]
        params.projections = ["material", "movimentoMaterial", "quantidade"]
        def movimentacaoList = paramsToCriteria(params)
        def listaFinal = []
        movimentacaoList.each {
            def modificador = it.movimentoMaterial?.entrada_saida == TipoMovimentoMaterial?.TIPO_SAIDA ? -1 : 1
            def map = ["material": it.material, "movimentoMaterial": it.movimentoMaterial, "quantidade": it.quantidade * modificador]
            def item = listaFinal.findIndexOf {
                it.material?.id == map.material?.id
            }
            if (item >= 0)
                listaFinal[item].quantidade += map.quantidade
            else
                listaFinal << map
        }
        movimentacaoList = listaFinal
        /*movimentacaoList = ItemMovimentoMaterial.withCriteria {
            "in"("movimentoMaterial", movimentacaoList)
            groupProperty("material.id")
            groupProperty("movimentoMaterial.entrada_saida")
            sum("quantidade")
        }*/
        //movimentacaoList = ItemMovimentoMaterial.findAllByMovimentoMaterialInList(movimentacaoList)

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
    def relatorio_movimentacao() {
        //Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder()

        //Estilos
        Style headerLeft = reportsService.leftHeaderStyle()
        Style headerRight = reportsService.rightHeaderStyle()
        Style rightStyle = reportsService.baseStyleRight()
        Style leftStyle = reportsService.baseStyleLeft()
        Style baseComDivisoria = reportsService.baseStyleLeft()
        baseComDivisoria.setHorizontalAlign(HorizontalAlign.RIGHT)
        baseComDivisoria.setBorderTop(Border.PEN_1_POINT())

        AbstractColumn columnMatId = ColumnBuilder.getNew()
                .setColumnProperty("material.id", Long.class.getName())
                .setTitle("Mat.")
                .setWidth(new Integer(5))
                .setPattern("0")
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnMaterial = ColumnBuilder.getNew()
                .setColumnProperty("material.descricao", String.class.getName())
                .setTitle("Material")
                .setWidth(new Integer(15))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnGrupo = ColumnBuilder.getNew()
                .setColumnProperty("material.grupo.descricao", String.class.getName())
                .setTitle("Grupo")
                .setWidth(new Integer(15))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnCentroCusto = ColumnBuilder.getNew()
                .setColumnProperty("centroCusto.descricao", String.class.getName())
                .setTitle("C. Custo")
                .setWidth(new Integer(10))
                .setStyle(leftStyle)
                .setHeaderStyle(headerLeft)
                .build()

        AbstractColumn columnQtdeEntrada = ColumnBuilder.getNew()
                .setColumnProperty("qtdEntrada", BigDecimal.class.getName())
                .setTitle("Qtde Entrada")
                .setWidth(new Integer(5))
                .setPattern("###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(headerRight)
                .build()

        AbstractColumn columnQtdeSaida = ColumnBuilder.getNew()
                .setColumnProperty("qtdSaida", BigDecimal.class.getName())
                .setTitle("Qtde Saída")
                .setWidth(new Integer(5))
                .setPattern("###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(headerRight)
                .build()

        AbstractColumn columnSaldoAtual = ColumnBuilder.getNew()
                .setColumnProperty("estoque", BigDecimal.class.getName())
                .setTitle("Saldo Atual")
                .setWidth(new Integer(5))
                .setPattern("###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(headerRight)
                .build()

        AbstractColumn columnEstoqueMinimo = ColumnBuilder.getNew()
                .setColumnProperty("estoque_minimo", BigDecimal.class.getName())
                .setTitle("Estoque Mínimo")
                .setWidth(new Integer(5))
                .setPattern("###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(headerRight)
                .build()

        AbstractColumn columnCustoTotal = ColumnBuilder.getNew()
                .setColumnProperty("custo_total", BigDecimal.class.getName())
                .setTitle("Valor do Material")
                .setWidth(new Integer(5))
                .setPattern("R\$ ###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(headerRight)
                .build()

        AbstractColumn columnTotal = ColumnBuilder.getNew()
                .setColumnProperty("totalEstoqueSaldo", BigDecimal.class.getName())
                .setTitle("Total")
                .setWidth(new Integer(5))
                .setPattern("R\$ ###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(headerRight)
                .build()

        AbstractColumn columnCentro = ColumnBuilder.getNew()
                .setColumnProperty("centroCusto.descricao", String.class.getName())
                .setTitle("C. Custo")
                .setWidth(new Integer(15))
                .setStyle(rightStyle)
                .setHeaderStyle(headerRight)
                .build()

        DJGroupLabel rotulo = reportsService.rotulo('Subtotal para Centro Custo:', baseComDivisoria)
        rotulo.setStyle(baseComDivisoria)
        DJGroup g1 = reportsService.group(columnCentro, rotulo, [(DJCalculation.SUM): [columnTotal]], baseComDivisoria)

        drb.addColumn(columnMatId)
        drb.addColumn(columnMaterial)
        drb.addColumn(columnGrupo)
        drb.addColumn(columnCentroCusto)
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
                .setGrandTotalLegendStyle(rightStyle)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setUseFullPageWidth(true)
                .setGrandTotalLegend("Totais:")
                .addGlobalFooterVariable(columnTotal, DJCalculation.SUM, rightStyle)
                .setSubtitle(reportsService.getSubtitle(params))


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

        movimentacaoGeralList.each { itemMovimentacao ->

            def row
            if (itemMovimentacao?.movimentoMaterial?.centroCusto) {
                row = relatorioList.find {
                    it.id == itemMovimentacao.materialId && it.centroCusto == itemMovimentacao.movimentoMaterial.centroCusto
                }
            } else {
                row = relatorioList.find {
                    it.id == itemMovimentacao.materialId
                }
            }

            if (row) {
                if (itemMovimentacao.quantidade < 0)
                    row.qtdEntrada = (row.qtdEntrada ?: 0) + (itemMovimentacao.quantidade ?: 0)
                else
                    row.qtdSaida = (row.qtdSaida ?: 0) + (itemMovimentacao.quantidade ?: 0)
                row.centroCusto = itemMovimentacao?.movimentoMaterial?.centroCusto //
            } else {
                row = [:]
                row.id = itemMovimentacao.materialId
                row.material = itemMovimentacao.material
                row.centroCusto = itemMovimentacao?.movimentoMaterial?.centroCusto ?: 0
                row.estoque = new BigDecimal(itemMovimentacao?.material?.estoque ?: 0)
                row.estoque_minimo = new BigDecimal(itemMovimentacao?.material?.estoque_minimo ?: 0)
                row.custo_total = new BigDecimal(itemMovimentacao?.material?.custo_total ?: 0)
                row.totalEstoqueSaldo = new BigDecimal(itemMovimentacao?.material?.totalEstoqueSaldo ?: 0)

                if (itemMovimentacao.quantidade > 0)
                    row.qtdEntrada = new BigDecimal(itemMovimentacao?.quantidade ?: 0)
                else
                    row.qtdSaida = new BigDecimal(itemMovimentacao?.quantidade ?: 0)

                relatorioList << row
            }

        }

        relatorioList.sort { a, b ->
            a.centroCusto?.descricao <=> b.centroCusto?.descricao
        }

        //groovy = empresa
        //java = consultoria

        JRDataSource ds = new JRBeanCollectionDataSource(relatorioList)

        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)


        Utils.JasperReportOutput(response, jp, params)

    }
}
