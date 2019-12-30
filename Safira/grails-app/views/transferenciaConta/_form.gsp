<%@ page import="com.br.asgardtecnologia.erp.cadastros.HistoricoPadrao; com.br.asgardtecnologia.erp.financeiro.TransferenciaConta" %>

<div class="widget-box">
    <div class="widget-title">
        <span class="icon">
            <i class="icon icon-th-list"></i>
        </span>
        <h5><g:message code="${params.controller}.label" span="6"/></h5>
    </div>

    <div class="widget-content">
        <div class="row">
            <asg:labeledCheckBox name="estornado" value="${transferenciaConta?.estornado}" span="6"
                                 forceshowmode="true"/>
        </div>

        <div class="row">
            <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.ContaCorrente" orderBy="conta"
                                     instance="${transferenciaConta}"
                                     name="contaCorrenteOrigem.id"
                                     id="contaCorrenteOrigem" filter='["ativa": true]'
                                     optionKey="id"
                                     value="${transferenciaConta.contaCorrenteOrigem?.id}" class="many-to-one"
                                     noSelection="['null': '']" span="6" required="true"/>
            <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.ContaCorrente" orderBy="conta"
                                     instance="${transferenciaConta}"
                                     name="contaCorrenteDestino.id"
                                     id="contaCorrenteDestino" filter='["ativa": true]'
                                     optionKey="id"
                                     value="${transferenciaConta.contaCorrenteDestino?.id}" class="many-to-one"
                                     noSelection="['null': '']" span="6" required="true"/>
        </div>

        <div class="row">

            <asg:labeledDatePicker name="data"
                                   instance="${new com.br.asgardtecnologia.erp.financeiro.TransferenciaConta()}"
                                   value="${transferenciaConta.data}" default="none"
                                   noSelection="['': '']" span="6" required="true"/>

        </div>

        <div class="row">

            <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.HistoricoPadrao"
                               instance="${transferenciaConta}" id="historicoPadrao" name="historicoPadrao.id"
                               optionKey="id"
                               value="${transferenciaConta?.historicoPadrao?.id}" class="many-to-one"
                               noSelection="['null': '']" span="6"/>

            <asg:labeledMoney name="valor" value="${transferenciaConta.valor}" span="6" required="true"/>
        </div>

        <div class="row">
            <asg:labeledTextArea name="descricao" value="${transferenciaConta.descricao}"
                                 span="12" required="true"/>
        </div>


        <div class="row">
            <asg:grid controller="movimentoFinanceiro" list="${transferenciaConta.movimentoFinanceiro}"
                      fields="id;dataDocumento[DATE];contaCaixa;descricao;valor[MONEY];movimentoFinanceiroEstorno;estornado"
                      itemButtons="show"
                      ignorePagination="${true}"/>
        </div>

        <g:if test="${!transferenciaConta.estornado}">
            <div class="row">
                <button type="submit" class="btn btn-danger" name="_action_estornar" formnovalidate>
                    <i class="icon icon-reply icon-white"></i>
                    <g:message code="default.button.estornar.label" default="Estornar"/>
                </button>
            </div>
        </g:if>
    </div>
</div>

<asg:hiddenField name="id" value="${transferenciaConta.id}"/>