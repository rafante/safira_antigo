<%@ page import="com.br.asgardtecnologia.erp.cadastros.HistoricoContato" %>

<div class="row">
    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios"
                             filter='[cliente:"true"]'
                             instance="${historicoContatoInstance}"
                             id="parceiroNegocios" name="parceiroNegocios.id" required=""
                             value="${historicoContatoInstance?.parceiroNegocios?.id}" readonly="true"/>
</div>

<div class="row">
    <asg:labeledDatePicker name="data"  instance="${historicoContatoInstance}"
                           value="${historicoContatoInstance?.data}" default="none" noSelection="['': '']"/>
</div>

<div class="row">
    <asg:labeledTextField name="contato" instance="${historicoContatoInstance}"
                          value="${historicoContatoInstance?.contato}"/>
</div>

<div class="row">
    <asg:labeledTextArea name="historico" instance="${historicoContatoInstance}"
                         value="${historicoContatoInstance?.historico}"/>
</div>