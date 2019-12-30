<%@ page import="com.br.asgardtecnologia.erp.financeiro.ParametrosFinanceiro; org.jrimum.bopepo.BancosSuportados; com.br.asgardtecnologia.erp.financeiro.ContaCorrente; com.br.asgardtecnologia.erp.financeiro.CustomBoleto" %>

<%
    def parametros = com.br.asgardtecnologia.erp.financeiro.ParametrosFinanceiro.get(1)
%>

<form method="POST" action="generateBoleto" >
    <div class="row">
        <asg:filter domain="com.br.asgardtecnologia.erp.financeiro.CustomBoleto"
                    params="${params}" hideButtons="${true}"
                    fields="cliente;data;vencimento"/>
    </div>
    <div class="row">
        <asg:labeledSelect domain="org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo"
                           id="tipoDeTitulo"
                           name="tipoDeTitulo.id"
                           from="${org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo?.values()}"
                           keys="${org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo?.values()*.name()}"
                           class="many-to-one" span="6"/>
    </div>
    <div class="row">
        <asg:labeledTextField name="caminho" span="6" value="C:\\" required=""/>
    </div>
    <div class="row">
        <asg:labeledTextField name="nossoNumero" span="6" value="${parametros?.nossoNumeroSemDigito}" required=""/>
    </div>
    <div class="row">
        <asg:labeledTextField name="nossoNumeroDigito" span="6" value="${parametros?.nossoNumeroDigito}" required=""/>
    </div>
    <div class="row">
        <asg:labeledSelect domain="org.jrimum.bopepo.BancosSuportados"
                           id="banco"
                           name="banco.id"
                           from="${org.jrimum.bopepo.BancosSuportados?.values()}"
                           keys="${org.jrimum.bopepo.BancosSuportados?.values()*.name()}"
                           class="many-to-one" span="6"/>
    </div>
    <div class="row">
        <asg:labeledTextField name="agencia" span="6" value="${parametros?.agenciaSemDigito}" required=""/>
        <asg:labeledTextField name="agenciaDigito" span="6" value="${parametros?.agenciaDigito}" required=""/>
    </div>
    <div class="row">
        <asg:labeledTextField name="conta" span="6" value="${parametros?.contaSemDigito}" required=""/>
        <asg:labeledTextField name="contaDigito" span="6" value="${parametros?.contaDigito}" required=""/>
    </div>
    <div class="row">
        <button class="btn btn-warning" type="submit">Gerar boletos</button>
    </div>
</form>
