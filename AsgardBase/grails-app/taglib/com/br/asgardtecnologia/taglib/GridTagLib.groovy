package com.br.asgardtecnologia.taglib

import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.base.Utils
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.GrailsClass
import org.springframework.web.servlet.support.RequestContextUtils

class GridTagLib {

    static namespace = "asg"

    def messageSource
    def grid = { attrs, body ->
        if (!attrs.name && attrs.controller) attrs.name = attrs.controller

        def template = "/_fields/grid"
        if (!attrs.actionName) attrs.actionName = actionName

        if (!attrs.list) attrs.list = []
        if (!attrs.modelInstance && attrs.controller) {
            attrs.modelInstance = grailsApplication.getArtefactByLogicalPropertyName("Domain", attrs.controller).clazz.newInstance()
        }
        //attrs.list.add(attrs.modelInstance)

        attrs.attrsclean = attrs.clone()
        attrs.attrsclean.remove("list")
        attrs.attrsclean.remove("modelInstance")
        if (!params.customAttrs) {
            params.customAttrs = attrs.attrsclean as JSON
        }
        attrs.attrscleanjson = attrs.attrsclean as grails.converters.JSON

        def model = ['attrs'      : attrs,
                     messageSource: messageSource]

        out << g.render(template: template, model: model, plugin: "asgard-base")
    }

    def tdFieldOutput = { attrs, body ->
        // Recupera o atributo field
        def field = attrs.field

        // Determina as configurações (definidas entre colchetes [])
        def configs = field.replace("]", "").split("\\[")

        // Determina o nome do campo (sem as configurações entre colchetes)
        field = configs[0]

        // Remove o primeiro item (no caso, o nome do campo) das configurações
        if (configs.size() > 1)
            configs = (configs as List)[1..-1] as String[]
        else
            configs = []

        def prefix = attrs.name + "[" + attrs.index + "]"
        def fieldName = prefix + "." + field
        def item = attrs.item

        def readonly = false
        def editableFields = attrs.editableFields?.split(";")

        def value
        def valueStr = ""

        if (field.contains("()")) {
            value = item."${field.replace("()", "")}"()
        } else {
            value = com.br.asgardtecnologia.base.Utils.GetProperty(item, field)
        }

        valueStr = value?.toString()

        // Formatação de Moeda
        if (configs?.contains("MONEY")) {
            if (!value) value = 0

            valueStr = formatNumber(number: value, type: "currency", currencyCode: "BRL", locale: new Locale("pt","BR"), minFractionDigits: 2, maxFractionDigits: 10)
        }
        // Formatação de Moeda
        if (configs?.contains("QUANTITY")) {
            if (!value) value = 0

            valueStr = formatNumber(number: value, type: "number", locale: new Locale("pt","BR"))
        }
        // Formatação de Porcentagem
        else if (configs?.contains("PERCENT")) {
            if (!item."${field}") item."${field}" = 0
            valueStr = formatNumber(number: com.br.asgardtecnologia.base.Utils.ToBigDecimal(value)?.div(100), type: "percent", locale: new Locale("pt","BR"))
        }
        // Formatação de Data
        else if (configs?.contains("DATE")) {
            valueStr = formatDate(date: com.br.asgardtecnologia.base.Utils.ToDate(value), format: "dd/MM/yyyy")
        }
        // Formatação de Enum
        else if (configs?.contains("ENUM")) {
            valueStr = (value ? g.message(code: "enum.value." + value) : "")
        }

        if (!attrs.editable) {
            if (org.codehaus.groovy.grails.commons.DomainClassArtefactHandler.isDomainClass(value.getClass())) {
                def controller = value.getClass().getSimpleName()
                def action = "show"
                def id = value.id

                valueStr = asg.link(controller: controller, action: action, id: id, value?.toString())
            }

            // Booleanos (Alternativa temporária)
            if (valueStr == "true") valueStr = "<center>" + asg.checkBox(value: false, disabled: true) + "</center>"
            else if (valueStr == "false") valueStr = ""

            else if (configs?.contains("CHECKBOX")) {
                valueStr = "<center>" + checkBox(name: fieldName, value: valueStr, readonly: readonly) + "</center>"
            }

            // Totalizadores
            def totalizador = configs?.find({it?.contains("TOTALIZE=")})
            if (totalizador) {
                def target = totalizador.split("=")[1]
                valueStr = "<div class='totalizer' asg-totalize-target='" + target + "'>" + valueStr + "</div>"
            }

            out << valueStr
        } else {
            if (!editableFields.find({ it == field })) {
                readonly = true
            }

            // Se for somente leitura e configurado para aparecer um label ao invés de um input desabilitado
            if (readonly && attrs.labelIfReadonly) {
                out << valueStr
            } else {
                //Input de Moeda
                if (configs?.contains("MONEY")) {
                    out << textField(name: fieldName, value: valueStr, readonly: readonly, class: "money")
                }
                // Input de Porcentagem
                else if (configs?.contains("PERCENT")) {
                    out << textField(name: fieldName, value: valueStr, readonly: readonly, class: "percent")
                }
                // Input de Data
                else if (configs?.contains("DATE")) {
                    out << asg.datePicker(name: fieldName, value: item."${field}", readonly: readonly)
                }
                // Input Invisível
                else if (configs?.contains("HIDDEN")) {
                    out << asg.hiddenField(name: fieldName, value: valueStr)
                } else {
                    out << dynamicField(name: fieldName, value: value, readonly: readonly, type: value.getClass(),
                            noLabel: true, keepValue: true)
                }
            }
        }
    }

