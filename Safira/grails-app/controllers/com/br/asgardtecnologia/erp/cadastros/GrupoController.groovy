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

class GrupoController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index() {
        redirect action: 'create', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def grupoInstanceList = paramsToCriteria(params)
        [grupoInstanceList: grupoInstanceList, grupoInstanceTotal: grupoInstanceList.totalCount]
    }

    def create() {
        switch (request.method) {
            case 'GET':
                [grupoInstance: new Grupo(params)]
                break
            case 'POST':
                def grupoInstance = new Grupo(params)
                setBaseData(grupoInstance)
                if (!grupoInstance.save(flush: true)) {
                    render view: 'create', model: [grupoInstance: grupoInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'grupo.label', default: 'Grupo'), grupoInstance.id])
                redirect action: 'show', id: grupoInstance.id
                break
        }
    }

    def show() {
        def grupoInstance = Grupo.read(params.id)
        if (!grupoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'grupo.label', default: 'Grupo'), params.id])
            redirect action: 'list'
            return
        }

        [grupoInstance: grupoInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def grupoInstance = Grupo.read(params.id)
                if (!grupoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'grupo.label', default: 'Grupo'), params.id])
                    redirect action: 'list'
                    return
                }

                [grupoInstance: grupoInstance]
                break
            case 'POST':
                def grupoInstance = Grupo.get(params.id)
                setBaseData(grupoInstance)
                if (!grupoInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'grupo.label', default: 'Grupo'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (grupoInstance.version > version) {
                        grupoInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'grupo.label', default: 'Grupo')] as Object[],
                                "Another user has updated this Grupo while you were editing")
                        render view: 'edit', model: [grupoInstance: grupoInstance]
                        return
                    }
                }

                grupoInstance.properties = params

                if (!grupoInstance.save(flush: true)) {
                    render view: 'edit', model: [grupoInstance: grupoInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'grupo.label', default: 'Grupo'), grupoInstance.id])
                redirect action: 'show', id: grupoInstance.id
                break
        }
    }

    def delete() {
        def grupoInstance = Grupo.get(params.id)
        if (!grupoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'grupo.label', default: 'Grupo'), params.id])
            redirect action: 'list'
            return
        }

        try {
            grupoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'grupo.label', default: 'Grupo'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'grupo.label', default: 'Grupo'), params.id])
            redirect action: 'show', id: params.id
        }
    }

    def report() {
        //Map params = new HashMap()
        FastReportBuilder drb = new FastReportBuilder();
        drb.addColumn("Código", "id", Long.class.getName(), 10)
                .addColumn("Descrição", "descricao", String.class.getName(), 30)
                .addColumn("Grupo", "grupo.descricao", String.class.getName(), 30)
                .setTitle("Relação de Grupos")
                .setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)

        DynamicReport dr = drb.build()

        JRDataSource ds = new JRBeanCollectionDataSource(SubGrupo.list())
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }
}
