function prepareSelect(scope) {
    // Dá a opçao de aplicar só dentro de um escopo
    scope = scope ? scope : document;

    $("select", scope).each(function () {
            var allowClear = $(this).attr("allowClear");
            $(this).change(function () {
                if ($(this).attr("name").indexOf(".op") !== -1) filterOpChange(this);
                executeAsgChain(this);
            });
        }
    );
}

function filterOpChange(opObj) {
    var op = $(opObj).val();
    var parent = $(opObj).parent().parent().parent();

    if (op == 'between') {
        $(parent).find('.asg-to-high, div#high').removeClass('asg-hide');
    }
    else {
        $(parent).find('.asg-to-high, div#high').addClass('asg-hide');
    }
}

function executeAsgChainGeneric(chainDomainName, chainFieldName, chainTargetName, selector) {
    var chainField = $(selector).attr(chainFieldName);
    if (chainField) {
        var chainTarget = $(selector).attr(chainTargetName);

        var id = $(selector).val();
        var domain = $(selector).attr(chainDomainName) ? $(selector).attr(chainDomainName) : $(selector).attr('domain');
        var selectorName = $(selector).attr('name');

        jQuery.ajax({
            type: 'POST',
            dataType: 'json',
            url: getUrl('javascript/readEntityFieldValue?domain=' + domain + '&id=' + id + '&fields=' + chainField),
            success: function (data, textStatus) {
                var chainFieldsArray = chainField.split(";");
                var chainTargetArray = chainTarget.split(";");

                for (var index in chainFieldsArray) {
                    // Seletor padrão: #campo
                    var selector = adjustSelector('#' + chainTargetArray[index]);

                    // Caso seja uma linha de um grid
                    if (selectorName.indexOf(']')) {
                        var endingBracketIndex = selectorName.indexOf(']');
                        var prefix = selectorName.substring(0, endingBracketIndex + 1);
                        if (prefix) prefix += '.'
                        selector = '[name="' + prefix + chainTargetArray[index] + '"],' +
                        '[name="' + prefix + chainTargetArray[index] + '.id"]';
                    }

                    var element = $(selector);
                    var is_element_select = $(element).is("select");

                    if (is_element_select) {
                        $('#' + chainTargetArray[index] + " option").filter(function () {
                            return $(this).val() == data[chainFieldsArray[index]];
                        }).prop('selected', true);
                    } else if ($(element).is("input[type='checkbox']")) {
                        if (data[chainFieldsArray[index]]) $(element).iCheck('check');
                        else $(element).iCheck('uncheck');
                    } else {
                        var strValue = data[chainFieldsArray[index]];

                        if (typeof strValue == 'object') {
                            strValue = JSON.stringify(strValue);
                        }

                        if (strValue == "null") strValue = ""

                        if ($(element).hasClass('decimal') || $(element).hasClass('money')) {
                            strValue = formatDecimal(strValue);
//                            alert(strValue);
                        }

                        $(element).val(strValue);
                    }

                    $(element).change();
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });
    }
}

function executeAsgChain(selector) {
    executeAsgChainGeneric('asg-domain-field', 'asg-chain-field', 'asg-chain-target', selector);
}

function onSelectHolderList(obj) {
    var newOptions = $.parseJSON($(obj).val());

    var select = $(obj).prevAll("select").first();

    $(select).html("");

    var cb = '<option value="null"></option>';
    $.each(newOptions, function (i, data) {
        var selected = data.selected ? ' selected="selected"' : "";
        cb += '<option value="' + data.id + '"' + selected + '>' + data.value + '</option>';
    });

    $(select).html(cb);
}