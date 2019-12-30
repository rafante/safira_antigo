package com.br.asgardtecnologia.taglib

import com.br.asgardtecnologia.base.BooleanResolver
import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.base.Utils
import grails.converters.JSON

class FieldsTagLib {

    static namespace = "asg"

    static String formControl = "form-control"
    static String inputClassAppend = "input-sm"
    static String valueMessagePrefix = "enum.value"

    def messageSource
//    def domainService = new DomainServiceController()

    def div = { attrs, body ->
        cleanupStyleAttrs(attrs)
        out << "<div "
        outputAttributes(attrs, out)
        out << ">"
        out << body()
        out << "</div>"
    }

    def span = { attrs, body ->
        cleanupStyleAttrs(attrs)
        out << "<span "
        outputAttributes(attrs, out)
        out << ">"
        out << body()
        out << "</span>"
    }

    def label = { attrs, body ->
        cleanupStyleAttrs(attrs)
        attrs.class += (attrs.isshowmode ? ' field-show-mode' : '').trim()

        attrs.remove('from')
        attrs.remove('keys')
        attrs.remove('noSelection')
        attrs.remove('ignoreGeneralAttrsAdjusts')
        attrs.remove('valueMessagePrefix')
        attrs.remove('isshowmode')

        out << "<div id='${attrs.id ?: attrs.name}' name='${attrs.name ?: attrs.id}' class='${attrs.class}' style='${attrs.style}'"
        out << outputAttributes(attrs, out)
        out << ">" + attrs.value + "</div>"
    }

    def field = { attrs, body ->
        cleanupStyleAttrs(attrs)

        if (isShowMode(attrs)) {
            out << asg.label(attrs, body)
            return
        }

        attrs.class = adjustInputCssClass(attrs.class)
        if (!attrs.type) attrs.type = "text"

        out << g.field(attrs, body)
    }

    def textField = { attrs, body ->
        cleanupStyleAttrs(attrs)

        if (isShowMode(attrs)) {
            out << asg.label(attrs, body)
            return
        }

        attrs.class = adjustInputCssClass(attrs.class)

        attrs.remove("domainClass")
        attrs.remove("instance")

        out << g.textField(attrs, body)
    }

    def textArea = { attrs, body ->
        cleanupStyleAttrs(attrs)

        if (isShowMode(attrs)) {
            out << asg.label(attrs, body)
            return
        }

        attrs.class = adjustFormControlCssClass(attrs.class)

        out << g.textArea(attrs, body)
    }

    def passwordField = { attrs, body ->
        cleanupStyleAttrs(attrs)

        if (isShowMode(attrs)) {
            out << asg.label(attrs, body)
            return
        }

        attrs.class = adjustFormControlCssClass(attrs.class)

        out << g.passwordField(attrs, body)
    }

    def hiddenField = { attrs, body ->
        out << g.hiddenField(attrs, body)
    }

    def checkBox = { attrs, body ->
        cleanupStyleAttrs(attrs)

        if (isShowMode(attrs)) {
            attrs.disabled = true
        }

        def checkBoxAttrs = attrs.clone()
        checkBoxAttrs["name"] = "_" + attrs.name
        checkBoxAttrs["hiddenfieldname"] = attrs.name
        checkBoxAttrs.remove("instance")
        checkBoxAttrs.remove("domain")

        out << "<div style='text-align: ${attrs.textAlign ?: "left"};'>"
        out << g.checkBox(checkBoxAttrs, body)

        if (attrs.name)
            out << g.hiddenField([name: attrs.name, value: attrs.value])

        out << "</div>"
    }

    def link = { attrs, body ->
        cleanupStyleAttrs(attrs)

        if (!attrs["update"]) attrs["update"] = "content"
        if (!attrs["data-pjax"]) attrs["data-pjax"] = "#content-main"

        if (attrs.action == "delete") {
            attrs["onclick"] = ''

            if (attrs?.ajax == 'true') {
                attrs["onclick"] += 'if (confirm("Confirma exclusão?")) {'
                attrs["onclick"] += '   var me = this;'
                attrs["onclick"] += '   $.ajax({'
                attrs["onclick"] += '       url: $(me).attr("href"),'
                attrs["onclick"] += '       success: function() {'
                attrs["onclick"] += '           $(me).closest("tr").remove();'
                attrs["onclick"] += '       }'
                attrs["onclick"] += '   });'
                attrs["onclick"] += '}'
                attrs["onclick"] += 'return false;'
            } else {
                attrs["onclick"] += 'return confirm("Confirma exclusão?");'
            }
        }

        out << g.link(attrs, body)
    }

