package com.br.asgardtecnologia.base

class Mensagens {

    static String RenderError(Object obj, String prefix, String key) {
        String msg = "Ocorreram erros ao tentar salvar " + prefix + " <b>" + key + "</b>:<br />"
        obj.errors.each {
            msg = msg + it
        }

        return msg + "<br /><br />"
    }

}

