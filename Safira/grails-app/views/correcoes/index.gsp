<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'correcoes.label', default: 'Correções')}"/>
    <title><g:message code="correcoes.label"/></title>
</head>

<body>
<div class="widget-content">
    <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
    </g:if>
    <g:form method="POST" action="corrigeCamposNulos">
        <div>
            <button class="btn btn-info">Correção de campos nulos</button>
        </div>
    </g:form>
    <g:form method="POST" action="correcaoColunas">
        <div>
            <button class="btn btn-info">Remoção de colunas não utilizadas</button>
        </div>
    </g:form>
    <g:form method="POST" controller="material" action="corrigirEstoqueMateriais">
        <div>
            <button class="btn btn-info">Corrige Estoque de Materiais</button>
        </div>
    </g:form>
</div>
</body>
</html>