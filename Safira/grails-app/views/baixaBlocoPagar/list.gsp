<%@ page import="com.br.asgardtecnologia.erp.financeiro.ItemContaPagar" %>
<% def compensacaoItemPagarInstance = new com.br.asgardtecnologia.erp.financeiro.CompensacaoItemPagar() %>

<g:render template='/financeiroBase/baixaBlocoList'
          model="[instance: compensacaoItemPagarInstance, list: itemContaPagarInstanceList,
                  entityName: 'contaPagar', itemDomain: 'com.br.asgardtecnologia.erp.financeiro.ItemContaPagar']"/>