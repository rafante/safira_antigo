package com.br.asgardtecnologia.base

import com.br.asgardtecnologia.erp.base.Empresa
import com.br.asgardtecnologia.erp.security.Usuario
import grails.util.Holders
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.web.binding.DataBindingUtils
import org.hibernate.criterion.CriteriaSpecification
import org.springframework.util.StringUtils

abstract class Persistente implements Comparable {

    Date data_inclusao
    Date data_ultima_alteracao
    Empresa empresa
    Usuario usuario

    Boolean deleted
    static transients = ["deleted"]

    static auditable = true

    protected void beforeSave() {}

    def beforeInsert() {
        this.data_inclusao = new Date()

        this.beforeSave()
    }

    @Override
    int compareTo(o) {
        (this?.id ?: 0)?.compareTo(o?.id ?: 0)
    }

    def deepClone() {
        return deepClone(this)
    }

    static EntityListToReferenceIdArray(list) {
        def ret = []
        for (item in list) {
            ret << item?.id
        }
        return ret
    }

    /*
    * Clones a domain object and recursively clones children, clearing ids and
    * attaching children to their new parents. Ownership relationships (indicated
    * by GORM belongsTo keyword) cause "copy as new" (a recursive deep clone),
    * but associations without ownership are shallow copied by reference.
    */

    def deepClone(domainInstanceToClone) {
        //Our target instance for the instance we want to clone
        // recursion
        def newDomainInstance = domainInstanceToClone.getClass().newInstance()
        //Returns a DefaultGrailsDomainClass (as interface GrailsDomainClass) for inspecting properties
        def domainClass = ApplicationHolder.application.getDomainClass(newDomainInstance.getClass().name)
        domainClass?.persistentProperties?.each { prop ->
            if (prop.association) {
                if (prop.owningSide) {
                    //we have to deep clone owned associations
                    if (prop.oneToOne) {
                        def newAssociationInstance = deepClone(domainInstanceToClone?."${prop.name}")
                        newDomainInstance."${prop.name}" = newAssociationInstance
                    } else {
                        domainInstanceToClone."${prop.name}".each { associationInstance ->
                            def newAssociationInstance = deepClone(associationInstance)
                            newDomainInstance."addTo${StringUtils.capitalize(prop.name)}"(newAssociationInstance)
                        }
                    }
                } else {
                    if (!prop.bidirectional) {
                        //If the association isn't owned or the owner, then we can just do a  shallow copy of the reference.
                        newDomainInstance."${prop.name}" = domainInstanceToClone."${prop.name}"
                    }
                    // @@JR
                    // Yes bidirectional and not owning. E.g. clone Report, belongsTo Organisation which hasMany
                    // manyToOne. Just add to the owning objects collection.
                    else {
                        if (prop.manyToOne) {
                            newDomainInstance."${prop.name}" = domainInstanceToClone."${prop.name}"
                            def owningInstance = domainInstanceToClone."${prop.name}"
                            // Need to find the collection.
                            String otherSide = StringUtils.capitalize(prop.otherSide.name)
                            owningInstance."addTo${otherSide}"(newDomainInstance)
                        }
                    }
                }
            } else {
                //If the property isn't an association then simply copy the value
                newDomainInstance."${prop.name}" = domainInstanceToClone."${prop.name}"
            }
        }
        return newDomainInstance
    }

    static GetCriteriaFromParams(Class cl, Map params) {
        def paramsList = []
        paramsList << params
        GetCriteriaFromParams(cl, paramsList)
    }

    static GetCriteriaFromParams(Class cl, Map params, Map options) {
        def paramsList = []
        paramsList << params
        GetCriteriaFromParams(cl, paramsList, options)
    }

    static GetCriteriaFromParams(Class cl, ArrayList paramsList) {
        GetCriteriaFromParams(cl, paramsList, null)
    }

    static GetCriteriaFromParams(Class cl, ArrayList paramsList, Map options) {
        GetCriteriaFromParams(cl, paramsList, options, {}, null)
    }

    static GetCriteriaFromParams(Class cl, Map params, Closure staticCriteria, ArrayList staticAliasList) {
        def paramsList = []
        paramsList << params
        GetCriteriaFromParams(cl, paramsList, null, staticCriteria, staticAliasList)
    }

