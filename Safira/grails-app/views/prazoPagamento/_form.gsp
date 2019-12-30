<%@ page import="com.br.asgardtecnologia.erp.cadastros.PrazoPagamento" %>

<div class="row">
    <asg:labeledTextField name="descricao" instance="${prazoPagamentoInstance}" maxlength="60"
                          value="${prazoPagamentoInstance?.descricao}"/>
</div>

<div class="row">
    <asg:grid ignorePagination="${true}" controller="itemPrazoPagamento"
              list="${prazoPagamentoInstance?.itemPrazoPagamento}"
              fields="percentual;prazo" instance="${prazoPagamentoInstance}"
              headerButtons="create" itemButtons="show;edit;delete;erros" inlineMode="${true}"
              modelInstance="${new com.br.asgardtecnologia.erp.cadastros.ItemPrazoPagamento()}"/>
</div>