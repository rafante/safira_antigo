<%@ page import="com.br.asgardtecnologia.erp.base.Empresa" %>

<div class="widget-box">
    <div class="widget-title">
        <ul class="nav nav-tabs">
            <li class="active"><a data-toggle="tab" href="#tab1">Dados Básicos</a></li>
            <li><a data-toggle="tab" href="#tab2">Endereços</a></li>
            <li><a data-toggle="tab" href="#tab3">Informações Fiscais</a></li>
        </ul>
    </div>

    <div class="widget-content tab-content">
        <div id="tab1" class="tab-pane active">
            <div class="row">
                <asg:labeledSelect domain="com.br.asgardtecnologia.erp.base.Empresa"
                                   instance="${empresaInstance}" id="empresa" name="empresa.id"
                                   optionKey="id" ignoreGeneralAttrsAdjusts="" value="${empresaInstance?.empresa?.id}"
                                   class="many-to-one"
                                   noSelection="['null': '']"/>
            </div>

            <div class="row">
                <asg:labeledTextField name="nome" instance="${empresaInstance}" maxlength="60"
                                      value="${empresaInstance?.nome}"/>
            </div>

            <div class="row">
                <asg:labeledTextField name="apelido" instance="${empresaInstance}" value="${empresaInstance?.apelido}"/>
            </div>

            <div class="row">
                <asg:labeledTextField name="cnpj_cpf" instance="${empresaInstance}" maxlength="14"
                                      value="${empresaInstance?.cnpj_cpf}"/>
            </div>

            <div class="row">
                <asg:labeledTextField name="inscricao_estadual" instance="${empresaInstance}" maxlength="14"
                                      value="${empresaInstance?.inscricao_estadual}"/>
            </div>

            %{--<div class="row">--}%
            %{--<asg:labeledFile instance="${empresaInstance}" id="logomarca" name="logomarca"/>--}%
            %{--</div>--}%

        </div>

        <div id="tab2" class="tab-pane">
            <g:render template="/endereco/form" model="[id: 'endereco_comercial', title: 'Endereço Comercial']"
                      bean="${empresaInstance?.endereco_comercial}"/>
            <g:render template="/endereco/form" model="[id: 'endereco_cobranca', title: 'Endereço Cobrança']"
                      bean="${empresaInstance?.endereco_cobranca}"/>
            <g:render template="/endereco/form" model="[id: 'endereco_entrega', title: 'Endereço Entrega']"
                      bean="${empresaInstance?.endereco_entrega}"/>
        </div>

        <div id="tab3" class="tab-pane">
            <div class="row">
                <asg:labeledField name="cnae" instance="${empresaInstance}" type="number"
                                  value="${empresaInstance.cnae}"/>
            </div>

            <div class="row">
                <asg:labeledTextField name="serial_certificado_digital" instance="${empresaInstance}" maxlength="100"
                                      value="${empresaInstance?.serial_certificado_digital}"/>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: empresaInstance, field: 'filiais', 'error')} ">
                <label class="control-label" for="filiais">
                    <g:message code="empresa.filiais.label" default="Filiais"/>

                </label>

                <div class="controls">

                    <ul class="one-to-many">
                        <g:each in="${empresaInstance?.filiais ?}" var="f">
                            <li><asg:link controller="empresa" action="show"
                                          id="${f.id}">${f?.encodeAsHTML()}</asg:link></li>
                        </g:each>
                        <li class="add">
                            <asg:link controller="empresa" action="create"
                                      params="['empresa.id': empresaInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'empresa.label', default: 'Empresa')])}</asg:link>
                        </li>
                    </ul>

                </div>
            </div>
        </div>

    </div>
</div>