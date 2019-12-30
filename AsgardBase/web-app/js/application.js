var appName = $('meta[name=appContext]').attr('content');

// Para evitar a execução desnecessária da função prepare() após carregar os componentes,
// faz-se necessário um contador de itens carregados via ajax restantes. Quando o mesmo for = 0,
// a função prepare() é chamada.
var countRemoteBeforePrepare = 0;

var sform_clean;
var submitting = false;

const C_FOCUSABLE = 'input,select,button,textarea,checkbox,a[data-toggle],a[data-action],a.asg-modal-create';
const C_FOCUSABLE_FILTER = ':visible:not(:disabled,[readonly]):not(.asg-ignore-focus):not(.asg-ignore-first-focus)';
const C_DIRTY_FORM_SELECTOR = "form:not(.asg-ignore-check-dirty)";
const C_DIRTY_FORM_SELECTOR_FILTER = ':input:not(.asg-ignore-check-dirty)';

onDocumentReady(function () {
    prepareOnce();
    prepare();

    // Tem que ser executado por último, após todas as modificações do carregamento da página
    prepareCheckDataChanged();
});

function prepareOnce() {
    prepareKeys();
    prepareMenu();
    prepareAjaxLoading();
    prepareRemoteTagRenderer();
    prepareScroll();
    prepareFocus();
    prepareTabDataToggle();
    prepareSubmit();
    prepareProgressBar();
    prepareDatePickerOnce();
}

function prepare(scope) {
    if (countRemoteBeforePrepare > 0) return;

//    console.log('begin:prepare()');

    prepareSelect(scope);
    prepareDatePicker(scope);
    prepareCheckBoxes(scope);
    prepareAutoComplete(scope);
    prepareMask(scope);
    prepareModal(scope);
    prepareDataTable(scope);
    preparePercentage(scope);
    prepareInput(scope);

//    console.log('end:prepare()');
}

function prepareCheckDataChanged() {
    // Serializa os campos de acordo com que foram carregados (não modificado)
    sform_clean = $(C_DIRTY_FORM_SELECTOR).find(C_DIRTY_FORM_SELECTOR_FILTER).serialize();

    // Antes de sair da página, verifica se algum dado foi alterado
    window.onbeforeunload = function (e) {
        var sform_dirty = $(C_DIRTY_FORM_SELECTOR).find(C_DIRTY_FORM_SELECTOR_FILTER).serialize();

        if (sform_clean != sform_dirty && !submitting) {
            return 'Tem certeza que deseja sair?\n\nAtenção! Todas as modificações poderão ser perdidas caso confirme a saída!';
        }
    };
}

function prepareKeys() {
    // Pressionar ENTER
    $(document).on('keydown', '*', function (e) {
            var self = $(this);
            var form = self.parents('form:eq(0)');

            switch (e.keyCode) {
                case 13: // ENTER
                    if ($(this).hasClass('asg-ignore-custom-enter') || $(this).parents(".asg-ignore-custom-enter").size()) {
                        e.stopPropagation();
                        return true;
                    }

                    if ($(this).is('a') || $(this).is('button') || ($(this).attr('type') in {
                            "submit": 1,
                            "button": 1
                        })) {
                        $(this).click();
                        e.stopPropagation();
                        return false;
                    }

                    // Caso o elemento atual não seja um TEXTAREA (Shift+Enter = linebreak)
                    if (e.shiftKey && $(this).is('textarea')) {
                        e.stopPropagation();
                        return false;
                    }

                    focusNext(this, (e.shiftKey ? -1 : 1));

                    e.stopPropagation();
                    return false;

                    break;
                default:
                    var btn = $(document).find('[data-asg-bind-key=' + e.keyCode + ']').filter(':visible');
                    if (btn.length) {
                        btn[0].focus(); // Para executar o blur() do campo que tiver focado
                        btn[0].click();
                        e.stopPropagation();
                        return false;
                    }
                    break;
            }
        }
    )
    ;
}

function focusNext(obj, direction) {
    var focusable = $(document).find(C_FOCUSABLE).filter(C_FOCUSABLE_FILTER);

    var next = focusable.eq(focusable.index(obj) + direction);
    if (next.length && // Caso exista um próximo elemento
        !focusable.hasClass('search-query')) { // Caso o elemento NÃO tenha a classe SEARCH-QUERY
        // Tratamento especial para MODAL
        focusAndSelect(next);
    } else {
        var $tabs = $('ul.nav > li');
        var $nextTab = $tabs.filter('.active').next('li').find('a[data-toggle="tab"]');

        // Tratamento de TABCONTROL
        if ($nextTab.length) {
            $nextTab.tab('show');
        }
        else if ($(obj).attr('name') != 'quicksearch' && // Não está focado no QUICKSEARCH
            ($("#btnSave").length)) { // Existe o botão de salvar
            confirmAndSave();
        }
    }
}

