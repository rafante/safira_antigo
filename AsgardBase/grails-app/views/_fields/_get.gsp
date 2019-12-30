<form>
    <label class="control-label" for="${controller}">
        <asg:link controller="${controller}">
            <g:message code="${controller}.label"/>
        </asg:link>

    </label>

    <div class="controls">
        <g:if test="${listfields == 'id'}">
            <asg:textField id="${controller}.id" name="${controller}.id"></asg:textField>
        </g:if>
        <g:if test="${listfields != 'id'}">
            <asg:autoComplete id="${controller}.id" name="${controller}.id" valuefield="${valuefield}"
                              listfields="${listfields}" domain="${domain}"></asg:autoComplete>
        </g:if>
        <a href="javascript:getController()" class="btn btn-small tip-bottom" title="Exibir"><i
                class="icon icon-zoom-in"></i></a>
    </div>

    <r:script disposition="defer">
    function getController() {
      var id = document.getElementById("${controller}.id").value;
      
      window.location.href=getUrl("${controller}/show/" + id);
    }
    </r:script>
</form>