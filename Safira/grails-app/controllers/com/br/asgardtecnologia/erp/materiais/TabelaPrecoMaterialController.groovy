package com.br.asgardtecnologia.erp.materiais

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
import ar.com.fdvs.dj.domain.constants.Font
import ar.com.fdvs.dj.domain.constants.GroupLayout
import ar.com.fdvs.dj.domain.constants.HorizontalAlign
import ar.com.fdvs.dj.domain.constants.LabelPosition
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

class TabelaPrecoMaterialController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    static TAB_PRECOS = "tab_precos"

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def tabelaPrecoMaterialInstanceList = paramsToCriteria(params)
        [tabelaPrecoMaterialInstanceList: tabelaPrecoMaterialInstanceList, tabelaPrecoMaterialInstanceTotal: tabelaPrecoMaterialInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [tabelaPrecoMaterialInstance: new TabelaPrecoMaterial(params)]
                break
            case 'POST':
                def tabelaPrecoMaterialInstance = new TabelaPrecoMaterial(params)
                setBaseData(tabelaPrecoMaterialInstance)
                if (!tabelaPrecoMaterialInstance.save(flush: true)) {
                    render view: 'create', model: [tabelaPrecoMaterialInstance: tabelaPrecoMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'tabelaPrecoMaterial.label', default: 'TabelaPrecoMaterial'), tabelaPrecoMaterialInstance.id])
                redirect controller: 'material', action: 'edit', id: tabelaPrecoMaterialInstance.material.id, fragment: TAB_PRECOS
                break
        }
    }

    def show() {
        def tabelaPrecoMaterialInstance = TabelaPrecoMaterial.read(params.id)
        if (!tabelaPrecoMaterialInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tabelaPrecoMaterial.label', default: 'TabelaPrecoMaterial'), params.id])
            redirect controller: 'material', action: 'edit', id: tabelaPrecoMaterialInstance.material.id, fragment: TAB_PRECOS
            return
        }

        [tabelaPrecoMaterialInstance: tabelaPrecoMaterialInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def tabelaPrecoMaterialInstance = TabelaPrecoMaterial.read(params.id)
                if (!tabelaPrecoMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'tabelaPrecoMaterial.label', default: 'TabelaPrecoMaterial'), params.id])
                    redirect controller: 'material', action: 'edit', id: tabelaPrecoMaterialInstance.material.id, fragment: TAB_PRECOS
                    return
                }

                [tabelaPrecoMaterialInstance: tabelaPrecoMaterialInstance]
                break
            case 'POST':
                def tabelaPrecoMaterialInstance = TabelaPrecoMaterial.get(params.id)
                setBaseData(tabelaPrecoMaterialInstance)
                if (!tabelaPrecoMaterialInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'tabelaPrecoMaterial.label', default: 'TabelaPrecoMaterial'), params.id])
                    redirect controller: 'material', action: 'edit', id: tabelaPrecoMaterialInstance.material.id, fragment: TAB_PRECOS
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (tabelaPrecoMaterialInstance.version > version) {
                        tabelaPrecoMaterialInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'tabelaPrecoMaterial.label', default: 'TabelaPrecoMaterial')] as Object[],
                                "Another user has updated this TabelaPrecoMaterial while you were editing")
                        render view: 'edit', model: [tabelaPrecoMaterialInstance: tabelaPrecoMaterialInstance]
                        return
                    }
                }

                tabelaPrecoMaterialInstance.properties = params

                if (!tabelaPrecoMaterialInstance.save(flush: true)) {
                    render view: 'edit', model: [tabelaPrecoMaterialInstance: tabelaPrecoMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'tabelaPrecoMaterial.label', default: 'TabelaPrecoMaterial'), tabelaPrecoMaterialInstance.id])
                redirect controller: 'material', action: 'edit', id: tabelaPrecoMaterialInstance.material.id, fragment: TAB_PRECOS
                break
        }
    }

    def delete() {
        def tabelaPrecoMaterialInstance = TabelaPrecoMaterial.get(params.id)
        if (!tabelaPrecoMaterialInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tabelaPrecoMaterial.label', default: 'TabelaPrecoMaterial'), params.id])
            redirect controller: 'material', action: 'edit', id: tabelaPrecoMaterialInstance.material.id, fragment: TAB_PRECOS
            return
        }

        try {
            tabelaPrecoMaterialInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'tabelaPrecoMaterial.label', default: 'TabelaPrecoMaterial'), params.id])
            redirect controller: 'material', action: 'edit', id: tabelaPrecoMaterialInstance.material.id, fragment: TAB_PRECOS
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'tabelaPrecoMaterial.label', default: 'TabelaPrecoMaterial'), params.id])
            redirect controller: 'material', action: 'edit', id: tabelaPrecoMaterialInstance.material.id, fragment: TAB_PRECOS
        }
    }

    def gridAdd() {
        switch (request.method) {
            case 'GET':
                [tabelaPrecoMaterialInstance: new TabelaPrecoMaterial(params)]
                break
            case 'POST':
                def tabelaPrecoMaterialInstance
                if (params.id) tabelaPrecoMaterialInstance = TabelaPrecoMaterial.get(params.id)
                else tabelaPrecoMaterialInstance = new TabelaPrecoMaterial(params)

                setBaseData(tabelaPrecoMaterialInstance)
                if (!tabelaPrecoMaterialInstance.save(validate: false, flush: true)) {
                    render view: 'create', model: [tabelaPrecoMaterialInstance: tabelaPrecoMaterialInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'tabelaPrecoMaterial.label', default: 'tabelaPrecoMaterial'), tabelaPrecoMaterialInstance.id])
                redirect controller: params.controllerRedirect, action: 'create', params: ['tabelaPrecoMaterial.id': tabelaPrecoMaterialInstance.id]

                break
        }
    }

    def report() {
//        render params
//        return

        ////Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder()

        Style numberStyle = new Style("numberStyle");
        numberStyle.setFont(new Font(10, Font._FONT_VERDANA, true));
        numberStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

        GroupBuilder gb1 = new GroupBuilder("group1")
        Style titleStyle = new Style("titleStyle")
        titleStyle.setFont(new Font(10, Font._FONT_VERDANA, true))

        AbstractColumn columnGrupo = ColumnBuilder.getNew()
                .setColumnProperty("tabela.descricao", String.class.getName()).setTitle("Tabela:")
                .setWidth(new Integer(500))
                .setStyle(titleStyle)
                .setHeaderStyle(titleStyle)
                .build()

        DJGroup g1 = gb1.setCriteriaColumn((PropertyColumn) columnGrupo)
                .setGroupLayout(GroupLayout.DEFAULT)
                .build()

        drb.addField("tabela.descricao", String.class.getName())
                .addColumn("Descrição", "material.descricao", String.class.getName(), 50)
                .addColumn("Valor", "valor", BigDecimal.class.getName(), 10)
                .addGroup(g1)
                .setTitle("Tabela de Preços")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)

        DynamicReport dr = drb.build()

        def tabelaInstanceList = paramsToCriteria(params)
        tabelaInstanceList.sort { a, b ->
            a.tabela.descricao<=>b.tabela.descricao ?:
                a.material.descricao<=>b.material.descricao
        }
        JRDataSource ds = new JRBeanCollectionDataSource(tabelaInstanceList)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }

}
