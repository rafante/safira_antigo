package com.br.asgardtecnologia.erp.cadastros

import ar.com.fdvs.dj.core.DynamicJasperHelper
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.DynamicReport
import ar.com.fdvs.dj.domain.builders.FastReportBuilder
import com.br.asgardtecnologia.base.Utils
import com.br.asgardtecnologia.erp.base.BaseController
import grails.plugins.springsecurity.Secured
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.springframework.dao.DataIntegrityViolationException

class GrupoParceiroNegociosController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def grupoParceiroNegociosInstanceList = paramsToCriteria(params)
        [grupoParceiroNegociosInstanceList: grupoParceiroNegociosInstanceList, grupoParceiroNegociosInstanceTotal: grupoParceiroNegociosInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [grupoParceiroNegociosInstance: new GrupoParceiroNegocios(params)]
                break
            case 'POST':
                def grupoParceiroNegociosInstance = new GrupoParceiroNegocios(params)
                setBaseData(grupoParceiroNegociosInstance)
                if (!grupoParceiroNegociosInstance.save(flush: true)) {
                    render view: 'create', model: [grupoParceiroNegociosInstance: grupoParceiroNegociosInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'grupoParceiroNegocios.label', default: 'GrupoParceiroNegocios'), grupoParceiroNegociosInstance.id])
                redirect action: 'show', id: grupoParceiroNegociosInstance.id
                break
        }
    }

    def show() {
        def grupoParceiroNegociosInstance = GrupoParceiroNegocios.read(params.id)
        if (!grupoParceiroNegociosInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'grupoParceiroNegocios.label', default: 'GrupoParceiroNegocios'), params.id])
            redirect action: 'list'
            return
        }

        [grupoParceiroNegociosInstance: grupoParceiroNegociosInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def grupoParceiroNegociosInstance = GrupoParceiroNegocios.read(params.id)
                if (!grupoParceiroNegociosInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'grupoParceiroNegocios.label', default: 'GrupoParceiroNegocios'), params.id])
                    redirect action: 'list'
                    return
                }

                [grupoParceiroNegociosInstance: grupoParceiroNegociosInstance]
                break
            case 'POST':
                def grupoParceiroNegociosInstance = GrupoParceiroNegocios.get(params.id)
                setBaseData(grupoParceiroNegociosInstance)
                if (!grupoParceiroNegociosInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'grupoParceiroNegocios.label', default: 'GrupoParceiroNegocios'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (grupoParceiroNegociosInstance.version > version) {
                        grupoParceiroNegociosInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'grupoParceiroNegocios.label', default: 'GrupoParceiroNegocios')] as Object[],
                                "Another user has updated this GrupoParceiroNegocios while you were editing")
                        render view: 'edit', model: [grupoParceiroNegociosInstance: grupoParceiroNegociosInstance]
                        return
                    }
                }

                grupoParceiroNegociosInstance.properties = params

                if (!grupoParceiroNegociosInstance.save(flush: true)) {
                    render view: 'edit', model: [grupoParceiroNegociosInstance: grupoParceiroNegociosInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'grupoParceiroNegocios.label', default: 'GrupoParceiroNegocios'), grupoParceiroNegociosInstance.id])
                redirect action: 'show', id: grupoParceiroNegociosInstance.id
                break
        }
    }

    def delete() {
        def grupoParceiroNegociosInstance = GrupoParceiroNegocios.get(params.id)
        if (!grupoParceiroNegociosInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'grupoParceiroNegocios.label', default: 'GrupoParceiroNegocios'), params.id])
            redirect action: 'list'
            return
        }

        try {
            grupoParceiroNegociosInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'grupoParceiroNegocios.label', default: 'GrupoParceiroNegocios'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'grupoParceiroNegocios.label', default: 'GrupoParceiroNegocios'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def report() {
        //Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder();
        drb.addColumn("Código", "id", Long.class.getName(), 10)
                .addColumn("Descrição", "descricao", String.class.getName(), 30)
                .addColumn("GrupoParceiroNegocios", "grupoParceiroNegocios.descricao", String.class.getName(), 30)
                .setTitle("Relação de GrupoParceiroNegocioss")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)

        DynamicReport dr = drb.build()

        JRDataSource ds = new JRBeanCollectionDataSource(SubGrupoParceiroNegocios.list())
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }
}
