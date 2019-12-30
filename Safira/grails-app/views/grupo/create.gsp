<%@ page import="com.br.asgardtecnologia.erp.cadastros.Grupo" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'grupo.label', default: 'Grupo')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body><div class="container-fluid">
    <div class="col-xs-12">

        <div class="col-12 col-sm-3" style="display:none">
            <div class="well">
                <ul class="nav nav-list">
                    <li class="nav-header">${entityName}</li>
                    <li>
                        <asg:link class="list" action="list">
                            <i class="icon icon-list"></i>
                            <g:message code="default.list.label" args="[entityName]"/>
                        </asg:link>
                    </li>
                    <li class="active">
                        <asg:link class="create" action="create">
                            <i class="icon icon-plus icon-white"></i>
                            <g:message code="default.create.label" args="[entityName]"/>
                        </asg:link>
                    </li>
                </ul>
            </div>
        </div>

        <div id="page" class="col-12">

            <g:if test="${flash.message}">
                <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <g:hasErrors bean="${grupoInstance}">
                <bootstrap:alert class="alert-error">
                    <ul>
                        <g:eachError bean="${grupoInstance}" var="error">
                            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                    error="${error}"/></li>
                        </g:eachError>
                    </ul>
                </bootstrap:alert>
            </g:hasErrors>

            <fieldset>
                <g:form class="form-horizontal" action="create">
                    <fieldset>
                        <g:render template="form" model="[grupoInstance: grupoInstance]"/>
                        <div class="form-actions form-buttons">
                            <button id="btnSalvar" type="submit" class="btn btn-primary">
                                <i class="icon icon-ok icon-white"></i>
                                <g:message code="default.button.save.label" default="Salvar"/>
                            </button>
                        </div>
                    </fieldset>
                </g:form>
            </fieldset>

        </div>

    </div>
</div></body>
</html>
