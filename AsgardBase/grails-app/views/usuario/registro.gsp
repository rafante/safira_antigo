<%@ page import="com.br.asgardtecnologia.erp.security.Usuario" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'usuario.label', default: 'Usuario')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body><div class="container-fluid">
    <div class="row">

        <div class="col-12 col-sm-3" style="display:none">
        </div>

        <div id="page" class="col-12">

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
                <g:form class="form-horizontal" action="registro" name="formregistro">
                    <fieldset>
                        <g:render template="form" model="[usuarioInstance: usuarioInstance]"/>

                        <!-- Início - ParceiroNegocios -->
                        <g:render template="/endereco/form"/>
                        <!-- Fim - ParceiroNegocios -->

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