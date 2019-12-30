package com.br.asgardtecnologia.erp.materiais

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
import com.br.asgardtecnologia.erp.ev.Exame
import com.mrkonno.plugin.jrimum.dsl.BoletoDsl
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo
import org.springframework.dao.DataIntegrityViolationException

import java.awt.Color

class MaterialController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def materialService
    def financeiroBaseService
    def reportsService

    def index() {
        redirect action: 'create', params: params
    }

    def teste() {
        def b = BoletoDsl.boleto {
            sacado('Mario Roberto', '117.053.316-70') {
                endereco {
                    uf 'PA'
                    logradouro 'Av Santo Antonio'
                    localidade 'Belem'
                    cep '66039-203'
                    bairro 'Centro'
                    numero '302'
                }
            }
            cedente('ACME INC', '18.410.773/0001-01') {
                endereco {
                    uf 'PA'
                    logradouro 'Av Conselheiro Furtado'
                    localidade 'Belem'
                    cep '660060-063'
                    bairro 'Sao Braz'
                    numero '3192'
                }
            }
            contaBancaria {
                banco 'BANCO_ITAU'
                conta 1310, '2'
                carteira 6
                agencia 2703, '2'
            }
            dataVencimento new Date() + 10
            numeroDocumento '000210'
            nossoNumero '42103929'
            digitoNossoNumero '3'
            valor 100.00
            tipoTitulo TipoDeTitulo.DM_DUPLICATA_MERCANTIL
            localPagamento "Pagavel em qualquer Banco"
            instrucoes """Aceitar ate a data de vencimento
Apos o vencimento aceito apenas nas agencias do Bradesco
Cobrar multa de 7% e juros de mora de 0,03% ao dia"""
        }
// the bytes property return a array of bytes and it can be used to send in response
        byte[] bt = b.bytes
// the pdf method can be used to save to a pdf file
        b.pdf('C:\\dev\\teste2.pdf')
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def materialInstanceList = paramsToCriteria(params)
        [materialInstanceList: materialInstanceList, materialInstanceTotal: materialInstanceList.totalCount]
    }

    def create() {

        switch (request.method) {
            case 'GET':
                [materialInstance: new Material(params)]
                break
            case 'POST':
                def materialInstance = new Material(params)
                setBaseData(materialInstance)
                if (!materialService.save(materialInstance)) {
                    render view: 'create', model: [materialInstance: materialInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'material.label', default: 'Material'), materialInstance.id])
                redirect action: 'show', id: materialInstance.id
                break
        }
    }

    def show() {
        def materialInstance = Material.read(params.id)
        if (!materialInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'material.label', default: 'Material'), params.id])
            redirect action: 'list'
            return
        }

        [materialInstance: materialInstance]
    }

    def inventario() {

        FastReportBuilder drb = new FastReportBuilder()

        Style title = new Style()
        title.setFont(new Font(10, Font._FONT_GEORGIA, true))
        title.setHorizontalAlign(HorizontalAlign.CENTER);

        Style subtitle = new Style()
        subtitle.setFont(new Font(8, Font._FONT_ARIAL, true))

        //Cria os estilos
        Style leftStyle = reportsService.baseStyleLeft()
        Style rightStyle = reportsService.baseStyleRight()
        Style rightStyleHeader = reportsService.rightHeaderStyle()
        Style leftStyleHeader = reportsService.leftHeaderStyle()
        Style leftStyleData = reportsService.dataStyleLeft()

        //Colunas
        AbstractColumn columnCod = ColumnBuilder.getNew()
                .setColumnProperty('id', Long.class.getName())
                .setTitle('Cód.')
                .setWidth(8)
                .setStyle(leftStyle)
                .setHeaderStyle(leftStyleHeader)
                .build()

        AbstractColumn columnDesc = ColumnBuilder.getNew()
                .setColumnProperty('descricao', String.class.getName())
                .setTitle('Descrição')
                .setWidth(70)
                .setStyle(leftStyle)
                .setHeaderStyle(leftStyleHeader)
                .build()

        AbstractColumn columnFinalidade = ColumnBuilder.getNew()
                .setColumnProperty('textoFinalidade', String.class.getName())
                .setTitle('Finalidade')
                .setWidth(25)
                .setStyle(leftStyle)
                .setHeaderStyle(leftStyleHeader)
                .build()

        AbstractColumn columnGrupo = ColumnBuilder.getNew()
                .setColumnProperty('grupo.descricao', String.class.getName())
                .setTitle('Grupo')
                .setWidth(30)
                .setStyle(leftStyle)
                .setHeaderStyle(leftStyleHeader)
                .build()

        AbstractColumn columnSubgrupo = ColumnBuilder.getNew()
                .setColumnProperty('sub_grupo.descricao', String.class.getName())
                .setTitle('Subgrupo')
                .setWidth(30)
                .setStyle(leftStyle)
                .setHeaderStyle(leftStyleHeader)
                .build()

        AbstractColumn columnValorEstoque = ColumnBuilder.getNew()
                .setColumnProperty("valorEstoque", BigDecimal.class.getName())
                .setTitle("Valor Estoque")
                .setWidth(new Integer(15))
                .setPattern("R\$ ###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(rightStyleHeader)
                .build()

//        AbstractColumn colunaGrupo = ColumnBuilder.getNew()
//                .setColumnProperty("grupo.descricao", String.class.getName())
//                .setTitle("Grupo")
////                .setWidth(30)
//                .setStyle(numberStyle)
//                .setHeaderStyle(headerStyleRight)
//                .build()

        AbstractColumn columnEstoque = ColumnBuilder.getNew()
                .setColumnProperty("estoque", BigDecimal.class.getName())
                .setTitle("Estoque")
                .setWidth(new Integer(15))
                .setPattern("###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(rightStyleHeader)
                .build()

        AbstractColumn columnCustoTotal = ColumnBuilder.getNew()
                .setColumnProperty("custo_total", BigDecimal.class.getName())
                .setTitle("Custo")
                .setWidth(new Integer(15))
                .setPattern("R\$ ###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(rightStyleHeader)
                .build()

        DJGroupLabel rotulo = reportsService.rotulo('Subtotal:')
        DJGroup g1 = reportsService.group(columnGrupo, rotulo, [(DJCalculation.SUM): [columnValorEstoque]])

        drb.addColumn(columnCod)
        drb.addColumn(columnDesc)
        drb.addColumn(columnFinalidade)
        drb.addColumn(columnGrupo)
        drb.addColumn(columnSubgrupo)
        drb.addColumn(columnEstoque)
        drb.addColumn(columnCustoTotal)
        drb.addColumn(columnValorEstoque)
        drb.addGroup(g1)
                .setTitle("Inventário")
                .setSubtitle(reportsService.getSubtitle(params))
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setGrandTotalLegendStyle(rightStyle)
                .setGrandTotalLegend("TOTAL GERAL:")
                .addGlobalFooterVariable(columnValorEstoque, DJCalculation.SUM, rightStyle)


        DynamicReport dr = drb.build()

        params.max = 10000
        def materialInstanceList = paramsToCriteria(params)

        materialInstanceList.sort { Material a, Material b ->
            a.grupo?.descricao <=> b.grupo?.descricao ?: a.descricao <=> b.descricao ?: a."${params.orderBy}" <=> b."${params.orderBy}"
        }

        for (Material materialInstance in materialInstanceList) {
            materialInstance.textoFinalidade = resolveEnumMessage(materialInstance.finalidade)
        }

        JRDataSource ds = new JRBeanCollectionDataSource(materialInstanceList)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }

    def materiaisPorExame() {
        switch (request.method) {
            case "GET":
                break
            case "POST":
                def materiais
                if (params.exameId) {
                    materiais = Material.withCriteria {
                        item_exame {
                            eq("exame", Exame.get(params.exameId))
                        }
                    }
                } else {
                    materiais = []
                }
                def attrs = [ignoreWidget: false, totalCount: 0, headerButtons: '', name: 'material', actionName: 'materiaisPorExame',
                             itemButtons : 'show', list: materiais, controller: 'material', modelInstance: Material.newInstance(), ignoreContainer: true,
                             fields      : 'id;descricao;finalidade[ENUM];grupo;sub_grupo', ignorePagination: true]
                render asg.grid(attrs)
                break
        }
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def materialInstance = Material.read(params.id)
                if (!materialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'material.label', default: 'Material'), params.id])
                    redirect action: 'list'
                    return
                }

                [materialInstance: materialInstance]
                break
            case 'POST':
                def materialInstance = Material.get(params.id)
                setBaseData(materialInstance)
                if (!materialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'material.label', default: 'Material'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (materialInstance.version > version) {
                        materialInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'material.label', default: 'Material')] as Object[],
                                "Another user has updated this Material while you were editing")
                        render view: 'edit', model: [materialInstance: materialInstance]
                        return
                    }
                }

                println(params)

                materialInstance.properties = params

                if (!materialService.save(materialInstance)) {
                    render view: 'edit', model: [materialInstance: materialInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'material.label', default: 'Material'), materialInstance.id])
                redirect action: 'show', id: materialInstance.id
                break
        }
    }

    def delete() {
        def materialInstance = Material.get(params.id)
        if (!materialInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'material.label', default: 'Material'), params.id])
            redirect action: 'list'
            return
        }

        try {
            materialService.delete(materialInstance)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'material.label', default: 'Material'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'material.label', default: 'Material'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [materialInstance: new Material(params)]
                break
            case 'POST':
                def materialInstance
                if (params.id) materialInstance = Material.get(params.id)
                else materialInstance = new Material()
                materialInstance.properties = params

                setBaseData(materialInstance)
                if (!materialService.save(materialInstance, [validate: false])) {
                    render view: 'create', model: [materialInstance: materialInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'material.label', default: 'material'), materialInstance.id])
                redirect controller: params.controllerRedirect[0], action: 'create', params: ['material.id': materialInstance.id]

                break
        }
    }

    def getLDM() {
//        params.controller = params.gridController
//        params.action = params.gridAction
        params.isRefreshing = true
        params.list = []

        for (ldm in Material.first().LDM) {
            def itemOP = [:]
            itemOP.material = ldm.material_composicao
            itemOP.quantidade = ldm.quantidade
            params.list << itemOP
        }

        render asg.grid(params)
        return
    }

    def lista_materiais_atualizados(list) {
        FastReportBuilder drb = new FastReportBuilder()

        Style numberStyle = new Style("numberStyle");
        numberStyle.setFont(new Font(7, Font._FONT_VERDANA, true));
        numberStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

        drb.addColumn("Mat.", "id", Long.class.getName(), 8)
        drb.addColumn("Descrição", "descricao", String.class.getName(), 60)
        drb.addColumn("Estoque", "estoque", BigDecimal.class.getName(), 20, false, "0.00")
        drb.addColumn("Estoque minímo", "estoque_minimo", BigDecimal.class.getName(), 20, false, "0.00")
        drb.addColumn("Estoque máximo", "estoque_maximo", BigDecimal.class.getName(), 20, false, "0.00")
        drb.addColumn("Ponto de pedido", "ponto_pedido", BigDecimal.class.getName(), 20, false, "0.00")
                .setTitle("Lista de Materiais Atualizados")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())

        DynamicReport dr = drb.build()

        JRDataSource ds = new JRBeanCollectionDataSource(list)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        return Utils.ExportReportPdf(jp).toByteArray()
    }

    def consumo_materiais() {
        //Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder()

        Style numberStyle = new Style("numberStyle");
        numberStyle.setFont(new Font(7, Font._FONT_VERDANA, true));
        numberStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

        drb.addColumn("Mat.", "id", Long.class.getName(), 8)
        drb.addColumn("Descrição", "descricao", String.class.getName(), 50)
        drb.addColumn("Quantidade", "estoque", BigDecimal.class.getName(), 50, false, "0.00")
        drb.addColumn("Unidade", "unidade_medida", String.class.getName(), 10)
        drb.addColumn("Valor", "custo_total", BigDecimal.class.getName(), 15, false, "R\$ 0.00")
                .setTitle("Relatório de Consumo de materiais")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())

        DynamicReport dr = drb.build()

        JRDataSource ds = new JRBeanCollectionDataSource(Material.list())
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)

    }

    def estoque_minimo() {
        //Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder()

        Style numberStyle = new Style("numberStyle");
        numberStyle.setFont(new Font(7, Font._FONT_VERDANA, true));
        numberStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

        drb.addColumn("Mat.", "id", Long.class.getName(), 8)
        drb.addColumn("Descrição", "descricao", String.class.getName(), 60)
        drb.addColumn("Estoque", "estoque", BigDecimal.class.getName(), 20, false, "0.00")
        drb.addColumn("Estoque minímo", "estoque_minimo", BigDecimal.class.getName(), 20, false, "0.00")
                .setTitle("Relatório de Estoque Mínimo")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setSubtitle(reportsService.getSubtitle(params))

        DynamicReport dr = drb.build()

        //def materialInstanceList = paramsToCriteria(params)
        def newOrderBy = params.orderBy
        if (params.orderBy.split("\\.").size() > 1)
            params.orderBy = "id"
        def materialInstanceList = paramsToCriteria(params)
        if (params.orderBy != newOrderBy) {
            if (newOrderBy == 'item_exame.exame.codExame')
                materialInstanceList.sort { it."item_exame".exame.codExame }
            else
                materialInstanceList.sort { it."${params.orderBy}" }
        } else
            materialInstanceList.sort { it."${params.orderBy}" }

        JRDataSource ds = new JRBeanCollectionDataSource(materialInstanceList)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }

    def estoque_maximo() {
        //Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder()

        Style numberStyle = new Style("numberStyle");
        numberStyle.setFont(new Font(7, Font._FONT_VERDANA, true));
        numberStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

        drb.addColumn("Mat.", "id", Long.class.getName(), 8)
        drb.addColumn("Descrição", "descricao", String.class.getName(), 60)
        drb.addColumn("Estoque", "estoque", BigDecimal.class.getName(), 20, false, "0.00")
        drb.addColumn("Estoque maximo", "estoque_maximo", BigDecimal.class.getName(), 20, false, "0.00")
                .setTitle("Relatório de Estoque Maximo")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setSubtitle(reportsService.getSubtitle(params))

        DynamicReport dr = drb.build()

        def materialInstanceList = paramsToCriteria(params)
        materialInstanceList.sort { a, b ->
            a.descricao <=> b.descricao
        }
        JRDataSource ds = new JRBeanCollectionDataSource(materialInstanceList)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }

    def listagem() {
        //Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder()

        Style numberStyle = new Style("numberStyle");
        numberStyle.setFont(new Font(7, Font._FONT_VERDANA, true));
        numberStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

        drb.addColumn("Mat.", "id", Long.class.getName(), 8)
        drb.addColumn("Descrição", "descricao", String.class.getName(), 60)
        drb.addColumn("Grupo", "grupo.descricao", String.class.getName(), 30)
        drb.addColumn("Subgrupo", "sub_grupo.descricao", String.class.getName(), 30)
                .setTitle("Listagem de Materiais")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setSubtitle(reportsService.getSubtitle(params))

        DynamicReport dr = drb.build()

        def newOrderBy = params.orderBy
        if (params.orderBy.split("\\.").size() > 1)
            params.orderBy = "id"
        def materialInstanceList = paramsToCriteria(params)
        if (params.orderBy != newOrderBy) {
            if (newOrderBy == 'item_exame.exame.codExame')
                materialInstanceList.sort { it."item_exame".exame.codExame }
            else
                materialInstanceList.sort { it."${params.orderBy}" }
        } else
            materialInstanceList.sort { it."${params.orderBy}" }

        JRDataSource ds = new JRBeanCollectionDataSource(materialInstanceList)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }

    def corrigirEstoqueMateriais() {
        // bom vamos começar por aqui. Criei o método como uma action e um controller em vez de colocar no Bootstrap porque o Bootstrap só acontece uma vez quando
        //o programa inicia e aí vc não consegue salvar e reavaliar. Do jeito que tá aqui, toda vez que vc acessar localhost:8080/Safira/material/corrigirEstoqueMateriais
        //ele vai cair aqui aí se vc quiser vc mexe em algo no código, salva espera 5 segundos e acessa de novo no chroem que ele vai cair aqui de novo aí fica melhor de trabalhar

        //3)Dentro do método: Ler todos os Item de Movimento de Material para uma lista
        def listaMovimentos = ItemMovimentoMaterial.list()
        //4)Ler todos os materiais para outra lista
        def materiais = Material.list()

        //5)Criar um Loop para a lista dos materiais
        materiais.each { material ->
            //6)Dentro do loop, para cada material, criar outro loop na lista dos Item de Movimento de Material
            //print (material)
            if (material.id == 566) {
                print('aqui!')
            }

            //Aqui tá inicializando a variável, blz tá certo
            def estoqueCorreto = 0

            //Aí inicia o loop nos movimentos, ok
            listaMovimentos.each { movimento ->
                //comparar valor da movimentação com o do estoque -> se movimento for entrada somar se for saida subitrair se o valor estiver igual manter o valor do estoque

                //onde vc tá usando movimento.quantidade eu vou colocar uma variável
                def quantidadeConvertida = movimento.quantidade

                //e aqui eu vou multiplicar pelo fator de conversao
                quantidadeConvertida *= material.getConversao(movimento.unidade_medida) // esse *= é que nem aquele += só que pra multiplicar

                int modificador = movimento.movimentoMaterial.entrada_saida == TipoMovimentoMaterial.TIPO_ENTRADA ? 1 : -1
                MaterialLote lote = movimento.materialLote
                if (movimento.material.id == material.id) {
                    if (lote && lote.material?.id == material?.id) {
                        quantidadeConvertida = modificador * movimento.quantidade * material.getConversao(movimento.unidade_medida)
                        lote.estoque = Utils.ToDouble(lote.estoque) + Utils.ToDouble(quantidadeConvertida)
                        estoqueCorreto += quantidadeConvertida
                        if (!lote.save(flush: true))
                            return false
                    } else {
                        if (movimento.material == material) {
                            // confere primeiro se o movimento atual é do material atual, ok tá correto
                            //se for do tipo entrada soma, se for saída subtraí, ok, tá correto (tem um jeito muitooooo mais simples de fazer, mas tá correto)
                            estoqueCorreto += (modificador * quantidadeConvertida)
                        }
                    }
                }
            }

            if (material.estoque != estoqueCorreto) {
                material.estoque = estoqueCorreto
                material.save(flush: true)
            }
        }
        flash.message = 'Estoque dos materiais atualizado'
        redirect(controller: 'correcoes', action: 'index')
    }

}
