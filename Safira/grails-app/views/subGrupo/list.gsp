<%@ page import="com.br.asgardtecnologia.erp.cadastros.SubGrupo" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'subGrupo.label', default: 'SubGrupo')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.cadastros.SubGrupo"
                        params="${params}"
                        fields="${[[name: "descricao"], [name: "grupo"], [name: "data_inclusao"], [name: "data_ultima_alteracao"], [name: "empresa"], [name: "usuario"]]}"/>

            <asg:grid controller="subGrupo" list="${subGrupoInstanceList}"
                      fields="descricao;grupo;data_inclusao;data_ultima_alteracao;empresa;usuario"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>


        </div>
    </div>
</div></body>
</html>
