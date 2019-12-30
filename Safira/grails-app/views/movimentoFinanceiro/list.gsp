<%@ page import="com.br.asgardtecnologia.erp.financeiro.MovimentoFinanceiro" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'movimentoFinanceiro.label', default: 'MovimentoFinanceiro')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<div class="container-fluid">
    <div class="col-xs-12">

        <div class="col-12 col-sm-3" style="display:none">
        </div>

        <div id="page" class="col-12">

            <g:if test="${flash.message}">
                <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <asg:filter domain="com.br.asgardtecnologia.erp.financeiro.MovimentoFinanceiro"
                        params="${params}"
                        fields="${[
                                [name: "dataDocumento"], [name: "dataEmissao"], [name: "debito_credito"], [name: "numeroDocumento"]
                        ]}"/>

            <asg:grid controller="movimentoFinanceiro" list="${movimentoFinanceiroList}"
                      fields="dataDocumento[DATE];numeroDocumento[STRING];descricao[STRING];valor[MONEY]"
                      itemButtons="show;edit;delete;"
                      customButtons="[['href': '#modalEstornar%id%', 'data-toggle': 'modal', 'class': 'btn-default', 'icon': 'icon-reply', 'label': 'compensacaoItem' + entity + '.estornar.label']]"/>

        </div>
    </div>
</div>
<g:each in="${movimentoFinanceiroList}" var="item">
    <asg:confirmModalForm name="modalEstornar${item.id}" action="estornar"
                          title="${g.message(code: "compensacaoItem${entity}.estornar.label", default: "Estornar")}"
                          hideConfirm="${item.estornado}">
        <fieldset>
            <g:if test="${!item.estornado}">
                <asg:labeledTextArea
                        name="justificativaEstorno"
                        forceeditmode="true"
                        span="12" required="true"/>
                <asg:hiddenField name="id" value="${item.id}"/>
                %{--<asg:hiddenField name="itemConta${entity}.id" value="${item."itemConta${entity}".id}"/>--}%
            </g:if>
            <g:else>
                ${message(code: "message.already.refunded")}
            </g:else>
        </fieldset>
    </asg:confirmModalForm>
</g:each>
</body>
</html>
