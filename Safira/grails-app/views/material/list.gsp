<%@ page import="com.br.asgardtecnologia.erp.materiais.Material" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'material.label', default: 'Material')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.materiais.Material" params="${params}"/>

            <asg:grid controller="material" list="${materialInstanceList}"
                      fields="id;descricao;finalidade[ENUM];grupo;sub_grupo"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>
        </div>
    </div>
</div></body>
</html>
