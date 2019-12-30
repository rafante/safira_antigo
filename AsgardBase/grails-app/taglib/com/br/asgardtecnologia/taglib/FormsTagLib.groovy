package com.br.asgardtecnologia.taglib

class FormsTagLib {
    static namespace = "asg"

    def modalDiv = { attrs, body ->
        out << "<div id='${attrs.name}' name='${attrs.name}' class='${attrs.class} modal' modalcreate='${attrs.modalcreate}' modalindex='${attrs.modalindex}'> " +
                "<div class='modal-dialog' style='min-width:1100px;${attrs.style}'> " +
                "<div class='modal-content'>" +
                "<div class='modal-header'> " +
                "<button data-dismiss='modal' class='close asg-ignore-focus' type='button'>×</button>" +
                "<h3>${attrs.title}</h3> " +
                "</div> " +
                "<div class='modal-body'> " +
                attrs.bodyBefore +
                body() +
                attrs.bodyAfter +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>"
    }

    def modalForm = { attrs, body ->
        def newBody = asg.modalDiv(attrs, body)

        if (!attrs.id) attrs.id = params.id
        attrs.name = "form" + attrs.name
        out << g.form(attrs, newBody)
    }

    def modalRender = { attrs, body ->
        def template = "/" + attrs.controller + "/form"
        def model = [:]
        model[attrs.controller + "Instance"] = attrs.instance

        def rendered = g.render(template: template, model: model)

        String newBody = rendered + body()
        if (!attrs.noButtons) {
            newBody += "<div class='modal-footer'> " +
                    "<a class='btn btn-primary btn-small' data-action='confirm' href='javascript:void(0)' onclick='return modalConfirm(event, \$(this).closest(\".modal-grid\"));'>" + g.message(code: 'default.action.confirm.label', default: 'Confirmar') + "</a>" +
                    "<a class='btn btn-default btn-small' data-action='cancel' data-dismiss='modal' href='javascript:void(0)'>" + g.message(code: 'default.action.cancel.label', default: 'Cancelar') + "</a>" +
                    "</div>"
        }

        out << modalDiv(attrs, newBody)
    }

    def confirmModalForm = { attrs, body ->
        def actionSubmit = !attrs.hideConfirm ? asg.actionSubmit(action: attrs.action, class: 'btn btn-primary btn-small', value: g.message(code: 'default.action.confirm.label', default: 'Confirmar')) : ""
        String newBody = body() + "<div class='modal-footer'> " + actionSubmit +
                "<a data-dismiss='modal' class='btn btn-default btn-small'>Cancelar</a>" +
                "</div>"

        out << modalForm(attrs, newBody)
    }
}