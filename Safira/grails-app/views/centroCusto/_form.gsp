<%@ page import="com.br.asgardtecnologia.erp.cadastros.CentroCusto" %>

<div class="row">
    <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.CentroCusto" instance="${centroCustoInstance}"
                       id="centroCusto" name="centroCusto.id"
                       optionKey="id"
                       value="${centroCustoInstance?.centroCusto?.id}" class="many-to-one" noSelection="['': '']"/>
</div>

<div class="row">
    <asg:labeledTextField name="codigo" instance="${centroCustoInstance}" value="${centroCustoInstance?.codigo}"/>
</div>

<div class="row">
    <asg:labeledTextField name="descricao" instance="${centroCustoInstance}" maxlength="60"
                          value="${centroCustoInstance?.descricao}"/>
</div>

<div class="row">
    <asg:grid ignorePagination="${true}" controller="centroCusto" list="${centroCustoInstance?.filhos}"
              fields="codigo;descricao" instance="${centroCustoInstance}"
              headerButtons="create" itemButtons="show;edit;delete;erros"/>
</div>