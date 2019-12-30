package com.br.asgardtecnologia.erp.base

import ar.com.fdvs.dj.core.DynamicJasperHelper
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.DJCalculation
import ar.com.fdvs.dj.domain.DJGroupLabel
import ar.com.fdvs.dj.domain.DynamicReport
import ar.com.fdvs.dj.domain.StringExpression
import ar.com.fdvs.dj.domain.Style
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
import com.br.asgardtecnologia.base.ColunaTotalizar
import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.base.Utils
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.apache.commons.lang.StringEscapeUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass

import java.awt.Color

class ReportsService {

    def grailsApplication
    def messageService

    static ReportsService getBean() {
        return ApplicationHolder.application.getMainContext().getBean("reportsService")
    }

    def leftHeaderStyle(){
        Style headerStyleLeft = new Style();
        headerStyleLeft.setFont(Font.ARIAL_MEDIUM_BOLD);
        headerStyleLeft.setBorderBottom(Border.PEN_1_POINT());
        headerStyleLeft.setBackgroundColor(Color.LIGHT_GRAY);
        //headerStyle.setTextColor(Color.black);
        headerStyleLeft.setHorizontalAlign(HorizontalAlign.LEFT);
        headerStyleLeft.setVerticalAlign(VerticalAlign.MIDDLE);
        headerStyleLeft.setTransparency(Transparency.OPAQUE);
        return headerStyleLeft
    }

    def rightHeaderStyle(){
        Style headerStyleRight = new Style();
        headerStyleRight.setFont(Font.ARIAL_MEDIUM_BOLD);
        headerStyleRight.setBorderBottom(Border.PEN_1_POINT());
        headerStyleRight.setBackgroundColor(Color.LIGHT_GRAY);
        //headerStyle.setTextColor(Color.black);
        headerStyleRight.setHorizontalAlign(HorizontalAlign.RIGHT);
        headerStyleRight.setVerticalAlign(VerticalAlign.MIDDLE);
        headerStyleRight.setTransparency(Transparency.OPAQUE);
        return headerStyleRight
    }

    def baseStyleLeft(){
        Style style = new Style();
        style.setFont(new Font(8, Font._FONT_VERDANA, true))
        style.setVerticalAlign(VerticalAlign.TOP)
        style.setHorizontalAlign(HorizontalAlign.LEFT)
        return style
    }

    def baseStyleRight(){
        Style style = new Style();
        style.setFont(new Font(8, Font._FONT_VERDANA, true))
        style.setVerticalAlign(VerticalAlign.TOP)
        style.setHorizontalAlign(HorizontalAlign.RIGHT)
        return style
    }

    def dataStyleLeft(String padrao = "dd/MM/yyyy"){
        Style style = new Style();
        style.setFont(new Font(8, Font._FONT_VERDANA, true))
        style.setVerticalAlign(VerticalAlign.TOP)
        style.setHorizontalAlign(HorizontalAlign.LEFT)
        style.setPattern(padrao)
        return style
    }

    def dataStyleRight(String padrao = "dd/MM/yyyy"){
        Style style = new Style();
        style.setFont(new Font(8, Font._FONT_VERDANA, true))
        style.setVerticalAlign(VerticalAlign.TOP)
        style.setHorizontalAlign(HorizontalAlign.LEFT)
        style.setPattern(padrao)
        return style
    }

    def footerDetailStyle(){
        Style footerDetail = new Style()
        footerDetail.setFont(new Font(10, Font._FONT_ARIAL, false))
        footerDetail.setHorizontalAlign(HorizontalAlign.CENTER)
        footerDetail.setTextColor(Color.BLACK)
        footerDetail.setVerticalAlign(VerticalAlign.MIDDLE)
        footerDetail.setPaddingTop(0)
        footerDetail.setPaddingBottom(0)
        footerDetail.setBorderTop(Border.PEN_1_POINT())
        return footerDetail
    }

    def footerNumberStyle(){
        Style footerNumberStyle = new Style()
        footerNumberStyle.setFont(new Font(8, Font._FONT_VERDANA, false))
        footerNumberStyle.setHorizontalAlign(HorizontalAlign.CENTER)
        footerNumberStyle.setVerticalAlign(VerticalAlign.MIDDLE)
        footerNumberStyle.setBorderTop(Border.PEN_1_POINT())
        return footerNumberStyle
    }

