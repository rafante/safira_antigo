package com.br.asgardtecnologia.base.net

import groovyx.net.http.ContentType
import groovyx.net.http.Method

/**
 * Created by asgard on 29/01/15.
 */
class AsgardBaseConnection {
    String user
    String pwd
    String url
    String path

    Method method = Method.GET
    ContentType contentType = ContentType.JSON

    def query = [:]
    def headers = []
    def successHandler = {resp, reader ->
        println(reader)
    }
    def failureHandler = {resp ->
        println(resp)
    }

    /**
     * Sets the values of successHandler, failureHandler, query, headers, method and conentType to the
     * default values back, in case another call have changed those
     */
    public void clear(){
        successHandler = {resp, reader ->}
        failureHandler = {resp ->}
        query = [:]
        headers = [:]
        method = Method.GET
        contentType = ContentType.JSON
    }

    public void putParameter(name, value){
        query << [(name): value]
    }

    public void putHeader(name, value){
        headers << [(name): value]
    }

    def getSuccessHandler() {
        return successHandler
    }

    def getHeaders() {
        return headers
    }

    def getQuery() {
        return query
    }

    def getFailureHandler() {
        return failureHandler
    }

    void setSuccessHandler(successHandler) {
        this.successHandler = successHandler
    }

    void setFailureHandler(failureHandler) {
        this.failureHandler = failureHandler
    }
}
