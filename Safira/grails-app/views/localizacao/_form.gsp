<%@ page import="com.br.asgardtecnologia.erp.materiais.Localizacao" %>


<div class="row">
    <asg:labeledTextField name="descricao" instance="${localizacaoInstance}" maxlength="60"
                          value="${localizacaoInstance?.descricao}"/>
</div>

          