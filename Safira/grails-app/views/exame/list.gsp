<%@ page import="com.br.asgardtecnologia.erp.ev.Exame" %>
<!doctype html>
<html>
<head>
	<meta name="layout" content="bootstrap">
	<g:set var="entityName" value="${message(code: 'exame.label', default: 'Cheque')}"/>
	<title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body><div class="container-fluid">
	<div class="col-xs-12">

		<div class="col-12 col-sm-3" style="display:none">
		</div>

		<div id="page" class="col-12">

			<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
			</g:if>

			<asg:filter domain="com.br.asgardtecnologia.erp.ev.Exame"
						params="${params}"
						fields="${[[name: "codExame"], [name: "mnemonico"], [name: "descricao"], [name: "precoCusto"], [name: "precoVenda"],
								   [name: "ativo"], [name: "setor"], [name: "recipiente"]]}"/>

			<asg:grid controller="exame" list="${exameInstanceList}"
					  fields="codExame;mnemonico;descricao;precoCusto;precoVenda;ativo;setor;recipiente"
					  headerButtons="create" itemButtons="show;edit;delete;erros"/>

		</div>
	</div>
</div></body>
</html>