    def detailStyle(){
        Style columnDetail = new Style()
        columnDetail.setHorizontalAlign(HorizontalAlign.LEFT)
        columnDetail.setTextColor(Color.BLACK)
        columnDetail.setVerticalAlign(VerticalAlign.MIDDLE)
        columnDetail.setPaddingTop(0)
        columnDetail.setPaddingBottom(0)
        return columnDetail
    }

    /***
     * DJGroupLabel para ser usado em um agrupador
     * @param label String a ser exibida ex.: Subtotal
     * @param estilo Estilo a ser aplicado, se não informado, usa o estilo base à direita: reportsService.baseStyleRight()
     * @param posicao Posicionamento, se não informado, posiciona à esquerda
     * @return
     */
    def rotulo(String label, Style estilo = this.baseStyleRight(), LabelPosition posicao = LabelPosition.LEFT){
        new DJGroupLabel(new StringExpression() {
            public Object evaluate(Map fields, Map variables, Map parameters) {
                return label
            }
        }, estilo, posicao)
    }

    /**
     * Cria um agrupador de valores para report
     * @param colunaAAgrupar Coluna principal para agrupar em torno dela
     * @param rotulo Label para o agrupador (ex.: Subtotais)
     * @param variaveisAProcessar Lista das variaveis de rodapé para adicionar com o addFooterVariable
     * ex.: [(DJCalculation.SUM):[coluna1,coluna2], (DJCalculation.AVERAGE):[coluna3, coluna4]]
     * @param estiloVariaveis Estilo a ser aplicado às variáveis, caso não informado, usa o estilo base a direita reportsService.baseStyleRight()
     * @param layout Layout padrão, se não informado, usa GroupLayout.VALUE_FOR_EACH que aparece o valor em cada linha
     * @return
     */
    DJGroup group(AbstractColumn colunaAAgrupar, DJGroupLabel rotulo,
                  Map<DJCalculation,List<PropertyColumn>> variaveisAProcessar = [:],
                  Style estiloVariaveis = this.baseStyleRight(),
                  layout = GroupLayout.VALUE_FOR_EACH){
        String nome = new Random().nextFloat().toString()
        GroupBuilder gb = new GroupBuilder(nome)
        gb.setCriteriaColumn((PropertyColumn) colunaAAgrupar)
        variaveisAProcessar.each { operacao, colunas ->
            colunas.each { coluna ->
                gb.addFooterVariable(coluna, operacao, estiloVariaveis)
            }
        }
        gb.setGroupLayout(layout)
        gb.setFooterLabel(rotulo)
        return gb.build()
    }

    /**
     * Cria um FastReportBuilder já com alguns parâmetros básicos setados:
     * página ocupando máximo de largura,
     * print de linhas zebrado
     * formato paisagem
     * @return
     */
    FastReportBuilder reportBuilderBasico(){
        FastReportBuilder drb = new FastReportBuilder()
        drb.setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
        return drb
    }

    def print(Class mainclass, response, params, String title, dataSource, fields, totalFields = null, grandTotalLegend = null) {
        FastReportBuilder drb = new FastReportBuilder()

        // Monta o subt?tulo
        String subtitle = getSubtitle(params)

        Style columnDetail = new Style()
        columnDetail.setFont(new Font(9, Font._FONT_ARIAL, true))
        columnDetail.setHorizontalAlign(HorizontalAlign.RIGHT)
        columnDetail.setTextColor(java.awt.Color.BLACK)
        columnDetail.setVerticalAlign(VerticalAlign.MIDDLE)
        columnDetail.setPaddingTop(0)
        columnDetail.setPaddingBottom(0)

        Style grandTotalLegendStyle = columnDetail.clone()
        grandTotalLegendStyle.setHorizontalAlign(HorizontalAlign.LEFT)

        fields = processFields(mainclass, fields)
        dataSource = processDataSource(dataSource, fields)

        this.addColumns(drb, mainclass, fields)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setUseFullPageWidth(true)
                .setPrintBackgroundOnOddRows(true)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
        if (params.columnDetailStyle) {
            drb.getColumns().each {
                it.setStyle(params.columnDetailStyle)
            }
        }
        if (totalFields?.size()) {
            drb.setGrandTotalLegend(grandTotalLegend ?: "Totais:")
            drb.setGrandTotalLegendStyle(grandTotalLegendStyle)
        }
        totalFields?.each {
            drb.addGlobalFooterVariable(it, DJCalculation.SUM, columnDetail)
        }

        DynamicReport dr = drb.build()

        JRDataSource ds = new JRBeanCollectionDataSource(dataSource)
        JasperReport jr = DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager(), params)
        JasperPrint jp = JasperFillManager.fillReport(jr, params, ds)