function focusAndSelect(obj) {
    obj.focus();
    if (obj.selectable) obj.select();
}

function getFocusable(selector) {
    selector = selector ? selector : '#content-body';

    var $elements = $(selector).find('input, textarea, select').filter(C_FOCUSABLE_FILTER);
    return $elements;
}

function prepareFocus(selector) {
    var $elements = getFocusable(selector);
    if ($elements.length) focusAndSelect($elements.get(0));
}

function prepareMask(scope) {
    // Dá a opçao de aplicar só dentro de um escopo
    scope = scope ? scope : document;

    $('input[type="text"][mask]', scope).setMask({attr: 'mask'});
}

function getLoadingHtml(contentBody) {
    return '<div style=""><div class="bubblingG"><span id="bubblingG_1"></span><span id="bubblingG_2"></span><span id="bubblingG_3"></span></div></div>' +
        '<div style="display:none">' + contentBody + '</div>'
}

function prepareAjaxLoading() {
//    $(document).pjax('a[data-pjax]', '#content-main', { fragment: '#content-main' });
//
//    $(document).on('pjax:start', function () {
//        startLoading();
//    })
//    $(document).on('pjax:end', function () {
//        stopLoading()
//    });
//
//    $(document).on('pjax:success', function () {
//        prepareRemoteTagRenderer();
//        prepare();
//    });
//
//    $(document).on('pjax:timeout', function (event) {
//        // Prevent default timeout redirection behavior
//        event.preventDefault();
//    });

//    $(document).ajaxStart(function () {
//        startLoading();
//    }).ajaxStop(function () {
//        stopLoading()
//    });
}

//function startLoading() {
//    NProgress.start(true);
//}
//
//function stopLoading() {
//    NProgress.done(true);
//}

function prepareScroll() {
    $(document).scroll(function () {
            if ($(window).width() > 480) {
                var classToChange = "navbar navbar-inverse navbar-fixed-top bs-docs-nav asg-fixed-top"
                if ($(window).scrollTop() > 56) {
                    $('#action-group').addClass(classToChange);
                    $('#action-group-left').show();
                } else {
                    $('#action-group').removeClass(classToChange);
                    $('#action-group-left').hide();
                }
            }
        }
    );
}

function prepareTabDataToggle() {
    // add a hash to the URL when the user clicks on a tab
    $('a[data-toggle="tab"]').on('click', function (e) {
        history.pushState(null, null, $(this).attr('href'));
    });
    // navigate to a tab when the history changes
    window.addEventListener("popstate", function (e) {
        var activeTab = $('[href=' + location.hash + ']');
        if (activeTab.length) {
            activeTab.tab('show');
        } else {
            $('.nav-tabs a:first').tab('show');
        }
    });
}

function prepareMenu() {
    $('.submenu').removeClass("open active");
    $('li.active').parents('.submenu').addClass("open active");

    $(".menu").click(function () {
        $('.menu.active').removeClass('active');
        $(this).addClass('active');

        $('.submenu').removeClass("open active");
        $('li.active').parents('.submenu').addClass("open active");
    });
}

function splitDate(element) {
    var dateText = element.value;
    var arr = dateText.split("/");

    var day = arr[0];
    var month = arr[1];
    var year = arr[2];

    // TODO: Tirar o HIGH daqui e deixar genérico (Problemas com o Filter)
    //var elementName = adjustSelector(element.name).replace(".high", "_high");
    var elementName = adjustSelector(element.name)

    $("#" + elementName.replace("_holder", "_day")).attr("value", day);
    $("#" + elementName.replace("_holder", "_month")).attr("value", month);
    $("#" + elementName.replace("_holder", "_year")).attr("value", year);
}

function adjustSelector(selector) {
    return escapeRegExp(selector);
    //return selector.
    //    replace("[", "\\[").
    //    replace("]", "\\]").
    //    replace(".", "\\.");
}

function isValidDate(datestring) {
    var checkdate = new Date(datestring);
    if (isNaN(checkdate.getFullYear())) {
        return false;
    }
    else {
        return true;
    }
}

