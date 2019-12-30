<%@ page import="com.br.asgardtecnologia.erp.ev.MovimentoExame" %>

<div class="row">
    <asg:labeledDatePicker name="dataMovimento" instance="${movimentoExameInstance}"
                           value="${movimentoExameInstance?.dataMovimento}" default="none"
                           span="6"/>

    <asg:labeledDecimal name="quantidade" instance="${itemEntradaInstance}" decimals="5"
                        value="${fieldValue(bean: itemEntradaInstance, field: 'quantidade')}" required="" span="6"/>

    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.ev.Exame" instance="${itemMovimentoMaterialInstance}"
                             id="exame" name="exame.id" required="" value="${itemMovimentoMaterialInstance?.exame?.id}" span="6"/>
</div>