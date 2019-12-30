package com.br.asgardtecnologia.taglib

class RemoteTagLib {

    static namespace = "asg"

    def includeContentRemoteLink = { attrs, body ->
        attrs.update = [success: "content"]

        out << _includeRemote(attrs, body)
    }

    def modalRemoteLink = { attrs, body ->
        def div = attrs.controller + attrs.action + attrs.id + '_modal'

        attrs.onSuccess = "modal()"
        attrs.update = [success: div]
        out << _includeRemote(attrs, body)

        out << "<div id='" + div + "' class='modal hide' />"

        var scriptBody = 'function modal() { $("#' + div + '").modal(); }'
        out << r.script(disposition: "defer", scriptBody)
    }

    def _includeRemote(attrs, body) {
        def icontroller = attrs.controller
        def iaction = attrs.action
        def iid = attrs.id

        attrs.controller = "Javascript"
        attrs.action = "includeContent"
        attrs.params = [icontroller: icontroller, iaction: iaction, iid: iid]
        attrs.href = '#!/' + icontroller + '/' + iaction + '/' + iid

        if (!attrs.method) attrs.method = "GET"

        return g.remoteLink(attrs, body)
    }


}