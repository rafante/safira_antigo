<%@ page import="com.br.asgardtecnologia.erp.materiais.ItemMovimentoMaterial" %>
<%@ page import="com.br.asgardtecnologia.erp.materiais.Finalidade" %>

<asg:hiddenField name="movimentoMaterial.id" instance="${itemMovimentoMaterialInstance}"
                 value="${itemMovimentoMaterialInstance?.movimentoMaterial?.id}"/>

<div class="row">
%{--    <asg:labeledField name="item" type="number" instance="${itemMovimentoMaterialInstance}" value="${itemMovimentoMaterialInstance.item}"--}%
%{--                      readonly="readonly" span="3"/>--}%

    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.materiais.Material" instance="${itemMovimentoMaterialInstance}"
                             id="material" name="material.id" required="" value="${itemMovimentoMaterialInstance?.material?.id}"
                             asg-chain-field="unidade_medidaId;estoqueDisponivel;custo_total"
                             asg-chain-target="unidade_medida;estoqueDisponivel;valor" span="6"/>
    <asg:labeledDecimal name="estoqueDisponivel" instance="${itemMovimentoMaterialInstance}" readonly="true"
                        value="${itemMovimentoMaterialInstance?.material?.estoqueDisponivel}" required="" span="6"/>
</div>

<div class="row">
    <asg:labeledField name="quantidade" instance="${itemMovimentoMaterialInstance}"
                      value="${fieldValue(bean: itemMovimentoMaterialInstance, field: 'quantidade')}" required="" span="4"/>

    <asg:labeledField name="valor" instance="${itemMovimentoMaterialInstance}"
                      value="${fieldValue(bean: itemMovimentoMaterialInstance, field: 'valor')}" required="" span="4"/>

    <asg:labeledSelect domain="com.br.asgardtecnologia.erp.materiais.UnidadeMedida"
                       instance="${itemMovimentoMaterialInstance}" id="unidade_medida" name="unidade_medida.id"
                       optionKey="id"
                       value="${itemMovimentoMaterialInstance?.unidade_medida?.id}" class="many-to-one"
                       noSelection="['null': '']" span="4"/>
</div>

<div class="row">
    <asg:labeledTextField name="lote" instance="${itemMovimentoMaterialInstance}" value="${itemMovimentoMaterialInstance.lote}"
                          span="6"/>
    <asg:labeledDatePicker name="validade_lote" instance="${itemMovimentoMaterialInstance}"
                           value="${itemMovimentoMaterialInstance?.validade_lote}" span="6"/>
</div>

<r:script disposition="defer">
    onDocumentReady(function () {
        $('#material\\.id').change(function () {
            clearAsgAutocompleteObjectValue('#materialLote');

            var newLoteFilter = "[material__id:" + $(this).val() + ", 'estoque.op': 'gt', 'estoque': 0]"
            $('#materialLote').attr("filter", newLoteFilter);
        });
    });
</r:script>
