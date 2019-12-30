<%@ page import="com.br.asgardtecnologia.erp.security.Requestmap" %>

<div class="row">
    <asg:labeledTextField name="url" instance="${requestmapInstance}" value="${requestmapInstance?.url}"/>
</div>

<div class="row">
    <asg:labeledTextField name="configAttribute" instance="${requestmapInstance}"
                          value="${requestmapInstance?.configAttribute}"/>
</div>

          