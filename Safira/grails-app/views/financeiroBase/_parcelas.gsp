<asg:hiddenField name="id" value="${instance.id}"/>

<g:set var="entity" value="${entityName.replace('Conta', '')}"/>
<g:if test="${!instance?."${"itens" + entityName}"}">
    <div class="widget-box">
        <div class="widget-title">
            <span class="icon">
                <i class="icon icon-th-list"></i>
            </span>
            <h5><g:message code="basico.parcelas.label" default="Parcelas"/></h5>
        </div>

        <div class="widget-content">

            <div class="row">
                <asg:labeledCalculatedDate name="primeiroVencimento" data-date-ref="dataDocumento"
                                           value="${params.primeiroVencimento ?: new Date()}"
                                           default="none" noSelection="['': '']" instance="${instance}"/>
            </div>

            <div class="row">
                <asg:labeledField name="numParcelas" type="number" value="${params.numParcelas ?: 1}" span="6"/>
            </div>
        </div>

        <div id="box_periodicidade">
            <g:render template="/periodicidade/frame"/>
        </div>

    </div>
</g:if>

<asg:grid ignorePagination="${true}" name="itens${entityName}" controller="item${entityName}" instance="${instance}"
          list="${instance?.getItens()?.sort({ a, b -> (a.item ?: 0)?.compareTo(b.item ?: 0) })}"
          fields="item;dataVencimento[DATE];diasVencimento;valor[MONEY];multa[MONEY];juros[PERCENT];descontos[PERCENT];valorCompensado[MONEY];valorRestante[MONEY]"
          headerButtons="create"
          itemButtons="show;edit;delete"
          customButtons="[
                  ['href': '#modalCompensar%id%', 'data-toggle': 'modal', 'class': 'btn-default', 'icon': 'icon-ok', 'label': 'compensar.label'],
                  ['href': '#modalProrrogar%id%', 'data-toggle': 'modal', 'class': 'btn-default', 'icon': 'icon-share-alt', 'label': 'prorrogar.label']
          ]"/>

%{--editable="true"--}%
%{--editableFields="dataVencimento;valor;multa;juros;descontos"--}%
%{--headerCreateOnClick="onClickCreateParcela();"--}%

</div>

<!-- CRIA AS MODALS PARA COMPENSAR A PARCELA -->
<g:each in="${instance?."${"itens" + entityName}"}" var="item">
    <g:render template="/itemFinanceiroBase/parcelas_modals" model="[item: item, entity: entity]"/>
</g:each>

<r:script disposition="defer">
    function onClickCreateParcela() {
        $("#numParcelas").val(0);
    }
</r:script>