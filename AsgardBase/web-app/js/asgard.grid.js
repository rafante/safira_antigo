function disableModal(id) {
    $(id).find("input, textarea, select").attr("disabled", true);
}

function enableModal(id) {
    $("input, textarea, select", $(id)).attr("disabled", false);
}

function prepareModal(scope) {
    // Dá a opçao de aplicar só dentro de um escopo
    scope = scope ? scope : document;

    $(".modal", scope).on("shown.bs.modal", function () {
        focusAndSelect($(C_FOCUSABLE, this).filter(C_FOCUSABLE_FILTER).first());
    });

    prepareSpecificModal('.modal-grid');

    disableModal($('.modal[modalcreate="true"]'));
    disableModal($('tr[data-trbase="true"]'));
}

function prepareSpecificModal(selector, scope) {
    // Dá a opçao de aplicar só dentro de um escopo
    scope = scope ? scope : document;

    $(selector, scope).on("shown.bs.modal", function () {
        focusAndSelect($(C_FOCUSABLE, this).filter(C_FOCUSABLE_FILTER).first());
    });

    $(selector, scope).on("hidden.bs.modal", function (e) {
        var $btnModalCreate = $(this).parents(".widget-box").first().find('.asg-modal-create').first();
        $btnModalCreate.focus();

        if (!$(this).data('confirmed')) modalCancel(e, $(this));
    });
}

function prepareDataTable(scope) {
    // Dá a opçao de aplicar só dentro de um escopo
    scope = scope ? scope : document;

    // Prepare Modals
    $('.modal-grid', scope).each(function () {
        var prefix = $(this).attr("name") + ".";
        $('[name]', this).each(function () {
            var name = prefix + $(this).attr("name");
            $(this).attr("name", name);

            // Salva o valor atual do campo em um atributo para recuperar caso a edicao seja cancelada
            $(this).attr("data-oldvalue", $(this).val());
        });
    });

    // Check boxes para select all
    var checkboxClass = 'icheckbox_flat-blue';
    $("span.icon input:checkbox, th input:checkbox", scope).on('ifChecked || ifUnchecked', function () {
        var checkedStatus = this.checked;
        var checkbox = $(this).parents('.widget-box').find('tr td:first-child input:checkbox');
        checkbox.each(function () {
            this.checked = checkedStatus;
            if (checkedStatus == this.checked) {
                $(this).closest('.' + checkboxClass).removeClass('checked');
            }
            if (this.checked) {
                $(this).closest('.' + checkboxClass).addClass('checked');
            }
        });
    });
}

function modalConfirm(e, modal) {
    e.preventDefault();

    $(modal).data("confirmed", true).modal('hide');

    $('body').removeClass('modal-open');
    $('.modal-backdrop').remove();

    // Atualiza os campos no TD de acordo com os valores no Modal
    ajaxFields(modal);

    return false;
}

function modalCancel(e, modal) {
    e.preventDefault();

    var $trbase = $(modal).parents('tr').get(0);

    if (!$($trbase).attr('data-asg-item-id')) $trbase.remove();
}

function modalCreate(btnAdd) {
    var modal = $("div.modal-grid[modalindex='']").first();

    var trbase = $(modal).closest('.widget-box').find('tr[data-trbase="true"]').get(0);
    var clone = $(trbase).clone();
    var controller = $(modal).attr("name").substr(0, $(modal).attr("name").indexOf('['));
    var i = 0;

    var search = $('.modal-grid:not([modalindex=""])', $(trbase).closest('table'));

    $(search).each(function () {
        i = parseInt($(this).attr('modalindex')) > i ? parseInt($(this).attr('modalindex')) : i;
    });

    // So acrescenta um se tiver achado o zero (ou maior) -- Isso evita listas começar do 1 em vez de 0
    if (search.length > 0) {
        i++;
    }

    $(clone).removeAttr('data-trbase');
    $(clone).attr('data-asg-index', i);
    $(clone).attr('data-asg-prefix', $(clone).attr('data-asg-prefix').replace('[]', '[' + i + ']'));

    $('[name]', clone).each(function () {
        if ($(this).attr('name').indexOf('[]')) {
            $(this).attr('name', $(this).attr('name').replace('[]', '[' + i + ']'));
        }
    });

    var modalClone = $('.modal-grid', clone);

    $(modalClone).attr('id', controller + i).attr('modalindex', i);
    $('.btn[data-toggle="modal"]', clone).attr('href', '#' + controller + i);

    $(clone).insertBefore(trbase);
    $(clone).show();

    enableModal("#" + controller + i);

    prepareAutoComplete($(clone));
    prepareDatePicker($(clone));
    preparePercentage($(clone));

    modal = modalClone;

    $modal = $('.modal', clone).first();
    prepareSpecificModal($modal);
    $modal.modal('show');
}

function ajaxGrid(btn, name, max, offset) {
    var container = '#container' + name;
    var attrs = $(container).attr('data-grid-attrs');
    $('#customAttrs').val(attrs);
    var btnBusca = $('#btnBusca').parents('form').first()
    var data = {}
    if (btnBusca == 'undefined' || btnBusca.size() == 0) {
        data.customAttrs = attrs;
    } else {
        data = $('#btnBusca').parents('form').first().serialize();
    }

    //if (max) data = data + "&max=" + max;
    if (max) {
        if (data.propertyIsEnumerable())
            data.max = max;
        else
            data += "&max=" + max;
    }
    //if (offset) data = data + "&offset=" + offset;
    if (offset) {
        if (data.propertyIsEnumerable())
            data.offset = offset;
        else
            data += "&offset=" + offset;
    }

    $.ajax({
        type: 'POST',
        data: data,
        url: '/' + appName + '/grid/gridAjax',
        success: function (data) {
            $(container).html(data);
            prepare();
            /*$(container).fadeOut(function () {

             $(container).fadeIn("fast");

             });*/

        },
        error: function (XMLHttpRequest) {
            var errorDiv = getDivErrorSpinner(XMLHttpRequest)
            $(container).html(errorDiv);

            prepare();
        }
    });

    return false;
}

function ajaxFields(modal) {
    var attrs = $(modal).parents('div[data-grid-attrs]').first().attr('data-grid-attrs');
    var $trAttrs = $(modal).parents('tr[data-asg-prefix]').first();

    var data = $(':input', modal).serializeArray();
    data.push({name: "id", value: $trAttrs.attr('data-asg-item-id')});
    data.push({name: "prefix", value: $trAttrs.attr('data-asg-prefix')});
    data.push({name: "index", value: $trAttrs.attr('data-asg-index')});
    data.push({name: "customAttrs", value: attrs});

    $.ajax({
        type: 'POST',
        dataType: "json",
        url: getUrl('grid/fieldAjax'),
        data: data,
        success: function (data) {
            $.each(data, function () {
                var selector = 'td[name="' + adjustSelector(this.field) + '"]';
                $(selector).html(this.value);
            });
            totalize();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            totalize();
        }
    });
}

function markAsDeleted(btn) {
    var row = $(btn).closest('tr');
    $(row).hide();

    var $inputDeleted = $(btn).nextAll(".asg-deleted").first();
    $inputDeleted.attr("disabled", false);
    $inputDeleted.val("true");

    totalize();
}