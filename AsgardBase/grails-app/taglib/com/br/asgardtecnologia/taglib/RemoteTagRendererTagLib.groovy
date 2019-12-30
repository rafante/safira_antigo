package com.br.asgardtecnologia.taglib

import grails.converters.JSON

class RemoteTagRendererTagLib {
    static namespace = "asg"

    def params = [:]

    def remoteTagRendererContainer = { attrs, body ->
        // Prepara o atributo instance, caso exista
        if (attrs.instance) {
            def instanceClassName = attrs.instance.class?.name
            def instanceId = attrs.instance?.id

            attrs.instance = [:]
            attrs.instance.id = instanceId
            attrs.instance.className = instanceClassName
        }

        // Renderiza a tag (JSON)
        String tag = "<div class='remote-tag-container' data-attrs='${(attrs as JSON).toString()}'><div class='--asg-spinner'></div></div>"
        out << tag
    }
}