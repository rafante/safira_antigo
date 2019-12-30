<%@ page import="com.br.asgardtecnologia.erp.financeiro.MovimentoFinanceiro" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'movimentoFinanceiro.label', default: 'MovimentoFinanceiro')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<div class="container-fluid">
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

            <g:hasErrors bean="${movimentoFinanceiro}">
                <bootstrap:alert class="alert-error">
                    <ul>
                        <g:eachError bean="${movimentoFinanceiro}" var="error">
                            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                    error="${error}"/></li>
                        </g:eachError>
                    </ul>
                </bootstrap:alert>
            </g:hasErrors>
            <g:if test="${!movimentoFinanceiroInstance?.estornado}">
                <div class="row">
                    <g:form action="estornar">
                        <asg:hiddenField name="id" value="${movimentoFinanceiroInstance?.id}"/>
                        <button class="btn btn-danger pull-right" id="modalEstornar"
                                onclick="confirmarEstorno()">Estornar<i
                                class="icon icon-share-alt"></i></button>
                        <button id="btnEstornar" type="submit" style="display: none"></button>
                    </g:form>

                    <r:script disposition="defer">
                        function confirmarEstorno() {
                            if (confirm("Confirma estorno?")) {
                                $("#btnEstornar").click();
                            }
                        }
                    </r:script>
                </div>
            </g:if>

            <fieldset>
                <g:form class="form-horizontal" action="show" id="${movimentoFinanceiro?.id}">
                    <g:hiddenField name="version" value="${movimentoFinanceiro?.version}"/>
                    <fieldset>
                        <g:render template="form" model="[movimentoFinanceiro: movimentoFinanceiro]"/>
                    </fieldset>
                </g:form>
            </fieldset>

        </div>

    </div>
</div>
</body>
</html>
