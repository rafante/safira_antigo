<%@ page import="com.br.asgardtecnologia.erp.materiais.Material" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'material.label', default: 'Material')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body><div class="container-fluid">

    <div class="col-xs-12">

        <div class="col-12 col-sm-3" style="display:none">
        </div>

        <div id="page" class="col-12">

            <g:if test="${flash.message}">
                <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <div class="col-md-push-7 well">
                <form controller="material" action="importer" method="post" enctype="multipart/form-data">
                    <asg:label id="mapping" name="mapping"/>
                    <fieldset>
                        <div class="row">
                            <asg:labeledField name="page" span="4" type="number"/>

                        </div>
                        <div class="row">
                            <asg:labeledField name="descricao" span="4"/>
                            <asg:labeledField name="grupo" span="4"/>
                            <asg:labeledField name="sub_grupo" span="4"/>
                        </div>
                        <div class="row">
                            <asg:labeledField name="descricao" span="4"/>
                        </div>

                    </fieldset>

                    <div class="row">
                        <label for="files">${message(code: 'material.importer.choosefile')}</label>
                        <input type="file" name="files" id="files" multiple/>
                    </div>
                    <button id="btnSalvar" type="submit"class="btn btn-info">
                        <i class="icon icon-ok icon-white"></i>
                        <g:message code="material.importer.execute.label" default="Salvar"/>
                    </button>
                </form>

            </div>

        </div>
    </div>
</div></body>
</html>
