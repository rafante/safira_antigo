<%@ page import="com.br.asgardtecnologia.erp.base.Estado" %>

<div class="row">
    <asg:labeledTextField name="sigla" instance="${estadoInstance}" maxlength="2" value="${estadoInstance?.sigla}"/>
</div>

<div class="row">
    <asg:labeledTextField name="nome" instance="${estadoInstance}" value="${estadoInstance?.nome}"/>
</div>