        Utils.JasperReportOutput(response, jp, params)
    }

    def getSubtitle(params, lineBreak = "\\n") {
        // Prepara vari?vel de retorno
        String ret = messageService.getMessage("executed.in") + " " + (new Date()).format("dd/MM/yyyy HH:mm:ss") + lineBreak + lineBreak

        // Loop nos par?metros
        params.each { k, v ->

            // Verifica se o par?metro atual do loop ? um operando (Cont?m .op?)
            if (k.contains(".op") && v) {
                def fieldname = k.replace(".op", "")
                def low = getProcessedFilterValue(params, fieldname)

                // Concatena os dados do par?metro atual (Exemplo: ID igual a 77)
                ret += StringEscapeUtils.escapeJava(messageService.getMessage(k.replace(".op", ".label")) + " " +
                        messageService.getMessage("default.operands." + v + ".label").toLowerCase() + " " +
                        low)

                if (v.equals("between")) {
                    def high = getProcessedFilterValue(params, fieldname + "_high")
                    ret += StringEscapeUtils.escapeJava(" " + messageService.getMessage("and.label") + " " + high)
                }

                ret += lineBreak
            }
        }
        return ret
    }

    def getProcessedFilterValue(params, fieldname) {
        def value = params[fieldname]

        // Autocompletes
        if (!value)
            value = params[fieldname?.replace(".", "__") + "__id"]

        if (value instanceof Map) {
            value = params[fieldname + "__id"]
        } else if (value instanceof Date) {
            value = value.format("dd/MM/yyyy")
        } else if (value.equals("true") || value.equals("false")) {
            value = messageService.getMessage(value + ".label")
        }

        return value
    }

    def processFields(mainclass, fields) {
        def ret = []

        GrailsDomainClass domainArtefact = grailsApplication.getArtefact("Domain", mainclass.name)
        fields.each { field ->
            def newfield = [:]

            if (field in String) newfield.name = field
            else if (field in Map) newfield = field

            newfield.property = Persistente.GetDomainPropertyRecursively(domainArtefact, newfield.name)

            ret << newfield
        }

        return ret
    }

    def processDataSource(dataSource, fields) {
        def ret = []

        dataSource.each { row ->
            def newRow = [:]

            fields.each { field ->
                newRow[field.name] = Utils.GetProperty(row, field.name)

                if (field.property.isEnum())
                    newRow[field.name] = messageService.getEnumMessage(newRow[field.name]?.toString())
                else if (field.property.type in Persistente)
                    newRow[field.name] = newRow[field.name]?.toString()
            }

            ret << newRow
        }

        return ret
    }

    def addColumns(FastReportBuilder drb, Class mainclass, fields) {
        fields.each { field ->
            this.addColumn(drb, field.name, field.property.type, field.width, field.mask ?: "0.00")
        }

        return drb
    }

    def addColumn(FastReportBuilder drb, String fieldname, fieldclass, width, mask) {
        if (!width) width = getWidth(fieldclass)

        def label = messageService.getMessage(fieldname + ".label")

        if (fieldclass in BigDecimal)
            drb.addColumn(label, fieldname, fieldclass.getName(), width, false, mask)
        else if (fieldclass.isEnum() || fieldclass in Persistente)
            drb.addColumn(label, fieldname, String.getName(), width)
        else
            drb.addColumn(label, fieldname, fieldclass.getName(), width)

    }

    def getWidth(fieldclass) {
        switch (fieldclass) {
            case Long:
                return 8
                break
            case String:
                return 60
                break
            case BigDecimal:
                return 10
            case Date:
                return 10
                break
            default:
                if (fieldclass in Persistente)
                    return 60
                else if (fieldclass?.isEnum())
                    return 30
                else
                    return 10
                break
        }
    }
}
