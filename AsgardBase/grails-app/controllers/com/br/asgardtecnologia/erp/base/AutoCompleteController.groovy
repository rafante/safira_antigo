package com.br.asgardtecnologia.erp.base

import com.br.asgardtecnologia.base.Persistente
import grails.converters.JSON
import grails.plugins.springsecurity.Secured

class AutoCompleteController {

    @Secured('IS_AUTHENTICATED_FULLY')
    def json = {
        if (!params.listFields) params.listFields = params.valueField

        /*
         Parâmetros Variáveis
          */
        def paramsListFields = [:]
        paramsListFields.max = params.max ?: 10
        paramsListFields.logicalOperand = "or"
        try {
            def c = Persistente.getCriteriaParameter("id", params.long("term"))
            paramsListFields.putAll(c)
        } catch (e) {
        }

        /*
         Preparação dos campos que foram determinados no listFields
          */
        params.listFields?.split(";").each() {
            if (it.equals("id")) return

            def c = Persistente.getCriteriaParameter(it, "*" + params.term + "*")
            paramsListFields.putAll(c)
        }

        /*
         Parâmetros Fixos
          */
        def paramsFilter = [:]
        paramsFilter.logicalOperand = params.filterLogicalOperand ?: "and"
        if (params.filter) {
            def filter = Eval.me(params.filter)
            filter.each { k, v ->
                if (k.contains(".op")) return

                def key = k.replaceAll("__", ".")
                def c = Persistente.getCriteriaParameter(key, v)

                if (filter[k + ".op"]) c[k + ".op"] = filter[k + ".op"]
                paramsFilter.putAll(c)
            }
        }

        /*
         Opções
          */
        def options = [orderBy: params.orderBy ?: "id", orderByDirection: params.orderByDirection ?: "asc"]

        // Executa a busca
        def list = Persistente.GetCriteriaFromParams(getInstance(params.domain).class, [paramsListFields, paramsFilter], options)

        def result = []
        list.each {
            def itemResult = [:]
            itemResult.put("id", it?.id)
            itemResult.put("label", it.toString())
            itemResult.put("value", it?.toString())
            result.add(itemResult)
        }

        render result as JSON
    }

    private getDomainClass(domain) {
        domain = domain.indexOf('.') > -1 ? domain : GCU.getClassNameRepresentation(domain)
        def domainClass = grailsApplication.getDomainClass(domain)

        return domainClass
    }

    private getInstance(domain) {
        def obj = getDomainClass(domain).newInstance()
        return obj
    }

    private Object read(domain, id) {
        return getInstance(domain).read(id)
    }

}
