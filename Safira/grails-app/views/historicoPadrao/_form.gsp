<div class="row">
    <asg:labeledTextField name="descricao" value="${historicoPadraoInstance?.descricao}"/>
</div>

<div class="row">
    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.PlanoContas"
                             instance="${historicoPadraoInstance}" span="6"
                             id="planoContas" name="planoContas.id" required=""
                             value="${historicoPadraoInstance?.planoContas?.id}"/>
    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.cadastros.CentroCusto"
                             instance="${historicoPadraoInstance}" span="6"
                             id="centroCusto" name="centroCusto.id" required=""
                             value="${historicoPadraoInstance?.centroCusto?.id}"/>
</div>
<div class="row">
    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.ContaCorrente"
                             instance="${historicoPadraoInstance}" span="6"
                             id="conta" name="conta.id" required=""
                             value="${historicoPadraoInstance?.conta?.id}"/>
</div>