    static ObterParametros(Map incommingParams, Class cl){
        def paramsList = []
        paramsList << incommingParams
        def criteriaParams = [:]
        def collectedCriteriaParamsList = [:]
        paramsList.each {
            // Junta todos os parâmetros em uma só variável, para a preparação do Alias
            collectedCriteriaParamsList.putAll(it.findAll { it.value })

            def param = [:]

            // Determina o operador lógico (EQ, NE, BETWEEN...)
            criteriaParams.logicalOperand = it.logicalOperand ?: "and"

            // Ajusta os parâmetros
            criteriaParams.paramsList = GetCriteriaParamList(cl, it)

            // Adiciona os parâmetros ajustados atuais à lista
            //criteriaParams << param
        }

        criteriaParams.params = [:]
        criteriaParams.paramsList.each{
            criteriaParams.params."${it.name}" = it
        }

        return criteriaParams
    }

    static GetCriteriaFromParams(Class cl, ArrayList paramsList, Map options, Closure staticCriteria, ArrayList staticAliasList) {
        def criteriaParams = []
        def collectedCriteriaParamsList = [:]

        // Prepara os filtros
        paramsList.each {
            // Junta todos os parâmetros em uma só variável, para a preparação do Alias
            collectedCriteriaParamsList.putAll(it.findAll { it.value })

            def param = [:]

            // Determina o operador lógico (EQ, NE, BETWEEN...)
            param.logicalOperand = it.logicalOperand ?: "and"

            // Ajusta os parâmetros
            param.criteriaParam = GetCriteriaParamList(cl, it)

            // Adiciona os parâmetros ajustados atuais à lista
            criteriaParams << param
        }

        // Prepara as opções
        if (!options) options = [:]

        if (!options.orderBy && collectedCriteriaParamsList.orderBy)
            options.orderBy = collectedCriteriaParamsList.orderBy

        if (!options.groupBy && collectedCriteriaParamsList.groupBy)
            options.groupBy = collectedCriteriaParamsList.groupBy

        if (!options.projections && collectedCriteriaParamsList.projections)
            options.projections = collectedCriteriaParamsList.projections

        if (!options.orderBy)
            options.orderBy = "id"

        if (!options.orderByDirection)
            options.orderByDirection = "asc"

        if (!options.orderByDirection)
            if (collectedCriteriaParamsList.orderByDirection) options.orderByDirection = collectedCriteriaParamsList.orderByDirection
            else options.orderByDirection = "asc"

        collectedCriteriaParamsList.putAll(options)

        // Prepara lista de Alias a serem criados
        final collectedAliasParamList = GetAliasParamList(collectedCriteriaParamsList)
        if (staticAliasList) {
            staticAliasList.each {
                collectedAliasParamList << ["${it.getKey()}": it.getValue()]
            }
            collectedAliasParamList.unique(true)
        }

        final collectedNonPersistentFilters = []

        // Cria CLOSURE dinâmica (de acordo com os params) para o criteria
        def dynamicCriteria = {

            collectedAliasParamList?.each {
                it.each {
                    createAlias(it.getKey(), it.getValue(), CriteriaSpecification.LEFT_JOIN)
                }
            }

            and {
                criteriaParams.each {
                    final criteriaParam = it.criteriaParam

                    "${it.logicalOperand}" {

                        criteriaParam?.each {
                            def param = it

                            param.name = GetPreparedFieldName(it.name)
                            // Campos que não estão no banco de dados serão filtrados após a QUERY
                            if (!param.domainProperty.isPersistent()) {
                                collectedNonPersistentFilters << param
                                return
                            }

                            if (param.op == "between") {
                                between(param.name, param.low, param.high)
                            } else
                            // Validação de Boolean quando for falso, para buscar os NULL também
                            if (param.low?.class == Boolean && !param.low) {
                                or {
                                    eq(param.name, param.low)
                                    isNull(param.name)
                                }
                            } else if (param.low == null) {
                                isNull(param.name)
                            } else {
                                "${param.op}"(param.name, param.low)
                            }
                        }
                    }
                }
            }

            order(options.orderBy, options.orderByDirection)

            if(options?.projections?.size()){
                resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
                projections {
                    options?.projections?.each {
//                        createAlias(it, it)
                        property(it, it)
                    }
                }
            }

            options?.groupBy?.each{
                groupProperty(it)
            }

            if (!collectedCriteriaParamsList.maxResults) {
                collectedCriteriaParamsList.maxResults = collectedCriteriaParamsList.max
            }

            if (collectedCriteriaParamsList.maxResults) {
                maxResults(Utils.ToInteger(collectedCriteriaParamsList.maxResults))
            }
        }

        // Unifica as closures DINÂMICA e ESTÁTICA
        def unifiedCriteria = staticCriteria << dynamicCriteria

        def results = cl.createCriteria().list(collectedCriteriaParamsList, unifiedCriteria)

        if (!collectedCriteriaParamsList.ignoreNonPersistentFilters && collectedNonPersistentFilters.size())
            results.removeAll({ !ValidateObjFromFilterList(it, collectedNonPersistentFilters) })

        return results
    }

