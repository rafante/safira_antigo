<%@ page import="com.br.asgardtecnologia.erp.materiais.Recipiente" %>

<div class="widget-box">
    <div class="widget-title">
        <h5><g:message code="recipiente.label" default="Recipiente"/></h5>
    </div>

    <div class="widget-content">
        <div class="row">
            <asg:labeledTextField name="codEv" value="${recipienteInstance?.codEv}"/>
        </div>

        <div class="row">
            <asg:labeledTextField name="descricao" value="${recipienteInstance?.descricao}"/>
        </div>

    </div>
</div>

