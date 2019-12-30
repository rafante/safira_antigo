<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="Testes"/>
    <title>Testes</title>
</head>

<body><div class="container-fluid">

    <div class="col-xs-12">

        <div class="col-12 col-sm-3" style="display:none">
        </div>

        <div id="page" class="col-12">
            <g:if test="${flash.message}">
                <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <g:form method="POST">
                <asg:textArea name="groovy" class="asg-ignore-custom-enter" value="${params.groovy}"
                              style="height: 300px"/>
                <asg:submitButton name="btnExecutar" value="Executar"/>
            </g:form>

            <br/>
            Resultado:
            <div>${result}</div>

            %{--<asg:filter domain="com.br.asgardtecnologia.erp.vendas.Pedido"--}%
            %{--params="${params}"--}%
            %{--fields="${[[name: "status_pedido"], [name: "data_faturamento"], [name: "data_pedido"], [name: "cliente"], [name: "representanteVenda"], [name: "comissao"]]}"/>--}%

            %{--<asg:grid controller="pedido" list="${pedidoInstanceList}"--}%
            %{--fields="status_pedido[ENUM];data_faturamento[DATE];data_pedido[DATE];cliente;representanteVenda;comissao"--}%
            %{--headerButtons="create" itemButtons="show;edit;delete;erros"/>--}%

        </div>
    </div>
</div></body>
</html>
