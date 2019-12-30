package com.br.asgardtecnologia.base.net

import com.br.asgardtecnologia.base.com.br.asgardtecnologia.base.exceptions.MissingDataConnectionException
//import sun.net.www.http.HttpClient

import groovyx.net.http.*
//import groovyx.net.http.ContentType
//import groovyx.net.http.HTTPBuilder

public class HttpClientService{

    /***
     * Makes an HTTP Request to an specified host. Its possible to define a closure for dealing with
     * the server's answer. To do so, set the AsgardBasConnection successHandler and failureHandler to
     * a custom closure.
     * @param connection
     */
    static makeRequest(AsgardBaseConnection connection){
        def attrs = []
        attrs << connection.properties
        makeRequest(attrs)
    }

    static makeRequest(attrs = []){
        if(!attrs.url || !attrs.method || !attrs.contentType)
            throw new MissingDataConnectionException(306)
        def ret = []
        HTTPBuilder http = new HTTPBuilder(attrs.url[0])
        if(attrs.user[0] != null && attrs.pwd[0] != null)
            http.auth.basic(attrs.user[0],attrs.pwd[0])

        //http.defaultResponseHandlers.success = attrs.successHandler[0]
        //http.defaultResponseHandlers.failure = attrs.failureHandler[0]
        http.request(attrs.method[0], ContentType.ANY) {
            if (attrs.headers && attrs.headers.size()) {
                attrs.headers[0].each {
                    headers << it
                }
            }
            if(attrs.query[0]?.size()){
                uri.query = attrs.query[0]
                contentType = attrs.contentType[0]
            }
            response.success = { resp, reader ->
                attrs.successHandler[0](resp, reader)
            }
            response.failure = { resp ->
                attrs.failureHandler[0](resp)
            }
        }
    }
}
