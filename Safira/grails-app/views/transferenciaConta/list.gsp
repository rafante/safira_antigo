<%@ page import="com.br.asgardtecnologia.erp.financeiro.TransferenciaConta" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'transferenciaConta.label', default: 'TransferenciaConta')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.financeiro.TransferenciaConta"
                        params="${params}"
                        fields="${[[name: "id"], [name: "contaCorrenteOrigem"], [name: "contaCorrenteDestino"], [name: "valor"], [name: "data"]]}"/>

            <asg:grid controller="transferenciaConta" list="${transferenciaContaList}"
                      fields="data[DATE];valor[MONEY];contaCorrenteOrigem.descricao[STRING];contaCorrenteDestino.descricao[STRING];"
                      itemButtons="show;"/>


        </div>
    </div>
</div></body>
</html>
