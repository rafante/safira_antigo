<%@ page import="com.br.asgardtecnologia.erp.financeiro.Banco" %>


<div class="row">
    <asg:labeledTextField name="descricao" instance="${bancoInstance}" maxlength="60"
                          value="${bancoInstance?.descricao}"/>
</div>

<div class="row">
    <asg:labeledTextField name="codigo" instance="${bancoInstance}"
                         value="${bancoInstance?.codigo}"/>
</div>