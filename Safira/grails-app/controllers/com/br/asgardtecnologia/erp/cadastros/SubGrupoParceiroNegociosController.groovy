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

class SubGrupoParceiroNegociosController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def subGrupoParceiroNegociosInstanceList = paramsToCriteria(params)
        [subGrupoParceiroNegociosInstanceList: subGrupoParceiroNegociosInstanceList, subGrupoParceiroNegociosInstanceTotal: subGrupoParceiroNegociosInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [subGrupoParceiroNegociosInstance: new SubGrupoParceiroNegocios(params)]
                break
            case 'POST':
                def subGrupoParceiroNegociosInstance = new SubGrupoParceiroNegocios(params)
                setBaseData(subGrupoParceiroNegociosInstance)
                if (!subGrupoParceiroNegociosInstance.save(flush: true)) {
                    render view: 'create', model: [subGrupoParceiroNegociosInstance: subGrupoParceiroNegociosInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'subGrupoParceiroNegocios.label', default: 'SubGrupoParceiroNegocios'), subGrupoParceiroNegociosInstance.id])
                redirect action: 'show', id: subGrupoParceiroNegociosInstance.id
                break
        }
    }

    def show() {
        def subGrupoParceiroNegociosInstance = SubGrupoParceiroNegocios.read(params.id)
        if (!subGrupoParceiroNegociosInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'subGrupoParceiroNegocios.label', default: 'SubGrupoParceiroNegocios'), params.id])
            redirect action: 'list'
            return
        }

        [subGrupoParceiroNegociosInstance: subGrupoParceiroNegociosInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def subGrupoParceiroNegociosInstance = SubGrupoParceiroNegocios.read(params.id)
                if (!subGrupoParceiroNegociosInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'subGrupoParceiroNegocios.label', default: 'SubGrupoParceiroNegocios'), params.id])
                    redirect action: 'list'
                    return
                }

                [subGrupoParceiroNegociosInstance: subGrupoParceiroNegociosInstance]
                break
            case 'POST':
                def subGrupoParceiroNegociosInstance = SubGrupoParceiroNegocios.get(params.id)
                if (!subGrupoParceiroNegociosInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'subGrupoParceiroNegocios.label', default: 'SubGrupoParceiroNegocios'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (subGrupoParceiroNegociosInstance.version > version) {
                        subGrupoParceiroNegociosInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'subGrupoParceiroNegocios.label', default: 'SubGrupoParceiroNegocios')] as Object[],
                                "Another user has updated this SubGrupoParceiroNegocios while you were editing")
                        render view: 'edit', model: [subGrupoParceiroNegociosInstance: subGrupoParceiroNegociosInstance]
                        return
                    }
                }

                subGrupoParceiroNegociosInstance.properties = params
                setBaseData(subGrupoParceiroNegociosInstance)

                if (!subGrupoParceiroNegociosInstance.save(flush: true)) {
                    render view: 'edit', model: [subGrupoParceiroNegociosInstance: subGrupoParceiroNegociosInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'subGrupoParceiroNegocios.label', default: 'SubGrupoParceiroNegocios'), subGrupoParceiroNegociosInstance.id])
                redirect action: 'show', id: subGrupoParceiroNegociosInstance.id
                break
        }
    }

    def delete() {
        def subGrupoParceiroNegociosInstance = SubGrupoParceiroNegocios.get(params.id)
        if (!subGrupoParceiroNegociosInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'subGrupoParceiroNegocios.label', default: 'SubGrupoParceiroNegocios'), params.id])
            redirect action: 'list'
            return
        }

        try {
            subGrupoParceiroNegociosInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'subGrupoParceiroNegocios.label', default: 'SubGrupoParceiroNegocios'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'subGrupoParceiroNegocios.label', default: 'SubGrupoParceiroNegocios'), params.id])
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

        JRDataSource ds = new JRBeanCollectionDataSource(SubGrupoParceiroNegocios.withCriteria {
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
