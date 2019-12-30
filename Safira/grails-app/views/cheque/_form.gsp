<%@ page import="com.br.asgardtecnologia.erp.financeiro.Cheque" %>


<div class="row">
    <asg:labeledTextField name="numero" instance="${chequeInstance}" maxlength="60"
                          value="${chequeInstance?.numero}"/>
</div>

<div class="row">
    <asg:labeledAutoComplete
            domain="com.br.asgardtecnologia.erp.financeiro.ContaCorrente"
            orderBy="id"
            instance="${chequeInstance}"
            id="contaCorrente"
            name="contaCorrente.id"
            value="${chequeInstance?.contaCorrente?.id}"
            label="${message(code: 'contaCorrente.label')}"
            optionKey="id"
            noSelection="['null': '']"
            span="4"
            required="true"
            ignoreGeneralAttrsAdjusts="true"/>
</div>

<div class="row">
    <asg:labeledMoney name="valor" value="${chequeInstance?.valor}" instance="${chequeInstance}" span="4"/>
</div>