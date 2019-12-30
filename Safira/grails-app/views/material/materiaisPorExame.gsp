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

            <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.ev.Exame" instance="${itemExameInstance}"
                                     id="exame" name="exame.id" required="" value="${itemExameInstance?.exame?.id}"
                                     onchange="filterMateriais(this);" onblur="blurMateriais();"
                                     span="8"/>

            <asg:grid controller="material" list="${materialInstanceList}" id="materiais"
                      fields="id;descricao;finalidade[ENUM];grupo;sub_grupo" ignorePagination="${true}"
                      itemButtons="show"/>

        </div>
    </div>
</div>
<r:script>
    function filterMateriais(exame) {
        var data = {}
        data.exameId = $(exame).attr('value')
        $.ajax({
            type: 'POST',
            data: data,
            url: '/' + appName + '/material/materiaisPorExame',
            success: function (data) {
                $("#containermaterial").html(data);
                prepare();
            }
        })
    }
    function blurMateriais(){
        filterMateriais(null);
    }
</r:script>
</body>
</html>
