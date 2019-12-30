<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <title>Dashboard</title>
</head>

<body>
<div class="widget-box">
    <div class="widget-title">
    </div>

    <div class="widget-content">
        <div class="row">
            <asg:widgetBox span="5" title="teste" icon="">
                <asg:widget template="/widgets/contaPagarVencendoHoje"/>
            </asg:widgetBox>
        </div>
    </div>
%{--<div class="container-fluid">
    <div align="center">
        <h1>Dashboard</h1>
    </div>
    <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
    </g:if>


        --}%%{--<div class="row">
            <g:each in="${widgetList}" var="widget">
                <sec:ifAnyGranted roles="${"ROLE_ADMIN, ROLE_" + widget.name}">
                    <asg:widgetBox span="${widget.span}" title="${widget.title}" icon="${widget.icon}">
                        <asg:widget template="${widget.template}"/>
                    </asg:widgetBox>
                </sec:ifAnyGranted>
            </g:each>
        </div>--}%%{--
    </div>--}%
</body>
</html>