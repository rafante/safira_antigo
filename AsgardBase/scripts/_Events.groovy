includeTargets << grailsScript("_GrailsInit")

eventConfigureTomcat = { tomcat ->
    tomcat.connector.setAttribute("compression", "on") //Disable on mac "off"
    tomcat.connector.setAttribute("compressableMimeType", "text/html,text/xml,text/plain,application/javascript")
    tomcat.connector.port = serverPort
}