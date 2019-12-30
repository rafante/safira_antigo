<div class="widget-box">
<div class="widget-box">
    <div class="widget-title">
        <span class="icon">
            <i class="icon icon-th-list"></i>
        </span>
        <h5><g:message code="${params.controller}.label" span="6"/> (${instance?.getDocumentoParcela()})</h5>
    </div>

    <div class="widget-content">
        <asg:hiddenField name="conta${entity}.id" instance="${instance}"
                         value="${instance?."conta${entity}"?.id}"/>

        <div class="row">
            <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.Conta${entity}"
                               instance="${instance}" id="conta${entity}" name="conta${entity}.id"
                               value="${instance?."conta${entity}"?.id}" forceshowmode="true" span="4"/>
            <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.FormaPagamento"
                               instance="${instance}" id="formaPagamento" name="formaPagamento.id"
                               optionKey="id"
                               value="${instance?.formaPagamento?.id}" class="many-to-one"
                               asg-chain-field="jurosPadrao;multaPadrao"
                               asg-chain-target="juros;multa"
                               noSelection="['null': '']" span="4"/>
            <asg:labeledCheckBox name="compensadoCompletamente" value="${instance.compensadoCompletamente}"
                                 forceshowmode="${true}" span="4"/>
        </div>

        <div class="row" style="display:none">
            <asg:labeledDatePicker name="dataDocumento" instance="${instance}"
                                   value="${instance?."conta${entity}"?.dataDocumento}" default="none"
                                   noSelection="['': '']"
                                   span="4"/>
        </div>

        <div class="row">
            <asg:labeledCalculatedDate name="dataVencimento" data-date-ref="dataDocumento" instance="${instance}"
                                       value="${instance?.dataVencimento}"
                                       default="none" noSelection="['': '']" span="12"/>
        </div>

        <div class="row">
            <asg:labeledMoney name="multa" value="${instance.multa}" span="4"/>

            <asg:labeledDecimal name="juros" value="${instance?.juros}"
                                span="4"/>

            <asg:labeledDecimal name="descontos" value="${instance?.descontos}"
                                span="4"/>
        </div>

        <div class="row">
            <asg:labeledMoney name="valor" value="${instance.valor}" span="4"/>
            <asg:labeledMoney name="valorCompensado"
                              value="${instance.valorCompensado}"
                              forceshowmode="true" span="4"/>
            <asg:labeledMoney name="valor_dia"
                              disabled="true"
                              value="${instance.getValorDia()}"
                              forceshowmode="true" span="4"/>
        </div>

        <g:if test="${instance?.id && !instance?.compensadoCompletamente}">
            <div class="row">
                <a title="${g.message(code: 'compensar.label', default: 'Compensar')}"
                   class="btn btn-default tip-bottom"
                   href="#modalCompensar${instance?.id}"
                   data-toggle="modal">
                    <i class="icon-ok"></i> ${g.message(code: 'compensar.label', default: 'Compensar')}
                </a>
                <a title="${g.message(code: 'prorrogar.label', default: 'Prorrogar')}"
                   class="btn btn-default tip-bottom"
                   href="#modalProrrogar${instance?.id}"
                   data-toggle="modal">
                    <i class="icon-share-alt"></i> ${g.message(code: 'prorrogar.label', default: 'Prorrogar')}
                </a>
            </div>
        </g:if>
    </div>
</div>

<asg:grid ignorePagination="${true}" name="compensacaoItem${entity}" controller="compensacaoItem${entity}"
          titleDefault="${message(code: "itemConta${entity}.valoresCompensados.label")}"
          list="${instance['compensacaoItem' + entity]?.sort({ a, b -> b.id?.compareTo(a?.id) })}"
          fields="data[DATE];valor[MONEY];contaCorrente[STRING];formaPagamento.descricao[STRING];cheque;movimentoFinanceiro;estornado;justificativaEstorno;movimentoFinanceiro.movimentoFinanceiroEstorno"
          customButtons="[['href': '#modalEstornar%id%', 'data-toggle': 'modal', 'class': 'btn-default', 'icon': 'icon-reply', 'label': 'compensacaoItem' + entity + '.estornar.label']]"/>

<asg:grid ignorePagination="${true}" name="prorrogacaoItem${entity}" controller="prorrogacaoItem${entity}"
          titleDefault="${message(code: "prorrogacoesItem${entity}.prorrogacoes.label")}"
          list="${instance['prorrogacaoItem' + entity]?.sort({ a, b -> b.id?.compareTo(a?.id) })}"
          fields="data[DATE];justificativa[STRING]"/>

<!-- CRIA AS MODALS PARA ESTORNAR A COMPENSACAO -->
<g:each in="${instance['compensacaoItem' + entity]}" var="item">
    <asg:confirmModalForm name="modalEstornar${item.id}" action="estornar"
                          title="${g.message(code: "compensacaoItem${entity}.estornar.label", default: "Estornar")}"
                          hideConfirm="${item.estornado}">
        <fieldset>
            <g:if test="${!item.estornado}">
                <asg:labeledTextArea
                        name="justificativaEstorno"
                        forceeditmode="true"
                        span="12" required="true"/>
                <asg:hiddenField name="compensacao_id" value="${item.id}"/>
                <asg:hiddenField name="itemConta${entity}.id" value="${item."itemConta${entity}".id}"/>
            </g:if>
            <g:else>
                ${message(code: "message.already.refunded")}
            </g:else>
        </fieldset>
    </asg:confirmModalForm>
</g:each>