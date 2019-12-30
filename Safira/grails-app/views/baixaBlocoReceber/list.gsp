<%@ page import="com.br.asgardtecnologia.erp.financeiro.ItemContaReceber" %>
<% def compensacaoItemReceberInstance = new com.br.asgardtecnologia.erp.financeiro.CompensacaoItemReceber() %>

<g:render template='/financeiroBase/baixaBlocoList'
          model="[instance: compensacaoItemReceberInstance, list: itemContaReceberInstanceList,
                  entityName: 'contaReceber', itemDomain: 'com.br.asgardtecnologia.erp.financeiro.ItemContaReceber']"/>