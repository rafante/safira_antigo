package com.br.asgardtecnologia.erp.cadastros

import ar.com.fdvs.dj.core.DynamicJasperHelper
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.*
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.builders.FastReportBuilder
import ar.com.fdvs.dj.domain.builders.GroupBuilder
import ar.com.fdvs.dj.domain.constants.Font
import ar.com.fdvs.dj.domain.constants.GroupLayout
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

class SubGrupoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def subGrupoInstanceList = paramsToCriteria(params)
        [subGrupoInstanceList: subGrupoInstanceList, subGrupoInstanceTotal: subGrupoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [subGrupoInstance: new SubGrupo(params)]
                break
            case 'POST':
                def subGrupoInstance = new SubGrupo(params)
                setBaseData(subGrupoInstance)
                if (!subGrupoInstance.save(flush: true)) {
                    render view: 'create', model: [subGrupoInstance: subGrupoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'subGrupo.label', default: 'SubGrupo'), subGrupoInstance.id])
                redirect action: 'show', id: subGrupoInstance.id
                break
        }
    }

    def show() {
        def subGrupoInstance = SubGrupo.read(params.id)
        if (!subGrupoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'subGrupo.label', default: 'SubGrupo'), params.id])
            redirect action: 'list'
            return
        }

        [subGrupoInstance: subGrupoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def subGrupoInstance = SubGrupo.read(params.id)
                if (!subGrupoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'subGrupo.label', default: 'SubGrupo'), params.id])
                    redirect action: 'list'
                    return
                }

                [subGrupoInstance: subGrupoInstance]
                break
            case 'POST':
                def subGrupoInstance = SubGrupo.get(params.id)
                if (!subGrupoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'subGrupo.label', default: 'SubGrupo'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (subGrupoInstance.version > version) {
                        subGrupoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'subGrupo.label', default: 'SubGrupo')] as Object[],
                                "Another user has updated this SubGrupo while you were editing")
                        render view: 'edit', model: [subGrupoInstance: subGrupoInstance]
                        return
                    }
                }

                subGrupoInstance.properties = params
                setBaseData(subGrupoInstance)

                if (!subGrupoInstance.save(flush: true)) {
                    render view: 'edit', model: [subGrupoInstance: subGrupoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'subGrupo.label', default: 'SubGrupo'), subGrupoInstance.id])
                redirect action: 'show', id: subGrupoInstance.id
                break
        }
    }

    def delete() {
        def subGrupoInstance = SubGrupo.get(params.id)
        if (!subGrupoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'subGrupo.label', default: 'SubGrupo'), params.id])
            redirect action: 'list'
            return
        }

        try {
            subGrupoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'subGrupo.label', default: 'SubGrupo'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'subGrupo.label', default: 'SubGrupo'), params.id])
            redirect action: 'show', id: params.id
        }
    }


    def report() {
        //Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder()
        GroupBuilder gb1 = new GroupBuilder("group1")
        Style titleStyle = new Style("titleStyle")
        titleStyle.setFont(new Font(12, Font._FONT_VERDANA, true))

        AbstractColumn columnGrupo = ColumnBuilder.getNew()
                .setColumnProperty("grupo.descricao", String.class.getName()).setTitle("Grupo")
                .setWidth(new Integer(500))
                .setStyle(titleStyle)
                .setHeaderStyle(titleStyle)
                .build()

        DJGroupLabel glabelTotal = new DJGroupLabel(new StringExpression() {
            public Object evaluate(Map fields, Map variables, Map parameters) {
                return "Total: " + ExpressionHelper.getGroupCount("group1", variables);
            }
        }, null, LabelPosition.LEFT)

        DJGroup g1 = gb1.setCriteriaColumn((PropertyColumn) columnGrupo)
                .setGroupLayout(GroupLayout.DEFAULT)
                .setFooterLabel(glabelTotal)
                .build()

        drb.addField("grupo.descricao", String.class.getName())
        //        .addGroups(1)
                .addColumn("Código", "id", Long.class.getName(), 10)
                .addColumn("Descrição", "descricao", String.class.getName(), 30)
                .addGroup(g1)
                .setTitle("Relação de Grupos")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setFooterLabel(new DJGroupLabel("TESTE", null))

        DynamicReport dr = drb.build()

        JRDataSource ds = new JRBeanCollectionDataSource(SubGrupo.withCriteria {
            grupo {
                order("descricao")
            }
            order("descricao")
        })
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }
}
