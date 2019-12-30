<%@ page import="com.br.asgardtecnologia.erp.cadastros.SubGrupoParceiroNegocios" %>

<div class="row">
    <asg:labeledTextField name="descricao" instance="${subGrupoParceiroNegociosInstance}" maxlength="60"
                          value="${subGrupoParceiroNegociosInstance?.descricao}"/>
</div>

    <div class="row">
        <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.GrupoParceiroNegocios" instance="${subGrupoParceiroNegociosInstance}"
                           id="grupo_parceiro" name="grupo_parceiro.id"
                           optionKey="id" value="${subGrupoParceiroNegociosInstance?.grupo_parceiro?.id}" class="many-to-one"
                           noSelection="['null': '']"/>
    </div>
