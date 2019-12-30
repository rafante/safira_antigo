<r:script disposition="defer">
    var renderer = function (instance, td, row, col, prop, value, cellProperties) {
        var escaped = Handsontable.helper.stringify(value);
        escaped = strip_tags(escaped, '<a><i>');
        td.innerHTML = escaped;
        return td;
    };
</r:script>

<div class="widget-box">
    <div class="widget-title">
        <g:if test="${"create" in headerButtons?.split(";")}">
            <span class="icon">
                <a href='javascript:document.getElementById("btnAdd${controller}").click();'
                   class="tip-top"
                   data-original-title="${message(code: 'default.add.label', args: [message(code: controller + '.label', default: titleDefault)])}">
                    <i class="icon icon-file"></i>
                </a>
            </span>
        </g:if>

        <g:if test="${"save" in headerButtons?.split(";")}">
            <span class="icon">
                <a href='javascript:document.getElementById("btnSave${controller}").click();'
                   class="tip-top"
                   data-original-title="${message(code: 'default.save.label', args: [message(code: controller + '.label', default: titleDefault)])}">
                    <i class="icon icon-ok"></i>
                </a>
            </span>
        </g:if>
        <h5><g:message code="${controller}.label" default="${titleDefault}"/></h5>
    </div>

    <div class="widget-content nopadding">
        <div id="table${name}" class="handsontable"></div>
    </div>

</div>