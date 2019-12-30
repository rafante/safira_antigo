<%@ page import="com.br.asgardtecnologia.erp.materiais.LDM" %>

<div class="row">
    <asg:hiddenField name="material.id" value="${LDMInstance?.material?.id}"/>
    <asg:labeledAutoComplete id="material" name="material.id" instance="${LDMInstance}" valuefield="descricao"
                             listfields="id;descricao"
                             domain="com.br.asgardtecnologia.erp.materiais.Material"
                             value="${LDMInstance?.material?.id}"
                             forceshowmode="true"></asg:labeledAutoComplete>
</div>

<div class="row">
    <asg:labeledAutoComplete id="material_composicao" name="material_composicao.id" instance="${LDMInstance}"
                             valuefield="descricao"
                             listfields="id;descricao"
                             domain="com.br.asgardtecnologia.erp.materiais.Material"
                             filter="[finalidade: ['\'MATERIA_PRIMA\'', '\'PRODUTO_INTERMEDIARIO\'']]"
                             filterLogicalOperand="or"
                             value="${LDMInstance?.material_composicao?.id}"></asg:labeledAutoComplete>
</div>

<div class="row">
    <asg:labeledDecimal name="quantidade" value="${LDMInstance.quantidade}" instance="${LDMInstance}" required=""/>
</div>

<asg:hiddenField name="id" value="${LDMInstance.id}" instance="${LDMInstance}"/>