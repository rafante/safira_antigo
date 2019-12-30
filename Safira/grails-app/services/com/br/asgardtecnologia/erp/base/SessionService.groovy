package com.br.asgardtecnologia.erp.base

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsHttpSession
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

class SessionService {

    def setSessionAttribute(String key, value) {
        GrailsWebRequest request = RequestContextHolder.currentRequestAttributes()
        GrailsHttpSession session = request.session
        session.setAttribute(key, value)
    }

    def getSessionAttribute(String key) {
        GrailsWebRequest request = RequestContextHolder.currentRequestAttributes()
        GrailsHttpSession session = request.session
        return session.getAttribute(key)
    }

}