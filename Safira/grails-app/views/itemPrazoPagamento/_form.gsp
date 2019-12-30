<%@ page import="com.br.asgardtecnologia.erp.cadastros.ItemPrazoPagamento" %>

<div class="row">
    <asg:labeledField name="prazo" instance="${itemPrazoPagamentoInstance}" type="number"
                      value="${itemPrazoPagamentoInstance?.prazo}" span="6"/>

    <asg:labeledField name="percentual" instance="${itemPrazoPagamentoInstance}"
                      value="${itemPrazoPagamentoInstance?.percentual}" span="6"/>
</div>