    static GetAliasParamList(criteriaParamList) {
        def ret = []

        criteriaParamList.each { k, v ->
//            if (!k.contains(".op") && !criteriaParamList[k + ".op"]) return

            def name = k

            if (name.contains(".") && !name.contains(".high") && !name.contains(".op")) {
                def splittedName = name?.split("\\.")
                def alias = ""
                for (String propName in splittedName) {
                    if (propName != splittedName.last()) {
                        alias += propName
                        ret << ["${alias}": propName]
                        alias += "."
                    }//
                }
            }
        }

        ret = ret.unique()
        return ret
    }

    /*
    Gets the field name matching the alias
     */

    static String GetPreparedFieldName(String fieldName) {
        def splitted = fieldName?.split("\\.")
        String ret = splitted?.last()

        if (splitted?.size() > 1) {
            ret = splitted[-2] + "." + ret
        }

        return ret
    }

    static GetCriteriaParamList(Class cl, params) {
        def ret = []

        // Faz o bind de todos os parâmetros de cada para que o tipo vá correto para a query
        final obj = cl.newInstance()
        obj.properties = params

        params.each { k, v ->
            k = k.replace(".id", "")

            // Verifica a existência do parâmetro atual no objeto e se o mesmo está preenchido
            if (params[k + ".op"] && IsCleanParam(k, v)) {
                if (k == "id" && !v) return

                def param = [:]
                param.name = k
                param.domainProperty = Persistente.GetDomainPropertyRecursively(obj, param.name)

                if (v?.class == ArrayList) {
                    // Faz o bind manualmente de cada item do Array para que o tipo vá correto para a query
                    v.eachWithIndex() { o, i ->
                        DataBindingUtils.bindObjectToInstance(obj, ["${k}": o], [k], [], null)
                        def val = Persistente.GetPropertyRecursively(obj, param.name)
                        v[i] = val
                    };

                    param.low = v
                } else {
                    if (param.domainProperty.isPersistent())
                        param.low = (k.equals("id") || k.endsWith(".id")) ? Utils.ToLong(v) : Persistente.GetPropertyRecursively(obj, k)
                    else {
                        param.low = Utils.ToAny(param.domainProperty.type, v)
                    }

                    // TODO: Ajuste técnico abaixo para filtro de relacionamento hasMany (o param.low vem Array ao invés do tipo da propriedade. Ex.: Lista de Contas a Pagar - Filtro por Vencimento que está no Item)
                    if (param.low?.class != param.domainProperty.type) {
                        param.low = Utils.ToAny(param.domainProperty.type, v)
                    }
                }

                if (Holders.getGrailsApplication().isDomainClass(param.low.getClass())) {
                    if (!param.low.id) param.low = null
                }

                param.op = params[k + ".op"]

                if (param.low?.class == Date) {
                    // Se for EQ, troca pra BETWEEN para cobrir todas as horas do dia
                    if (param.op == "eq") param.op = "between"
                    // Se for LT ou GE, coloca o LOW na primeira hora do dia, englobando somente as datas/horas menores que a data atual
                    else if (param.op == "lt" || param.op == "ge") param.low = (new org.joda.time.LocalDate(param.low)).toDateTimeAtStartOfDay().toDate()
                    // Se for LE ou GT, coloca o LOW na última hora do dia, englobando a data atual também
                    else if (param.op == "le" || param.op == "gt") param.low = (new org.joda.time.LocalDate(param.low)).plusDays(1).toDateTimeAtStartOfDay().minusSeconds(1).toDate()
                }

                if (param.op == "between") {

                    // Se o tipo do HIGH for diferente do LOW, dá o bind no objeto para trazer o tipo correto
                    if (params["${k}_high"].getClass() == param.low?.getClass())
                        param.high = params["${k}_high"]
                    else {
                        obj.setPropertyRecursively(k, params["${k}_high"])
                        param.high = obj.getPropertyRecursively(k)
                    }

                    if (param.low.class == Date) {
                        def localDateLow = new org.joda.time.LocalDate(param.low)
                        def localDateHigh = new org.joda.time.LocalDate(param.high ?: param.low)

                        param.low = localDateLow.toDateTimeAtStartOfDay().toDate()
                        param.high = localDateHigh.plusDays(1).toDateTimeAtStartOfDay().minusSeconds(1).toDate()
                    }

                    param.op = "between"
                } else {
                    // Valida asterisco
                    if (param.low?.class == String && param.low?.contains("*")) {
                        param.op = "ilike"
                        param.low = param.low?.toString().replace("*", "%")
                    }
                }

                ret << param
            }
        }

        return ret.sort { a, b -> a.name <=> b.name }
    }

