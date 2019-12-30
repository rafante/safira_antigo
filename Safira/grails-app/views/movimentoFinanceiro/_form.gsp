<%@ page import="com.br.asgardtecnologia.erp.financeiro.MovimentoFinanceiro" %>

<div class="widget-box">
    <div class="widget-title">
        <span class="icon">
            <i class="icon icon-th-list"></i>
        </span>
        <h5><g:message code="${params.controller}.label" span="6"/></h5>
    </div>

    <div class="widget-content">
        <g:if test="${!movimentoFinanceiroInstance?.estornado}">

        </g:if>

        <div class="row">
            <asg:labeledField name="numeroDocumento" value="${movimentoFinanceiroInstance.numeroDocumento}" span="6"/>

            <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.TipoDocumento"
                               instance="${movimentoFinanceiroInstance}"
                               id="tipoDocumento" name="tipoDocumento.id" optionKey="id"
                               value="${movimentoFinanceiroInstance.tipoDocumento}" class="many-to-one"
                               noSelection="['null': '']" span="6"/>
        </div>

        <div class="row">
            <asg:labeledCheckBox name="estornado" instance="${movimentoFinanceiroInstance}"
                                 value="${movimentoFinanceiroInstance?.estornado}"
                                 span="4" forceshowmode='${actionName != "create"}'/>
            <asg:labeledCheckBox name="conciliado" instance="${movimentoFinanceiroInstance}"
                                 value="${movimentoFinanceiroInstance?.conciliado}"
                                 span="4" forceshowmode="${true}"/>
            <asg:labeledCheckBox name="eEstorno" instance="${movimentoFinanceiroInstance}"
                                 value="${movimentoFinanceiroInstance?.eEstorno}"
                                 span="4" forceshowmode="${true}"/>
        </div>

        <div class="row">
            <asg:labeledTextField name="descricao" value="${movimentoFinanceiroInstance.descricao}" span="12"/>
        </div>

        <g:if test="${movimentoFinanceiroInstance?.estornado}">
            <div class="row">
                <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.MovimentoFinanceiro"
                                         instance="${movimentoFinanceiroInstance}" id="movimentoFinanceiroEstorno"
                                         name="movimentoFinanceiroEstorno.id" optionKey="id" forceshowmode="true"
                                         value="${movimentoFinanceiroInstance?.movimentoFinanceiroEstorno?.id}"
                                         required="true" span="6"/>
            </div>
        </g:if>

        <div class="row">
            <asg:labeledDatePicker name="dataDocumento"
                                   instance="${movimentoFinanceiroInstance}"
                                   value="${movimentoFinanceiroInstance.dataDocumento}" default="none"
                                   noSelection="['': '']" span="6"/>
            <asg:labeledDatePicker name="dataEmissao"
                                   instance="${movimentoFinanceiroInstance}"
                                   value="${movimentoFinanceiroInstance.dataDocumento}" default="none"
                                   noSelection="['': '']" span="6"/>
        </div>

        <div class="row">
            <asg:labeledAutoComplete id="historicoPadrao" name="historicoPadrao.id"
                                     instance="${movimentoFinanceiroInstance}"
                                     domain="com.br.asgardtecnologia.erp.cadastros.HistoricoPadrao"
                                     value="${movimentoFinanceiroInstance?.historicoPadrao?.id}" span="6"/>
            <asg:labeledMoney name="valor" value="${movimentoFinanceiroInstance.valor}" span="6"/>

        </div>

        <div class="row">
            <asg:labeledSelect name="debito_credito" instance="${movimentoFinanceiroInstance}"
                               from="${com.br.asgardtecnologia.erp.financeiro.TipoMovimentoFinanceiro?.values()}"
                               keys="${com.br.asgardtecnologia.erp.financeiro.TipoMovimentoFinanceiro?.values()*.name()}"
                               value="${movimentoFinanceiroInstance?.debito_credito}"
                               noSelection="['': '']" span="6"/>

            <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.PlanoContas" orderBy="codigo"
                                     instance="${movimentoFinanceiroInstance}" id="planoContas"
                                     name="planoContas.id" filter="['temFilhos': false]"
                                     value="${movimentoFinanceiroInstance?.planoContas?.id}" span="6"/>

        </div>

        <div class="row">
            <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.ContaCorrente"
                                     instance="${movimentoFinanceiroInstance}" id="contaCorrente"
                                     name="contaCorrente.id"
                                     value="${movimentoFinanceiroInstance?.contaCorrente?.id}" span="6"/>

            <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.cadastros.FormaPagamento"
                                     instance="${movimentoFinanceiroInstance}" id="formaPagamento"
                                     name="formaPagamento.id"
                                     value="${movimentoFinanceiroInstance?.formaPagamento?.id}" span="6"/>
        </div>
    </div>
</div>
<asg:hiddenField name="id" value="${movimentoFinanceiroInstance.id}"/>