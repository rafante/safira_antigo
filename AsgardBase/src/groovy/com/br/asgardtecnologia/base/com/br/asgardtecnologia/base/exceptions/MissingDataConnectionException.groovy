package com.br.asgardtecnologia.base.com.br.asgardtecnologia.base.exceptions

import javax.xml.ws.http.HTTPException

/**
 * Created by asgard on 29/01/15.
 */
class MissingDataConnectionException extends HTTPException{
    /** Constructor for the HTTPException
     * @param statusCode <code>int</code> for the HTTP status code
     *
     */
    MissingDataConnectionException(int statusCode) {
        super(statusCode)
        this.message = "Missing data into your AsgardBaseConnection"
    }

}
