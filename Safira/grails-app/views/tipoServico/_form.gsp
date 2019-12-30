<%@ page import="com.br.asgardtecnologia.erp.servicos.TipoServico" %>


<div class="row">
    <asg:labeledTextField name="cod_tipo_servico" instance="${tipoServicoInstance}" maxlength="5"
                          value="${tipoServicoInstance?.cod_tipo_servico}"/>
</div>

<div class="row">
    <g:labeledTextArea name="descricao" instance="${tipoServicoInstance}" cols="40" rows="5" maxlength="500"
                       value="${tipoServicoInstance?.descricao}"/>
</div>

