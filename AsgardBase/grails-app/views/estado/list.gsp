<%@ page import="com.br.asgardtecnologia.erp.base.Estado" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'estado.label', default: 'Estado')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.base.Estado"
                        params="${params}"
                        fields="${[[name: "sigla"], [name: "nome"], [name: "pais"], [name: "empresa"]]}"/>

            <asg:grid controller="estado" list="${estadoInstanceList}"
                      fields="sigla;nome;pais;data_inclusao;data_ultima_alteracao;empresa"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>


        </div>
    </div>
</div></body>
</html>
