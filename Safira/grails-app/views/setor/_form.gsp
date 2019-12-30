<%@ page import="com.br.asgardtecnologia.erp.cadastros.Setor" %>

<div class="widget-box">
	<div class="widget-title">
		<h5><g:message code="setor.label" default="Setor"/></h5>
	</div>

	<div class="widget-content">

		<div class="row">
			<asg:labeledTextField name="descricao" value="${setorInstance?.descricao}"/>
		</div>

	</div>
</div>

