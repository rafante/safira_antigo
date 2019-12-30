<%@ page import="com.br.asgardtecnologia.erp.ev.Exame" %>
<!doctype html>
<html>
<head>
	<meta name="layout" content="bootstrap">
	<g:set var="entityName" value="${message(code: 'exame.label', default: 'Exame')}"/>
	<title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body><div class="container-fluid">
	<div class="col-xs-12">

		<div class="col-12 col-sm-3" style="display:none">
			<div class="well">
				<ul class="nav nav-list">
					<li class="nav-header">${entityName}</li>
					<li>
						<asg:link class="list" action="list">
							<i class="icon icon-list"></i>
							<g:message code="default.list.label" args="[entityName]"/>
						</asg:link>
					</li>
					<li>
						<asg:link class="create" action="create">
							<i class="icon icon-plus"></i>
							<g:message code="default.create.label" args="[entityName]"/>
						</asg:link>
					</li>
				</ul>
			</div>
		</div>

		<div id="page" class="col-12">

			<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
			</g:if>

			<g:hasErrors bean="${exameInstance}">
				<bootstrap:alert class="alert-error">
					<ul>
						<g:eachError bean="${exameInstance}" var="error">
							<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
									error="${error}"/></li>
						</g:eachError>
					</ul>
				</bootstrap:alert>
			</g:hasErrors>

			<fieldset>
				<g:form class="form-horizontal" action="show" id="${exameInstance?.id}">
					<g:hiddenField name="version" value="${exameInstance?.version}"/>
					<fieldset>
						<g:render template="form" model="[exameInstance: exameInstance]"/>
					</fieldset>
				</g:form>
			</fieldset>

		</div>

	</div>
</div></body>
</html>
