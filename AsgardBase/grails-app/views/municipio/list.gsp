<%@ page import="com.br.asgardtecnologia.erp.base.Municipio" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'municipio.label', default: 'Municipio')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.base.Municipio"
                        params="${params}"
                        fields="${[[name: "nome"], [name: "estado"], [name: "empresa"], [name: "usuario"]]}"/>

            <asg:grid controller="municipio" list="${municipioInstanceList}"
                      fields="nome;estado;data_inclusao;data_ultima_alteracao;empresa;usuario"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>


        </div>
    </div>
</div></body>
</html>
