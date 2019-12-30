<%@ page import="com.br.asgardtecnologia.erp.cadastros.FormaPagamento" %>


<div class="row">
    <asg:labeledTextField name="descricao" instance="${formaPagamentoInstance}" maxlength="60"
                          value="${formaPagamentoInstance?.descricao}"/>
</div>

<div class="row">
    <asg:labeledCheckBox name="controle_cheque" instance="${formaPagamentoInstance}"
                         value="${formaPagamentoInstance?.controle_cheque}"/>
</div>