function StringToDate(data) {
    var div = data.split("/");

    return new Date(div[2], div[1], div[0]);
}

function escapeRegExp(str) {
    return str ? str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&") : '';
}

function prepareCheckBoxes(scope) {
    // Dá a opçao de aplicar só dentro de um escopo
    scope = scope ? scope : document;

    // Joga o valor para um input invisível
    $("input[type='checkbox']", scope).on('ifCreated || ifChecked || ifUnchecked', function () {
        var hiddenfieldname = adjustSelector($(this).attr('hiddenfieldname'));
        $("#" + hiddenfieldname).val(this.checked);

        // Esconde se for false?
        if ($(this).attr("asg-hide-if-false"))
            if (!this.checked) {
                $('[id="' + $(this).attr("asg-hide-if-false") + '"]').hide();
                prepareFocus(this)
            }
            else {
                $('[id="' + $(this).attr("asg-hide-if-false") + '"]').show();
                prepareFocus(this)
            }


        // Esconde se for true?
        if ($(this).attr("asg-hide-if-true"))
            if (this.checked) {
                $("#" + $(this).attr("asg-hide-if-true")).hide();
                prepareFocus(this)
            }
            else {
                $("#" + $(this).attr("asg-hide-if-true")).show();
                prepareFocus(this)
            }
    });

    $("input[type='checkbox'],input[type='radio']", scope).iCheck('destroy');
    $("input[type='checkbox'],input[type='radio']", scope).iCheck({
        checkboxClass: 'icheckbox_flat-blue',
        radioClass: 'iradio_flat-blue'
    });

}

function cbCheck(selector, checkedStatus) {
    var checkbox = $(selector).find('input:checkbox');
    checkbox.each(function () {
        this.checked = checkedStatus;
        if (checkedStatus == this.checked) {
            $(this).closest('.icheckbox_flat-blue').removeClass('checked');
        }
        if (this.checked) {
            $(this).closest('.icheckbox_flat-blue').addClass('checked');
        }
    });
}

function prepareSubmit() {
    $('form').submit(function (e) {
        submitting = true;

        var success = true;

        $('[required="true"]:visible', this).each(function (i, field) {
            if ((field.tagName.toLowerCase() === 'select' && (field.value === '' || field.value === 'null')) ||
                $.trim(field.value) === '') {
                $(field).parent().addClass('has-error');
                success = false;
            }
            else {
                $(field).parent().removeClass('has-error');
            }
        });

        return true;
    });
}

function strip_tags(input, allowed) {
    // +   original by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    allowed = (((allowed || "") + "").toLowerCase().match(/<[a-z][a-z0-9]*>/g) || []).join(''); // making sure the allowed arg is a string containing only tags in lowercase (<a><b><c>)
    var tags = /<\/?([a-z][a-z0-9]*)\b[^>]*>/gi,
        commentsAndPhpTags = /<!--[\s\S]*?-->|<\?(?:php)?[\s\S]*?\?>/gi;
    return input.replace(commentsAndPhpTags, '').replace(tags, function ($0, $1) {
        return allowed.indexOf('<' + $1.toLowerCase() + '>') > -1 ? $0 : '';
    });
}

function prepareRemoteTagRenderer(selector) {
    selector = selector ? selector : '.remote-tag-container';

    countRemoteBeforePrepare = 0;
    $(selector).each(function () {
        countRemoteBeforePrepare += 1;

        var myself = $(this);
        var data = escape($(this).attr('data-attrs'));

        $.ajax({
            type: 'POST',
            data: "attrs=" + data,
            url: '/' + appName + '/javascript/remoteTagRenderer',
            success: function (data, textStatus) {
                myself.fadeOut(function () {
                    countRemoteBeforePrepare -= 1

                    myself.html(data);
                    myself.fadeIn("fast");

                    prepare();
                });

            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                countRemoteBeforePrepare -= 1

                var errorDiv = getDivErrorSpinner(XMLHttpRequest)
                myself.html(errorDiv);

                prepare();
            }
        });
    });
}

function getDivErrorSpinner(XMLHttpRequest) {
    return "<div class='asg-spinner-error' onclick='$(this).next().toggle();'></div><div style='display:none'>" + XMLHttpRequest.responseText + "</div>"
}

function prepareProgressBar() {

    $('.progress-bar').each(function () {
        $(this).css('width', $(this).attr('data-value'));
    });

}

