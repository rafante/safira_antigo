<%@ page import="com.br.asgardtecnologia.erp.base.ParametrosGerais" %>

<asg:widgetBox id="cadastros" title="Cadastros" icon="icon-user">
    <div class="row">
        <asg:labeledCheckBox name="cpfCnpjObrigatorio" instance="${parametrosGeraisInstance}"/>
    </div>
</asg:widgetBox>

<asg:widgetBox id="geral" title="Geral" icon="icon-wrench">
    <div class="row">
        <asg:labeledCheckBox name="ignoreAjaxLoadingFilter" instance="${parametrosGeraisInstance}"/>
    </div>

    <div class="row">
        <asg:labeledCheckBox name="ignoreAjaxLoadingGrid" instance="${parametrosGeraisInstance}"/>
    </div>

    <div class="row">
        <asg:labeledCheckBox name="ignoreAjaxLoadingSelect" instance="${parametrosGeraisInstance}"/>
    </div>
</asg:widgetBox>