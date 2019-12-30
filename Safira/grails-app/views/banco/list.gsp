<%@ page import="com.br.asgardtecnologia.erp.financeiro.Banco" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'banco.label', default: 'Banco')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.financeiro.Banco"
                        params="${params}"
                        fields="${[[name: "descricao"]]}"/>

            <asg:grid controller="banco" list="${bancoInstanceList}"
                      fields="id;descricao;controle_cheque"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>


        </div>
    </div>
</div></body>
</html>
