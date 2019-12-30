<%@ page import="com.br.asgardtecnologia.erp.materiais.UnidadeMedida" %>

<div class="row">
    <asg:labeledTextField name="unidade" instance="${unidadeMedidaInstance}" maxlength="5"
                          value="${unidadeMedidaInstance?.unidade}"/>
</div>

<div class="row">
    <asg:labeledTextField name="descricao" instance="${unidadeMedidaInstance}" maxlength="60"
                          value="${unidadeMedidaInstance?.descricao}"/>
</div>

          