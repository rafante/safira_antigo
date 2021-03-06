<%@ page import="com.br.asgardtecnologia.erp.security.Usuario" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'usuario.label', default: 'Usuario')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body><div class="container-fluid">
    <div class="row">

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
                    <li>
                        <asg:link class="create" action="create">
                            <i class="icon icon-plus"></i>
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

            <g:hasErrors bean="${usuarioInstance}">
                <bootstrap:alert class="alert-error">
                    <ul>
                        <g:eachError bean="${usuarioInstance}" var="error">
                            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                    error="${error}"/></li>
                        </g:eachError>
                    </ul>
                </bootstrap:alert>
            </g:hasErrors>

            <fieldset>
                <g:form class="form-horizontal" action="changePassword">
                    <fieldset>
                        <div class="control-group fieldcontain ${hasErrors(bean: usuarioInstance, field: 'password', 'error')} ">
                            <label class="control-label" for="password">

                                <g:message code="usuario.password.label" default="Password"/>

                            </label>

                            <div class="controls">
                                <g:passwordField name="password"/>
                            </div>
                        </div>

                        <div class="control-group fieldcontain ${hasErrors(bean: usuarioInstance, field: 'password_confirm', 'error')} ">
                            <label class="control-label" for="password_confirm">

                                <g:message code="usuario.password_confirm.label" default="Confirmar senha"/>

                            </label>

                            <div class="controls">
                                <g:passwordField name="password_confirm"/>
                            </div>
                        </div>

                        <div class="form-actions">
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
