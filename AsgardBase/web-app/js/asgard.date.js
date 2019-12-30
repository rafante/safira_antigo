function prepareDatePickerOnce() {
    /* Portuguese initialisation for the jQuery UI date picker plugin. */
    $.datepicker.regional['pt'] = {
        closeText: 'Fechar',
        prevText: '<Anterior',
        nextText: 'Seguinte',
        currentText: 'Hoje',
        monthNames: ['Janeiro', 'Fevereiro', 'Mar&ccedil;o', 'Abril', 'Maio', 'Junho',
            'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
        monthNamesShort: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun',
            'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'],
        dayNames: ['Domingo', 'Segunda-feira', 'Ter&ccedil;a-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'S&aacute;bado'],
        dayNamesShort: ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'S&aacute;b'],
        dayNamesMin: ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'S&aacute;b'],
        weekHeader: 'Sem',
        dateFormat: 'dd/mm/yy',
        firstDay: 0,
        isRTL: false,
        showMonthAfterYear: false,
        yearSuffix: ''
    };
    $.datepicker.setDefaults($.datepicker.regional['pt']);
}

function prepareDatePicker(scope) {
    // Dá a opçao de aplicar só dentro de um escopo
    scope = scope ? scope : document;

    $(".date", scope).datepicker();
    $(".date", scope).datepicker("option", "showAnim", "");

    $(".date", scope).autocompletedate();

    $(".date", scope).each(function () {
        splitDate(this);
    });

    $(".date", scope).change(function () {
        splitDate(this);
    });

    $(".date", scope).blur(function () {
        $(this).change();
    });

    prepareCalculatedDate(scope);
}

function prepareCalculatedDate(scope) {
    // Dá a opçao de aplicar só dentro de um escopo
    scope = scope ? scope : document;

    $('.asg-dias-calc', scope).each(function () {
        $(this).blur(function () {
            // Recupera a data que será atualizada (Ex.: Vencimento)
            var dateFieldName = adjustSelector($(this).attr('data-date-field') + "_holder");

            // Recupera a data de referência (Ex.: Data do Documento)
            var dateRefName = $("#" + dateFieldName).attr('data-date-ref');
            var day = $("#" + dateRefName + "_day").val();
            var month = $("#" + dateRefName + "_month").val();
            var year = $("#" + dateRefName + "_year").val();

            var dias = parseInt($(this).val());
            var newDate = new Date(year, month - 1, day);
            newDate.setDate(newDate.getDate() + dias);
            $(dateField).val(convertDate(newDate));
            $(dateField).change();
        });

// Recupera a data que será atualizada (Ex.: Vencimento)
        var dateFieldName = $(this).attr('data-date-field') + "_holder";
        var dateField = $("#" + dateFieldName);
        var name = ($(dateField).attr("name") || '').replace("_holder", "");
        var diasField = "#" + name + "_dias_calc";
        $(dateField).change(function () {
            setCalculatedDiasValue(diasField);
        });

        var dateRefName = $(dateField).attr('data-date-ref') + "_holder";
        $("#" + dateRefName).change(function () {
            $(diasField).blur();
        });

        setCalculatedDiasValue(this);
    });
}

function setCalculatedDiasValue(diasSelector) {
    var dateFieldName = $(diasSelector).attr('data-date-field');

    var day = $("#" + dateFieldName + "_day").val();
    var month = $("#" + dateFieldName + "_month").val();
    var year = $("#" + dateFieldName + "_year").val();

    // Recupera a data de referência (Ex.: Data do Documento)
    var dateRefName = $("#" + dateFieldName + "_holder").attr('data-date-ref');
    var day_ref = $("#" + dateRefName + "_day").val();
    var month_ref = $("#" + dateRefName + "_month").val();
    var year_ref = $("#" + dateRefName + "_year").val();

    // Recupera as datas e calcula o número de dias
    var date = new Date(year, month - 1, day);
    var dateRef = new Date(year_ref, month_ref - 1, day_ref);
    var dias = date.getDate() - dateRef.getDate()
    if (dias >= 0) $(diasSelector).val(dias);
}

function convertDate(inputFormat) {
    function pad(s) {
        return (s < 10) ? '0' + s : s;
    }

    var d = new Date(inputFormat);
    return [pad(d.getDate()), pad(d.getMonth() + 1), d.getFullYear()].join('/');
}

$.prototype.autocompletedate = function () {
    $(this).each(function (i, field) {
        field.onkeyup = function () {
            if (this.value.match(/[^0-9\/]/)) {
                this.value = this.value.replace(/[^0-9\/]/g, '');
            }
        };

        field.onblur = function () {
            var value = this.value,
                splits = value.split('/');

            if (value === '') {
                return;
            }
            else if (splits.length === 1) {
                var month = ("0" + (new Date().getMonth() + 1)).slice(-2);

                value = splits[0] + '/' + month + '/' + new Date().getFullYear();
            }
            else if (splits.length === 2 && splits[1].length === 2) {
                value = splits[0] + '/' + splits[1] + '/' + new Date().getFullYear();
            }
            else if (splits.length === 2 && splits[1].length === 4) {
                value = '01/' + splits[0] + '/' + splits[1];
            }

            this.value = value;
        };
    })
}