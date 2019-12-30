<%@ page import="com.br.asgardtecnologia.erp.materiais.MovimentoMaterial" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'label', default: 'Saida')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
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

            <g:hasErrors bean="${movimentoMaterialInstance}">
                <bootstrap:alert class="alert-error">
                    <ul>
                        <g:eachError bean="${movimentoMaterialInstance}" var="error">
                            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                    error="${error}"/></li>
                        </g:eachError>
                    </ul>
                </bootstrap:alert>
            </g:hasErrors>

            <fieldset>
                <g:form class="form-horizontal" action="create">
                    <fieldset>
                        <div class="control-group fieldcontain ${hasErrors(bean: movimentoMaterialInstance, field: 'id', 'error')} ">
                            <label class="control-label" for="id">

                                <g:message code="movimentoMaterial.id.label" readonly="readonly" default="Id da Saida"/>

                            </label>

                            <div class="controls">
                                <asg:textField name="id" value="${movimentoMaterialInstance?.id}" readonly="readonly"/>
                            </div>
                        </div>

                        <div class="control-group fieldcontain ${hasErrors(bean: movimentoMaterialInstance, field: 'motivo', 'error')} ">
                            <label class="control-label" for="motivo">
                                <g:message code="movimentoMaterial.motivo.label" readonly="readonly" default="Motivo"/>
                            </label>

                            <div class="controls">
                                <asg:textField name="motivo" value="${movimentoMaterialInstance?.motivo}"/>
                            </div>
                        </div>

                        <div class="col-12 col-sm-12 form-actions">
                            <button id="btnSalvar" type="submit" class="btn btn-primary" name="_action_corrigir">
                                <i class="icon icon-ok icon-white"></i>
                                <g:message code="default.button.corrigir.label" default="Corrigir"/>
                            </button>
                        </div>
                    </fieldset>
                </g:form>
            </fieldset>

        </div>

    </div>
</div></body>
</html>