/* FINANCEIRO */
function clickAddCompensacao(btn) {
    var widgetBox = $(btn).closest('.widget-box').get(0);
    var row = $(widgetBox).find('.widget-content > .row:last-child').get(0);
    var newRow = $(row).clone();
    var elements = 'select, input'
    var valorInserido = 0.0;

    $(widgetBox).find('input').each(function (i, obj) {
        if (obj.name.indexOf(".valor") !== -1) {
            valorInserido = valorInserido + getMoney($(obj).val());
        }
    });

    $(newRow).find(elements).each(function (i, obj) {
        if (obj.name.match(/\[[0-9]+\]/)) {
            var newRow = $(widgetBox).find('.widget-content > .row').length;
            $(obj).attr('name', obj.name.replace(/\[[0-9]+\]/, '[' + newRow + ']'));
            $(obj).attr('id', obj.id.replace(/\[[0-9]+\]/, '[' + newRow + ']'));

            // Calcula o valor restante
            if (obj.name.indexOf(".valor") !== -1) {
                var fieldValorRestante = $(obj).attr("data-valor-name");
                var valorRestante = getMoney($("#" + fieldValorRestante).val());
                var valorInserir = valorRestante - valorInserido;

                $(obj).val(valorInserir);
                $(obj).setMask({attr: 'mask'});
            }
        }
    });

    $(newRow).insertAfter(row);

    prepareAutoComplete(newRow);
}

function clickRemoveLineCompensacao(btn) {
    var row = $(btn).closest('.row');

    // Se só tiver uma linha, nao exclui, so limpa a linha
    if ($(row).siblings().length === 0) {
        $('input, select', row).val('');
    }
    else {
        $(row).remove();
    }
}

function submitMainForm() {
    submitting = true;
    $($("form").get(1)).trigger('submit');//.submit();
}

function onDocumentReady(fn) {
    $(document).ready(fn);
//    $(document).on('pjax:success', fn);
}

function asgConfirm(msg) {
    return confirm(msg ? msg : "Confirma operação?");
}

function confirmAndSave(msg) {
    if (asgConfirm("Deseja salvar?")) {
        $('#btnSave').click();
    }
}

function readEntityFieldValue(domain, id, field) {
    return jQuery.ajax({
        type: 'POST',
        dataType: 'text',
        url: getUrl('javascript/readEntityFieldValue?domain=' + domain + '&id=' + id + '&field=' + field),
        async: false
    }).responseText;
}

function getUrl(urlComplement) {
    return "/" + appName + "/" + urlComplement
}

// TODO! Otimizar esta rotina!
function totalize() {
    // Mantém, por target, o total
    var totalizers = [];

    // Varre todos os campos com a classe .totalizer
    $("[asg-totalize-target]").each(function () {
        var totalize;

        // Recupera o campo a ser atualizado com o total (atributo "asg-totalize-target")
        var target = "#" + $(this).attr("asg-totalize-target");

        // Verifica se o target atual já está na lista final
        var totalizeGrep = $.grep(totalizers, function (e) {
            return e.target == target;
        });

        // Verifica se o totalizador para este elemento é condicional
        // (Se contém um script a ser executado antes para validar o somatório)
        var totalizeEval = $(this).attr('asg-totalize-eval');

        // Se tiver um script em asg-totalize-eval, executa o script via eval para ver se somará ou não
        var execute = (totalizeEval ? eval(totalizeEval) : true);

        // Caso não tenha sido encontrado o target, cria um novo na lista final
        if (totalizeGrep.length == 0) {
            totalize = {};
            totalize.target = target;

            var value = $(this).is("input") ? $(this).val() : $(this).html();

            // Considera-se apenas os valores visíveis
            totalize.value = (execute && $(this).is(":visible")) ? asgParseFloat(value) : 0;

            totalizers.push(totalize);
        } else {
            // Caso haja um target já na lista, incrementa seu valor
            totalize = totalizeGrep[0];

            var value = $(this).is("input") ? $(this).val() : $(this).html();
            // Considera-se apenas os valores visíveis
            totalize.value = totalize.value + ((execute && $(this).is(":visible")) ? asgParseFloat(value) : 0);
        }

    });

    // Atualiza os targets com os valores totais somados acima
    for (var i = 0; i < totalizers.length; i++) {
        $(totalizers[i].target).val(formatDecimal(totalizers[i].value));
    }

}