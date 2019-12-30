<div class="row">

    <asg:labeledSelect domain="com.br.asgardtecnologia.erp.base.Empresa" instance="${parametrosFinanceiroInstance?.empresa}"
                       id="empresa" name="empresa.id" label="Cedente"
                       optionKey="id" value="${parametrosFinanceiroInstance?.empresa?.id}" class="many-to-one"/>
</div>

<div class="row">
    <asg:labeledTextField name="nossoNumero" instance="${parametrosFinanceiroInstance}"
                          value="${parametrosFinanceiroInstance?.nossoNumero}" span="4"/>
</div>

<div class="row">
    <asg:labeledTextField name="agencia" instance="${parametrosFinanceiroInstance}"
                          value="${parametrosFinanceiroInstance?.agencia}" span="4"/>
    <asg:labeledTextField name="conta" instance="${parametrosFinanceiroInstance}"
                          value="${parametrosFinanceiroInstance?.conta}" span="4"/>
</div>
<div class="row">
    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.ContaCorrente"
                             instance="${parametrosFinanceiroInstance}" id="contaCorrente" name="contaCorrente.id"
                             value="${parametrosFinanceiroInstance?.contaCorrente?.id}" required="true"
                             span="12"/>
</div>