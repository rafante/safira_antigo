package com.br.asgardtecnologia.erp.base

import com.br.asgardtecnologia.base.ExcelBuilder
import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.erp.security.Usuario
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.GrailsClass
import org.springframework.beans.PropertyEditorRegistry
import org.springframework.beans.propertyeditors.CustomNumberEditor

import java.text.DecimalFormat

class BaseController {

    public GrailsClass domainArtefact
    public Class cl

    protected String getControllerName() {
        def cn = params?.controllerName
        if (!cn) cn = controllerName
        return cn
    }

    protected void setBaseData(Persistente persistente) {
        def usuario = Usuario.read(principal.id)

        persistente.usuario = usuario
        persistente.empresa = Empresa.first()
    }

    protected getDomainArtefact() {
        if (!domainArtefact) domainArtefact = grailsApplication.getArtefactByLogicalPropertyName("Domain", getControllerName())

        return domainArtefact
    }

    protected Class getDomainClass() {
        if (!cl) cl = getDomainArtefact().clazz
        return cl
    }

    protected paramsToCriteria(params, clazz, options) {
        return Persistente.GetCriteriaFromParams(clazz, params, options)
    }

    protected paramsToCriteria(params) {
        return Persistente.GetCriteriaFromParams(getDomainClass(), params)
    }

    protected paramsToCriteria(params, clazz) {
        return Persistente.GetCriteriaFromParams(clazz, params)
    }

    protected paramsToCriteria(params, Closure staticCriteria, ArrayList staticAliasList) {
        return Persistente.GetCriteriaFromParams(getDomainClass(), params, staticCriteria, staticAliasList)
    }

    protected String resolveMessage(msg) {
        String ret = ""
        if (msg?.class == grails.validation.ValidationErrors)
            ret = g.message(error: msg?.fieldError, default: msg?.fieldError)
        else if (msg?.class == org.springframework.validation.FieldError)
            ret = g.message(error: msg, default: msg)
        else
            ret = g.message(code: msg, default: msg)

        return ret
    }

    protected String resolveEnumMessage(_enum) {
        if (_enum)
            return message(code: "enum.value." + _enum.name())
        else return ""
    }

    /**
     * Recebe uma string dos arquivos para os quais será feito upload e retorna as linhas da tabela do excel
     * @param files
     * @return
     */
    protected getRowsFromXLSDocs(files, page = 0) {
        def rowsMap = []
        request.getFiles(files).each {
            def excel = new ExcelBuilder(it.inputStream)
            def pagesNumber = excel.workbook
            def folha = excel.getSheet(page)
            def rows = folha._rows
            def referenceRow = rows.get(0)
            def referenceRows = referenceRow.cells[0]._sheet._rows
            rowsMap << referenceRows
        }
        return rowsMap
    }

    private respond(obj) {
        render obj as JSON
    }

//    def copy() {
//        render "Ainda não funciona! :("
////        def clazz = getDomainClass().newInstance().getClass()
////        def domainInstance = clazz.read(params.id)
////        def newDomainInstance = deepClone(domainInstance)
////
////        newDomainInstance.save(flush: true)
////
////        redirect action: "edit", id: newDomainInstance.id
//    }

//    public Object deepClone(domainInstanceToClone) {
//
//        //Our target instance for the instance we want to clone
//        // recursion
//        def newDomainInstance = domainInstanceToClone.getClass().newInstance()
//
//        //Returns a DefaultGrailsDomainClass (as interface GrailsDomainClass) for inspecting properties
//        def domainClass = domainInstanceToClone.domainClass
//
//        domainClass?.persistentProperties?.each { prop ->
//            if (prop.association) {
//                if (prop.owningSide) {
//                    //we have to deep clone owned associations
//                    if (prop.oneToOne) {
//                        def newAssociationInstance = deepClone(domainInstanceToClone?."${prop.name}")
//                        newDomainInstance."${prop.name}" = newAssociationInstance
//                    } else {
//                        domainInstanceToClone."${prop.name}".each { associationInstance ->
//                            def newAssociationInstance = deepClone(associationInstance)
//                            newDomainInstance."addTo${StringUtils.capitalize(prop.name)}"(newAssociationInstance)
//                        }
//                    }
//                } else {
//                    if (!prop.bidirectional) {
//                        //If the association isn't owned or the owner, then we can just do a  shallow copy of the reference.
//                        newDomainInstance."${prop.name}" = domainInstanceToClone."${prop.name}"
//                    }
//                    // @@JR
//                    // Yes bidirectional and not owning. E.g. clone Report, belongsTo Organisation which hasMany
//                    // manyToOne. Just add to the owning objects collection.
//                    else {
//                        if (prop.manyToOne) {
//                            newDomainInstance."${prop.name}" = domainInstanceToClone."${prop.name}"
//                            def owningInstance = domainInstanceToClone."${prop.name}"
//                            // Need to find the collection.
//                            String otherSide = StringUtils.capitalize(prop.otherSide.name)
//                            owningInstance."addTo${otherSide}"(newDomainInstance)
//                        }
//                    }
//                }
//            } else {
//                //If the property isn't an association then simply copy the value
//                newDomainInstance."${prop.name}" = domainInstanceToClone."${prop.name}"
//            }
//        }
//
//        return newDomainInstance
//    }

//    def next() {
//        def id = getDomainClass().newInstance().getClass().withCriteria {
//            projections {
//                property("id")
//            }
//
//            gt("id", params.id.toLong())
//            maxResults(1)
//            order("id", "asc")//assuming auto increment just to make sure
//        }[0]
//
//        redirect action: "show", id: id
//    }
//
//    def prev() {
//        def id = getDomainClass().newInstance().getClass().withCriteria {
//            projections {
//                property("id")
//            }
//
//            lt("id", params.id.toLong())
//            maxResults(1)
//            order("id", "desc")//assuming auto increment just to make sure
//        }[0]
//
//        redirect action: "show", id: id
//    }
}
