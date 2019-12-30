<%@ page import="com.br.asgardtecnologia.erp.cadastros.TipoDocumento" %>


<div class="row">
    <asg:labeledTextField name="descricao" instance="${tipoDocumentoInstance}" maxlength="60"
                          value="${tipoDocumentoInstance?.descricao}"/>
</div>