    def datePicker = { attrs, body ->
        def newAttrs = attrs.clone()

        cleanupStyleAttrs(newAttrs)

        if (newAttrs.value) newAttrs.value = Utils.ToDate(newAttrs.value)
        newAttrs.value = formatDate(date: newAttrs.value, format: "dd/MM/yyyy")
        if (isShowMode(newAttrs)) {
            out << asg.label(newAttrs, body)
            return
        }

        def name = newAttrs.name
        def id = newAttrs.id ?: name

        newAttrs.name = "${name}_holder"
        newAttrs.type = "text"
        newAttrs.class = "date " + newAttrs.class
        newAttrs.mask = 'date'
        newAttrs.remove("instance")

        out.println asg.textField(newAttrs)
        out.println "<input type=\"hidden\" name=\"${name}\" value=\"date.struct\" />"
        out.println "<input class=\"date_child\" type=\"hidden\" name=\"${name}_day\" id=\"${id}_day\" />"
        out.println "<input class=\"date_child\" type=\"hidden\" name=\"${name}_month\" id=\"${id}_month\" />"
        out.println "<input class=\"date_child\" type=\"hidden\" name=\"${name}_year\" id=\"${id}_year\" />"
    }

    def select = { attrs, body ->
        if (attrs.domain && attrs.directTagRenderer) {
            def instanceClazz = grailsApplication.getArtefact("Domain", attrs.instance?.className)?.getClazz()
            attrs.instance = attrs.instance?.id ? instanceClazz.read(attrs.instance?.id?.toLong()) : null

            if (!isShowMode(attrs)) {
                if (attrs?.name.indexOf('.id') == -1) {
                    attrs.name = attrs.name + '.id';
                }

                if (!attrs?.id) {
                    attrs.id = attrs.name
                }
            }
        }
        def objDomain = com.br.asgardtecnologia.base.Utils.GetProperty(attrs.instance, (attrs.id ?: attrs.name))

        if (attrs.keys) attrs.valueMessagePrefix = valueMessagePrefix
        if (attrs.filter && attrs.filter.class == String) {
            attrs.filter = JSON.parse(attrs.filter.toString())
        }

        if (isShowMode(attrs)) {
            attrs.value = objDomain?.toString()

            if (attrs.keys && attrs.value) attrs.value = g.message(code: valueMessagePrefix + "." + attrs.value)

            if (objDomain && !attrs.keys) {
                def controller = objDomain.domainClass.logicalPropertyName
                //grailsApplication.getArtefact("Controller", attrs.domain + "Controller")?.logicalPropertyName

                attrs.class += (attrs.isshowmode ? ' field-show-mode' : '')
                out << "<div id='${attrs.id ?: attrs.name}' name='${attrs.name ?: attrs.id}' class='${attrs.class}'>"
                out << asg.link(controller: controller, action: 'show', id: objDomain.id, attrs.value)
                out << "</div>"
            } else {
                out << asg.label(attrs, body)
            }
            return
        } else {
            // Caso o Domain tenha sido passado e o select não, busca a lista
            if (attrs.domain && !attrs.from) {
                def domainClazz = grailsApplication.getArtefact("Domain", attrs.domain)?.getClazz()
                attrs.from = attrs.filter ? Persistente.GetCriteriaFromParams(domainClazz, attrs.filter) : domainClazz?.list()
            }
        }

        out << "<div class='input-group input-group-sm'>"
        attrs["class"] = " form-control"
        out << g.select(attrs, body)

        def scriptBody = ""

        if (attrs.onChange) {
            scriptBody += "\$('#${attrs.name}').on('change', function(e) {${attrs.onChange}});"
        }

        out << r.script(disposition: "defer", scriptBody)

        if (attrs.domain) {

            out << '<span class="input-group-btn">'

            def bodyLink = "<i class='refresh-select icon icon-refresh' style='vertical-align: middle'></i>"
            out << g.remoteLink(
                    id: attrs.id + "_refresh",
                    controller: "Javascript",
                    action: "domainAsSelectOption",
                    params: ['domain': attrs.domain],
                    update: attrs.id,
                    bodyLink,
                    tabindex: '-1')

//            link = link.toString().replaceAll("#" + attrs.id,"[name='" + attrs.name + "']")
//            out << link

            out << '</span>'
        }

        // Cria um input para guardar a lista (FROM) do select, caso seja alterado,
        // automaticamente as options do select serão alteradas
        def listChangeHolderName = attrs.id + "List"
        out << asg.hiddenField([id: listChangeHolderName, name: listChangeHolderName, onchange: "onSelectHolderList(this);"])

        // Fecha a última DIV
        out << "</div>"
    }

