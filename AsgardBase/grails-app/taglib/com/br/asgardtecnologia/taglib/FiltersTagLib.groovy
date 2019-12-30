package com.br.asgardtecnologia.taglib

import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.scaffolding.DomainClassPropertyComparator

class FiltersTagLib {

    static namespace = "asg"

    def params = [:]

    def filter = { attrs, body ->

        // Caso o domain não tenha sido passado, recupera o domínio de acordo com o controller atual
        if (!attrs.domain) {
            attrs.domain = grailsApplication.getArtefactByLogicalPropertyName(DomainClassArtefactHandler.TYPE, controllerName).fullName
        }

        // Recupera o Artefact referente ao domínio
        def domainDescriptor = grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, attrs.domain)

        // Determinação do NAME
        if (!attrs.name) attrs.name = domainDescriptor.logicalPropertyName
        if (!attrs.gridName) attrs.gridName = attrs.name

        // Caso não tenham sido passados os campos via TAG, recupera o 'defaultFilterFields' do Domain
        if (!attrs.fields) {
            attrs.fields = domainDescriptor.getPropertyValue("defaultFilterFields")
            if (!attrs.fields) {
                attrs.fields = []
            }
        }

        if (!attrs.customFields) {
            def customFields = domainDescriptor.getPropertyValue("customFilterFields")
        }

        if (attrs.customFields) {
            attrs.fields += attrs.customFields
        }

        // Declaração do Array utilizado para a determinação dos campos
        def fields = []

        // Caso ainda assim os campos não foram determinados,
        if (!attrs.fields) {
            def props = domainDescriptor.persistentProperties as List
            def domainClass = grailsApplication.getDomainClass(attrs.domain)
            Collections.sort(props, new DomainClassPropertyComparator(domainClass))

            for (prop in props) {
                fields << [name: prop.name]
            }

            attrs.fields = fields

        } else

        // Caso o atributo 'fields' seja uma string, deverá ser separada por ponto e vírgula (;)
        if (attrs.fields.class == String) {
            // busca os campos persistentes declarados e ordenados via 'constraints'

            for (field in attrs.fields.split(";")) {
                fields << [name: field]
            }

            attrs.fields = fields
        }

        // Campos de Ordenação
        if (!attrs.sortfields) {
            attrs.sortfields = domainDescriptor.getPropertyValue("defaultSortFields") ?: attrs.fields
        }

        // Caso o atributo 'fields' seja uma string, deverá ser separada por ponto e vírgula (;)
        if (attrs.sortfields.class == String) {
            fields = []

            for (field in attrs.sortfields.split(";")) {
                fields << [name: field]
            }

            attrs.sortfields = fields
        }

        params = attrs.params

        out << '<div class="widget-box collapsible">'
        out << '<a href="#' + attrs.name + '" data-toggle="collapse" onclick="\$(\'#' + attrs.name + ' :input:enabled:visible:first\').focus();">'
        out << '<div class="widget-title">'
        out << '<span class="icon"><i class="icon icon-filter"></i></span>'
        out << '<h5>' + g.message(code: 'default.filter.label') + '</h5>'
        out << '</div>'
        out << '</a>'
        out << '<div class="widget-content nopadding in collapse" id=' + attrs.name + '>'

        def formName = "form" + attrs.name
        def formFilter = "<div class='niceScroll' style='overflow-x:hidden; /*max-height:210px;*/'>"

        def instance = domainDescriptor?.clazz?.newInstance()
        def count = 0
        for (field in attrs.fields) {
            if (!field.tag) formFilter += dynamicFilter(field, domainDescriptor, instance)
            else formFilter += "${field.tag}"(field)
            count += 1
        }

        if (!attrs.noextrafilter) {
            def defaultFilterFieldsExtra = attrs.defaultFilterFieldsExtra ?: domainDescriptor.getPropertyValue("defaultFilterFieldsExtra")
            if (defaultFilterFieldsExtra) {
                for (field in defaultFilterFieldsExtra) {
                    field.domainDescriptor = domainDescriptor
                    formFilter += asg.labeledDynamicField(field)
                    count += 1
                }
            }
        }

        // Determina ordenaçao
        def sortAttrs = [:]
        sortAttrs.name = "orderBy"
        sortAttrs.span = 6
        sortAttrs.keys = ["id"] + attrs.sortfields*.name
        sortAttrs.from = [resolveMessage(controllerName, "id")]
        attrs.sortfields.each { sortAttrs.from << resolveMessage(controllerName, it.name) }

        sortAttrs.value = "id"

        formFilter += "<div class='row' style='background-color: #f4f4f4'>"
        formFilter += asg.labeledSelect(sortAttrs)
        formFilter += "</div>"

        if (attrs.templateAfter) {
            formFilter += g.render(template: attrs.templateAfter)
        }

        formFilter += asg.hiddenField([name: "customAttrs"])

        formFilter += "</div>"

        if (attrs.endOfFilter) formFilter += attrs.endOfFilter

