<%@ page import="groovy.json.StringEscapeUtils" %>
<tr data-index="${index}" data-item-id="${instance.id}" style="${style}">

    <% def prefix = name + "[" + index + "]" %>

<!-- COLUNAS DOS ITENS -->
    <g:each in="${fields}" var="field">
        <td style="/*text-align: center;*/">
            <%
                def fieldAttrs = field.clone()
                fieldAttrs['data-item-id'] = instance.id
                fieldAttrs['data-field-name'] = fieldAttrs.name
                fieldAttrs.instance = instance
                fieldAttrs.value = instance.getPropertyRecursively(fieldAttrs.name)
                fieldAttrs.name = prefix + "." + fieldAttrs.name
                out << asg.dynamicField(fieldAttrs)
            %>
        </td>
    </g:each>

    <g:if test="${!isShowMode}">
        <td>
            <a href="#${attrs.name + index}"
               onclick="editableGrid.remove(this);
               return false;"
               class="btn btn-danger btn-xs tip-bottom"
               title="${message(code: "default.button.delete.label")}">
                <i class="icon icon-trash icon-white"></i>
            </a>
            <input type="hidden" name="${prefix + ".deleted"}" value="false" class="asg-deleted"/>
        </td>
    </g:if>

</tr>
<!-- NÃO INCLUIR NADA A PARTIR DESTA LINHA -->