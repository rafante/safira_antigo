<%@ page import="com.br.asgardtecnologia.erp.base.Pais" %>

<div class="row">
    <asg:labeledTextField name="cod_pais" instance="${paisInstance}" maxlength="5" value="${paisInstance?.cod_pais}"/>
</div>

<div class="row">
    <asg:labeledTextField name="nome" instance="${paisInstance}" maxlength="100" value="${paisInstance?.nome}"/>
</div>

          