    def excelTable = { attrs, body ->
        def template = "/_fields/excelTable"

        def model = ['name'         : attrs.name,
                     'controller'   : attrs.controller,
                     'headerButtons': attrs.headerButtons,
                     'itemButtons'  : attrs.itemButtons,
                     'messageSource': messageSource]

        out << g.render(template: template, model: model)

        def excelTableData = getExcelTableData(attrs)
        def excelTableHeaders = getExcelTableHeaders(attrs)

        def handsontable = [:]
        handsontable.columns = attrs.columns
        handsontable.data = excelTableData
        handsontable.colHeaders = excelTableHeaders
        handsontable.rowHeaders = true
        handsontable.columnSorting = true
        handsontable.readOnly = true
        handsontable.stretchH = 'all'

        def handsontable_json = new grails.converters.JSON(handsontable).toString()

        // Aplica o renderer no campo BUTTONS
        handsontable_json = handsontable_json.replace('{"data":"buttons"}', '{"data":"buttons", "type":{"renderer":renderer}}')

        def scriptBody = "\$('#table${attrs.name}').handsontable(${handsontable_json});"
        scriptBody += " \$('#table${attrs.name}').addClass('table-striped');"
        scriptBody += " \$('#table${attrs.name}').addClass('table-hover');"
        scriptBody += " \$('#table${attrs.name}').trigger('focus');"

        out << r.script(disposition: "defer", scriptBody)
    }

    def remotePaginate = { attrs ->
        def writer = out

        if (attrs.total == null)
            throwTagError("Tag [remotePaginate] is missing required attribute [total]")

        def messageSource = grailsApplication.getMainContext().getBean("messageSource")
        def locale = RequestContextUtils.getLocale(request)

        Integer total = attrs.int('total') ?: 0
        Integer offset = params.int('offset') ?: (attrs.int('offset') ?: 0)
        Integer max = params.int('max') ?: (attrs.int('max') ?: grailsApplication.config.grails.plugins.remotepagination.max as Integer)
        Integer maxsteps = params.int('maxsteps') ?: (attrs.int('maxsteps') ?: 10)
        Boolean alwaysShowPageSizes = new Boolean(attrs.alwaysShowPageSizes ?: false)
        def pageSizes = attrs.pageSizes ?: []
        Map linkTagAttrs = attrs
        boolean bootstrapEnabled = grailsApplication.config.grails.plugins.remotepagination.enableBootstrap as boolean

        if (bootstrapEnabled) {
            writer << '<ul class="pagination">'
        }

        Map linkParams = [offset: offset - max, max: max]
        Map selectParams = [:]
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order
        if (attrs.params) {
            linkParams.putAll(attrs.params)
            selectParams.putAll(linkParams)
        }

        if (attrs.id != null) {
            linkTagAttrs.id = attrs.id
        }
        linkTagAttrs.params = linkParams

        // determine paging variables
        boolean steps = maxsteps > 0
        Integer currentstep = (offset / max) + 1
        Integer firststep = 1
        Integer laststep = Math.round(Math.ceil(total / max))

        // display previous link when not on firststep
        if (currentstep > firststep) {
            linkTagAttrs.class = 'prevLink'
            linkParams.offset = offset - max
            writer << wrapInListItem(bootstrapEnabled, asg.createPaginationLink(linkTagAttrs.clone()) {
                (attrs.prev ? attrs.prev : messageSource.getMessage('paginate.prev', null, messageSource.getMessage('default.paginate.prev', null, 'Previous', locale), locale))
            })
        }

        // display steps when steps are enabled and laststep is not firststep
        if (steps && laststep > firststep) {
            linkTagAttrs.class = 'step'

            // determine begin and endstep paging variables
            Integer beginstep = currentstep - Math.round(maxsteps / 2) + (maxsteps % 2)
            Integer endstep = currentstep + Math.round(maxsteps / 2) - 1

            if (beginstep < firststep) {
                beginstep = firststep
                endstep = maxsteps
            }
            if (endstep > laststep) {
                beginstep = laststep - maxsteps + 1
                if (beginstep < firststep) {
                    beginstep = firststep
                }
                endstep = laststep
            }

            // display firststep link when beginstep is not firststep
            if (beginstep > firststep) {
                linkParams.offset = 0
                writer << wrapInListItem(bootstrapEnabled, asg.createPaginationLink(linkTagAttrs.clone()) {
                    firststep.toString()
                })
                writer << wrapInListItem(bootstrapEnabled, '<span class="step">..</span>')
            }

            // display paginate steps
            (beginstep..endstep).each { i ->
                if (currentstep == i) {
                    writer << wrapInListItem(bootstrapEnabled, "<span class=\"currentStep\">${i}</span>")
                } else {
                    linkParams.offset = (i - 1) * max
                    writer << wrapInListItem(bootstrapEnabled, asg.createPaginationLink(linkTagAttrs.clone()) {
                        i.toString()
                    })
                }
            }

            // display laststep link when endstep is not laststep
            if (endstep < laststep) {
                writer << wrapInListItem(bootstrapEnabled, '<span class="step">..</span>')
                linkParams.offset = (laststep - 1) * max
                writer << wrapInListItem(bootstrapEnabled, asg.createPaginationLink(linkTagAttrs.clone()) {
                    laststep.toString()
                })
            }
        }
        // display next link when not on laststep
        if (currentstep < laststep) {
            linkTagAttrs.class = 'nextLink'
            linkParams.offset = offset + max
            writer << wrapInListItem(bootstrapEnabled, asg.createPaginationLink(linkTagAttrs.clone()) {
                (attrs.next ? attrs.next : messageSource.getMessage('paginate.next', null, messageSource.getMessage('default.paginate.next', null, 'Next', locale), locale))
            })
        }

        if ((alwaysShowPageSizes || total > max) && pageSizes) {
            selectParams.remove("max")
            selectParams.offset = 0
            String paramsStr = selectParams.collect { it.key + "=" + it.value }.join("&")
            paramsStr = '\'' + paramsStr + '&max=\' + this.value'
            linkTagAttrs.params = paramsStr
            Boolean isPageSizesMap = pageSizes instanceof Map

            writer << wrapInListItem(bootstrapEnabled, "<span>" + select(from: pageSizes, value: max, name: "max", onchange: "${remoteFunction(linkTagAttrs.clone())}", class: 'remotepagesizes',
                    optionKey: isPageSizesMap ? 'key' : '', optionValue: isPageSizesMap ? 'value' : '') + "</span>")
        }

        if (bootstrapEnabled) {
            writer << '</ul>'
        }
    }

