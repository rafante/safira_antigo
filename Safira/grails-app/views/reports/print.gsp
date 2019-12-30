<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: params.reportController + '.' + params.reportAction + '.label')}"/>
    <title><g:message code="default.report.label" args="[entityName]"/></title>
</head>


<body><div class="container-fluid">
    <div class="col-xs-12">

        <div class="col-12 col-sm-3" style="display:none">
        </div>

        <div id="page" class="col-12">

            <g:if test="${flash.message}">
                <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <asg:filter domain="${params.domain}" params="${params}" forcesubmit="${true}" target="_blank"
                        endOfFilter="${asg.hiddenField([name: 'extension', value: 'PDF'])}"
                        buttons="[
                                [buttonLabel: message(code: 'default.button.print.label'), buttonIcon: 'icon-print',
                                        onclick: '\$(\'#extension\').val(\'PDF\');'],
                                [buttonLabel: message(code: 'default.action.exportarXLS.label'), buttonIcon: 'icon-file-text',
                                        onclick: '\$(\'#extension\').val(\'XLS\');'],

                        ]"/>

        </div>
    </div>
</div></body>
</html>