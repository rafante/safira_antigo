<%@ page import="com.br.asgardtecnologia.erp.financeiro.ContaCorrente" %>

<div class="row">
    <asg:labeledTextField name="descricao" instance="${contaCorrenteInstance}" maxlength="60"
                          value="${contaCorrenteInstance?.descricao}" span="6"/>
    <asg:labeledAutoComplete
            domain="com.br.asgardtecnologia.erp.financeiro.Banco"
            orderBy="codigo"
            instance="${contaCorrenteInstance}"
            id="banco"
            name="banco.id"
            value="${contaCorrenteInstance?.banco?.id}"
            label="${message(code: 'banco.label')}"
            optionKey="id"
            noSelection="['null': '']"
            span="6"
            required="true"
            ignoreGeneralAttrsAdjusts="true"/>
</div>
<div class="row">

    <asg:labeledTextField name="agencia" instance="${contaCorrenteInstance}" maxlength="60"
                          value="${contaCorrenteInstance?.agencia}" span="6"/>

    <asg:labeledTextField name="conta" instance="${contaCorrenteInstance}" maxlength="60"
                          value="${contaCorrenteInstance?.conta}" span="6"/>
</div>


<div class="row">
    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.TipoConta"
                             instance="${contaCorrenteInstance}" id="tipoConta" name="tipoConta.id"
                             value="${contaCorrenteInstance?.tipoConta?.id}" span="6"/>

    <asg:labeledCheckBox instance="${contaCorrenteInstance}"
                         name="ativa" span="6"
                         value="${contaCorrenteInstance?.ativa}"/>
</div>

<div>
    <asg:labeledDecimal name="limiteCreditoEspecial" value="${contaCorrenteInstance?.limiteCreditoEspecial}"
                        span="6"/>
    <asg:labeledDecimal name="percentJurosCredEspecial" value="${contaCorrenteInstance?.percentJurosCredEspecial}"
                        span="6"/>
</div>

<div>
    <asg:labeledDecimal name="limiteCapitalGiro" value="${contaCorrenteInstance?.limiteCapitalGiro}"
                        span="6"/>
    <asg:labeledDecimal name="percentJurosCapitalGiro" value="${contaCorrenteInstance?.percentJurosCapitalGiro}"
                        span="6"/>
</div>

<div>
    <asg:labeledDecimal name="codigoCarteira" value="${contaCorrenteInstance?.codigoCarteira}"  maxlength="1"
                        span="6"/>
    <asg:labeledDecimal name="codigoComplemento" value="${contaCorrenteInstance?.codigoComplemento}" maxlength="1"
                        span="6"/>
</div>

<div>
    <asg:labeledDecimal name="codigoTransmissao" value="${contaCorrenteInstance?.codigoTransmissao}" maxlength="20"
                        span="6"/>

</div>