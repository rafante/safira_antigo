<%@ page import="com.br.asgardtecnologia.erp.materiais.TabelaConversao" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'tabelaConversao.label', default: 'TabelaConversao')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.materiais.TabelaConversao"
                        params="${params}"
                        fields="${[[name: "material"], [name: "unidade_medida"], [name: "quantidade"], [name: "data_inclusao"], [name: "data_ultima_alteracao"], [name: "empresa"]]}"/>

            <asg:grid controller="tabelaConversao" list="${tabelaConversaoInstanceList}"
                      fields="material;unidade_medida;quantidade;data_inclusao;data_ultima_alteracao;empresa"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>


        </div>
    </div>
</div></body>
</html>
