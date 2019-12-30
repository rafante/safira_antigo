<%@ page import="com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'parceiroNegocios.label', default: 'parceiroNegocios')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios"
                        params="${params}"
                        fields="${[[name: "cnpj_cpf"], [name: "nome"], [name: "cliente"], [name: "fornecedor"], [name: "representanteVenda"], [name: "transportadora"]]}"/>

            <asg:grid controller="parceiroNegocios" list="${parceiroNegociosInstanceList}"
                      fields="id;cnpj_cpf;nome;cliente;fornecedor;representanteVenda;transportadora"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>


            <div>
                <g:jasperReport controller="ParceiroNegocios" action="cliente_analitico" jasper="clientes_analitico" format="PDF" name="Listagem de Clientes">
                </g:jasperReport>
            </div>

        </div>
    </div>
</div></body>
</html>
