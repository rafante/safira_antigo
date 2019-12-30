<%@ page import="com.br.asgardtecnologia.erp.ev.Exame" %>

<div class="widget-box">
	<div class="widget-title">
		<h5><g:message code="exame.label" default="Exame"/></h5>
	</div>

	<div class="widget-content">
		<div class="row">
			<asg:labeledCheckBox name="ativo" value="${exameInstance?.ativo}" span="6"/>
		</div>
		<div class="row">
			<asg:labeledTextField name="codExame" value="${exameInstance?.codExame}" span="6"/>
			<asg:labeledTextField name="mnemonico" value="${exameInstance?.mnemonico}" span="6"/>
		</div>

		<div class="row">
			<asg:labeledTextField name="descricao" value="${exameInstance?.descricao}" span="12"/>

		</div>
		<div class="row">
			<asg:labeledDecimal name="precoCusto" value="${exameInstance?.precoCusto}" span="6"/>
			<asg:labeledDecimal name="precoVenda" value="${exameInstance?.precoVenda}" span="6"/>
		</div>
		<div class="row">

			<asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.cadastros.Setor"
									 instance="${exameInstance}" id="setor" name="setor.id"
									 value="${exameInstance?.setor?.id}" span="12"/>
		</div>
		<div class="row">
			<asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.materiais.Recipiente"
									 instance="${exameInstance}" id="recipiente" name="recipiente.id"
									 value="${exameInstance?.recipiente?.id}" span="12"/>
		</div>
	</div>
</div>

