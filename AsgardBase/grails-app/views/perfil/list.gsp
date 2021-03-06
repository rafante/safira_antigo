<%@ page import="com.br.asgardtecnologia.erp.security.Perfil" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'perfil.label', default: 'Perfil')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body><div class="container-fluid">
    <div class="col-xs-12">

        <div class="col-12 col-sm-3" style="display:none">
        </div>

        <div id="page" class="col-12">

            <g:if test="${flash.message}">
                <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <asg:filter domain="com.br.asgardtecnologia.erp.security.Perfil"
                        params="${params}"
                        fields="${[[name: "authority"]]}"/>

            <asg:grid controller="perfil" list="${perfilInstanceList}"
                      fields="authority"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>


        </div>
    </div>
</div></body>
</html>
