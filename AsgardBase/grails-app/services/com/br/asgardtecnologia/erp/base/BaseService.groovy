package com.br.asgardtecnologia.erp.base

import com.br.asgardtecnologia.base.ExcelBuilder
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClassProperty

public class BaseService {

    def save(object, Map params) {

        if (!beforeSave(object)) return false

        hasManyToBeDeleted(object)

        if(!object.validate()){
            print(object.errors)
        }

        def ret = object.save(params)
        if (!ret) {
            return false
        }

        afterSave(object)

        return ret
    }

    def teste(){
        ExcelBuilder builder = new ExcelBuilder(inputStream)
    }

    def hasManyToBeDeleted(object) {
        def domainClass = object.domainClass
        for (DefaultGrailsDomainClassProperty prop in domainClass.persistentProperties) {
            if (prop.oneToMany) {
                def items = object[prop.name]

                def _toBeDeleted = items.findAll { if (it.hasProperty("deleted")) it.deleted }
                for (item in _toBeDeleted) {
                    object?."removeFrom${prop.name.capitalize()}"(item)
                    item.delete()
                }

            }
        }
    }

    def save(object) {
        return this.save(object, [flush: true, validate: true])
    }

    Boolean beforeSave(object) { return true }

    Boolean afterSave(object) { return true }

    def merge(object, Map params) {

        beforeSave(object)

        def ret = object.merge(params)
        if (!ret) {
            object.errors.each {
                println it
            }
        }

        return ret
    }

    def merge(object) {
        return this.merge(object, [flush: true, validate: true])
    }

    def delete(object, Map params) {
        beforeDelete(object)
        def ret = object.delete(params)
        afterDelete(object)

        return ret
    }

    def delete(object) {
        return this.delete(object, [flush: true, validate: true])
    }

    Boolean beforeDelete(object) { return true }

    Boolean afterDelete(object) { return true }
}
