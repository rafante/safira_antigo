<%@ page import="com.br.asgardtecnologia.erp.materiais.TabelaPrecoMaterial" %>

<div class="row">
    <asg:hiddenField name="material.id" value="${tabelaPrecoMaterialInstance?.material?.id}"/>
    <asg:labeledAutoComplete id="material" name="material.id" instance="${tabelaPrecoMaterialInstance}"
                             valuefield="descricao"
                             listfields="id;descricao"
                             domain="com.br.asgardtecnologia.erp.materiais.Material"
                             value="${tabelaPrecoMaterialInstance?.material?.id}" span="6"
                             forceshowmode="true"></asg:labeledAutoComplete>
</div>

<div class="row">
    <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.TabelaPreco"
                       instance="${tabelaPrecoMaterialInstance}" id="tabela" name="tabela.id"
                       optionKey="id"
                       value="${tabelaPrecoMaterialInstance?.tabela?.id}" class="many-to-one"/>
</div>

<div class="row">
    <asg:labeledMoney name="valor" instance="${tabelaPrecoMaterialInstance}"
                      value="${tabelaPrecoMaterialInstance?.valor}" span="4"/>
    <asg:labeledCheckBox name="fixar_preco" instance="${tabelaPrecoMaterialInstance}"
                         value="${tabelaPrecoMaterialInstance?.fixar_preco}" span="3"/>
</div>

<div class="row">
    <asg:labeledDecimal name="margem" instance="${tabelaPrecoMaterialInstance}"
                        value="${tabelaPrecoMaterialInstance?.margem}" required="" span="4"/>
</div>

<r:script disposition="defer">
    function setFixar(value) {
        $("#valor").attr('readonly', value);
        $("#margem").attr('readonly', !value);
    }

    onDocumentReady(function () {
        var obj = $("#fixar_preco");
        $(obj).on('ifChecked', function(event){
            setFixar(false);
        });

        $(obj).on('ifUnchecked', function(event){
            setFixar(true);
        });

        setFixar(${tabelaPrecoMaterialInstance?.fixar_preco ? 'false' : 'true'});
    });

</r:script>