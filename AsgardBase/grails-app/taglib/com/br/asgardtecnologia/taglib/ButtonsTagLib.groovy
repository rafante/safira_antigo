package com.br.asgardtecnologia.taglib

class ButtonsTagLib {

    static namespace = "asg"

    def submitButton = { attrs, body ->
        out << g.submitButton(attrs, body)
    }

    def actionSubmit = { attrs, body ->
        out << g.actionSubmit(attrs, body)
    }

}
