function prepareAutoComplete(scope) {
    // Dá a opçao de aplicar só dentro de um escopo
    scope = scope ? scope : document;

    $(".asg-autocomplete", scope).each(function () {
            var field = $(this).attr("name");
            var $hiddenField = $(this).prev("input[type='hidden']");
            var minlength = $(this).attr("minLength");
            var $this = $(this);

            $hiddenField.change(function () {
                var data = {domain: $this.attr("domain"),
                    listFields: "id",
                    term: $hiddenField.val()};

                var url = getUrl("autoComplete/json");

                $.ajax({
                    url: url, // remote datasource
                    data: data,
                    dataType: "json",
                    success: function (data) {
                        $this.val(data[0].value);
                    },
                    error: function () {
                    }
                });
            });

            var autocompleteSelect = function (event, ui) {
                $hiddenField.val(ui.item.id).trigger('change');
                $("#" + field).val(ui.item.value).trigger('change');
                $("#" + field).attr("oldtext", ui.item.value);

                executeAsgChain($hiddenField);

                var submitonselect = $("#" + field).attr("submitonselect");
                if (submitonselect) {
                    $("#" + field).closest("form").submit();
                }
            };

            $(this).autocomplete({
                delay: 0,
                autoFocus: true,
                scroll: true,

                source: function (request, response) {
                    var data = {domain: $this.attr("domain"),
                        valueField: $this.attr("valueField"),
                        listFields: $this.attr("listFields"),
                        filter: $this.attr("filter"),
                        filterLogicalOperand: $this.attr("filterLogicalOperand"),
                        orderBy: $this.attr("orderBy"),
                        orderByDirection: $this.attr("orderByDirection"),
                        max: $this.attr("max"),
                        term: ""};
                    data.term = removerAcentos(request.term)

                    var url = getUrl("autoComplete/json");

                    $.ajax({
                        url: url, // remote datasource
                        data: data,
                        dataType: "json",
                        success: function (data) {
                            response(data); // set the response

                            if ($this.is(':focus') === false) {
                                autocompleteSelect(null, {item: data[0]});
                                $this.val(data[0].value);
                                $this.autocomplete('close');
                            }
                        },
                        error: function () {
                        }
                    });
                },
                minLength: minlength,
                select: autocompleteSelect
            });

            $(this).blur(function () {
                var newText = $(this).val();
                if (newText) {
                    var oldText = $(this).attr("oldtext");
                    if (oldText && newText != oldText) {
                        $(this).val(oldText).trigger('change');
                    }
                } else {
                    $($hiddenField).val("null");
                }

                if ($(this).val() != '' && $($hiddenField).val() == '') {
                    $(this).autocomplete('search');
                }
            });

            $(this).mouseup(function () {
                $(this).select();
            });

            $(this).keydown(function (e) {
                if (!$(this).val() && e.keyCode == 40) {
                    $(this).val('*');
                    $(this).autocomplete('search');
                }

                return true;
            });

            if ($this.attr("listonfocus")) {
                $(this).focus(function () {
                    var value = $(this).val() ? $(this).val() : "*";
                    $(this).autocomplete("search", value);
                });
            }
        }
    )
    ;
}

function getAsgAutocompleteValue(element) {
    var textElement = $(element).next('input');

    var id = $(element).val();
    var text = $(textElement).val();

    return { id: id, text: text };
}

function setAsgAutocompleteValue(element, id, text) {
    element = element.replace(/\./g, "\\.");

    setAsgAutocompleteObjectValue($(element + "\\.id"), id, text);
}

function setAsgAutocompleteObjectValue(element, id, text) {
    var textElement = $(element).next('input');

    $(element).val(id);
    $(textElement).val(text);
}

function copyAsgAutocomplete(from, to) {
    var obj = getAsgAutocompleteValue(from);
    setAsgAutocompleteObjectValue(to, obj.id, obj.text);
}

function clearAsgAutocompleteObjectValue(element) {
    setAsgAutocompleteObjectValue(element, "", "");
}