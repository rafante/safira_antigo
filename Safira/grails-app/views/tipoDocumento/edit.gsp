<%@ page import="com.br.asgardtecnologia.erp.cadastros.TipoDocumento" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'tipoDocumento.label', default: 'TipoDocumento')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
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

            <g:hasErrors bean="${tipoDocumentoInstance}">
                <bootstrap:alert class="alert-error">
                    <ul>
                        <g:eachError bean="${tipoDocumentoInstance}" var="error">
                            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                    error="${error}"/></li>
                        </g:eachError>
                    </ul>
                </bootstrap:alert>
            </g:hasErrors>

            <fieldset>
                <g:form class="form-horizontal" action="edit" id="${tipoDocumentoInstance?.id}">
                    <g:hiddenField name="version" value="${tipoDocumentoInstance?.version}"/>
                    <fieldset>
                        <g:render template="form" model="[tipoDocumentoInstance: tipoDocumentoInstance]"/>
                        <div class="form-actions form-buttons">
                            <button id="btnSalvar" type="submit" class="btn btn-primary">
                                <i class="icon icon-ok icon-white"></i>
                                <g:message code="default.button.save.label" default="Salvar"/>
                            </button>
                            <button type="submit" class="btn btn-danger" name="_action_delete" formnovalidate>
                                <i class="icon icon-trash icon-white"></i>
                                <g:message code="default.button.delete.label" default="Delete"/>
                            </button>
                        </div>
                    </fieldset>
                </g:form>
            </fieldset>

        </div>

    </div>
</div></body>
</html>
