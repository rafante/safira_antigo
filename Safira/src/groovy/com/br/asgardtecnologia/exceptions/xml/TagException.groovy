package com.br.asgardtecnologia.exceptions.xml

/**
 * Created by Bruno on 28/07/2016.
 */
class TagException extends Exception{

    protected String tag

    public TagException(tag){
        this(tag, "")
    }

    public TagException(tag, message){
        super(message)
        this.tag = tag
    }
}