    def autoComplete = { attrs, body ->
        cleanupStyleAttrs(attrs)

        if (!attrs.id) attrs.id = attrs.name.replace(".id", "")

        if (isShowMode(attrs)) {
            def obj = com.br.asgardtecnologia.base.Utils.GetProperty(attrs.instance, PureAttributeName(attrs.id ?: attrs.name))
            attrs.value = obj?.toString()

            if (obj && !attrs.keys) {
                def controller = grailsApplication.getArtefact("Controller", attrs.domain + "Controller")?.logicalPropertyName

                attrs.class += (attrs.isshowmode ? ' field-show-mode' : '')
                out << "<div id='${attrs.id ?: attrs.name}' name='${attrs.name ?: attrs.id}' class='${attrs.class}'>"
                out << asg.link(controller: controller, action: 'show', id: obj.id, attrs.value)
                out << "</div>"
            } else {
                out << asg.label(attrs, body)
            }
            return
        }

        def id = attrs.value
        def value = ""
        def instance

        if (id) {
            instance = read(attrs.domain, id)
            if (instance) {
                if (attrs.valuefield) value = instance."${attrs.valuefield}"
                else value = instance.toString()
            }
        }

        if (!attrs.id) attrs.id = attrs.name
        if (!attrs.hiddenfieldname) attrs.hiddenfieldname = attrs.name

        // Renderiza o campo oculto (container do ID)
        def attrsHiddenField = [:]
        attrsHiddenField["name"] = attrs.hiddenfieldname
        attrsHiddenField["domain"] = attrs.domain
        attrsHiddenField["value"] = id
        if (attrs["asg-chain-field"]) {
            attrsHiddenField["asg-domain-field"] = attrs["asg-domain-field"]
            attrsHiddenField["asg-chain-field"] = attrs["asg-chain-field"]
            attrsHiddenField["asg-chain-target"] = attrs["asg-chain-target"]
            attrs.remove("asg-domain-field")
            attrs.remove("asg-chain-field")
            attrs.remove("asg-chain-target")
        }
        if (attrs.onchange) {
            attrsHiddenField["onchange"] = attrs.onchange
            attrs.remove("onchange")
        }
        out << g.hiddenField(attrsHiddenField)

        // Prepara o campo visível (descritivo)
        attrs.id = attrs.id.replace(".", "__")
        attrs.name = attrs.name.replace(".", "__").replace("[", "_").replace("]", "_")
        attrs.value = value
        attrs.oldValue = ""
        attrs.class = "asg-autocomplete " + attrs.class

        if (attrs.filter) attrs.filter = attrs.filter?.toString()
        else {
            def defaultFilter = Persistente.GetConstraintsRecursively(attrs.instance, attrs.hiddenfieldname)
            if (defaultFilter) attrs.filter = defaultFilter.toString()
        }

        if (!instance) instance = getInstance(attrs.domain)

        attrs.valuefield = attrs.valuefield ?: instance?.properties["defaultValueField"]
        attrs.listfields = attrs.listfields ?: instance?.properties["defaultAutoCompleteFields"] ?: instance?.properties["defaultFilterFields"]

        out << asg.textField(attrs)

        boolean validateOnField = grailsApplication.config.grails.plugins.asgard.validateOnField as boolean
        if (validateOnField && attrs.validate && instance?.id) {
            instance?.validate()
            if (instance?.errors?.allErrors?.size() > 0) {
                out << '<div class="alert alert-block alert-error"><a class="close" data-dismiss="alert">×</a><p>'
                out << '<ul>'
                instance?.errors?.allErrors?.each() {
                    out << '<li>' + messageSource.getMessage(it, Locale.getDefault()) + '</li>'
                }
                out << '</ul>'
                out << '</div>'
            }
        }

    }

