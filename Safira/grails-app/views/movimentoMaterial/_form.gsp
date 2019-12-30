<%@ page import="com.br.asgardtecnologia.erp.materiais.MovimentoMaterial" %>
<div class="widget-box">
    <div class="widget-title">
        <span class="icon">
            <i class="icon icon-th-list"></i>
        </span>
        <h5><g:message code="movimentoMaterial.cabecalho" default="Cabeçalho"/></h5>
    </div>

    <div class="widget-content">

        <div class="row">
            <asg:labeledLinearStatus name="status" instance="${movimentoMaterialInstance}"/>
        </div>

        <div class="row">
            <asg:labeledDatePicker name="data_movimento" instance="${movimentoMaterialInstance}"
                                   value="${movimentoMaterialInstance?.data_movimento}" default="none"
                                   span="6"/>
        </div>

        <div class="row">

            <asg:labeledMoney name="total" instance="${movimentoMaterialInstance}"
                              value="${movimentoMaterialInstance?.total}" readonly="readonly" span="6"/>

            <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.cadastros.CentroCusto" instance="${movimentoMaterialInstance}"
                                     id="centroCusto" filter='["temFilhos": false]'
                                     name="centroCusto.id" required="" value="${movimentoMaterialInstance?.centroCusto?.id}"
                                     span="6"/>
        </div>

        <div class="row">
            <asg:labeledTextArea instance="${movimentoMaterialInstance}" name="motivo"
                                 value="${movimentoMaterialInstance?.motivo}"
                                 span="12"/>
        </div>

        <div class="row">
            <asg:labeledSelect name="entrada_saida" instance="${movimentoMaterialInstance}"
                               from="${com.br.asgardtecnologia.erp.materiais.TipoMovimentoMaterial?.values()}"
                               keys="${com.br.asgardtecnologia.erp.materiais.TipoMovimentoMaterial?.values()*.name()}"
                               value="${movimentoMaterialInstance?.entrada_saida?.name()}"
                               noSelection="['': '']" span="6"/>
        </div>

    </div>

    <div class="row">
            <asg:grid ignorePagination="${true}" name="itemMovimentoMaterial" controller="itemMovimentoMaterial"
                      list="${movimentoMaterialInstance?.itemMovimentoMaterial?.sort({ a, b -> a.id?.compareTo(b.id) })}"
                      fields="item;material;material.estoqueDisponivel[QUANTITY];quantidade[QUANTITY];valor[MONEY]"
                  headerButtons="create"
                  itemButtons="show;edit;delete"/>
    </div>
</div>