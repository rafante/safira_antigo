<%@ page import="com.br.asgardtecnologia.erp.materiais.TipoMaterial; com.br.asgardtecnologia.erp.materiais.Material" %>

<div class="widget-box">
    <div class="widget-title">
        <ul class="nav nav-tabs">
            <li class="active"><a data-toggle="tab" href="#tab_dados_basicos"><g:message
                    code="material.tab_dados_basicos.label" default="Dados Básicos"/></a></li>
            <li><a id="tab_h_estoque" data-toggle="tab" href="#tab_estoque"><g:message code="material.tab_estoque.label"
                                                                                       default="Estoque"/></a></li>
            <li><a id="tab_h_lote" data-toggle="tab" href="#tab_lote"><g:message code="material.tab_lote.label"
                                                                                 default="Lotes"/></a>
            </li>
            <li><a id="tab_h_custo" data-toggle="tab" href="#tab_custo"><g:message code="material.tab_custo.label"
                                                                                   default="Histórico de Custos"/></a>
            </li>
            <li><a id="tab_h_composicao" data-toggle="tab" href="#tab_composicao"><g:message
                    code="material.tab_composicao.label" default="Composição"/></a></li>
            <li><a id="tab_h_precos" data-toggle="tab" href="#tab_precos"><g:message code="material.tab_precos.label"
                                                                                     default="Preços"/></a></li>
            <li><a id="tab_h_desc_forn" data-toggle="tab" href="#tab_desc_forn"><g:message
                    code="material.tab_desc_forn.label" default="Descrições de Fornecedor"/></a></li>
            <li><a id="tab_h_conv_unid" data-toggle="tab" href="#tab_conv_unid"><g:message
                    code="material.tab_conv_unid.label" default="Conversão de Unidade"/></a></li>
            <li><a id="tab_h_obs" data-toggle="tab" href="#tab_obs"><g:message code="material.tab_obs.label"
                                                                               default="Observações"/></a></li>
        </ul>
    </div>

    <div class="widget-content tab-content">
        <div id="tab_dados_basicos" class="tab-pane active">
            <div class="row"></div>
            <div class="row">
                <asg:labeledTextField name="descricao" instance="${materialInstance}" maxlength="120" span="12"/>
            </div>

            <div class="row">
                <asg:labeledSelect name="tipo_material" instance="${materialInstance}"
                                   from="${com.br.asgardtecnologia.erp.materiais.TipoMaterial?.values()}"
                                   keys="${com.br.asgardtecnologia.erp.materiais.TipoMaterial?.values()*.name()}"
                                   value="${materialInstance?.tipo_material?.name()}"
                                   onChange="tipoMaterialChange();"
                                   noSelection="['': '']" span="6"/>
                <asg:labeledCheckBox name="controleLoteValidade" value="${materialInstance.controleLoteValidade}"
                                     span="6"/>

            </div>

            <div class="row">

                <asg:labeledSelect name="finalidade" instance="${materialInstance}"
                                   from="${com.br.asgardtecnologia.erp.materiais.Finalidade?.values()}"
                                   keys="${com.br.asgardtecnologia.erp.materiais.Finalidade.values()*.name()}"
                                   value="${materialInstance?.finalidade?.name()}" noSelection="['': '']" span="6"/>
                <asg:labeledCheckBox name="vendido_separadamente" value="${materialInstance.vendido_separadamente}"
                                     span="6"/>
            </div>

            <div class="row">
                <asg:labeledTextField name="apelido" instance="${materialInstance}" maxlength="100" span="12"/>
            </div>

            <div class="row">
                <asg:labeledSelect domain="com.br.asgardtecnologia.erp.materiais.UnidadeMedida" id="unidade_medida"
                                   name="unidade_medida.id" instance="${materialInstance}"
                                   optionKey="id"
                                   class="many-to-one" value="${materialInstance?.unidade_medida?.id}"
                                   noSelection="['': '']" span="6"/>
                <asg:labeledAutoComplete id="ncm" name="ncm.id" instance="${materialInstance}" valuefield="codigo"
                                         listfields="codigo;descricao"
                                         domain="com.br.asgardtecnologia.erp.materiais.NCM"
                                         value="${materialInstance?.ncm?.id}" span="6"></asg:labeledAutoComplete>
            </div>

            <div class="row">

                <asg:labeledTextField name="ean" instance="${materialInstance}" maxlength="20" span="6"/>
                <asg:labeledMoney name="custo_total" instance="${materialInstance}" decimals="5" span="6"/>
            </div>

            <div class="row">

                <asg:labeledAutoComplete id="ult_doc_ent" name="ult_doc_ent.id" instance="${materialInstance}"
                                         valuefield="id" listfields="id"
                                         domain="com.br.asgardtecnologia.erp.materiais.EntradaMaterial"
                                         value="${materialInstance?.ult_doc_ent}" forceshowmode="true" span="6"/>
                <asg:labeledDecimal name="peso" instance="${materialInstance}" decimals="5" span="6"
                                    value="${materialInstance?.getPesoCalc()}"/>
            </div>

            <div class="row">
                <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.Grupo" id="grupo"
                                   name="grupo.id" instance="${materialInstance}"
                                   optionKey="id"
                                   value="${materialInstance?.grupo?.id}" class="many-to-one"
                                   noSelection="['null': '']" span="6"/>
                <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.SubGrupo" id="sub_grupo"
                                   name="sub_grupo.id" instance="${materialInstance}"
                                   optionKey="id"
                                   value="${materialInstance?.sub_grupo?.id}" class="many-to-one"
                                   noSelection="['null': '']" span="6"/>
            </div>

            <div class="row">
                <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.GrupoTributacao" id="grupo_tributacao"
                                   name="grupo_tributacao.id" instance="${materialInstance}"
                                   optionKey="id"
                                   value="${materialInstance?.grupo_tributacao?.id}" class="many-to-one"
                                   noSelection="['null': '']" span="6"/>
                <asg:labeledSelect domain="com.br.asgardtecnologia.erp.materiais.Localizacao" id="localizacao"
                                   name="localizacao.id" instance="${materialInstance}"
                                   optionKey="id"
                                   value="${materialInstance?.localizacao?.id}" class="many-to-one"
                                   noSelection="['null': '']" span="6"/>
                <br/>
            </div>

            <div class="row">
                <asg:grid ignorePagination="${true}" name="itemExame" controller="itemExame"
                          list="${materialInstance.item_exame?.sort { a, b -> b.id <=> a.id }}"
                          fields="exame"
                          headerButtons="create"
                          itemButtons="show;edit;delete;erros"/>
            </div>
        </div>

        <div id="tab_estoque" class="tab-pane">
            <div class="row">
                <asg:labeledDecimal name="estoque" instance="${materialInstance}" readonly="true" span="6"/>
                <asg:labeledDecimal name="estoqueDisponivel" instance="${materialInstance}" readonly="true" span="6"/>
            </div>

            <div class="row">
                <asg:labeledDecimal name="estoque_maximo" instance="${materialInstance}" readonly="true" span="6"/>
                <asg:labeledDecimal name="fator_estoque_maximo" instance="${materialInstance}" span="6"/>
            </div>

            <div class="row">
                <asg:labeledDecimal name="estoque_minimo" instance="${materialInstance}" span="6"/>
                <asg:labeledDecimal name="fator_estoque_minimo" instance="${materialInstance}" span="6"/>
            </div>

            <div class="row">
                <asg:labeledDecimal name="ponto_pedido" instance="${materialInstance}" span="6"/>
                <asg:labeledDecimal name="fator_ponto_pedido" instance="${materialInstance}" span="6"/>
            </div>

            <div class="row">
                <asg:labeledCheckBox name="abaixoEstoqueMinimo" instance="${materialInstance}" forceshowmode="${true}"
                                     span="6"/>
                <asg:labeledCheckBox name="acimaEstoqueMaximo" instance="${materialInstance}" forceshowmode="${true}"
                                     span="6"/>
            </div>

        </div>

        <div id="tab_lote" class="tab-pane">
            <div id="tab_r_lote">
                <div class="row">
                    <asg:grid ignorePagination="${true}" controller="materialLote"
                              list="${materialInstance.materialLote?.sort { a, b -> b.validade_lote <=> a.validade_lote }}"
                              fields="fornecedor;lote;validade_lote[DATE];estoque"/>
                </div>
            </div>
        </div>

        <div id="tab_custo" class="tab-pane">
            <div id="tab_r_custo">
                <div class="row">
                    <asg:grid ignorePagination="${true}" controller="historicoCustoMaterial"
                              list="${materialInstance.historicoCustoMaterial?.sort { a, b -> b.id <=> a.id }}"
                              fields="data;entradaMaterial;custo_total[MONEY];"/>
                </div>
            </div>
        </div>

        <div id="tab_composicao" class="tab-pane">
            <div id="tab_r_composicao">
                <div class="row">
                    <asg:grid ignorePagination="${true}" controller="LDM" list="${materialInstance.LDM}"
                              fields="material_composicao;quantidade[QUANTITY];custo[MONEY];custoTotal[MONEY]"
                              inlineMode="${false}"
                              headerButtons="create" itemButtons="show;edit;delete;erros"
                              modelInstance="${new com.br.asgardtecnologia.erp.materiais.LDM(material: materialInstance)}"/>
                </div>

                <div class="row">
                    <asg:labeledMoney name="custo_total" instance="${materialInstance}" span="6" forceshowmode="true"
                                      value="${materialInstance?.getCustoTotalCalc()}"/>
                </div>
            </div>
        </div>

        <div id="tab_precos" class="tab-pane">
            <asg:grid ignorePagination="${true}" name="tabela_preco_material" controller="tabelaPrecoMaterial"
                      list="${materialInstance.tabela_preco_material?.sort { a, b -> b.tabela.descricao <=> a.tabela.descricao }}"
                      fields="tabela;fixar_preco;tabela.margem_sugerida[QUANTITY];precoSugerido[MONEY];margem[QUANTITY];valor[MONEY]"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>
        </div>

        <div id="tab_desc_forn" class="tab-pane">
            <asg:grid ignorePagination="${true}" name="descricao_fornecedor" controller="descricaoFornecedor"
                      list="${materialInstance.descricao_fornecedor}"
                      fields="fornecedor;codigo;descricao"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>
        </div>

        <div id="tab_conv_unid" class="tab-pane">
            <asg:grid ignorePagination="${true}" name="tabela_conversao" controller="tabelaConversao"
                      list="${materialInstance.tabela_conversao}"
                      fields="material;quantidade;unidade_medida"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>
        </div>

        <div id="tab_obs" class="tab-pane">
            <asg:textArea name="observacoes" value="${materialInstance?.observacoes}" rows="10"/>
        </div>
    </div>
</div>

<r:script disposition="defer">
    function tipoMaterialChange() {
        var obj_custo_total = $("#custo_total");
        var obj_peso = $("#peso");
        if (isNormal()) {
            if (obj_custo_total.val()) obj_custo_total.attr('readonly', false);
            if (obj_peso.val()) obj_peso.attr('readonly', false);

            $("#tab_h_composicao").hide();
            $("#tab_r_composicao").hide();
        }
        else {
            if (obj_custo_total.val()) obj_custo_total.attr('readonly', true);
            if (obj_peso.val()) obj_peso.attr('readonly', true);

            $("#tab_h_composicao").show();
            $("#tab_r_composicao").show();
        }
    }

    function isNormal() {
        var normalKey = "${com.br.asgardtecnologia.erp.materiais.TipoMaterial?.NORMAL.toString()}"

        var obj_tipo_material = $("#tipo_material");
        var value = obj_tipo_material.val();
        if (value)
        return (value == normalKey) // Edit mode: DYNAMICALLY
        else
        return (${materialInstance.tipo_material == TipoMaterial.NORMAL}) // Show mode: STATICALLY
    }

onDocumentReady(function () {
    tipoMaterialChange();
});
</r:script>