<asg:widgetBox span="${attrs.span ?: 12}" title="${attrs.title ?: "Listagem"}" icon="${attrs.icon}" noPadding="true">

    <table id="${attrs.name}" class="table table-bordered table-hover table-condensed"
           data-latest-index="${attrs.list?.size() ?: 0}"
           data-domain-class="${grailsApplication.getArtefactByLogicalPropertyName("Domain", controllerName).fullName}" data-domain-id="${params.id}">

        <!-- CABEÇALHO -->
        <thead>

        <tr>

        <!-- COLUNAS DO CABEÇALHO -->
            <g:each in="${attrs.fields}" var="field">
                <th><asg:message code="${field.name}.label"/></th>
            </g:each>

            <g:if test="${!isShowMode}">
                <%
                    def thButtonsSize = (attrs.itemButtons?.split(";")?.size() ?: 0) + (attrs.customButtons?.size() ?: 0)
                    thButtonsSize = (thButtonsSize ?: 1) * 47
                %>
                <th style="width:${thButtonsSize}px;"></th>
            </g:if>

        </tr>

        </thead>


        <!-- ITENS -->
        <tbody>

        <!-- INICIA O ÍNDICE -->
        <% def index = 0 %>

        <!-- LOOP NOS ITENS -->
        <g:each in="${attrs.list}" var="item">

            <!-- RENDERIZA A ROW -->
            <g:render template="/_fields/editableGrid/editableGridRow"
                      model="[name: attrs.name, fields: attrs.fields, instance: item, index: index]"/>

            <!-- INCREMENTA O ÍNDICE -->
            <% index++ %>
        </g:each>

        <g:if test="${!isShowMode}">
            <!-- RENDERIZA A ROW VAZIA EM MODO DE EDIÇÃO -->
            <g:render template="/_fields/editableGrid/editableGridRow"
                      model="[name: attrs.name, fields: attrs.fields, instance: attrs.domainClass.newInstance(), index: index]"/>
        </g:if>

        </tbody>

    </table>

</asg:widgetBox>

<r:script>
    var editableGrid = new EditableGrid("${attrs.name}");
</r:script>