<%@ page import="com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios" %>

<div class="widget-box">
    <div class="widget-title">
        <ul class="nav nav-tabs">
            <li class="active"><a data-toggle="tab" href="#tab1">Dados Básicos</a></li>
            <li><a data-toggle="tab" href="#tab2">Endereços</a></li>
            <li><a data-toggle="tab" href="#tab3">Histórico de Contato</a></li>
        </ul>
    </div>

    <div class="widget-content tab-content">
        <div id="tab1" class="tab-pane active">

            <div class="row">
                <asg:labeledCheckBox name="cliente" instance="${parceiroNegociosInstance}"
                                     value="${parceiroNegociosInstance?.cliente}" asg-hide-if-false="box_cliente"
                                     span="4"/>
                <asg:labeledCheckBox name="fornecedor" instance="${parceiroNegociosInstance}"
                                     value="${parceiroNegociosInstance?.fornecedor}" span="4"/>

            </div>

            <div class="row">
                <asg:labeledCheckBox name="representanteVenda" instance="${parceiroNegociosInstance}"
                                     value="${parceiroNegociosInstance?.representanteVenda}"
                                     asg-hide-if-false="box_representantevenda" span="4"/>
                <asg:labeledCheckBox name="transportadora" instance="${parceiroNegociosInstance}"
                                     value="${parceiroNegociosInstance?.transportadora}" span="4"/>
            </div>

            <div class="row">
                <asg:labeledTextField name="nome" instance="${parceiroNegociosInstance}" maxlength="60"
                                      value="${parceiroNegociosInstance?.nome}" span="12"/>

            </div>
            <div class="row">
                <asg:labeledTextField name="apelido" instance="${parceiroNegociosInstance}"
                                      value="${parceiroNegociosInstance?.apelido}" span="12"/>
            </div>

            <div class="row">
                <asg:labeledTextField name="cnpj_cpf" instance="${parceiroNegociosInstance}" maxlength="14"
                                      value="${parceiroNegociosInstance?.cnpj_cpf}" span="6"/>

                <asg:labeledTextField name="inscricao_estadual" instance="${parceiroNegociosInstance}" maxlength="14"
                                      value="${parceiroNegociosInstance?.inscricao_estadual}" span="6"/>
            </div>

            <div class="row">
                <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.GrupoParceiroNegocios" id="grupo_parceiro"
                                   name="grupo_parceiro.id" instance="${parceiroNegociosInstance}"
                                   optionKey="id"
                                   value="${parceiroNegociosInstance?.grupo_parceiro?.id}" class="many-to-one"
                                   noSelection="['null': '']" span="6"/>
                <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.SubGrupoParceiroNegocios" id="sub_grupo_parceiro"
                                   name="sub_grupo_parceiro.id" instance="${parceiroNegociosInstance}"
                                   optionKey="id"
                                   value="${parceiroNegociosInstance?.sub_grupo_parceiro?.id}" class="many-to-one"
                                   noSelection="['null': '']" span="6"/>
            </div>

            <div class="row">
                <asg:labeledDatePicker name="data_inclusao" instance="${parceiroNegociosInstance}" maxlength="14"
                                       value="${parceiroNegociosInstance?.data_inclusao}" forceshowmode="true"/>
            </div>

            <!-- Cliente -->
            <div id="box_cliente" class="widget-box collapsible">
                <div class="widget-title">
                    <span class="icon"><i class="icon icon-user"></i></span>
                    <h5>Dados de Cliente</h5>
                </div>

                <div class="widget-content">
                    <div class="row">
                        <asg:labeledField name="credito" instance="${parceiroNegociosInstance}"
                                          value="${fieldValue(bean: parceiroNegociosInstance, field: 'credito')}"
                                          span="6"/>

                        <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.TabelaPreco" id="tabela_preco"
                                           name="tabela_preco.id" instance="${parceiroNegociosInstance}"
                                           optionKey="id"
                                           value="${parceiroNegociosInstance?.tabela_preco?.id}" class="many-to-one"
                                           noSelection="['null': '']" span="6"/>
                    </div>
                </div>
            </div>

            <!-- Representante de Venda -->
            <div id="box_representantevenda" class="widget-box collapsible">
                <div class="widget-title">
                    <span class="icon"><i class="icon icon-shopping-cart"></i></span>
                    <h5>Dados do Representante de Venda</h5>
                </div>

                <div class="widget-content">

                    <div class="row">
                        <asg:labeledField name="percentual_comissao" instance="${parceiroNegociosInstance}"
                                          value="${fieldValue(bean: parceiroNegociosInstance, field: 'percentual_comissao')}"
                                          span="6"/>

                        <asg:labeledSelect name="tipo_comissao" instance="${parceiroNegociosInstance}"
                                           from="${com.br.asgardtecnologia.erp.cadastros.TipoComissao?.values()}"
                                           keys="${com.br.asgardtecnologia.erp.cadastros.TipoComissao.values()*.name()}"
                                           value="${parceiroNegociosInstance?.tipo_comissao?.name()}"
                                           noSelection="['': '']" span="6"/>
                    </div>

                    <div class="row">
                        <asg:labeledSelect name="tipo_vendedor" instance="${parceiroNegociosInstance}"
                                           from="${com.br.asgardtecnologia.erp.cadastros.TipoVendedor?.values()}"
                                           keys="${com.br.asgardtecnologia.erp.cadastros.TipoVendedor.values()*.name()}"
                                           value="${parceiroNegociosInstance?.tipo_vendedor?.name()}"
                                           noSelection="['': '']" span="6"/>
                        <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.PrazoPagamento"
                                           instance="${parceiroNegociosInstance}" id="prazo_pagamento_default"
                                           name="prazo_pagamento_default.id" optionKey="id"
                                           value="${parceiroNegociosInstance?.prazo_pagamento_default?.id}"
                                           class="many-to-one" noSelection="['null': '']" span="6"/>
                    </div>
                </div>
            </div>

            <!-- Transportadora -->

        </div>

        <div id="tab2" class="tab-pane">
            <g:render template="/endereco/form"
                      model="${[id: 'endereco_comercial', title: 'Endereço Comercial', instance: parceiroNegociosInstance]}"
                      bean="${parceiroNegociosInstance?.endereco_comercial}" plugin="asgard-base"/>
            <g:render template="/endereco/form"
                      model="${[id: 'endereco_cobranca', title: 'Endereço Cobrança', instance: parceiroNegociosInstance]}"
                      bean="${parceiroNegociosInstance?.endereco_cobranca}" plugin="asgard-base"/>
            <g:render template="/endereco/form"
                      model="${[id: 'endereco_entrega', title: 'Endereço Entrega', instance: parceiroNegociosInstance]}"
                      bean="${parceiroNegociosInstance?.endereco_entrega}" plugin="asgard-base"/>
        </div>

        <div id="tab3" class="tab-pane">
            <div class="row">
                <asg:grid ignorePagination="${true}" controller="historicoContato"
                          list="${parceiroNegociosInstance?.historicoContato}"
                          fields="contato;data;historico"
                          headerButtons="create" itemButtons="show;edit;delete;erros"/>
            </div>
        </div>

    </div>
</div>