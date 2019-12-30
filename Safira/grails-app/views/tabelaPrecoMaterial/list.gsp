<%@ page import="com.br.asgardtecnologia.erp.materiais.TabelaPrecoMaterial" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'tabelaPrecoMaterial.label', default: 'TabelaPrecoMaterial')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.materiais.TabelaPrecoMaterial"
                        params="${params}"
                        fields="${[[name: "material"], [name: "tabela"], [name: "valor"], [name: "data_inclusao"], [name: "data_ultima_alteracao"], [name: "empresa"]]}"/>

            <asg:grid controller="tabelaPrecoMaterial" list="${tabelaPrecoMaterialInstanceList}"
                      fields="material;tabela;valor;data_inclusao;data_ultima_alteracao;empresa"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>


        </div>
    </div>
</div></body>
</html>
