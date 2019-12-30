<%@ page import="com.br.asgardtecnologia.erp.materiais.DescricaoFornecedor" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'descricaoFornecedor.label', default: 'DescricaoFornecedor')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.materiais.DescricaoFornecedor"
                        params="${params}"
                        fields="${[[name: "material"], [name: "fornecedor"], [name: "codigo"], [name: "descricao"], [name: "data_inclusao"], [name: "data_ultima_alteracao"]]}"/>

            <asg:grid controller="descricaoFornecedor" list="${descricaoFornecedorInstanceList}"
                      fields="material;fornecedor;codigo;descricao;data_inclusao;data_ultima_alteracao"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>

        </div>
    </div>
</div></body>
</html>