package com.br.asgardtecnologia.erp.materiais

import ar.com.fdvs.dj.core.DynamicJasperHelper
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.*
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.builders.FastReportBuilder
import ar.com.fdvs.dj.domain.builders.GroupBuilder
import ar.com.fdvs.dj.domain.constants.Font
import ar.com.fdvs.dj.domain.constants.GroupLayout
import ar.com.fdvs.dj.domain.constants.HorizontalAlign
import ar.com.fdvs.dj.domain.constants.LabelPosition
import ar.com.fdvs.dj.domain.constants.Page
import ar.com.fdvs.dj.domain.entities.DJGroup
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn
import com.br.asgardtecnologia.base.Utils
import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.springframework.dao.DataIntegrityViolationException

class LDMController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def LDMService
    def reportsService

    static TAB_COMP = "tab_composicao"

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def LDMInstanceList = paramsToCriteria(params)
        [LDMInstanceList: LDMInstanceList, LDMInstanceTotal: LDMInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [LDMInstance: new LDM(params)]
                break
            case 'POST':
                def LDMInstance = new LDM(params)
                setBaseData(LDMInstance)
                if (!LDMService.save(LDMInstance)) {
                    render view: 'create', model: [LDMInstance: LDMInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'LDM.label', default: 'LDM'), LDMInstance.id])
                redirect controller: 'material', action: 'edit', id: LDMInstance.material.id, fragment: TAB_COMP
                break
        }
    }

    def show() {
        def LDMInstance = LDM.read(params.id)
        if (!LDMInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'LDM.label', default: 'LDM'), params.id])
            redirect action: 'list'
            return
        }

        [LDMInstance: LDMInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def LDMInstance = LDM.read(params.id)
                if (!LDMInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'LDM.label', default: 'LDM'), params.id])
                    redirect controller: 'material', action: 'edit', id: LDMInstance.material.id, fragment: TAB_COMP
                    return
                }

                [LDMInstance: LDMInstance]
                break
            case 'POST':
                def LDMInstance = LDM.get(params.id)
                setBaseData(LDMInstance)
                if (!LDMInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'LDM.label', default: 'LDM'), params.id])
                    redirect controller: 'material', action: 'edit', id: LDMInstance.material.id, fragment: TAB_COMP
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (LDMInstance.version > version) {
                        LDMInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'LDM.label', default: 'LDM')] as Object[],
                                "Another user has updated this LDM while you were editing")
                        render view: 'edit', model: [LDMInstance: LDMInstance]
                        return
                    }
                }

                LDMInstance.properties = params

                if (!LDMService.save(LDMInstance)) {
                    render view: 'edit', model: [LDMInstance: LDMInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'LDM.label', default: 'LDM'), LDMInstance.id])
                redirect controller: 'material', action: 'edit', id: LDMInstance.material.id, fragment: TAB_COMP
                break
        }
    }

    def delete() {
        def LDMInstance = LDM.get(params.id)
        if (!LDMInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'LDM.label', default: 'LDM'), params.id])
            redirect controller: 'material', action: 'edit', id: LDMInstance.material.id, fragment: TAB_COMP
            return
        }

        try {
            LDMService.delete(LDMInstance)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'LDM.label', default: 'LDM'), params.id])
            redirect controller: 'material', action: 'edit', id: LDMInstance.material.id, fragment: TAB_COMP
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'LDM.label', default: 'LDM'), params.id])
            redirect controller: 'material', action: 'edit', id: LDMInstance.material.id, fragment: TAB_COMP
        }
    }

    def report() {
//        render params
//        return

        ////Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder()

        //Cria os estilos
        Style leftStyle = reportsService.baseStyleLeft()
        Style rightStyle = reportsService.baseStyleRight()
        Style rightStyleHeader = reportsService.rightHeaderStyle()
        Style leftStyleHeader = reportsService.leftHeaderStyle()
        Style leftStyleData = reportsService.dataStyleLeft()

        //Colunas
        AbstractColumn columnComposicao = ColumnBuilder.getNew()
                .setColumnProperty("material_composicao.id", Long.class.getName())
                .setTitle("Código")
                .setWidth(new Integer(8))
                .setHeaderStyle(leftStyleHeader)
                .setStyle(leftStyle)
                .build()

        AbstractColumn columnComposicaoDesc = ColumnBuilder.getNew()
                .setColumnProperty("material_composicao.descricao", String.class.getName())
                .setTitle("Descrição")
                .setWidth(new Integer(50))
                .setHeaderStyle(leftStyleHeader)
                .setStyle(leftStyle)
                .build()

        AbstractColumn colunaQuantidade = ColumnBuilder.getNew()
                .setColumnProperty("quantidade", BigDecimal.class.getName())
                .setTitle("Quantidade")
                .setWidth(10)
                .setPattern("0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(rightStyleHeader)
                .build()

        AbstractColumn colunaCusto = ColumnBuilder.getNew()
                .setWidth(22)
                .setPattern("R\$ ###,##0.00")
                .setTitle("Custo")
                .setColumnProperty("custo", BigDecimal.class.getName())
                .setStyle(rightStyle)
                .setHeaderStyle(rightStyleHeader)
                .build()

        AbstractColumn columnTotal = ColumnBuilder.getNew()
                .setColumnProperty("custoTotal", BigDecimal.class.getName()).setTitle("Total")
                .setWidth(new Integer(10))
                .setPattern("R\$ ###,##0.00")
                .setStyle(rightStyle)
                .setHeaderStyle(rightStyleHeader)
                .build()

        AbstractColumn columnGrupo = ColumnBuilder.getNew()
                .setColumnProperty("material.descricao", String.class.getName()).setTitle("Produto:")
                .setWidth(new Integer(500))
                .setStyle(leftStyle)
                .setHeaderStyle(leftStyleHeader)
                .build()

        DJGroupLabel glabelTotal = reportsService.rotulo('Subtotal')

        DJGroup g1 = reportsService.group(columnGrupo, glabelTotal, [(DJCalculation.SUM): [columnTotal]], null, GroupLayout.DEFAULT)

        drb.addField("material.descricao", String.class.getName())
                .addColumn(columnComposicao)
                .addColumn(columnComposicaoDesc)
                .addColumn(colunaQuantidade)
                .addColumn(colunaCusto)
                .addColumn(columnTotal)
                .addGroup(g1)
                .setTitle("Relação de Composição de Materiais")
                .setPrintBackgroundOnOddRows(true)
                .setSubtitle(reportsService.getSubtitle(params))
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setGrandTotalLegend("TOTAL GERAL:")
                .addGlobalFooterVariable(columnTotal, DJCalculation.SUM, rightStyle)
                .setUseFullPageWidth(true)

        DynamicReport dr = drb.build()

        //def LDMInstanceList = paramsToCriteria(params)
        def newOrderBy = params.orderBy
        if (params.orderBy.split("\\.").size() > 1)
            params.orderBy = "id"
        def LDMInstanceList = paramsToCriteria(params)
        if (params.orderBy != newOrderBy) {
            if (newOrderBy == 'material.grupo')
                LDMInstanceList.sort { it.material.grupo }
            else if (newOrderBy == 'material.sub_grupo')
                LDMInstanceList.sort { it.material."sub_grupo" }
            else if (newOrderBy == 'material.localizacao')
                LDMInstanceList.sort { it.material.localizacao }
            else
                LDMInstanceList.sort { it."${params.orderBy}" }
        } else
            LDMInstanceList.sort { it."${params.orderBy}" }

        JRDataSource ds = new JRBeanCollectionDataSource(LDMInstanceList)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }
}