    def money = { attrs, body ->
        cleanupStyleAttrs(attrs)

        if (!attrs.decimals && attrs.domain) {
            def cp = Utils.GetDomainClass(grailsApplication, attrs.domain).constrainedProperties[FieldsTagLib.PureAttributeName(attrs.name)]
            attrs.decimals = cp?.scale ?: 5
        }

        if (isShowMode(attrs)) {
            attrs.style = "text-align: ${attrs.textAlign ?: 'right'};"
            attrs.value = g.formatNumber(number: attrs.value, type: "currency", currencyCode: "BRL", locale: new Locale("pt", "BR"), minFractionDigits: attrs.decimals)
            out << asg.label(attrs, body)
            return
        }

        def value = attrs.value = g.formatNumber(number: attrs.value, type: "number", minFractionDigits: attrs.decimals)
        attrs.value = !attrs.value || attrs.value == '' ? '0' : value

        out << asg.decimal(attrs, body)
    }

    def percent = { attrs, body ->
        cleanupStyleAttrs(attrs)

        if (isShowMode(attrs)) {
            attrs.style = "text-align: ${attrs.textAlign ?: 'center'};"
            attrs.value = g.formatNumber(number: attrs.value, type: "percent")

            out << asg.label(attrs, body)
            return
        }

        out << asg.textField(
                name: attrs.name,
                value: attrs.value,
                class: "percent",
                body
        )

        if (!attrs.decimals) attrs.decimals = 2

        def scriptBody = "\$('#${attrs.name}').priceFormat({"
        scriptBody += "prefix: '',"
        scriptBody += "clearSuffix: true,"
        scriptBody += "centsSeparator: ',',"
        scriptBody += "thousandsSeparator: ''"
        scriptBody += "        centsLimit: ${attrs.decimals}"
        scriptBody += "    });"
        out << r.script(disposition: "defer", scriptBody)

    }

    def decimal = { attrs, body ->
        cleanupStyleAttrs(attrs)

        if (!attrs.decimals && attrs.domain) {
            def cp = Utils.GetDomainClass(grailsApplication, attrs.domain).constrainedProperties[FieldsTagLib.PureAttributeName(attrs.name)]
            attrs.decimals = cp?.scale ?: 0
        }

        if (attrs.value) attrs.value = g.formatNumber(number: attrs.value, type: "number",locale: new Locale("pt","BR"), minFractionDigits: attrs.decimals)
        if (isShowMode(attrs)) {
            attrs.style = "text-align: ${attrs.textAlign ?: 'center'};"
            out << asg.label(attrs, body)
            return
        }

        attrs.type = "decimal"
        attrs.class = ((attrs.class ?: " ") + "decimal").trim()
        out << asg.textField(attrs)
    }

    def file = { attrs, body ->
        cleanupStyleAttrs(attrs)

        if (isShowMode(attrs)) {
            out << asg.label(attrs, body)
            return
        }

        out << "<input type='file'' id='${attrs.id}' name='${attrs.name}' />"
    }

    def progressBar = { attrs, body ->
        cleanupStyleAttrs(attrs)

        if (!attrs.status) {
            if (attrs.value < 30) attrs.status = 'danger'
            else if (attrs.value < 60) attrs.status = 'warning'
            else attrs.status = 'success'
        }

        if (!attrs.id) attrs.id = attrs.name
        def value = attrs.value <= 100 ? attrs.value : 100
        out << "<div class='progress progress-striped active' style='height:30px;margin-bottom:0;'>"
        out << "<div id='${attrs.id ?: attrs.name}' style='width: 0%;' class='progress-bar progress-bar-${attrs.status}' data-value='${value}%'></div>"
        out << "</div>"
    }

    def message = { attrs, body ->
        cleanupStyleAttrs(attrs)

        // Resolve mensagem
        if (attrs.value?.getClass()?.isEnum()) attrs.code = (attrs.valueMessagePrefix ?: valueMessagePrefix) + "." + attrs.value?.toString() + attrs.valueMessageSuffix
        if (!attrs.code) attrs.code = attrs.value

        if (attrs["class"].isAllWhitespace()) attrs.remove("class")
        if (attrs["style"].isAllWhitespace()) attrs.remove("style")

        attrs.remove("valueMessageSuffix")
        out << asg.span(attrs, g.message(attrs, body))
    }

