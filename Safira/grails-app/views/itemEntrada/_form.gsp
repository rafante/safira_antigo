<%@ page import="com.br.asgardtecnologia.erp.materiais.ItemEntrada" %>
<%@ page import="com.br.asgardtecnologia.erp.materiais.Finalidade" %>

<asg:hiddenField name="entradaMaterial.id" instance="${itemEntradaInstance}"
                 value="${itemEntradaInstance?.entradaMaterial?.id}"/>

<div class="row">
    <asg:labeledField name="item" type="number" instance="${itemEntradaInstance}" value="${itemEntradaInstance.item}"
                      readonly="readonly" span="4"/>

    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.materiais.Material" instance="${itemEntradaInstance}"
                             id="material" name="material.id" required="" value="${itemEntradaInstance?.material?.id}"
                             asg-chain-field="unidadesMedidaPossiveisUI;controleLoteValidade;lotesPossiveisUI;valorEstoque"
                             asg-chain-target="unidade_medidaList;controleLoteValidade;loteList;valor"
                             span="8"/>

    <div style="display:none"><g:checkBox name="controleLoteValidade" value="" asg-hide-if-false="box_lote"/></div>
    <div style="display:none"><g:checkBox name="controleLoteValidade" value="" asg-hide-if-false="box_lote"
                                          checked="${itemEntradaInstance?.material?.controleLoteValidade ? 'true' : 'false'}" /></div>
</div>

<!-- Lote -->
<div id="box_lote" class="row">
    <asg:labeledTextField name="lote" instance="${itemEntradaInstance}" value="${itemEntradaInstance.lote}"
                          span="6"/>
    <asg:labeledDatePicker id="validade_lote" name="validade_lote" instance="${itemEntradaInstance}"
                           value="${itemEntradaInstance?.validade_lote}" span="6"/>
</div>

<div class="row">
    <asg:labeledDecimal name="quantidade" instance="${itemEntradaInstance}"
                        value="${fieldValue(bean: itemEntradaInstance, field: 'quantidade')}" required="" span="4"/>
    <asg:labeledSelect domain="com.br.asgardtecnologia.erp.materiais.UnidadeMedida"
                       instance="${itemEntradaInstance}" id="unidade_medida" name="unidade_medida.id"
                       from="${itemEntradaInstance?.material?.getUnidadesMedidaPossiveis()}"
                       optionKey="id" value="${itemEntradaInstance?.unidade_medida?.id}" class="many-to-one"
                       noSelection="['null': '']" span="4"/>
    <asg:labeledMoney name="valor" instance="${itemEntradaInstance}"
                      value="${itemEntradaInstance?.valor}" span="4"/>
</div>

<div class="row">
    <asg:labeledMoney name="icms" instance="${itemEntradaInstance}"
                      value="${itemEntradaInstance?.icms}" span="3"/>

    <asg:labeledMoney name="ipi" instance="${itemEntradaInstance}"
                      value="${itemEntradaInstance?.ipi}" span="3"/>

    <asg:labeledMoney name="pis" instance="${itemEntradaInstance}"
                      value="${itemEntradaInstance?.pis}" span="3"/>

    <asg:labeledMoney name="cofins" instance="${itemEntradaInstance}"
                      value="${itemEntradaInstance?.cofins}" span="3"/>

    <asg:labeledMoney name="descontos" instance="${itemEntradaInstance}"
                      value="${itemEntradaInstance?.descontos}" span="3"/>
</div>

<div class="row">
    <asg:labeledMoney name="totalLiquido" instance="${itemEntradaInstance}"
                      value="${itemEntradaInstance?.totalLiquido}" readonly="readonly" span="4"/>

    <asg:labeledMoney name="totalImpostos" instance="${itemEntradaInstance}"
                      value="${itemEntradaInstance?.totalImpostos}" readonly="readonly" span="4"/>

    <asg:labeledMoney name="totalBruto" instance="${itemEntradaInstance}"
                      value="${itemEntradaInstance?.totalBruto}" readonly="readonly" span="4"/>
</div>
