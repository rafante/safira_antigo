<div class="col-12 col-sm-${attrs.span}">
    <div id="${attrs.id}" class="widget-box ${attrs.collapsible ? 'collapsible' : ''}">
        <div class="widget-title">
            <span class="icon"><i class="icon ${attrs.icon ?: 'icon-table'}"></i></span>
            <h5>${attrs.title}</h5>
        </div>

        <div class="widget-content${attrs.noPadding ? "nopadding" : ""}">
            <div class="row">
                ${attrs.body}
            </div>
        </div>
    </div>
</div>