        if (!attrs.hideButtons) {
            formFilter += '<div class="form-actions">'

            if (!attrs.buttons) {
                def onclick = attrs.forcesubmit ? "" : "ajaxGrid(this, '${attrs.gridName}'); return false;"
                attrs.buttons = []
                attrs.buttons << [onclick: onclick, buttonIcon: attrs.buttonIcon, buttonLabel: attrs.buttonLabel]
            }

            // Botão Buscar
            for (btn in attrs.buttons) {
                formFilter += '<button type="submit" id="btnBusca" onclick="' + btn.onclick + '" class="btn btn-primary"><i class="icon ' + (btn.buttonIcon ?: 'icon-search') + ' icon-white"></i> ' + (btn.buttonLabel ?: "Buscar") + "</button> "
            }

            // Botão Limpar (AJAX)
            //def jsClearAttrs = '$(\'#' + formName + '\')[0].reset();$(\'.resetable > select\').select2(\'data\', null);'
            //formFilter += ' <a href=' + createLink(controller: controllerName, action: actionName) + ' type="submit" class="btn btn-danger"><i class="icon icon-refresh icon-white"></i> ' + (attrs.buttonLabel ?: "Limpar") + "</a>"
            formFilter += '</div>'
        }

        if (count > 0) formFilter += "</div>"

        def formController = attrs.params.reportController ?: (!attrs.forcesubmit ? 'grid' : controllerName)
        def formAction = attrs.params.reportAction ?: (!attrs.forcesubmit ? 'gridAjax' : actionName)
        out << g.form([name: formName, class: 'form-horizontal', method: 'POST', controller: formController, action: formAction, target: attrs.target], formFilter)

        out << '</div>'
    }

    def transformFields(fields) {

    }

    def dynamicFilter(field, domainDescriptor, instance) {
        def ret = "<div class='row'>"

        // Atributos do campo atual do filtro
        def lowAttrs = [:]
        lowAttrs.name = (field['name'] ?: field)
        lowAttrs.domainDescriptor = domainDescriptor
        lowAttrs.instance = instance
        lowAttrs.onchange = "selectOperand(this, 'eq');"
        lowAttrs.onkeyup = "selectOperand(this, 'eq');"
        if (field.type) lowAttrs.type = field.type

        def dynamicFieldsTagLib = new DynamicFieldsTagLib()
        Boolean isBoolean = dynamicFieldsTagLib.isBooleanProperty(lowAttrs)

        lowAttrs.noLabel = true
        def highAttrs = lowAttrs.clone()
        highAttrs.name += "_high"
        highAttrs.value = params[highAttrs.name]

        // Body do campo atual do filtro
        def fieldBody = '<div class="input-group">'

        // Prepare operands
        def operands = []
        operands << getOperand("")
        operands << getOperand("eq")
        operands << getOperand("ne")
        operands << getOperand("between")
        operands << getOperand("lt")
        operands << getOperand("le")
        operands << getOperand("gt")
        operands << getOperand("ge")

        def operandsAttrs = [:]
        operandsAttrs.name = lowAttrs.name + ".op"
        operandsAttrs.from = operands*.value
        operandsAttrs.keys = operands*.name
        operandsAttrs.value = field.operandDefault
        operandsAttrs.style = "width:150px;"

        lowAttrs["data-asg-operand"] = operandsAttrs.name.replace(".", "\\.")

        fieldBody += '<div class="input-group-btn"' + (isBoolean ? 'style="display:none"' : '') + '>'
        fieldBody += asg.select(operandsAttrs)
        fieldBody += '</div>'

        if (isBoolean) {
            def boolList = []
            boolList << [name: "", value: null]
            boolList << [name: "true", value: g.message(code: "sim.label")]
            boolList << [name: "false", value: g.message(code: "nao.label")]

            def boolAttrs = [:]
            lowAttrs.from = boolList*.value
            lowAttrs.keys = boolList*.name
            lowAttrs.style = "width:100px;"
            lowAttrs.value = field.value

            def jsThis = "\$(this)"
            def jsOperand = "\$('#" + operandsAttrs.name + "')"
            lowAttrs.onChange = "if (" + jsThis + ".val()) selectOperand(this, 'eq'); else selectOperand(this);"

            fieldBody += '<div class="row">'
            fieldBody += '<div id="low" class="col-12 col-sm-5">'
            fieldBody += asg.select(lowAttrs)
            fieldBody += '</div>'
        } else {
            fieldBody += '<div class="row">'
            fieldBody += '<div id="low" class="col-12 col-sm-5">'
            fieldBody += asg.dynamicField(lowAttrs)
            fieldBody += '</div>'
        }

        fieldBody += asg.message(code: 'default.filter.to', class: (field.operandDefault != 'between' ? 'asg-hide' : '') + ' asg-to-high')

        // High (Quando há BETWEEN)
        fieldBody += '<div id="high" class="' + (field.operandDefault != 'between' ? 'asg-hide' : '') + ' col-12 col-sm-5">'
        fieldBody += asg.dynamicField(highAttrs)
        fieldBody += '</div>'
        fieldBody += '</div>'

        fieldBody += '</div>'

        def labeledAttrs = lowAttrs.clone()
        labeledAttrs.remove("noLabel")
        ret += asg.labeled(labeledAttrs, fieldBody)

        ret += '</div><!-- /row -->'

        return ret
    }

    def getOperand(String operand) {
        def ret = [:]
        ret.name = operand
        ret.value = operand ? g.message(code: "default.operands.${operand}.label") : ""
        return ret
    }

    boolean isShowMode(attrs) {
        return (actionName == "show")
    }

    String resolveMessage(controllerLabel, name) {
        def code = cleanName(controllerLabel) + "." + name + ".label"
        def label = getMessage(code)
        if (label == code) {
            code = cleanName(name) + ".label"
            label = getMessage(code)
            if (label == code) {
                code = cleanName(name)?.tokenize('.')[-1] + ".label"
                label = getMessage(code)
            }
        }

        return label
    }

    String cleanName(code) {
        return code.replace("__", ".")
    }

    String getMessage(code) {
        return g.message(code: code)
    }

}