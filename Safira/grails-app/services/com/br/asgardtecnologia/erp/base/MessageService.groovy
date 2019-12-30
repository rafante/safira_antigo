package com.br.asgardtecnologia.erp.base

import grails.validation.ValidationErrors
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.validation.Errors
import org.springframework.validation.FieldError

public class MessageService {

    def sessionService
    def messageSource

    static MessageService getBean() {
        return ApplicationHolder.application.getMainContext().getBean("messageService")
    }

    // GETTERS

    def getInfoMessages() {
        return sessionService.getSessionAttribute("infoMessages") ?: []
    }

    def getWarningMessages() {
        return sessionService.getSessionAttribute("warningMessages") ?: []
    }

    def getErrorMessages() {
        return sessionService.getSessionAttribute("errorMessages") ?: []
    }

    def getMessages() {
        def messages = []
        messages << [type: "I", messages: getInfoMessages()]
        messages << [type: "W", messages: getWarningMessages()]
        messages << [type: "E", messages: getErrorMessages()]

        return messages
    }

    // SETTERS

    public void info(message) {

        def _info = getInfoMessages()
        if (!_info)
            _info = []

        _info << message
        sessionService.setSessionAttribute("infoMessages", _info)
    }

    public void warning(message) {
        def _warning = getWarningMessages()
        if (!_warning)
            _warning = []

        _warning << message
        sessionService.setSessionAttribute("warningMessages", _warning)
    }

    public void error(message) {
        def _error = getErrorMessages()
        if (!_error)
            _error = []

        _error << message
        sessionService.setSessionAttribute("errorMessages", _error)
    }

    public void error(Errors errors) {
        for (ValidationErrors validationErrors in errors) {
            for (FieldError fieldError in validationErrors.allErrors)
                this.error(messageSource.getMessage(fieldError, new Locale("pt","BR")))
        }
    }

    public void clearInfo() {
        sessionService.setSessionAttribute("infoMessages", [])
    }

    public void clearWarnings() {
        sessionService.setSessionAttribute("warningMessages", [])
    }

    public void clearErrors() {
        sessionService.setSessionAttribute("errorMessages", [])
    }

    public void clearMessages() {
        clearInfo()
        clearWarnings()
        clearErrors()
    }

    def getMessageCssClass(String type) {
        switch (type) {
            case "I":
                return "alert-info"
                break
            case "W":
                return "alert-warning"
                break
            case "E":
                return "alert-danger"
                break
        }
    }

    /**
     *
     * @param msgKey
     * @param defaultMessage default message to use if none is defined in the message source
     * @param objs objects for use in the message
     * @return
     */
    def getMessage(String msgKey, String defaultMessage = null, List objs = null) {
        if (!msgKey) return ""

        def msg = messageSource.getMessage(msgKey, objs?.toArray(), defaultMessage, new Locale("pt","BR"))

        if (!msg) {
            if (msgKey.indexOf(".") == -1)
                return ""

            msg = getMessage(msgKey.substring(msgKey.indexOf(".") + 1))
        }

        return msg
    }

    def getEnumMessage(String value) {
        return this.getMessage("enum.value." + value)
    }

}
