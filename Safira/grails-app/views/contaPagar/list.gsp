<%@ page import="com.br.asgardtecnologia.erp.financeiro.ContaPagar" %>
<g:render template="/financeiroBase/list"
          model="[instance: contaPagarInstanceList, entityName: 'ContaPagar', tipoParceiroNegocios: 'fornecedor']"/>