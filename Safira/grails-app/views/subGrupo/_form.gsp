<%@ page import="com.br.asgardtecnologia.erp.cadastros.SubGrupo" %>

<div class="row">
    <asg:labeledTextField name="descricao" instance="${subGrupoInstance}" maxlength="60"
                          value="${subGrupoInstance?.descricao}"/>
</div>

    <div class="row">
        <asg:labeledSelect domain="com.br.asgardtecnologia.erp.cadastros.Grupo" instance="${subGrupoInstance}"
                           id="grupo" name="grupo.id"
                           optionKey="id" value="${subGrupoInstance?.grupo?.id}" class="many-to-one"
                           noSelection="['null': '']"/>
    </div>