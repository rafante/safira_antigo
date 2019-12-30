<%@ page import="com.br.asgardtecnologia.erp.base.Endereco" %>
<%
    def prefix = ''
    def collapse = 'collapse'
    if (!id) {
        id = 'endereco'
        collapse = ''
    } else {
        prefix = id + '.'
    }

    if (!title) {
        title = 'Endereço'
    }
%>

<r:script type="text/javascript">
  function getEndereco${id}() {
      // Se o campo CEP não estiver vazio
      if($.trim($("#${id}\\.cep").val()) != ""){
          $.getJSON("http://viacep.com.br/ws/"+$("#${id}\\.cep").val()+"/json/", function(cep){
//            console.log(cep);
            jQuery.ajax({
                    type: 'POST',
                    dataType: 'text',
                    url: getUrl('javascript/readEntityFieldValue?domain=com.br.asgardtecnologia.erp.base.Municipio&id=' + cep.ibge),
                    success: function (data) {
                        setAsgAutocompleteValue("#${id}.municipio", cep.ibge, data);
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
            });


            $("#${id}\\.logradouro").val(cep.logradouro);
                      $("#${id}\\.bairro").val(cep.bairro);
                      $("#${id}\\.cidade").val(cep.localidade);
                      $("#${id}\\.numero").focus();
                  });
              }
        }
</r:script>

<div class="widget-box collapsible">
    <div class="widget-title">
        <a href="#${id}" data-toggle="${collapse}">
            <span class="icon"><i class="icon icon-road"></i></span>
            <h5>${title} [${it}]</h5>
        </a>
    </div>

    <div class="${collapse}" id="${id}">
        <div class="widget-content">
            <div class="row">
                <asg:labeledTextField label="${g.message(code: "cep.label")}" name="${prefix}cep"
                                      instance="${instance}" id="${prefix}cep" maxlength="8" value="${it?.cep}"
                                      onblur="getEndereco${id}()" span="6"/>
            </div>
            <div class="row">

                <asg:labeledTextField label="${g.message(code: "logradouro.label")}" name="${prefix}logradouro"
                                      instance="${instance}" id="${prefix}logradouro" maxlength="60"
                                      value="${it?.logradouro}" span="8"/>
            </div>

            <div class="row">
                <asg:labeledTextField label="${g.message(code: "numero.label")}" name="${prefix}numero"
                                      instance="${instance}" id="${prefix}numero" maxlength="10" value="${it?.numero}"
                                      span="6"/>

                <asg:labeledTextField label="${g.message(code: "complemento.label")}"
                                      name="${prefix}complemento" instance="${instance}" id="${prefix}complemento"
                                      maxlength="10" value="${it?.complemento}" span="6"/>
            </div>

            <div class="row">
                <asg:labeledTextField label="${g.message(code: "bairro.label")}" name="${prefix}bairro"
                                      instance="${instance}" id="${prefix}bairro" maxlength="60" value="${it?.bairro}"
                                      span="6"/>

                <asg:labeledAutoComplete label="${g.message(code: "municipio.label")}"
                                         domain="com.br.asgardtecnologia.erp.base.Municipio"
                                         instance="${instance}" id="${prefix}municipio" name="${prefix}municipio.id"
                                         value="${it?.municipio?.id}" span="6"/>
            </div>

            <div class="row">
                <asg:labeledTextField label="${g.message(code: "contato.label")}" name="${prefix}contato"
                                      instance="${instance}" maxlength="20" value="${it?.contato}" span="6"/>
                <asg:labeledTextField label="${g.message(code: "email.label")}" name="${prefix}email"
                                      instance="${instance}" maxlength="60" value="${it?.email}" span="6"/>
            </div>

            <div class="row">
                <asg:labeledTextField label="${g.message(code: "telefone1.label")}" name="${prefix}telefone1"
                                      instance="${instance}" maxlength="15" value="${it?.telefone1}" span="6"/>
                <asg:labeledTextField label="${g.message(code: "telefone2.label")}" name="${prefix}telefone2"
                                      instance="${instance}" maxlength="15" value="${it?.telefone2}" span="6"/>
            </div>

            <div class="row">
                <asg:labeledTextField label="${g.message(code: "celular1.label")}" name="${prefix}celular1"
                                      instance="${instance}" maxlength="15" value="${it?.celular1}" span="6"/>
                <asg:labeledTextField label="${g.message(code: "celular2.label")}" name="${prefix}celular2"
                                      instance="${instance}" maxlength="15" value="${it?.celular2}" span="6"/>
            </div>

        </div>
    </div>
</div>