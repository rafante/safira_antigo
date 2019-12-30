function selectOperand(obj, val) {
    var operandName = $(obj).attr("data-asg-operand");

    if (!val) $("#" + operandName).val("");

    if ($("#" + operandName).val() || !$(obj).val()) return false;

    $("#" + operandName + " option").filter(function () {
        var selectVal = $(this).val();
        return selectVal == val;
    }).prop('selected', (val != null));

    return true
}