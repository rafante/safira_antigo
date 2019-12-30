<%@ page import="com.br.asgardtecnologia.erp.financeiro.PlanoContas" %>

<div class="row">
    <asg:labeledSelect domain="com.br.asgardtecnologia.erp.financeiro.PlanoContas" instance="${planoContasInstance}"
                       id="planoContas" name="planoContas.id"
                       optionKey="id"
                       value="${planoContasInstance?.planoContas?.id}" class="many-to-one" noSelection="['': '']"/>
</div>

<div class="row">
    <asg:labeledTextField name="codigo" instance="${planoContasInstance}" maxlength="60"
                          value="${planoContasInstance?.codigo}" span="6"/>
</div>

<div class="row">
    <asg:labeledTextField name="descricao" instance="${planoContasInstance}" maxlength="60"
                          value="${planoContasInstance?.descricao}"/>
    <asg:labeledSelect name="natureza" instance="${planoContasInstance}"
                       from="${com.br.asgardtecnologia.erp.financeiro.Natureza?.values()}"
                       keys="${com.br.asgardtecnologia.erp.financeiro.Natureza?.values()*.name()}"
                       value="${planoContasInstance?.natureza?.name()}" span="6"/>
</div>

<div class="row">
    <asg:grid ignorePagination="${true}" controller="planoContas" list="${planoContasInstance?.filhos}"
              fields="codigo;descricao" instance="${planoContasInstance}"
              headerButtons="create" itemButtons="show;edit;delete;erros"/>
</div>