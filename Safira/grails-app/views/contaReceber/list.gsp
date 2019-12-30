<%@ page import="com.br.asgardtecnologia.erp.financeiro.ContaReceber" %>
<g:render template="/financeiroBase/list"
          model="[instance: contaReceberInstanceList, entityName: 'ContaReceber', tipoParceiroNegocios: 'cliente']"/>