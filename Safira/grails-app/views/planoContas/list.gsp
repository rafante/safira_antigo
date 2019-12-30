<%@ page import="com.br.asgardtecnologia.erp.financeiro.PlanoContas" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'planoContas.label', default: 'PlanoContas')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.financeiro.PlanoContas"
                        params="${params}"
                        fields="${[[name: "codigo"], [name: "descricao"], [name: "natureza"], [name: "data_inclusao"], [name: "data_ultima_alteracao"]]}"/>

            <asg:grid controller="planoContas" list="${planoContasInstanceList}"
                      fields="codigo;descricao;natureza;data_inclusao;data_ultima_alteracao"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>


        </div>
    </div>
</div></body>
</html>
