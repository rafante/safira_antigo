<%@ page import="com.br.asgardtecnologia.erp.cadastros.TabelaPreco" %>

<div class="row">
    <asg:labeledTextField name="descricao" instance="${tabelaPrecoInstance}" value="${tabelaPrecoInstance?.descricao}"/>
</div>
<div class="row">
    <asg:labeledDecimal name="margem_sugerida" instance="${tabelaPrecoInstance}"
                        value="${tabelaPrecoInstance?.margem_sugerida}" required="" span="4"/>
    <asg:labeledDecimal name="margem_minima" instance="${tabelaPrecoInstance}"
                        value="${tabelaPrecoInstance?.margem_minima}" required="" span="4"/>
</div>


          