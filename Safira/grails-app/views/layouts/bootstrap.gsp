<%@ page import="org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    
    <meta charset="utf-8">
    <title><g:layoutTitle default="${meta(name: 'app.name')}"/></title>
    <meta name="description" content="">
    <meta name="author" content="">
    <meta name="viewport" content="width=device-width,initial-scale = 1.0">
    <meta name="appContext" content="${meta(name: 'app.name')}"/>

    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
    <r:script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></r:script>
    <![endif]-->
    <!-- Le fav and touch icons -->
    %{--<link rel="shortcut icon" href="${resource(dir: 'img', file: 'favicon.ico')}" type="image/x-icon">--}%
    <g:layoutHead/>
    <r:require modules="forms"/>
    <r:layoutResources disposition="head"/>
</head>

<body><div class="container-fluid">
    <%
        def version = g.meta([name: "app.version"])
    %>
    <div id="header">
        <h1 style="background: url('${resource(dir: 'img', file: 'safira_logo.png')}') no-repeat scroll 0 0 transparent;">
            <a href="./dashboard.html"><g:message code="system.title" default=""></g:message></a>
        </h1>
        <a id="menu-trigger" href="#"><i class="icon icon-list"></i></a>
    </div>

    <div id="user-nav">
        <ul class="btn-group">
            <li class="btn alert-warning"><asg:link>Safira Lab - versão: ${version}</asg:link></li>
            <li class="btn"><asg:link controller='usuario'><i class="icon icon-user"></i> <span
                    class="text"><sec:username/></span></asg:link></li>
            <li class="btn"><asg:link controller='logout'><i class="icon icon-share-alt"></i> <span
                    class="text">Logout</span></asg:link></li>

        </ul>
    </div>

    <div id="sidebar">
        <ul>
            <li>
                <asg:link controller="dashboard"><i class="icon icon-home"></i>
                    <span><g:message code="dashboard.label" default="Dashboard"/></span>
                </asg:link>
            </li>

            <li class="submenu">
                <a href="#"><i class="icon icon-pencil"></i> <span><g:message code="erp.menu.cadastro"
                                                                              default="Cadastro"/></span></a>
                <ul>
                    <asg:menuitem controller="empresa"/>
                    <asg:menuitem controller="parceiroNegocios"/>
                    <asg:menuitem controller="grupoParceiroNegocios"/>
                    <asg:menuitem controller="subGrupoParceiroNegocios"/>
                    <asg:menuitem controller="centroCusto"/>
                    <asg:menuitem controller="planoContas"/>
                    <asg:menuitem controller="formaPagamento"/>
                    <asg:menuitem controller="prazoPagamento"/>
                    <asg:menuitem controller="tipoDocumento"/>
                    <asg:menuitem controller="municipio"/>
                    <asg:menuitem controller="setor"/>
                </ul>
            </li>

            <li class="submenu">
                <a href="#"><i class="icon icon-tags"></i> <span><g:message code="erp.materiais.label"
                                                                            default="Estoque"/></span></a>
                <ul>
                    <asg:menuitem controller="material"/>
                    <asg:menuitem controller="entradaMaterial"/>
                    <asg:menuitem controller="movimentoMaterial"/>
                    <asg:menuitem controller="unidadeMedida"/>
                    <asg:menuitem controller="grupo"/>
                    <asg:menuitem controller="subGrupo"/>
                    <asg:menuitem controller="localizacao"/>
                    <asg:menuitem controller="recipiente"/>
                    <asg:menuitem controller="material" action="importer"/>
                </ul>
            </li>

            <li class="submenu">
                <a href="#"><i class="icon icon-calendar"></i> <span><g:message
                        code="erp.menu.financeiro.label"
                        default="Financeiro"/></span></a>
                <ul>
                    <asg:menuitem controller="contaPagar"/>
                    <asg:menuitem controller="baixaBlocoPagar"/>
                    <asg:menuitem controller="contaReceber"/>
                    <asg:menuitem controller="baixaBlocoReceber"/>
                    <asg:menuitem controller="banco"/>
                    <asg:menuitem controller="historicoPadrao"/>
                    <asg:menuitem controller="contaCorrente"/>
                    <asg:menuitem controller="transferenciaConta"/>
                    <asg:menuitem controller="movimentoFinanceiro"/>
                    <asg:menuitem controller="customBoleto"/>
                    <asg:menuitem controller="itemContaReceber" action="arquivoRemessa"/>
                </ul>
            </li>

            <li class="submenu">
                <a href="#"><i class="icon icon-archive"></i> <span><g:message
                        code="erp.menu.exame.label"
                        default="Exame"/></span></a>
                <ul>
                    <asg:menuitem controller="exame"/>
                    <asg:menuitem controller="movimentoExame"/>
                    <asg:menuitem controller="material" action="materiaisPorExame"/>
                    <asg:menuitem controller="exame" action="importarXML"/>
                </ul>
            </li>

            <li class="submenu">
                <a href="#"><i class="icon icon-puzzle-piece"></i> <span><g:message
                        code="erp.menu.ev.label"
                        default="Esmeralda Visual"/></span></a>
                <ul>
                    <asg:menuitem controller="movimentoFinanceiro" action="importarCaixaEV"/>
                    <asg:menuitem controller="contaReceber" action="importarNFe"/>
                    <asg:menuitem controller="contaReceber" action="importarFaturas"/>
                    <asg:menuitem controller="movimentoExame" action="importarMovimentos"/>
                </ul>
            </li>

            <li class="submenu">
                <a href="#"><i class="icon icon-file-text-alt"></i> <span><g:message code="erp.menu.reports.label"
                                                                                     default="Relatórios"/></span>
                </a>
                <ul>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.materiais.LDM" reportController="LDM"
                                        reportAction="report"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.materiais.Material"
                                        reportController="Material"
                                        reportAction="listagem"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.materiais.Material"
                                        reportController="material"
                                        reportAction="inventario"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.materiais.Material"
                                        reportController="material"
                                        reportAction="estoque_minimo"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.materiais.Material"
                                        reportController="material"
                                        reportAction="estoque_maximo"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.materiais.ItemMovimentoMaterial"
                                        reportController="itemMovimentoMaterial"
                                        reportAction="estoquePorCentroCusto"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.materiais.ItemMovimentoMaterial"
                                        reportController="itemMovimentoMaterial"
                                        reportAction="relatorio_movimentacao"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.materiais.ItemMovimentoMaterial"
                                        reportController="itemMovimentoMaterial"
                                        reportAction="relatorio_movimentacao_sintetico"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.financeiro.ItemContaPagar"
                                        reportController="itemContaPagar"
                                        reportAction="relatorio_analitico"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.financeiro.ItemContaReceber"
                                        reportController="itemContaReceber"
                                        reportAction="relatorio_analitico"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios"
                                        reportController="parceiroNegocios"
                                        reportAction="cliente_analitico"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.financeiro.MovimentoFinanceiro"
                                        reportController="movimentoFinanceiro"
                                        reportAction="relatorio_movimentacao"/>
                    %{--<asg:menuitemReport domain="com.br.asgardtecnologia.erp.materiais.Material"
                                        reportController="movimentoFinanceiro"
                                        reportAction="boleto"/>--}%
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.financeiro.PlanoContas"
                                        reportController="planoContas"
                                        reportAction="listagem"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.financeiro.Balancete"
                                        reportController="planoContas"
                                        reportAction="balancete"/>
                    <asg:menuitemReport domain="com.br.asgardtecnologia.erp.materiais.ItemMovimentoMaterial"
                                        reportController="itemMovimentoMaterial"
                                        reportAction="usedMaterials"/>
                </ul>
            </li>

            <li class="submenu">
                <a href="#"><i class="icon icon-user"></i> <span><g:message code="erp.menu.seguranca.label"
                                                                            default="Segurança"/></span></a>
                <ul>
                    <asg:menuitem controller="usuario"/>
                </ul>
            </li>

            <li class="submenu">
                <a href="#"><i class="icon icon-wrench"></i> <span><g:message code="erp.menu.config.label"
                                                                              default="Configurações"/></span>
                </a>
                <ul>
                    <asg:menuitem controller="periodicidade"/>
                    <asg:menuitem controller="parametrosMateriais"/>
                    <asg:menuitem controller="parametrosGerais"/>
                    <asg:menuitem controller="parametrosFinanceiro"/>
                    <asg:menuitem controller="parametrosEV"/>
                    <asg:menuitem controller="correcoes"/>
                </ul>
            </li>
        </ul>
    </div>

    <div id="style-switcher" style="display:none">
        <i class="icon icon-arrow-left icon-white"></i>
        <span><g:message code="default.style" default="Estilo"/></span>
        <a href="#grey" style="background-color: #555555;border-color: #aaaaaa;"></a>
        <a href="#blue" style="background-color: #2D2F57;"></a>
        <a href="#red" style="background-color: #673232;"></a>
    </div>

    <div id="content-main">
        <div id="content">
            <g:render template="/layouts/content-header" plugin="asgard-base"/>
            <asg:breadcrumb/>

            <div id="content-body">
                <g:layoutBody/>
            </div>

        </div>

        <div id="content-modal"></div>

    </div>
</div>

<r:layoutResources disposition="defer"/>
</body>
</html>