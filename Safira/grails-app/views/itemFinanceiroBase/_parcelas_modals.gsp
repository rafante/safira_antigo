<%@ page import="com.br.asgardtecnologia.erp.financeiro.Cheque" %>
<%
    def compensacaoItemInstance = (entity == 'Receber') ? new com.br.asgardtecnologia.erp.financeiro.CompensacaoItemReceber() : new com.br.asgardtecnologia.erp.financeiro.CompensacaoItemPagar()
    def prorrogacaoItemInstance = (entity == 'Receber') ? new com.br.asgardtecnologia.erp.financeiro.ProrrogacaoItemReceber() : new com.br.asgardtecnologia.erp.financeiro.ProrrogacaoItemPagar()
%>

<asg:confirmModalForm name="modalCompensar${item.id}" controller="conta${entity}" action="compensar"
                      title="${g.message(code: "conta" + entity + ".compensar.label", default: "Compensar")}"
                      style="width:1100px;height:auto;" hideConfirm="${item.compensadoCompletamente}">
    <fieldset>
        <g:if test="${!item.compensadoCompletamente}">
            <%
                def multa = item.dataVencimento?.compareTo(new Date()) > 0 ? item.multa : 0
            %>
            <div class="form-horizontal">
                <div class="row">
                    <asg:labeledMoney
                            label="${g.message(code: 'itemConta' + entity + '.multa.label', default: 'Multa')}"
                            name="multa" forceshowmode="true" value="${item.multa}" span="6"/>
                    <asg:labeledDecimal
                            label="${g.message(code: 'itemConta' + entity + '.juros.label', default: 'Juros')}"
                            name="juros" forceshowmode="true" value="${item.juros}" span="6"/>
                </div>

                <div class="row">
                    <asg:labeledDecimal
                            label="${g.message(code: 'itemConta' + entity + '.descontos.label', default: 'Descontos')}"
                            name="descontos" forceshowmode="true" value="${item.descontos}" span="6"/>

                    <asg:labeledDatePicker name="dataCompensacao${item.id}" instance="${item}"
                                           class="asg-ignore-first-focus"
                                           value="${new Date()}" forceeditmode="true"
                                           label="${g.message(code: 'dataCompensacao.label')}"
                                           required="true" span="6"/>

                    <asg:hiddenField name="valor${item.id}" value="${item.getValorDia()}" forceeditmode="true"/>
                </div>
            </div>

            <div class="widget-box" style="margin-top:20px">
                <div class="widget-title">
                    <span class="icon">
                        <a href="javascript:void(0);" class="tip-top"
                           data-original-title="${message(code: 'default.action.add.label')}" id="btnAddLine"
                           onclick="clickAddCompensacao(this)">
                            <i class="icon-plus"></i>
                        </a>
                    </span>
                </div>

                <div class="widget-content">
                    <div class="row">
                        <asg:labeledMoney
                                name="compensacao[0].valor"
                                label="${message(code: 'valorCompensado.label')}"
                                id="valorCompensado[0]"
                                value="${item.getValorDia()}"
                                forceeditmode="true"
                                span="3" required="true" autofocus="true" data-valor-name="valor${item.id}"/>

                        <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.ContaCorrente"
                                                 orderBy="descricao"
                                                 instance="${compensacaoItemInstance}" id="contaCorrente[0]"
                                                 name="compensacao[0].contaCorrente.id"
                                                 label="${message(code: 'contaCorrente.label')}"
                                                 optionKey="id"
                                                 value="${item?.getParent()?.contaCorrente?.id}"
                                                 noSelection="['null': '']" forceeditmode="true" span="4"
                                                 required="true" filter='["ativa": true]'
                                                 ignoreGeneralAttrsAdjusts="true"/>

                        <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.FormaPagamento"
                                           instance="${compensacaoItemInstance}"
                                           id="_formaPagamento[0]"
                                           name="compensacao[0].formaPagamento.id"
                                           label="${message(code: 'formaPagamento.label')}"
                                           optionKey="id"
                                           value="${item?.formaPagamentoId}" class="many-to-one"
                                           noSelection="['null': '']" forceeditmode="true" span="4" required=""
                                           ignoreGeneralAttrsAdjusts="true"/>

                        <div class="col-12 col-sm-1 form-group fieldcontain">
                            <label class="control-label" style="display:block; margin-bottom: 16px;"></label>
                            <a title="${message(default: 'Excluir linha')}"
                               class="btn btn-danger tip-bottom"
                               href="javascript:void(0);"
                               onclick="clickRemoveLineCompensacao(this)">
                                <i class="icon icon-trash icon-white"></i>
                            </a>
                        </div>
                    </div>

                    <div class="row">
                        <div><asg:labeledCheckBox name="compensacao[0].controle_cheque" value="Pgto. Cheque"
                                                  asg-hide-if-false="box_cheque[0]" span="2"/></div>

                        <div id="box_cheque[0]">
                            <% def chequeInstance = new Cheque() %>
                            <asg:labeledTextField name="compensacao[0].cheque.numero" instance="${chequeInstance}"
                                                  forceeditmode="true"
                                                  value="${chequeInstance?.numero}"
                                                  span="6"/>
                        </div>

                    </div>
                </div>
            </div>

            <asg:hiddenField name="itemConta${entity}.id" value="${item.id}"/>
        </g:if>
        <g:else>
            ${message(code: "itemFinanceiroBase.compensado.completamente")}
        </g:else>
    </fieldset>
</asg:confirmModalForm>

<asg:confirmModalForm name="modalProrrogar${item.id}" controller="conta${entity}" action="prorrogar"
                      title="${g.message(code: "prorrogar.label", default: "Prorrogar")}">
    <fieldset>
        <asg:labeledDatePicker name="data"
                               instance="${prorrogacaoItemInstance}"
                               value="" default="none" forceeditmode="true"
                               noSelection="['': '']" span="12"
                               label="${message(code: 'data.label')}"
                               id="data_${item.id}"
                               required="true"/>
        <asg:labeledTextArea name="justificativa"
                             label="${message(code: 'justificativa.label')}"
                             forceeditmode="true"
                             span="12"/>
        <asg:hiddenField name="itemConta${entity}.id" value="${item.id}"/>
    </fieldset>
</asg:confirmModalForm>