<%@ page import="com.br.asgardtecnologia.erp.materiais.StatusEntrada; com.br.asgardtecnologia.erp.materiais.EntradaMaterial" %>
<div class="widget-box">
    <div class="widget-title">
        <h5><g:message code="entradaMaterial.label" default="Entrada de Materiais"/></h5>
    </div>

    <div class="widget-content">

        <div class="row">
            <asg:labeledLinearStatus name="status" instance="${entradaMaterialInstance}"/>
            <asg:confirmModalForm id="modalCorrigir" action="corrigirEntrada"
                                  title="${g.message(code: "entradaMaterial.corrigir.label", default: "Corrigir")}">
                <asg:labeledTextArea name="motivo" value="${entradaMaterialInstance?.motivo}" forceeditmode="true" span="12"/>
            </asg:confirmModalForm>

            <r:script disposition="defer">
                $("#CORRIGIR").click(function () {
                    $("#modalCorrigir").modal("show");
                    return false;
                });
            </r:script>
        </div>


        <div class="row">
            <asg:labeledDatePicker name="data_pedido_compra" instance="${entradaMaterialInstance}"
                                   value="${entradaMaterialInstance?.data_pedido_compra}" default="none"
                                   span="6"/>
            <asg:labeledDatePicker name="data_entrada" instance="${entradaMaterialInstance}"
                                   value="${entradaMaterialInstance?.data_entrada}" default="none"
                                   span="6"/>
        </div>
        <div class="row">
            <asg:labeledTextField name="num_pedido_compra" instance="${entradaMaterialInstance}"
                                  value="${entradaMaterialInstance?.num_pedido_compra}" span="6"/>
            <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.TipoDocumento"
                               instance="${entradaMaterialInstance}" id="tipoDocumento"
                               name="tipoDocumento.id"
                               optionKey="id"
                               value="${entradaMaterialInstance?.tipoDocumento?.id}" class="many-to-one" span="6"
                               noSelection="['null': '']"/>
        </div>
        <div class="row">
            <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios"
                                     filter='[fornecedor: "true"]'
                                     instance="${entradaMaterialInstance}" id="fornecedor" name="fornecedor.id"
                                     value="${entradaMaterialInstance?.fornecedor?.id}" span="12"/>
        </div>

        <div class="row">
            <asg:labeledTextField name="documento" instance="${entradaMaterialInstance}"
                                  value="${entradaMaterialInstance?.documento}" maxlength="20" span="6"/>
        </div>

        <div class="row">
            <asg:labeledMoney name="frete" instance="${entradaMaterialInstance}"
                              value="${entradaMaterialInstance?.frete}"  span="5"/>
            <asg:labeledMoney name="icms" instance="${entradaMaterialInstance}"
                              value="${entradaMaterialInstance?.icms}" readonly="readonly" span="5"/>
        </div>
        <div class="row">
            <asg:labeledMoney name="ipi" instance="${entradaMaterialInstance}"
                              value="${entradaMaterialInstance?.ipi}" readonly="readonly" span="5"/>
            <asg:labeledMoney name="pis" instance="${entradaMaterialInstance}"
                              value="${entradaMaterialInstance?.pis}" readonly="readonly" span="5"/>
        </div>
        <div class="row">
            <asg:labeledMoney name="cofins" instance="${entradaMaterialInstance}"
                              value="${entradaMaterialInstance?.cofins}" readonly="readonly" span="5"/>
        </div>

        <div class="row">


            <asg:labeledMoney name="totalLiquido" instance="${entradaMaterialInstance}"
                              value="${entradaMaterialInstance?.totalLiquido}" readonly="readonly" span="5"/>

            <asg:labeledMoney name="totalImpostos" instance="${entradaMaterialInstance}"
                              value="${entradaMaterialInstance?.totalImpostos}" readonly="readonly" span="5"/>

        </div>

        <div class="row">
            <asg:labeledMoney name="totalBruto" instance="${entradaMaterialInstance}"
                              value="${entradaMaterialInstance?.totalBruto}" readonly="readonly" span="5"/>
        </div>

        <div class="row">
            <asg:grid ignorePagination="${true}" name="itemEntrada" controller="itemEntrada"
                      list="${entradaMaterialInstance?.itemEntrada?.sort({ a, b -> a.id?.compareTo(b.id) })}"
                      fields="item;material;lote;validade_lote[DATE];quantidade[QUANTITY];valor[MONEY];totalLiquido[MONEY];descontos[DECIMAL];totalImpostos[MONEY];totalBruto[MONEY]"
                      headerButtons="create"
                      itemButtons="show;edit;delete"/>
        </div>

        <div id="financeiro" class="widget-box">
            <div class="widget-title">
                <h5><g:message code="contaPagar.label" default="Contas a Pagar"/></h5>
            </div>

            <div class="widget-content">
                <div class="row">
                    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.ContaPagar"
                                             instance="${entradaMaterialInstance}" id="contaPagar" name="contaPagar.id"
                                             value="${entradaMaterialInstance?.contaPagar?.id}" forceshowmode="true"/>
                </div>

                <div class="row">
                    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.PlanoContas"
                                             filter='["temFilhos":false]'
                                             instance="${entradaMaterialInstance}" id="planoContas" name="planoContas.id"
                                             value="${entradaMaterialInstance?.planoContas?.id}" filterOperation="ne" />


                    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.ContaCorrente"
                                             filter='["ativa":true]'
                                             instance="${entradaMaterialInstance}" id="contaCorrente" name="contaCorrente.id"
                                             value="${entradaMaterialInstance?.contaCorrente?.id}" />

                    %{--<asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.CentroCusto"
                                       instance="${entradaMaterialInstance}" id="centroCusto" name="centroCusto.id"
                                       optionKey="id"
                                       value="${entradaMaterialInstance?.centroCusto?.id}" class="many-to-one"
                                       noSelection="['null': '']" span="4"/>--}%
                    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.cadastros.CentroCusto"
                                             instance="${entradaMaterialInstance}" id="centroCusto" name="centroCusto.id"
                                             filter='["temFilhos":false]'
                                             value="${entradaMaterialInstance?.centroCusto?.id}" span="4"/>
                </div>

                <div class="row">
                    <asg:labeledCalculatedDate name="primeiroVencimento" instance="${entradaMaterialInstance}"
                                               default="none" noSelection="['': '']" data-date-ref="data_entrada"
                                               value="${entradaMaterialInstance?.primeiroVencimento}"/>
                </div>

                <div class="row">
                    <asg:labeledMoney name="valorTotal" span="6" forceshowmode="true"
                                      value="${entradaMaterialInstance?.totalBruto}"/>

                    <asg:labeledField name="numParcelas" type="number" span="5"
                                      value="${entradaMaterialInstance?.numParcelas}"/>
                </div>

                <div id="periodicidade">
                    <div class="widget-box">
                        <div class="widget-title">
                            <span class="icon">
                                <i class="icon icon-th-list"></i>
                            </span>
                            <h5><g:message code="periodicidade.label" default="Periodicidade"/></h5>
                        </div>

                        <div class="widget-content">
                            <div class="row">
                                <asg:labeledSelect domain="com.br.asgardtecnologia.erp.config.Periodicidade"
                                                   id="periodicidade"
                                                   name="periodicidade.id"
                                                   optionKey="id"
                                                   asg-chain-field="diario" asg-chain-target="periodicidade_diario"
                                                   class="many-to-one" span="8"/>

                                <asg:labeledField name="diasPeriodicidade" type="number" span="4"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

<r:script>
    $(function () {
        $('#data_pedido_compra_holder').trigger('focus');
    });
</r:script>