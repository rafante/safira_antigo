<%@ page import="com.br.asgardtecnologia.erp.materiais.ItemEntrada" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'itemEntrada.label', default: 'ItemEntrada')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.materiais.ItemEntrada"
                        params="${params}"
                        fields="${[[name: "item"], [name: "material"], [name: "quantidade"], [name: "unidade_medida"], [name: "valor"], [name: "data_inclusao"]]}"/>

            <asg:grid controller="itemEntrada" list="${itemEntradaInstanceList}"
                      fields="item;material;quantidade;unidade_medida;valor;data_inclusao"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>


        </div>
    </div>
</div></body>
</html>
