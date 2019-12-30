<%@ page import="com.br.asgardtecnologia.erp.base.Endereco" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'endereco.label', default: 'Endereco')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.base.Endereco"
                        params="${params}"
                        fields="${[[name: "logradouro"], [name: "numero"], [name: "complemento"], [name: "bairro"], [name: "municipio"], [name: "cep"]]}"/>

            <asg:grid controller="endereco" list="${enderecoInstanceList}"
                      fields="logradouro;numero;complemento;bairro;municipio;cep"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>

        </div>
    </div>
</div></body>
</html>
