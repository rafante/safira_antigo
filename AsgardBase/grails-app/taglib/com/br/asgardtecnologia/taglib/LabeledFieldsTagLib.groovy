package com.br.asgardtecnologia.taglib

class LabeledFieldsTagLib {

    static namespace = "asg"

    def labeled = { attrs, body ->
        if (attrs.hideIfEmpty && !attrs.value) return

        generalAttrsAdjusts(attrs)

        out << getLabeledOpen(attrs)
        out << body()
        out << getLabeledClose(attrs)
    }

    def labeledLabel = { attrs, body ->
        attrs.style += "padding-top: 5px;"
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.label(attrs, body))
    }

    def labeledField = { attrs, body ->
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.field(attrs, body))
    }

    def labeledTextField = { attrs, body ->
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.textField(attrs, body))
    }

    def labeledTextArea = { attrs, body ->
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.textArea(attrs, body))
    }

    def labeledPasswordField = { attrs, body ->
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.passwordField(attrs, body))
    }

    /**
     * @attr name REQUIRED O nome do atributo
     * @attr value REQUIRED O valor que vai aparecer lá
     */
    def labeledCheckBox = { attrs, body ->
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.checkBox(attrs, body))
    }

    def labeledLink = { attrs, body ->
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.link(attrs, body))
    }

    def labeledDatePicker = { attrs, body ->
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.datePicker(attrs, body))
    }

    def labeledSelect = { attrs, body ->
        generalAttrsAdjusts(attrs)
        attrs.ignoreGeneralAttrsAdjusts = true
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.select(attrs, body))
    }

    def labeledAutoComplete = { attrs, body ->
        generalAttrsAdjusts(attrs)
        if (!attrs.id) attrs.id = attrs.name.replace(".id", "")
        attrs.ignoreGeneralAttrsAdjusts = true
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.autoComplete(attrs, body))
    }

    def labeledMoney = { attrs, body ->
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.money(attrs, body))
    }

    def labeledPercent = { attrs, body ->
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.percent(attrs, body))
    }

    def labeledDecimal = { attrs, body ->
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.decimal(attrs, body))
    }

    def labeledFile = { attrs, body ->
        generalAttrsAdjusts(attrs)
        if (isShowMode(attrs)) {
            return
        }

        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.file(attrs, body))
    }

    def labeledProgressBar = { attrs, body ->
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.progressBar(attrs, body))
    }

    def labeledLinearStatus = { attrs, body ->
        generalAttrsAdjusts(attrs)
        def attrsLabel = attrs.clone()
        out << asg.labeled(attrsLabel, asg.linearStatus(attrs, body))
    }

    def generalAttrsAdjusts(attrs) {
        if (attrs.ignoreGeneralAttrsAdjusts) return

        attrs.span = attrs.span ?: "12"
        attrs.value = attrs.value ?: com.br.asgardtecnologia.base.Utils.GetProperty(attrs.instance, attrs.name)
    }

    def getLabeledOpen(attrs) {
        if (attrs.noLabel) return ""

        def controllerLink =
                (attrs.domain ? grailsApplication.getArtefact("Controller", attrs.domain + "Controller")?.logicalPropertyName : controllerName)

        def controllerLabel =
                params.domain ? grailsApplication.getArtefact("Controller", params.domain + "Controller")?.logicalPropertyName : controllerName

        if (attrs.instance?.domainClass?.logicalPropertyName) controllerLabel = attrs.instance?.domainClass?.logicalPropertyName

        def name = attrs.id ?: attrs.name

        def label = attrs.label

        if (!label) {
            label = resolveMessage(controllerLabel, name)
        }

        if (attrs.domain) {
            return "<div class='col-12 col-sm-${attrs.span} form-group fieldcontain " + g.hasErrors(bean: attrs.instance, field: attrs.name, 'error') + "' >" +
                    "<label class='control-label' for='${attrs.name}'>" +
                    asg.link([controller: controllerLink, action: 'list', tabindex: '-1'], label) +
                    "</label>" +
                    "<div class='controls'>"
        } else {
            return "<div class='col-12 col-sm-${attrs.span} form-group fieldcontain " + g.hasErrors(bean: attrs.instance, field: attrs.name, 'error') + "' >" +
                    "<label class='control-label' for='${attrs.name}'>" + label + "</label>" +
                    "<div class='controls'>"
        }
    }

    def getLabeledClose(attrs) {
        if (attrs.noLabel) return ""

        return "</div></div>"
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