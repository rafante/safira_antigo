<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
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

            <g:hasErrors bean="${instance}">
                <bootstrap:alert class="alert-error">
                    <ul>
                        <g:eachError bean="${instance}" var="error">
                            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                    error="${error}"/></li>
                        </g:eachError>
                    </ul>
                </bootstrap:alert>
            </g:hasErrors>

            <fieldset>
                <g:form class="form-horizontal" action="edit" id="${instance?.id}">
                    <g:hiddenField name="version" value="${instance?.version}"/>
                    <fieldset>
                        <g:render template="/itemFinanceiroBase/form" model="[instance: instance]"/>
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

            <g:render template="/itemFinanceiroBase/parcelas_modals" model="[item: instance, entity: entity]"/>

        </div>

    </div>
</div></body>
</html>
