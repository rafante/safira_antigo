<%@ page import="com.br.asgardtecnologia.erp.security.Usuario" %>

<div class="row">
    <asg:labeledTextField name="username" instance="${usuarioInstance}" value="${usuarioInstance?.username}" required=""
                          onblur="getParceiroNegocios()" span="6"/>

    <g:if test="${params.action != "show"}">
        <asg:labeledPasswordField name="password" instance="${usuarioInstance}" value="${usuarioInstance?.password}"
                                  required="" span="6"/>
    </g:if>
</div>

<div class="row">
    <asg:labeledTextField name="nome" instance="${usuarioInstance}" value="${usuarioInstance?.nome}" required=""/>
</div>

<div class="row">
    <asg:labeledTextField name="email" instance="${usuarioInstance}" value="${usuarioInstance?.email}" required=""/>
</div>

<g:if test="${params.action != "registro"}">
    <div class="row">
        <asg:labeledSelect domain="com.br.asgardtecnologia.erp.base.Empresa" instance="${usuarioInstance}"
                           id="empresa" name="empresa.id"
                           optionKey="id" value="${usuarioInstance?.empresa?.id}" class="many-to-one"
                           noSelection="['null': '']"/>
    </div>

    <div class="row">
        <asg:labeledTextField name="documento" instance="${usuarioInstance}" value="${usuarioInstance?.documento}"
                              span="4"/>

        <asg:labeledCheckBox name="accountExpired" instance="${usuarioInstance}"
                             value="${usuarioInstance?.accountExpired}" span="4"/>

        <asg:labeledCheckBox name="accountLocked" instance="${usuarioInstance}"
                             value="${usuarioInstance?.accountLocked}" span="4"/>
    </div>

    <div class="row">
        <asg:labeledCheckBox name="enabled" instance="${usuarioInstance}" value="${usuarioInstance?.enabled}" span="4"/>

        <asg:labeledCheckBox name="passwordExpired" instance="${usuarioInstance}"
                             value="${usuarioInstance?.passwordExpired}" span="4"/>
    </div>
</g:if>

<div class="row">
    <asg:link class="btn btn-default" controller="usuarioPerfil" action="edit"
              id="${usuarioInstance?.id}">Editar Perfil</asg:link>
</div>

<r:script disposition="defer">
    function getParceiroNegocios() {
        if (!$("#username").val()) return;

        jQuery.ajax({
            type: 'POST',
            dataType: 'text',
            url: '/AsgardErp/parceiro_negocios/getParceiroNegocios?cnpj_cpf=' + unescape($("#username").val()),
            success: function (data, textStatus) {
                var json = jQuery.parseJSON(data);

                $("#nome").val(json.nome);
                $("#ie").val(json.ie);
                $("#suframa").val(json.suframa);
                $("#end").val(json.end);
                $("#num").val(json.num);
                $("#compl").val(json.compl);
                $("#cep").val(json.cep);
                $("#bairro").val(json.bairro);

                ///
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'text',
                    url: getUrl('municipio/getMunicipio?estado=' + unescape(json.municipio.uf) + '&municipio=' + unescape(json.municipio.nome)),
                    success: function (data, textStatus) {
                        json = jQuery.parseJSON(data);
                        jQuery('#municipio\\.id').val(json.id);
                        jQuery('#municipio__id').val(json.nome + " / " + json.uf);
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }});
                ///
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });
    }
</r:script>

<r:script disposition="defer">
    $("#formregistro").validate({
        rules: {
            password: {
                required: true
            },
            password2: {
                equalTo: "#password"
            }
        },
        errorClass: "help-inline",
        errorElement: "span",
        highlight: function (element, errorClass, validClass) {
            $(element).parents('.control-group').addClass('error');
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).parents('.control-group').removeClass('error');
            $(element).parents('.control-group').addClass('success');
        }
    });
</r:script>