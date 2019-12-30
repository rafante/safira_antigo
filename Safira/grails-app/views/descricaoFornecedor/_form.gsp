<%@ page import="com.br.asgardtecnologia.erp.materiais.DescricaoFornecedor" %>
<div class="row">
    <asg:hiddenField name="material.id" value="${descricaoFornecedorInstance?.material?.id}"/>
    <asg:labeledAutoComplete id="material" name="material.id" instance="${descricaoFornecedorInstance}"
                             valuefield="descricao"
                             listfields="id;descricao"
                             domain="com.br.asgardtecnologia.erp.materiais.Material"
                             value="${descricaoFornecedorInstance?.material?.id}" span="6"
                             forceshowmode="true"></asg:labeledAutoComplete>
</div>

<div class="row">
    <asg:labeledAutoComplete id="fornecedor" name="fornecedor.id" instance="${descricaoFornecedorInstance}"
                             valuefield="nome"
                             listfields="id;nome"
                             domain="com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios"
                             filter='[fornecedor:"true"]'
                             value="${descricaoFornecedorInstance?.fornecedor?.id}"></asg:labeledAutoComplete>
</div>

<div class="row">
    <asg:labeledField name="codigo" instance="${descricaoFornecedorInstance}"
                      value="${descricaoFornecedorInstance?.codigo}"/>
</div>

<div class="row">
    <asg:labeledTextField name="descricao" instance="${descricaoFornecedorInstance}"
                          value="${descricaoFornecedorInstance?.descricao}"/>
</div>