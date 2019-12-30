<%@ page import="com.br.asgardtecnologia.erp.ev.ParametrosEV" %>
<div class="row">
    <asg:labeledTextField instance="${parametrosEVInstance}" name="examesEncoding" value="${parametrosEVInstance.examesEncoding}"/>
</div>

<div class="row">
    <asg:labeledTextField instance="${parametrosEVInstance}" name="caixaEVEncoding" value="${parametrosEVInstance.caixaEVEncoding}"/>
</div>

<div class="row">
    <asg:labeledTextField instance="${parametrosEVInstance}" name="nfeEncoding" value="${parametrosEVInstance.nfeEncoding}"/>
</div>

<div class="row">
    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.PlanoContas"
                             instance="${parametrosEVInstance}" id="planoContas" name="planoContas.id"
                             value="${parametrosEVInstance?.planoContas?.id}" />
</div>

<div class="row">
    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.ContaCorrente"
                             instance="${parametrosEVInstance}" id="contaCorrente" name="contaCorrente.id"
                             value="${parametrosEVInstance?.contaCorrente?.id}" />
</div>

<div class="row">
    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.cadastros.CentroCusto"
                             instance="${parametrosEVInstance}" id="centroCusto" name="centroCusto.id"
                             value="${parametrosEVInstance?.centroCusto?.id}" />
</div>