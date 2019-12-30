var EditableGrid = function (id) {
    this.id = id;
    this.table = $('#' + id);
    this.fieldsSelector = 'input'; // Por enquanto só funciona com inputs

    this.refreshLastRow();
    this.setInitialValues();
    this.setKeyUp();
    this.setSubmit();
    this.setBlur();
}

EditableGrid.prototype.setInitialValues = function () {
    $(this.fieldsSelector, this.lastRow).each(function () {
        $(this).attr('data-initialValue', this.value);
    });
}

EditableGrid.prototype.clearRow = function (row) {
    $(this.fieldsSelector, row).each(function () {
        $(this).val($(this).attr('data-initialValue'));
    });
}

EditableGrid.prototype.incrementIndex = function (row) {
    var idx = parseInt($(row).attr('data-index')) + 1;

    $(row).attr('data-index', idx);

    var increment = function (attrs, object) {
        for (i = 0; i < attrs.length; i++) {
            var attrName = attrs[i];

            if ($(object).is('[' + attrName + ']')) {
                var match = $(object).attr(attrName).match(/^(\w+(_|\[))(\d+)((_|\])(\w|\.|_)*)$/);
                $(object).attr(attrName, match[1] + idx + match[4]);
            }
        }
    };

    $('input, button', row).each(function () {
        increment(['id', 'name', 'hiddenfieldname'], this);
    });
}

/*
 Remove um item da tabela. Caso não seja um item novo, seta o campo "deleted" como true e econde a linha
 */
EditableGrid.prototype.remove = function (btn) {
    var row = $(btn).closest('tr');
    $(row).hide();

    var $inputDeleted = $(btn).nextAll(".asg-deleted").first();
    $inputDeleted.attr("disabled", false);
    $inputDeleted.val("true");

    totalize();
}

/*
 Atualiza o atributo lastRow com a última linha (vazia)
 */
EditableGrid.prototype.refreshLastRow = function () {
    this.lastRow = $('tr', this.table).last();
}

/*
 Remove a última linha (vazia)
 */
EditableGrid.prototype.cleanupGrid = function () {
    var self = this;

    self.lastRow.remove();
    self.refreshLastRow();
}

/*
 Verifica se a linha está vazia
 */
EditableGrid.prototype.isRowEmpty = function (row) {
    var ret = true;

    $(this.fieldsSelector, row).each(function () {
        if (this.value != $(this).attr('data-initialValue')) {
            ret = false;
        }
    });

    return ret;
};

/*
 Determina o evento KeyUp, que verifica se é necessário ou não criar uma nova linha
 */
EditableGrid.prototype.setKeyUp = function () {
    var self = this;

    $(this.fieldsSelector, self.lastRow).keyup(function () {
        var isLastChild = $(this).closest('tr').is(':last-child');
        var isPenultimate = $(this).closest('tr').index() == $('tr:last-child', self.table).prev('tr').index();

        if (isLastChild && this.value != $(this).attr('data-initialValue')) {
            var clone = $(self.lastRow).clone();

            self.clearRow(clone);
            self.incrementIndex(clone);

            prepare(clone);
            clone.appendTo(self.table);

            self.lastRow = clone;
            self.setKeyUp();
        }
        else if (isPenultimate && self.isRowEmpty($(this).closest('tr'))) {
            self.cleanupGrid();
        }
    });
}

/*
 Exclui a última linha ao submitar
 */
EditableGrid.prototype.setSubmit = function () {
    var self = this;

    $(self.table).closest('form').submit(function (e) {
        self.cleanupGrid();
    });
}

/*
 Determina o evento KeyUp, que verifica se é necessário ou não criar uma nova linha
 */
EditableGrid.prototype.setBlur = function () {
    var self = this;

    $(this.fieldsSelector, self.table).blur(function () {
        // Serializa o array com todos os campos do FORM
        var data = $($(self.table).parents('form').first()).serializeArray();

        // Recupera o id do item a ser atualizado
        var itemId = $(this).attr('data-item-id');

        // Determina os campos a serem retornados do servidor em Array
        var fieldsArray = []
        $("[updateOnBlur]").each(function () {
            fieldsArray.push($(this).attr('data-field-name'));
        });

        // Concatena os campos separados por ponto e vírgula (;)
        var fields = fieldsArray.unique().join(";");

        // Executa a busca dos dados no servidor
        $.ajax({
            type: 'POST',
            dataType: "json",
            url: getUrl('javascript/readItemFields?domain=' + self.table.attr('data-domain-class') +
            '&itemsFieldName=' + self.id + '&itemId=' + itemId + '' + '&itemFields=' + fields),
            data: data,
            success: function (data) {
                var selector = "[data-item-id='" + data.id + "'][updateOnBlur]";
                $(selector).each(function () {
                    $(this).html(data[$(this).attr('data-field-name')]);
                });
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });
    });
}
