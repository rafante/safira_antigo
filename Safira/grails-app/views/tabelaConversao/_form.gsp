<%@ page import="com.br.asgardtecnologia.erp.materiais.TabelaConversao" %>

<div class="row">
    <asg:hiddenField name="material.id" value="${tabelaConversaoInstance?.material?.id}"/>
    <asg:labeledAutoComplete id="material" name="material.id" instance="${tabelaConversaoInstance}"
                             valuefield="descricao"
                             listfields="id;descricao"
                             domain="com.br.asgardtecnologia.erp.materiais.Material"
                             value="${tabelaConversaoInstance?.material?.id}" span="6"
                             forceshowmode="true"></asg:labeledAutoComplete>
</div>

<div class="row">
    <asg:labeledSelect domain="com.br.asgardtecnologia.erp.materiais.UnidadeMedida"
                       instance="${tabelaConversaoInstance}" id="unidade_medida" name="unidade_medida.id"
                       optionKey="id"
                       value="${tabelaConversaoInstance?.unidade_medida?.id}" class="many-to-one"
                       noSelection="['null': '']"/>
</div>

<div class="row">
    <asg:labeledField name="quantidade" instance="${tabelaConversaoInstance}"
                      value="${fieldValue(bean: tabelaConversaoInstance, field: 'quantidade')}"/>
</div>
