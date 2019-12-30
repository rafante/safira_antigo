<div class="row">
    <asg:labeledAutoComplete domain="com.br.asgardtecnologia.erp.security.Usuario" instance="${instance}"
                             id="usuario" name="usuario.id" span="12"
                             optionKey="id" value="${instance?.usuario?.id}" class="many-to-one"/>
</div>

<div class="row">
    <asg:labeledCheckBox id="cb_admin" name="isAdmin" label="${message(code: 'admin.label')}" span="6" value="${instance?.isAdmin}"/>

    <asg:labeledCheckBox id="cb_chkall" label="${message(code: 'selecionaTudo.label')}" span="6"/>
</div>

<div class="row" style="overflow: auto;">
    <asg:grid name="perfil" titleDefault="Perfis" ignorePagination="${true}" list="${instance?.perfis}"
              fields="${instance?.fields}" editable="true" editableFields="${instance?.editableFields}"
              labelIfReadonly="true" ignoreWidget="false" horizontalBar="true"/>
</div>

<r:script disposition="defer">
    $(function () {
        $("#cb_chkall").on('ifChecked || ifUnchecked', function () {
            var checkedStatus = this.checked;
            cbCheck('table', checkedStatus);
        });
    });
</r:script>