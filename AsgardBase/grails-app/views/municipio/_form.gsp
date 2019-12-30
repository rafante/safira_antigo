<%@ page import="com.br.asgardtecnologia.erp.base.Municipio" %>

<div class="row">
    <asg:labeledTextField name="nome" instance="${municipioInstance}" maxlength="60"
                          value="${municipioInstance?.nome}"/>
</div>

<div class="row">
    <asg:labeledSelect domain="com.br.asgardtecnologia.erp.base.Estado" instance="${municipioInstance}" id="estado"
                       name="estado.id" optionKey="id"
                       value="${municipioInstance?.estado?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>