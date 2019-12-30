<%@ page import="java.text.SimpleDateFormat" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
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
            <asg:filter domain="${itemDomain}" params="${params}" noextrafilter="${true}"
                        fields="${[[name: "dataVencimento"], [name: entityName + ".parceiroNegocios"], [name: 'compensadoCompletamente', value: false, operandDefault: "eq"]]}"/>

            <g:form action="compensar">
                <asg:grid controller="item${entityName.capitalize()}" list="${list}"
                          fields="check[CHECKBOX];dataVencimento[DATE];valor[MONEY];valorCompensado[MONEY];${entityName}.parceiroNegocios.apelido"
                          createHiddenFields="true"
                          itemButtons="show"/>

                <div class="widget-box">
                    <div class="widget-title">
                        <span class="icon">
                            <i class="icon icon-th-list"></i>
                        </span>
                        <h5><g:message code="${params.controller}.label" span="6"/></h5>
                    </div>

                    <div class="widget-content">
                        <div class="row">
                            <bootstrap:alert class="alert-danger">Os títulos selecionados serão baixados com a data de hoje ${new java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date())}</bootstrap:alert>
                        </div>
                        <div class="row">
                            <asg:labeledMoney name="totalParcelas"
                                              id="totalParcelas"
                                              forceeditmode="true"
                                              span="3" readonly="true"/>
                            <asg:labeledMoney name="totalCompensar"
                                              id="totalCompensar"
                                              forceeditmode="true"
                                              span="3" readonly="true"/>
                        </div>

                        <div class="row">
                            <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.financeiro.ContaCorrente"
                                                     orderBy="descricao"
                                                     instance="${instance}" id="contaCorrente.id"
                                                     name="contaCorrente.id"
                                                     label="${message(code: 'contaCorrente.label')}"
                                                     optionKey="id"
                                                     forceeditmode="true" span="4"
                                                     required="true" filter='["ativa":true]'
                                                     ignoreGeneralAttrsAdjusts="true"/>

                            <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.FormaPagamento"
                                               instance="${instance}"
                                               id="formaPagamento"
                                               name="formaPagamento.id"
                                               optionKey="id"
                                               value="" class="many-to-one"
                                               noSelection="['null': '']" forceeditmode="true" span="6"
                                               required="true"/>
                        </div>

                        <asg:actionSubmit class="btn btn-success btn-small" action="compensar"
                                          value="${message(code: entityName + '.compensar.label')}"/>
                    </div>
                </div>

            </g:form>

        </div>
    </div>
</div>


<r:script disposition="defer">
    function calculaTotais() {
        var entityName = '${entityName.capitalize()}';
        var divItemName = "divitem" + entityName;
        var checkboxes = $('#' +divItemName+ ' input[type="checkbox"]:checked');
        var totalParcelas = 0;
        var totalCompensar = 0;

        for (i in checkboxes) {
            if (checkboxes[i].name) {
                var name = checkboxes[i].name;
                var index = name.substring(name.indexOf('[') + 1, name.indexOf(']'))

                var elementValorName = 'item' + entityName + '[' + index + '].valor';
                var elementValorCompensadoName = 'item' + entityName + '[' + index + '].valorCompensado';

                var valor = document.getElementById(elementValorName) ? parseFloat(document.getElementById(elementValorName).value) : 0;
                var valorCompensado = document.getElementById(elementValorCompensadoName) ? parseFloat(document.getElementById(elementValorCompensadoName).value) : 0;

                valor = !isNaN(valor) ? valor : 0;
                valorCompensado = !isNaN(valorCompensado) ? valorCompensado : 0;

                totalParcelas += valor;
                totalCompensar += valor - valorCompensado;
            }
        }

        $('#totalParcelas').val(formatCurrency(totalParcelas));
        $('#totalCompensar').val(formatCurrency(totalCompensar));
    }

    var entityName = '${entityName.capitalize()}';
    var divItemName = "divitem" + entityName;

    function checkboxesEvent() {
        $('#' +divItemName+ ' .iCheck-helper').click(function () {
            calculaTotais()
        });

        $('#' +divItemName+ ' input[type="checkbox"]').change(function () {
            calculaTotais()
        });
    }

    onDocumentReady(function () {
        calculaTotais();
        checkboxesEvent();

        $(document).ajaxStop(function () {
            // Esse setTimeout é necessário para esperar os elementos renderizarem apos o ajax
            setTimeout(function() {
                calculaTotais();
                checkboxesEvent();
            }, 1000);
        });
    });
    function formatCurrency(valor){
     return formatDecimal(valor)
    }
</r:script>

</body>
</html>