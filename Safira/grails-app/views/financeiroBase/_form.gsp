<div class="widget-box">
    <div class="widget-title">
        <span class="icon">
            <i class="icon icon-th-list"></i>
        </span>
        <h5><g:message code="${params.controller}.label" span="6"/></h5>
    </div>

    <div class="widget-content">
        <div class="form-horizontal">
            <div class="row">
                <asg:labeledDatePicker name="dataEmissao" instance="${instance}"
                                       value="${instance?.dataEmissao ?: new Date()}" default="none"
                                       noSelection="['': '']" span="6"/>
                <asg:labeledDatePicker name="dataDocumento" instance="${instance}"
                                       value="${instance?.dataDocumento ?: new Date()}" default="none"
                                       noSelection="['': '']"
                                       span="6"/>
            </div>

            <div class="row">
                <asg:labeledTextField name="documento" instance="${instance}"
                                      value="${instance?.documento}" span="6"/>
                <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.TipoDocumento"
                                   instance="${instance}"
                                   id="tipoDocumento"
                                   name="tipoDocumento.id"
                                   optionKey="id"
                                   value="${instance?.tipoDocumento?.id}" class="many-to-one"
                                   noSelection="['null': '']" span="6"/>
            </div>

            <div class="row">
                <asg:labeledTextField name="referencia" instance="${instance}"
                                      value="${instance?.referencia}" span="6"/>
                <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.PlanoContas"
                                         filter='["temFilhos": false]' instance="${instance}" id="planoContas"
                                         name="planoContas.id" value="${instance?.planoContas?.id}" span="6"/>

            </div>

            <div class="row">
                <asg:labeledTextField name="descricao" instance="${instance}"
                                      value="${instance?.descricao}" span="12" required="true"/>
            </div>

            <div class="row">
                <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios"
                                         filter='["${tipoParceiroNegocios}": "true"]'
                                         instance="${instance}" id="parceiroNegocios" name="parceiroNegocios.id"
                                         value="${instance?.parceiroNegocios?.id}" required="true"
                                         span="12"/>
            </div>

            <div class="row">
                %{--<asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.CentroCusto"
                                   instance="${instance}" id="centroCusto" name="centroCusto.id"
                                   optionKey="id" filter='["temFilhos": false]'
                                   value="${instance?.centroCusto?.id}" class="many-to-one"
                                   noSelection="['null': '']" span="6"/>--}%

                <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.cadastros.CentroCusto" instance="${instance}"
                                         id="centroCusto" name="centroCusto.id" required="" value="${instance?.centroCusto?.id}"
                                         filter='["temFilhos": false]'
                                         span="6"/>

                <asg:labeledMoney name="valorTotal" value="${instance?.valorTotal}" instance="${instance}" span="6"/>

            </div>

            <div class="row">
                <asg:labeledMoney name="valorTotalParcelas" value="${instance?.valorTotalParcelas}"
                                  instance="${instance}" forceshowmode="true" span="6"/>
            </div>

            <g:if test="${instance?.id && instance?.isValorInconsistente()}">
                <div class="row">
                    <bootstrap:alert class="alert-error">
                        <ul>
                            <li><g:message code="financeiroBase.mensagens.valoresInconsistentes"/></li>
                        </ul>
                    </bootstrap:alert>
                </div>
            </g:if>

            <g:if test="${instance?.class == com.br.asgardtecnologia.erp.financeiro.ContaPagar}">
                <div class="row">
                    <asg:labeledCheckBox name="boletoRecebido" value="${instance?.boletoRecebido}"
                                         instance="${instance}"/>
                </div>
            </g:if>
        </div>

        <g:render template="/financeiroBase/parcelas"/>
    </div>
</div>

<r:script>
    $(function () {
        $('#dataEmissao_holder').trigger('focus');
    });
</r:script>