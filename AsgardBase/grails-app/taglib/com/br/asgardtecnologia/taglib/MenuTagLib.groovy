package com.br.asgardtecnologia.taglib

class MenuTagLib {

    static namespace = "asg"

    def menu = { attrs, body ->

        //      <li class="submenu open">
        //        <a href="#"><i class="icon icon-pencil"></i> <span><g:message code="erp.menu.cadastro" default="Cadastro" /></span></a>
        //        <ul>
    }

    def menuitem = { attrs, body ->

        def code
        if (attrs["code"]) {
            code = attrs["code"]
        } else {
            code = attrs["controller"]

            if (attrs["action"]) code = code + "." + attrs["action"]

            code = code + ".label"
        }

        if (!attrs["action"]) attrs["action"] = "list"

        def label = attrs.label ?: g.message("code": code, "default": attrs["default"] ?: code)
        def liTagClose = getTagClose(params, attrs)

        attrs['class'] = 'MenuItem'
        attrs['update'] = 'content'
        attrs['data-pjax'] = '#content-main'

        out << '<li' + liTagClose
        out << asg.link(attrs, label)
    }

    def menuitemReport = { attrs, body ->
        attrs.controller = "reports"
        attrs.action = "print"
        attrs.code = attrs.reportController + "." + attrs.reportAction + ".label"

        attrs.params = [:]
        attrs["params"].domain = attrs.domain
        attrs["params"].reportController = attrs.reportController
        attrs["params"].reportAction = attrs.reportAction
        attrs["params"].templateAfterFilter = attrs.templateAfterFilter

        out << asg.menuitem(attrs, body)
    }

    String getTagClose(params, attrs) {
        if (params.controller == attrs.controller && params.action == attrs.action &&
                params.reportController == attrs.reportController && params.reportAction == attrs.reportAction)
            return ' class="menu active">'
        else
            return ' class="menu">'
    }

}