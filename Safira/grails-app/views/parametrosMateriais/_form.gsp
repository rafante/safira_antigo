<%@ page import="com.br.asgardtecnologia.erp.materiais.ParametrosMateriais" %>
<div class="row">
    <asg:labeledSelect name="tipoCalculoCustoEntradaDefault" instance="${parametrosMateriaisInstance}"
                       from="${com.br.asgardtecnologia.erp.materiais.TipoCalculoCustoEntrada?.values()}"
                       keys="${com.br.asgardtecnologia.erp.materiais.TipoCalculoCustoEntrada.values()*.name()}"
                       value="${parametrosMateriaisInstance?.tipoCalculoCustoEntradaDefault?.name()}"
                       noSelection="['': '']" span="6"/>
</div>

<div class="row">
    <asg:labeledDecimal name="mesesCalculoEstoquePA" instance="${parametrosMateriaisInstance}"/>
</div>

<div class="row">
    <asg:labeledDecimal name="mesesCalculoEstoquePP" instance="${parametrosMateriaisInstance}"/>
</div>