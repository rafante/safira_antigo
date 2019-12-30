package com.br.asgardtecnologia.exceptions.xml

import groovy.util.slurpersupport.NodeChild

/**
 * Created by Bruno on 28/07/2016.
 */
class MissingTagException extends TagException{

    public String missingTag
    def node

    public MissingTagException(node, tag){
        super(tag)
        missingTag = tag
    }

    public String errorMessage(){
        return "Tag faltando: ${missingTag}"
    }
}
