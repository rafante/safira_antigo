<%@ page import="com.br.asgardtecnologia.erp.ev.ItemExame" %>

<asg:hiddenField name="material.id" instance="${itemExameInstance}"
				 value="${itemExameInstance?.material?.id}"/>

<div class="row">
	<asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.ev.Exame" instance="${itemExameInstance}"
							 id="exame" name="exame.id" required="" value="${itemExameInstance?.exame?.id}"
							 span="8"/>
</div>
