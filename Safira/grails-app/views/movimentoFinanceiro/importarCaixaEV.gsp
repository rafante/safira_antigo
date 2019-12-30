<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: controllerName + '.label')}"/>
    <title>Importar XML</title>
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
                <g:form class="nav" controller="movimentoFinanceiro" method="post" action="importarCaixaEV" enctype="multipart/form-data">
                    <label for="files">${message(code: 'mensagens.importarXML')}</label>
                    <input type="file" name="files" id="files" multiple/>
                    <input class="save" type="submit" value="Importar"/>
                </g:form>
            </fieldset>

        </div>

    </div>
</div></body>
</html>
