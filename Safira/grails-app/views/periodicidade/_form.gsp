<%@ page import="com.br.asgardtecnologia.erp.config.Periodicidade" %>

<div class="row">
    <asg:labeledField name="ordem" type="number" instance="${periodicidadeInstance}"
                      value="${periodicidadeInstance.ordem}" required=""/>
</div>

<div class="row">
    <asg:labeledTextField name="descricao" maxlength="60" instance="${periodicidadeInstance}"
                          value="${periodicidadeInstance?.descricao}"/>
</div>

<div class="row">
    <asg:labeledCheckBox name="diario" instance="${periodicidadeInstance}" value="${periodicidadeInstance?.diario}"
                         asg-hide-if-true="box_periodicidade"/>
</div>

<div id="box_periodicidade">
    <div class="row">
        <asg:labeledField name="dias" type="number" instance="${periodicidadeInstance}"
                          value="${periodicidadeInstance.dias}" required=""/>
    </div>

    <div class="row">
        <asg:labeledField name="meses" type="number" instance="${periodicidadeInstance}"
                          value="${periodicidadeInstance.meses}" required=""/>
    </div>

    <div class="row">
        <asg:labeledField name="anos" type="number" instance="${periodicidadeInstance}"
                          value="${periodicidadeInstance.anos}" required=""/>
    </div>
</div>

<r:script disposition="defer">
    onDocumentReady(function () {
        $("#diario").on('ifChecked', function (event) {
            $("#box_periodicidade input").val(0);
            $("#box_periodicidade").hide();
        });

        $("#diario").on('ifUnchecked', function (event) {
            $("#box_periodicidade").show();
        });
    });
</r:script>