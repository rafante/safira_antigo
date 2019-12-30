package com.br.asgardtecnologia.erp.base

import com.br.asgardtecnologia.base.Persistente
import grails.converters.JSON
import grails.plugins.springsecurity.Secured

class GridController extends BaseController {

    @Secured('IS_AUTHENTICATED_FULLY')
    def gridAjax() {
//        params.max = params.max ? Math.min(params.max ? params.int('max') : 10, 100) : Integer.MAX_VALUE
        params.max = params.forcemax ? params.int('max') : Math.min(params.max ? params.int('max') : 10, 100)
        //params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def attrs = JSON.parse(params.customAttrs)
        def domainArtefact = grailsApplication.getArtefactByLogicalPropertyName("Domain", attrs.controller)

        attrs.list = Persistente.GetCriteriaFromParams(domainArtefact.clazz, params)
        attrs.ignoreContainer = true

        render asg.grid(attrs)
    }

    /*@Secured('IS_AUTHENTICATED_FULLY')
    def widgetGridAjax(){
        def attrs = JSON.parse(params.)
        def domainArtefact = grailsApplication.getArtefactByLogicalPropertyName("Domain", attrs.controller)
    }*/

    @Secured('IS_AUTHENTICATED_FULLY')
    def fieldAjax() {
        def ret = []

        // Recupera as configurações do Grid (e.g Fields, que são os campos configurados para aparecerem)
        def gridAttrs = JSON.parse(params.customAttrs)
        def gridFields = []
        gridAttrs.fields?.split(";").each {
            def itemGridFields = [:]
            itemGridFields.name = it.split("\\[").first() // Name sem as configurações (e.g. DATE)
            itemGridFields.field = it
            gridFields << itemGridFields
        }

        // Prepara os parâmetros para o Bind, com os nomes coerentes
        def newParams = [:]
        params.each { k, v ->
            newParams.put(k.replaceFirst(~/[^.]*+./, ''), v) // Name sem o prefix
        }

        // Busca o artefact de acordo com o controller
        def domainArtefact = grailsApplication.getArtefactByLogicalPropertyName("Domain", gridAttrs.controller)

        // Busca o item pelo ID, caso não econtra, instancia um novo
        def objItem = domainArtefact.clazz.read(params.id)
        if (!objItem) objItem = domainArtefact.clazz.newInstance()

        // Bind do params x properties do Domain
        objItem.properties = newParams

        // Cria os corpos dos TD's exibidos na tela
        gridFields.each {
            def attrsTd = [:]
            attrsTd.index = params["index"]
            attrsTd.name = it.name
            attrsTd.field = it.field
            attrsTd.item = objItem
            attrsTd.editable = false

            def itemRet = [:]
            itemRet.field = params["prefix"] + "." + it.name
            itemRet.value = asg.tdFieldOutput(attrsTd)
            ret << itemRet
        }

        render ret as JSON
    }

}