package com.br.asgardtecnologia.erp.base

import com.br.asgardtecnologia.base.Persistente
import grails.converters.JSON
import grails.plugins.springsecurity.Secured

class JavascriptController {

    @Secured('IS_AUTHENTICATED_FULLY')
    def remoteTagRenderer() {
        def attrs = params.attrs

        // Clona os atributos e atribui o "directTagRenderer" para reconhecimento pela própria Tag
        // ATENÇÃO! Caso o "directTagRenderer" não seja setado aqui, entrará em LOOP INFINITO!
        def attrsRemote = grails.converters.JSON.parse(attrs)
        attrsRemote.directTagRenderer = true

        // Prepara e remove o atributo "remoteTagName"
        def remoteTagName = attrsRemote.remoteTagName
        attrsRemote.remove("remoteTagName")

        render asg."${remoteTagName}"(attrsRemote, attrsRemote.body ?: "")
    }

    @Secured('IS_AUTHENTICATED_FULLY')
    def include() {
        render g.include(controller: params.icontroller, action: params.iaction, id: params.iid)
    }

    @Secured('IS_AUTHENTICATED_FULLY')
    def readEntityFieldValue() {
        def bean = GetDomainInstance(params.domain)?.read(params.id)

        if (params.fields) {
            def ret = [:]
            for (field in params.fields?.split(";")) {
                ret[field] = bean[field]
            }

            render ret as JSON
        } else {
            render bean?.toString()
        }
    }

    @Secured('IS_AUTHENTICATED_FULLY')
    def readItemFields() {
        // Declara o retorno
        def ret = [:]

        def domainInstance = GetDomainInstance(params.domain)

        // Busca o registro de acordo com a classe e id
        Persistente instance = domainInstance?.read(params.long('id'))

        // Caso não encontre o registro, determina como um novo registro
        if (!instance)
            instance = domainInstance

        // Atualiza as propriedades de acordo com os parâmetros
        instance.properties = params

        // Recupera a lista de acordo com os itens
        def items = instance[params.itemsFieldName]

        // Busca o item determinado
        for (Persistente item in items) {
            if (params.long('itemId').equals(item.id)) {
                ret.id = item.id
                for (field in params.itemFields?.split(";")) {
                    ret[field] = item.getPropertyRecursively([field]);
                }
            }
        }

        // Descarta as modificações
        instance.discard()

        render ret as JSON
    }

    @Secured('IS_AUTHENTICATED_FULLY')
    def domainAsSelectOption() {
        render GetDomainAsSelectOption(params.domain)
    }

    @Secured('IS_AUTHENTICATED_FULLY')
    def setMunicipio() {
        def municipio = Municipio.GetOrCreateAndGet(params.estado, params.municipio)

        def options = ListToOptionSelect(Municipio.list())
        def ret = ['id'     : municipio.id,
                   'text'   : municipio.toString(),
                   'options': options]

        render(contentType: 'text/json') { ret }
    }

    private Object GetDomainInstance(String domain) {
        domain = domain.indexOf('.') > -1 ? domain : GCU.getClassNameRepresentation(domain)
        def domainClass = grailsApplication.getDomainClass(domain)

        return domainClass.newInstance()
    }

    private String GetDomainAsSelectOption(String dom) {
        return ListToOptionSelect(GetDomainInstance(dom).list())
    }

    private String ListToOptionSelect(list) {
        String ret = ""

        list.each() {
            ret += '<option value="' + it.id + '">' + it + '</option>';
        }

        return ret
    }
}