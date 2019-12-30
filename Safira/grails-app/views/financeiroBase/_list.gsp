<%@ page import="org.codehaus.groovy.grails.commons.DomainClassArtefactHandler" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<div class="container-fluid">
    <div class="row">

        <div id="page" class="col-sm-12">

            <g:if test="${flash.message}">
                <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <asg:filter params="${params}" gridName="instance"
                        domain="${grailsApplication.getArtefactByLogicalPropertyName(DomainClassArtefactHandler.TYPE, params.controller).fullName}"/>
                        %{--fields="${[[name: "parceiroNegocios"]]}"/>--}%

            <asg:grid name="instance" controller="${params.controller}"
                      list="${instance?.sort({ a, b -> a.id?.compareTo(b.id) })}"
                      fields="id;parceiroNegocios;descricao;dataDocumento[DATE];valorTotal[MONEY];documento"
                      itemButtons="show;edit;delete"
                      searchable="true"/>
        </div>
    </div>
</div>
</body>
</html>