    static IsCleanParam(k, v) {
        return (!k.contains("_holder") && !(k in ["action", "controller", "customAttrs", "max"]))
    }

    def getPropertyRecursively(String property) {
        return GetPropertyRecursively(this, property)
    }

    def setPropertyRecursively(String property, value) {
        return SetPropertyRecursively(this, property, value)
    }

    def getDomainPropertyRecursively(String property) {
        return GetDomainPropertyRecursively(this, property)
    }

    def getConstraintsRecursively(String property) {
        return GetConstraintsRecursively(this, property)
    }

    static GetPropertyRecursively(Object o, String property) {
        try {
            property?.tokenize('.').inject o, { obj, prop ->
                obj != null ? obj[prop] : null
            }
        } catch (e) {
            return null
        }
    }

    static SetPropertyRecursively(Object o, String property, value) {
        try {
            property?.tokenize('.').inject o, { obj, prop ->
                if (!obj) return null

                if (prop == property?.tokenize('.').last()) {
                    obj[prop] = value
                }
            }
        } catch (e) {
            return null
        }
    }


    static GrailsDomainClassProperty GetDomainPropertyRecursively(Object o, String property) {
        return GetDomainPropertyRecursively(o.domainClass, property)
    }

    static GrailsDomainClassProperty GetDomainPropertyRecursively(GrailsDomainClass cl, String property) {
        final _cl = cl

        def splittedProperty = property.split("\\.")

        for (String prop in splittedProperty) {
            def p = _cl.getPropertyByName(prop)

            if (prop == property?.tokenize('.').last()) {
                return p
            } else {
                _cl = p.referencedDomainClass
            }
        }
    }

    static GetConstraintsRecursively(Object o, String property) {
        try {
            def propertyType = o.class
            def fieldConstraints
            property?.tokenize('.').inject o, { obj, prop ->
                if (prop == property?.tokenize('.').last()) {
                    return fieldConstraints?.metaConstraints?.asgDefaultFilter
                } else {
                    fieldConstraints = propertyType.constraints?."${prop}"
                    propertyType = fieldConstraints.propertyType
                }
            }
        } catch (e) {
            return null
        }
    }

    static getCriteriaParameter(String field, value) {
        def operand = value?.class == ArrayList ? "in" : "eq"
        return getCriteriaParameter(field, operand, value)
    }

    static getCriteriaParameter(String field, operand, value) {
        def ret = [:]

        def fieldOp = field.replace('.id', '') + ".op"
        ret[fieldOp] = operand
        ret[field] = value

        return ret
    }

    static ValidateObjFromFilterList(obj, filterList) {
        Boolean ret = true

        filterList.each {
            if (!ret) return false

            if (it.op == "between") {
                if (!filter_between(obj[it.name], it.low, it.high)) ret = false
            } else {
                if (!"filter_${it.op}"(obj[it.name], it.low)) ret = false
            }
        }

        return ret
    }

    static filter_eq(obj, obj1) {
        Boolean ret = obj == obj1
        return ret
    }

    static filter_ne(obj, obj1) { return obj != obj1 }

    static filter_gt(obj, obj1) { return obj > obj1 }

    static filter_lt(obj, obj1) { return obj < obj1 }

    static filter_ge(obj, obj1) { return obj >= obj1 }

    static filter_le(obj, obj1) { return obj <= obj1 }

    static filter_between(obj, obj1, obj2) { return filter_ge(obj, obj1) && filter_le(obj, obj2) }

}