    def fieldTitle = { attrs, body ->
        // TODO: Codificar modo RECURSIVO!
        def code = attrs.controller + "." + attrs.field + ".label"
        def label = g.message(code: code)
        if (label == code) {
            code = attrs.field + ".label"
            label = g.message(code: code)
            if (label == code) {
                def tok = attrs.field?.tokenize('.')
                code = (tok?.size() ? tok[-1] : attrs.field) + ".label"
                label = g.message(code: code)
            }
        }

        out << label
    }

    def linearStatus = { attrs, body ->
        if (!attrs.value) attrs.value = attrs.instance?."${attrs.name}"

        def values = attrs.linearstatusrules ?: attrs.instance?.getPossibleStatus()

        String classBefore = ""
        String classAfter = "asg-arrow-list-after"
        Integer enumCount = values?.size()
        Integer itemCount = 0
        String valueMessageSuffix = ""

        out << "<ul class='asg-arrow-list'>"
        out << g.hiddenField(name: attrs.name, value: attrs.value)

        for (item in values) {
            itemCount += 1

            // Verifica o último para evitar a seta
            if (itemCount == enumCount) classAfter = ""

            // Escreve o LI, caso seja o value o item atual, altera o class
            out << "<li class='${((attrs.value == item) ? 'asg-arrow-list-selected' : '')}'>"

            // Caso tenha que escrever o class da seta anterior
            if (classBefore) out << "<span class='${classBefore}'></span>"

            // Escreve o valor do item, passando o MessageSuffix (.action), se necessário
            String value = asg.message(id: item, value: item, valueMessageSuffix: valueMessageSuffix)
            def beforeSubmit = attrs.beforeSubmit ? attrs.beforeSubmit : ""
            if (attrs.value != item) {
                def onclick = beforeSubmit + "\$(\"#${attrs.name}\").val('${item.toString()}');\$(\"#${attrs.name}\").closest(\"form\").submit();"
                out << g.link(base: "#", onclick: onclick, value)
            } else {
                out << value
            }

            if (!attrs.instance?.id) break

            // Determina o MessageSuffix ".action" caso seja um item posterior ao item ativo
            if (attrs.value == item) {
                valueMessageSuffix = ".action"
            }
            if (!classBefore) classBefore = 'asg-arrow-list-before'

            // Caso tenha que escrever o class da seta posterior
            if (classAfter) out << "<span class='${classAfter}'></span>"
            out << "</li>"

        }

        out << "</ul>"
    }

    public static boolean IsShowMode(params, attrs, actionName) {
        def _actionName = params.actionName ?: actionName
        attrs.isshowmode = ((attrs.forceshowmode) || (!attrs.forceeditmode && _actionName == "show"))
        return attrs.isshowmode
    }

    boolean isShowMode(attrs) {
        return IsShowMode(params, attrs, actionName)
    }

    String adjustInputCssClass(inputClass) {
        String newCssClass = formControl + " " + inputClassAppend
        return inputClass ? (inputClass + " " + newCssClass) : newCssClass
    }

    String adjustFormControlCssClass(inputClass) {
        String newCssClass = formControl
        return inputClass ? (inputClass + " " + newCssClass) : newCssClass
    }

    def cleanupStyleAttrs(attrs) {
        // Resolve o style e a classe dinamicamente (caso exista regra, classrule ou stylerule)
        attrs.style += (' ' + BooleanResolver.solveRuleMultiple(attrs.stylerule, attrs.value)?.join(' ') + ' ')
        attrs.class += (' ' + BooleanResolver.solveRuleMultiple(attrs.classrule, attrs.value)?.join(' ') + ' ')
        attrs.remove("stylerule")
        attrs.remove("classrule")
    }

    /**
     * Dump out attributes in HTML compliant fashion.
     */
    void outputAttributes(attrs, writer) {
        attrs.remove('tagName') // Just in case one is left
        attrs.each { k, v ->
            if (v != null) {
                def html
                try {
                    html = v.encodeAsHTML()
                } catch (e) {
                    return
                }

                writer << k
                writer << '="'
                writer << html
                writer << '" '
            }
        }
    }

    public getDomainClass(domain) {
        return Utils.GetDomainClass(grailsApplication, domain)
    }

    public Object read(domain, id) {
        return getInstance(domain).read(id)
    }

    public getInstance(domain) {
        def obj = getDomainClass(domain).newInstance()
        return obj
    }

    public static String PureAttributeName(String attrName) {
        return attrName.replaceFirst(".*?].", "")
    }

}