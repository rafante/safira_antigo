<%@ page import="com.br.asgardtecnologia.erp.security.Usuario" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'usuario.label', default: 'Usuario')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.security.Usuario"
                        params="${params}"
                        fields="${[[name: "username"], [name: "password"], [name: "nome"], [name: "empresa"], [name: "accountExpired"], [name: "accountLocked"]]}"/>

            <asg:grid controller="usuario" list="${usuarioInstanceList}"
                      fields="username;nome;empresa;accountExpired;accountLocked"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>

        </div>
    </div>
</div></body>
</html>
