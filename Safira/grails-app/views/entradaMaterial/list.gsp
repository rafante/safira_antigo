<%@ page import="com.br.asgardtecnologia.erp.materiais.EntradaMaterial" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'entradaMaterial.label', default: 'EntradaMaterial')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.materiais.EntradaMaterial"
                        params="${params}"
                        fields="${[[name: "status"], [name: "fornecedor"], [name: "data_entrada"], [name: "data_pedido_compra"], [name: "documento"], [name: "itemEntrada.material.ean"]]}"/>

            <asg:grid controller="entradaMaterial" list="${entradaMaterialInstanceList}"
                      fields="id;status[ENUM];documento;data_entrada[DATE];data_pedido_compra[DATE];fornecedor"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>

        </div>
    </div>
</div></body>
</html>
