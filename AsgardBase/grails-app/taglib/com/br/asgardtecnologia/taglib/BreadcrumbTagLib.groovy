package com.br.asgardtecnologia.taglib

class BreadcrumbTagLib {

    static namespace = "asg"

    def breadcrumb = { attrs, body ->

        def artefact = grailsApplication.getArtefactByLogicalPropertyName("Domain", controllerName)

        out << '<div id="breadcrumb" class="row">'
        out << '<div class="col-12 col-sm-9">'
        out << '<a href="' + createLink(uri: '/') +
                '" title="Início" class="tip-bottom"><i class="icon icon-home"></i>' +
                g.message("code": "default.start", "default": "Início") + '</a>'

        if (!attrs.controller) {
            attrs.controller = params.reportController ?: params.controller
            attrs.action = params.reportAction ?: params.action
        }

        if (attrs.controller && artefact) {
            out << asg.link(controller: attrs.controller, 'update': 'content', 'data-pjax': '#content-main',
                    g.message("code": attrs.controller + ".label"))
        }

        if (attrs.action) {
            out << '<a href="#" class="current">' +
                    g.message("code": "default.action." + attrs.action + ".label",
                            default: g.message("code": attrs.controller + "." + attrs.action + ".label")) + '</a>'
        }

        out << '</div>'

        if (artefact) {
            out << '<div class="col-12 col-sm-3">'

            def formBody = "<div class='input-group input-group-sm' style='right:20px;padding-bottom:1px'>" +
                    asg.autoComplete([name: 'quicksearch', hiddenfieldname: "id",
                            forceeditmode: true, submitonselect: true,
                            domain: artefact.fullName,
                            placeholder: "${g.message("code": "default.quicksearch", "default": "Busca rápida...")}"]) +
                    "<span class='input-group-addon'><i class='icon icon-search'></i></span>"
            out << '</div>'

            out << g.form([action: "show", method: "GET", class: "form-search asg-ignore-check-dirty"], formBody)
            out << '</div>'
        }

        out << '</div>'

    }

}