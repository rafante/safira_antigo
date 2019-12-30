<%@ page import="com.br.asgardtecnologia.erp.financeiro.ContaCorrente" %>
<div class="row">
    <asg:labeledAutoComplete
            domain="com.br.asgardtecnologia.erp.financeiro.Banco"
            orderBy="codigo"
            instance="${contaBancariaInstance}"
            id="banco"
            name="banco.id"
            value="${contaBancariaInstance?.banco?.id}"
            label="${message(code: 'banco.label')}"
            optionKey="id"
            noSelection="['null': '']"
            span="6"
            required="true"
            ignoreGeneralAttrsAdjusts="true"/>
</div>

<div class="row">
    <asg:labeledTextField name="agencia" instance="${contaBancariaInstance}" maxlength="60"
                          value="${contaBancariaInstance?.agencia}"/>
</div>

<div class="row">
    <asg:labeledTextField name="conta" instance="${contaBancariaInstance}" maxlength="60"
                          value="${contaBancariaInstance?.conta}"/>
</div>