    def createPaginationLink = { attrs, body ->
        def linkAttrs = [:]
        linkAttrs.onclick = "ajaxGrid(this, '${attrs.name}', '${attrs.max}', '${attrs.params.offset}');return false;"

        out << g.link(linkAttrs, body)
    }

    /*
    Novo Grid editável
     */
    def editableGrid = { attrs, body ->

        GrailsClass grailsDomainClass = Utils.GetDomainClass(grailsApplication, attrs.domainClass.name)

        // Prepara os atributos dos campos
        for (field in attrs.fields) {
            field.domainClass = attrs.domainClass
            field.keepValue = true
            field.noLabel = true

            if (!Persistente.GetDomainPropertyRecursively(grailsDomainClass, FieldsTagLib.PureAttributeName(field.name))?.isPersistent())
                field.readonly = true

            // Caso o 'forceshowmode' não tenha sido passado, busca do readonly
            if (!field.forceshowmode && field.readonly) field.forceshowmode = field.readonly
        }

        // Renderiza o template _editableGrid
        def model = ['attrs': attrs, 'isShowMode': isShowMode(attrs)]
        out << g.render(template: "/_fields/editableGrid/editableGrid", model: model, plugin: "asgard-base")
    }

    private def wrapInListItem(Boolean bootstrapEnabled, def val) {
        bootstrapEnabled ? "<li>$val</li>" : val
    }

    def getExcelTableData(attrs) {
        def data = []

        for (item in attrs.list) {
            def itemMap = [:]

            for (column in attrs.columns) {
                itemMap[column.data] = item."${column.data}"?.toString()
            }

            itemMap["buttons"] = getButtons(attrs, item.id)

            data << itemMap
        }

        return data
    }

    def getButtons(attrs, id) {
        def ret = asg.link([controller: attrs.controller, action: "show", id: id, class: "btn btn-small tip-bottom", title: g.message(code: "default.button.show.label")], "<i class='icon-zoom-in'></i>")
        ret += asg.link([controller: attrs.controller, action: "edit", id: id, class: "btn btn-small tip-bottom", title: g.message(code: "default.button.edit.label")], "<i class='icon-pencil'></i>")
        ret += asg.link([controller: attrs.controller, action: "delete", id: id, class: "btn btn-small tip-bottom btn-danger", title: g.message(code: "default.button.delete.label")], "<i class='icon-trash icon-white'></i>")
    }

    def getExcelTableHeaders(attrs) {
        def headers = []

        for (column in attrs.columns) {
            headers << g.message(code: attrs.controller + "." + column.data + ".label")
        }

        attrs.columns << [data: "buttons"]
        headers << ""

        return headers
    }

    boolean isShowMode(attrs) {
        return FieldsTagLib.IsShowMode(params, attrs, actionName)
    }

}