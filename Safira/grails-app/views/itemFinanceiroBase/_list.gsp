<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body><div class="container-fluid">
    <div class="row">

        <div class="col-12 col-sm-3" style="display:none">
        </div>

        <div id="page" class="col-12">

            <g:if test="${flash.message}">
                <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <asg:grid name="instanceList" controller="${params.controller}"
                      list="${instanceList?.sort({ a, b -> a.id?.compareTo(b.id) })}"
                      fields="dataVencimento[DATE];valor[MONEY];juros[MONEY];descontos[MONEY];valorCompensado[MONEY]"
                      itemButtons="show;edit;delete"
                      searchable="true"/>

        </div>
    </div>
</div></body>
</html>
