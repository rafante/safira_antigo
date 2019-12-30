package com.br.asgardtecnologia.taglib

import com.br.asgardtecnologia.base.Persistente
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClassProperty
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler

class DynamicFieldsTagLib {
    static namespace = "asg"

    def span = 12

    def dynamicField = { attrs, body ->
        def domainDescriptor = getDomainDescriptor(attrs)
        if (attrs.type) {
            def type = attrs.type
            attrs.remove("type")

            out << renderProperty(domainDescriptor.identifier.domainClass, type, attrs)
        } else {
            def property = getProperty(attrs)
            if (property) {
                out << renderProperty(domainDescriptor.identifier.domainClass, property, attrs)
            }
        }
    }

    def labeledDynamicField = { attrs, body ->
        def attrsLabel = attrs.clone()
        out << asg.dynamicField(attrsLabel, asg.label(attrs, body))
    }

    DefaultGrailsDomainClass getDomainDescriptor(attrs) {
        DefaultGrailsDomainClass domainDescriptor = attrs.domainDescriptor

        // Caso o domainDescriptor já tenha sido passado, não precisa fazer o processo novamente
        if (!attrs.domainDescriptor) {
            // Caso o domain não tenha sido passado, recupera o domínio de acordo com a domainClass passado
            if (!attrs.domain) {
                // Caso o domain não tenha sido passado, recupera o domínio de acordo com o controller atual
                if (attrs.domainClass)
                    attrs.domain = attrs.domainClass.name
                else {
                    // Caso o domainClass não tenha sido passado, recupera o domínio de acordo com o controller atual
                    if (attrs.instance)
                        attrs.domain = attrs.getClazz().name
                    else
                        attrs.domain = grailsApplication.getArtefactByLogicalPropertyName(DomainClassArtefactHandler.TYPE, controllerName).fullName
                }
            }

            // Recupera o Artefact referente ao domínio
            domainDescriptor = grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, attrs.domain)
        }

        return domainDescriptor
    }

    def getProperty(attrs) {
        if (attrs.type) {
            return [type: attrs.type]
        } else {
            def strProperty = FieldsTagLib.PureAttributeName(attrs.name.replace("_high", ""))
            def property = Persistente.GetDomainPropertyRecursively(getDomainDescriptor(attrs), strProperty)

            return property
        }
    }

    Boolean isBooleanProperty(attrs) {
        def property = getProperty(attrs)
        return (property.type == Boolean || property.type == boolean)
    }

    String renderProperty(DefaultGrailsDomainClass domainClass, Class type, attrs) {
        def property = [type: type]
        return renderProperty(domainClass, property, attrs)
    }

    String renderProperty(DefaultGrailsDomainClass domainClass, property, attrs) {
        def ret = ""

        if (property.type == Boolean || property.type == boolean)
            ret = renderBooleanProperty(domainClass, property, attrs)
        else if (property.type && Number.isAssignableFrom(property.type) || (property.type?.isPrimitive() && property.type != boolean))
            ret = renderNumberProperty(domainClass, property, attrs)
        else if (property.type == String)
            ret = renderStringProperty(domainClass, property, attrs)
        else if (property.type == Date || property.type == java.sql.Date || property.type == java.sql.Time || property.type == Calendar)
            ret = renderDateProperty(domainClass, property, attrs)
        else if (property.type == URL)
            ret = renderStringProperty(domainClass, property, attrs)
        else if (property.class == DefaultGrailsDomainClassProperty && property.type && property.isEnum())
            ret = renderEnumProperty(domainClass, property, attrs)
        else if (property.type == TimeZone)
            ret = renderSelectTypeProperty("timeZone", domainClass, property, attrs)
        else if (property.type == Locale)
            ret = renderSelectTypeProperty("locale", domainClass, property, attrs)
        else if (property.type == Currency)
            ret = renderSelectTypeProperty("currency", domainClass, property, attrs)
        else if (property.type == ([] as Byte[]).class) //TODO: Bug in groovy means i have to do this :(
            ret = renderByteArrayProperty(domainClass, property, attrs)
        else if (property.type == ([] as byte[]).class) //TODO: Bug in groovy means i have to do this :(
            ret = renderByteArrayProperty(domainClass, property, attrs)
        else if (property.manyToOne || property.oneToOne)
            ret = renderManyToOne(domainClass, property, attrs)

        return ret
    }

    private renderEnumProperty(DefaultGrailsDomainClass domainClass, property, attrs) {

        def values = property.type.values()
        attrs.name = attrs.name
        attrs.value = attrs.keepValue ? attrs.value : (params ? params[attrs.name] : null)
        attrs.span = span
        attrs.from = values
        attrs.keys = values*.name()
        attrs.noSelection = ['': '']
        attrs.allowClear = true

        return asg.labeledSelect(attrs)
    }

    private renderStringProperty(DefaultGrailsDomainClass domainClass, property, attrs) {
        attrs.name = attrs.name
        attrs.value = attrs.keepValue ? attrs.value : (params ? params[attrs.name] : null)
        attrs.span = span

        return asg.labeledTextField(attrs)
    }

    private renderByteArrayProperty(DefaultGrailsDomainClass domainClass, property, attrs) {
        return "<input type=\"file\" id=\"${attrs.name}\" name=\"${attrs.name}\" />"
    }

    private renderManyToOne(domainClass, property, attrs) {
        if (property.association) {
            attrs.domain = property.type.name
            attrs.id = attrs.name
            attrs.name = attrs.name + ".id"
            attrs.value = attrs.keepValue ? attrs.value?.id : ((params && params[attrs.name]) ? params[attrs.name] : null)
            attrs.span = span

            attrs.optionKey = "id"
            attrs.class = "many-to-one"

            return asg.labeledAutoComplete(attrs)
        }
    }

    private renderNumberProperty(DefaultGrailsDomainClass domainClass, property, attrs) {
        def cp = domainClass.constrainedProperties[FieldsTagLib.PureAttributeName(attrs.name)]

        attrs.name = attrs.name
        attrs.value = attrs.keepValue ? attrs.value : (params ? params[attrs.name] : null)
        attrs.span = span
        if (attrs.money || cp?.metaConstraints?.money)
            return asg.labeledMoney(attrs)
        else
            return asg.labeledDecimal(attrs)
    }

    private renderBooleanProperty(DefaultGrailsDomainClass domainClass, property, attrs) {

        def cp = domainClass.constrainedProperties[attrs.name]

        attrs.name = attrs.name
        attrs.span = span
        attrs.value = attrs.keepValue ? attrs.value : (params ? params[attrs.name] : null)
        if (cp) {
            if (cp.widget) attrs.widget = cp.widget
            cp.attributes.each { k, v ->
                attrs[k] = v
            }
            //            return sb.toString()
        }

        return asg.labeledCheckBox(attrs)
    }

    private renderDateProperty(DefaultGrailsDomainClass domainClass, property, attrs) {

        def cp = domainClass.constrainedProperties[attrs.name]

        attrs.name = attrs.name
        attrs.value = attrs.keepValue ? attrs.value : (params ? params[attrs.name] : null)
        attrs.span = span
        def precision = (property.type == Date || property.type == java.sql.Date || property.type == Calendar) ? "day" : "minute";
        if (!cp) {
            attrs.precision = precision
        } else {
            if (cp.format) attrs.format = cp.format
            if (cp.widget) attrs.widget = cp.widget
            cp.attributes.each { k, v ->
                attrs[k] = v
            }
            if (!cp.attributes.precision) {
                attrs.precision = precision
            }
        }

        asg.labeledDatePicker(attrs)
    }

}