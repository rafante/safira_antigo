package com.br.asgardtecnologia.taglib

import com.br.asgardtecnologia.erp.base.BaseController

class WidgetsTagLib {

    def groovyPagesTemplateEngine

    static namespace = "asg"

    /*def widgetBox = { attrs, body ->
        attrs.body = body()

        def template = "/_fields/widgetBox"
        def model = ['attrs': attrs]

        out << g.render(template: template, model: model)
    }

    def widget = { attrs, body ->
        out << g.render(template: attrs.template, model: attrs.model)
    }*/


    def widgetBox = { attrs, body ->
        attrs.body = body()

        def template = "/_fields/widgetBox"
        def model = ['attrs': attrs]

        out << g.render(template: template, model: model, plugin: 'asgard-base')
    }

    def widget = { attrs, body ->
        if (attrs.template)
            out << g.render(template: attrs.template, model: attrs.model)
        else
            groovyPagesTemplateEngine.createTemplate(attrs.viewSource, 'gspTemplateWidgetTmp').
                    make().writeTo(out)
    }
}