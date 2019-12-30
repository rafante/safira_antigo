<%@ page import="com.br.asgardtecnologia.erp.config.Periodicidade" %>

<div class="widget-box">
    <div class="widget-title">
        <span class="icon">
            <i class="icon icon-th-list"></i>
        </span>
        <h5><g:message code="basico.periodicidade.label" default="Periodicidade"/></h5>
    </div>

    <div class="widget-content">
        <div class="row">
            <div class="col-12 col-sm-8 control-group fieldcontain ${hasErrors(bean: instance, field: 'periodicidade', 'error')} ">
                <label class="control-label" for="numParcelas">

                    <g:message code="${params.controller}.periodicidade.label" default="Periodicidade"/>

                </label>

                <div class="controls">
                    <asg:select domain="com.br.asgardtecnologia.erp.config.Periodicidade" id="planoContas"
                                name="periodicidade.id"
                                optionKey="id"
                                asg-chain-field="diario" asg-chain-target="periodicidade_diario" class="many-to-one"/>
                    <input type="text" id="periodicidade_diario" name="periodicidade_diario" style="display:none"/>
                </div>
            </div>

            <div class="col-12 col-sm-4 control-group fieldcontain ${hasErrors(bean: instance, field: 'diasPeriodicidade', 'error')} ">
                <div id="box_diasPeriodicidade">
                    <label class="control-label" for="diasPeriodicidade">

                        <g:message code="${params.controller}.diasPeriodicidade.label" default="Dias"/>

                    </label>

                    <div class="controls">
                        <asg:field name="diasPeriodicidade" type="number" value="${params.diasPeriodicidade}"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<r:script disposition="defer">
    onDocumentReady(function () {
        $("#periodicidade_diario").bind('change', function () {
            if ($(this).val() == "true") {
                $("#box_diasPeriodicidade").show("slow", function () {
                    $('#diasPeriodicidade').focus()
                });
            } else {
                $("#box_diasPeriodicidade").hide("slow");
            }

        });

    });

</r:script>