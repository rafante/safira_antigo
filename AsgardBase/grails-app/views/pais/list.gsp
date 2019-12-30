<%@ page import="com.br.asgardtecnologia.erp.base.Pais" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'pais.label', default: 'Pais')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.base.Pais"
                        params="${params}"
                        fields="${[[name: "cod_pais"], [name: "nome"], [name: "usuario"], [name: "empresa"]]}"/>

            <asg:grid controller="pais" list="${paisInstanceList}"
                      fields="cod_pais;nome;data_inclusao;data_ultima_alteracao;usuario;empresa"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>


        </div>
    </div>
</div></body>
</html>
