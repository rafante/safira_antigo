<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'asgardScript.label', default: 'AsgardScript')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body><div class="container-fluid">
    <div class="col-xs-12">

        <div id="page" class="col-12">
            <fieldset>
                <g:form class="form-horizontal" action="execute">
                    <fieldset>
                        <asg:textArea name="script" rows="30" class="asg-ignore-custom-enter"/>
                        <asg:submitButton name="btnExecute" value="Executar" />
                    </fieldset>
                </g:form>
            </fieldset>

        </div>

    </div>
</div></body>
</html>
