<%--
  Created by IntelliJ IDEA.
  User: rafan
  Date: 11/02/2019
  Time: 14:42
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: controllerName + '.label')}"/>
    <title>Gera arquivo remessa</title>

    <script>
        function gerar(){

            document.getElementById("formitemContaReceber").submit()
        }
    </script>
</head>

<body><div class="container-fluid">
    <div class="col-xs-12">
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
                <asg:filter params="${params}" gridName="instance" forcesubmit="${true}"
                            domain="${com.br.asgardtecnologia.erp.financeiro.ItemContaReceber.name}" buttons    ="[[onclick: 'gerar()', buttonIcon: 'icon-cog', buttonLabel: 'gerar']]"/>
                <g:form class="nav" controller="itemContaReceber" method="post" action="arquivoRemessa">

                %{--<div class="row">
                    <label for="files">${message(code: 'mensagens.importarXML')}</label>
                </div>
                <div class="row">
                    <asg:labeledTextField name="referencia" span="4"/>
                    <asg:labeledDatePicker name="dataVencimento" span="4" required=""/>
                </div>--}%
                    %{--<div class="row">
                        <input class="btn btn-info save" type="submit" span="6" value="Gerar"/>
                    </div>--}%
                </g:form>
            </fieldset>

        </div>

    </div>

</div></body>
</html>