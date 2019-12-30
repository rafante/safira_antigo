<%@ page import="com.br.asgardtecnologia.erp.servicos.TipoServico" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'tipoServico.label', default: 'TipoServico')}"/>
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

            <asg:filter domain="com.br.asgardtecnologia.erp.servicos.TipoServico"
                        params="${params}"
                        fields="${[[name: "cod_tipo_servico"], [name: "descricao"], [name: "data_inclusao"], [name: "data_ultima_alteracao"], [name: "usuario"], [name: "empresa"]]}"/>

            <asg:grid controller="tipoServico" list="${tipoServicoInstanceList}"
                      fields="cod_tipo_servico;descricao;data_inclusao;data_ultima_alteracao;usuario;empresa"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>


        </div>
    </div>
</div></body>
</html>
