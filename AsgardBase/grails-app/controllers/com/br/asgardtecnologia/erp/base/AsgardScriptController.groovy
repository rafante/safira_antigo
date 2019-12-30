package com.br.asgardtecnologia.erp.base

class AsgardScriptController {

    def execute = {
        switch (request.method) {
            case 'GET':

                break
            case 'POST':
                GroovyShell shell = new GroovyShell(this.class.classLoader);

                render shell.evaluate(params.script);
                break
        }


    }

}
