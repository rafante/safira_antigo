<%@ page import="com.br.asgardtecnologia.erp.financeiro.ItemContaPagar" %>
<%
    def itemContaPagarList = ItemContaPagar.withCriteria {
        def localDate = new org.joda.time.LocalDate(new Date())

        def inicio = localDate.toDateTimeAtStartOfDay().toDate()
        def fim = localDate.plusDays(1).toDateTimeAtStartOfDay().minusSeconds(1).toDate()

        eq("compensadoCompletamente", false)
        between("dataVencimento", inicio, fim)
    }

    def totalRestante = 0
    for (ItemContaPagar itemContaPagar in itemContaPagarList) totalRestante += itemContaPagar.valorRestante
%>
<center>Total: <b><g:formatNumber number="${totalRestante}" type="currency" currencyCode="BRL"></g:formatNumber></b></center>
<asg:grid name="instance" controller="itemContaPagar" ignoreWidget="${true}" style="'font-family': 'Verdana'"
          list="${itemContaPagarList?.sort({ a, b -> a.documentoParcela?.compareTo(b.documentoParcela) })}"
          fields="contaPagar.parceiroShort;dataVencimento[DATE];valorRestante[QUANTITY]"/>