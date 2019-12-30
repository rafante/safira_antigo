<%@ page import="org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass" %>

<%
    /**
     * INFORMAÇÕES IMPORTANTES:
     * 1-NUNCA incluir o campo ID no _form.gsp dos itens quando for utilizar o inLine="true" (Modo modal)
     */
    if (!attrs.name) attrs.name = attrs.controller
    def listAsId = false
    def artefact
    def newList = []
    for (item in attrs.list) {
        if (item?.class == Integer) {
            if (!artefact) artefact = grailsApplication.getArtefactByLogicalPropertyName("Domain", attrs.controller)

            newList << artefact.clazz.read(item)
            listAsId = true
        }
    }

    if (newList.size() > 0) attrs.list = newList

    if (listAsId) {
        // Determina o número de registros
        if (!attrs.ignorePagination) attrs.totalCount = artefact.clazz.count()
    }

    // Determina o número de registros
    try {
        if (!attrs.totalCount) attrs.totalCount =
                attrs.list?.class in [java.util.ArrayList, org.hibernate.collection.PersistentSet, org.codehaus.groovy.grails.web.json.JSONArray] ? attrs.list.size() : attrs.list?.totalCount ?: 0
    } catch (e) {
        attrs.totalCount = 1
    }
//    attrs.totalCount -= 1
    def isShowMode = attrs.actionName == 'show'
    attrs.ignoreWidget = false
%>
<g:if test="${!attrs.ignoreWidget}">
    <g:if test="${!attrs.ignoreContainer}">
        <div id="container${attrs.name}" data-grid-attrs='${attrs.attrscleanjson}'>
    </g:if>
    <g:hiddenField name="customAttrs" value=""/>
    <div id="div${attrs.name}" class="widget-box">

    <div class="widget-title">
        <g:if test="${"create" in attrs.headerButtons?.split(";")}">
            <!-- MODO MODAL? -->
            <g:if test="${attrs.inlineMode}">
                <g:if test="${!isShowMode}">
                    <span class="icon">
                        <a href="#${attrs.name + ix}"
                           class="tip-top asg-modal-create"
                           data-original-title="${message(code: 'default.add.label', args: [message(code: controller + '.label', default: titleDefault)])} (F6)"
                           data-asg-bind-key="117" onclick="modalCreate(this);
                        return false;">
                            <i class="icon icon-file"></i>
                        </a>
                    </span>
                </g:if>
            </g:if><g:else>
            <span class="icon">
                <a href='javascript:document.getElementById("btnAdd<% out << attrs.controller %>").click();'
                   id='add${attrs.controller}'
                   class="tip-top asg-modal-create"
                   <g:if test="${attrs.headerCreateOnClick}">onclick="${attrs.headerCreateOnClick}"</g:if>
                   data-pjax="#content-main" data-asg-bind-key="117"
                   data-original-title="${message(code: 'default.add.label', args: [message(code: controller + '.label', default: titleDefault)])} (F6)">
                    <i class="icon icon-file"></i>
                </a>
            </span>
        </g:else>
        </g:if>

        <g:if test="${"save" in attrs.headerButtons?.split(";")}">
            <span class="icon">
                <a href='javascript:document.getElementById("btnSave$controller").click();'
                   class="tip-top" data-pjax="#content-main"
                   data-original-title="${message(code: 'default.save.label', args: [message(code: controller + '.label', default: titleDefault)])}">
                    <i class="icon icon-ok"></i>
                </a>
            </span>
        </g:if>

        <h5><g:message code="${attrs.controller}.label" default="${attrs.titleDefault}"/> <g:if
                test="${!attrs.inlineMode}">(<g:message
                    code="grid.records.found" args="${attrs?.totalCount ?: "0"}"
                    default=""/>)</g:if></h5>
    </div>

    <div class="widget-content nopadding">
</g:if>
<g:else>
    <div class="container" style="overflow: auto;">
</g:else>
<table class="table table-bordered ${attrs.tableClass} header-fixed" style="overflow: auto;">
    <thead>
    <tr>

        <g:if test="${!attrs.fields}">
            <th class="header"><asg:message code="${attrs.controller}.label"/></th>
        </g:if>

        <g:each in="${attrs.fields?.split(";")}" var="field">
            <th class="header">
                <g:if test="${field.contains("CHECKBOX")}">
                    <center>${checkBox(checked: false, id: 'togglecheck_' + field?.split("\\[")[0])}</center>

                    <r:script disposition="defer">
                    $(function () {
                        $("#togglecheck_${field?.split("\\[")[0]}").on('ifChecked || ifUnchecked', function () {
                            var checkedStatus = this.checked;
                            cbCheck('table', checkedStatus);
                        });
                    });
                    </r:script>
                </g:if>
                <g:else>
                    <% field = field?.split("\\[")[0] %>
                    <asg:fieldTitle controller="${attrs.controller}" field="${field}"/>
                </g:else>
            </th>
        </g:each>

        <g:if test="${attrs.itemButtons || attrs.customButtons}">
            <%
                def thButtonsSize = (attrs.itemButtons?.split(";")?.size() ?: 0) + (attrs.customButtons?.size() ?: 0)
                thButtonsSize = thButtonsSize * 47
            %>
            <th style="width:${thButtonsSize}px;"></th>
        </g:if>

    </tr>
    </thead>
    <tbody>

    <%
        def index = 0
        def old_index = index
    %>
    <g:each in="${attrs.list}" var="item">
        <%
            boolean isModel = (item == attrs.modelInstance)

            if (index != '') {
                old_index = index
            }


            if (isModel) {
                index = ''
            }

            def prefix = attrs.name + "[" + index + "]"
        %>

        <tr ${isModel ? 'data-trbase="true" style="display:none"' : ''} data-asg-item-id="${item?.id}"
                                                                        data-asg-prefix="${prefix}"
                                                                        data-asg-index="${index}">
            <g:if test="${!attrs.fields}">
                <td>${item}</td>
            </g:if>

            <g:each in="${attrs.fields?.split(";")}" var="field">
                <%
                    def simpleField = field?.split("\\[")[0]
                    def fieldName = prefix + "." + simpleField
                %>
                <td name="${fieldName}">
                    <%
                        def attrsTd = attrs.clone()
                        attrsTd.field = field
                        attrsTd.index = index
                        attrsTd.item = item

                        out << asg.tdFieldOutput(attrsTd)
                    %>
                </td>
            </g:each>

            <g:if test="${attrs.createHiddenFields}">
                <div id="hiddenFields" style="display:none">
                    <%
                        def d = new DefaultGrailsDomainClass(item.class)
                        for (p in d.properties) {
                            hiddenFieldName = prefix + "." + p.name
                            out << hiddenField(name: hiddenFieldName, value: item[p.name])
                        }
                    %>
                </div>
            </g:if>

            <g:if test="${attrs.itemButtons || attrs.customButtons}">
                <td class="link">
                    <g:if test="${"show" in attrs.itemButtons?.split(";")}">
                        <!--###########################################################-->
                        <!--######################### MODAL ###########################-->
                        <!--###########################################################-->
                        <g:if test="${attrs.inlineMode}">
                            <a href="#${prefix?.replace("[", "\\[").replace("]", "\\]")}"
                               class="btn btn-default tip-bottom"
                               title="${message(code: "default.button.${attrs.actionName}.label")}"
                               data-toggle="modal">
                                <i class="icon icon-${(isShowMode) ? "zoom-in" : "pencil"}"></i>
                            </a>

                            <g:if test="${!isShowMode}">
                                <a href="#${attrs.name + index}"
                                   onclick="markAsDeleted(this, '#${prefix + ".deleted"}');
                                   return false;"
                                   class="btn btn-danger tip-bottom"
                                   title="${message(code: "default.button.delete.label")}">
                                    <i class="icon icon-trash icon-white"></i>
                                </a>
                            </g:if>

                            <asg:modalRender id="${prefix}" modalindex="${index}"
                                             name="${prefix}"
                                             class="modal-grid" actionName="${attrs.actionName}"
                                             controller="${attrs.controller}" noButtons="${isShowMode}"
                                             index="${index}" instance="${item}"
                                             title="${message(code: "${attrs.controller}.label")}"/>

                            <input type="hidden" name="${prefix + ".deleted"}" value="false" class="asg-deleted"/>
                        </g:if>
                        <!--###########################################################-->
                        <!--###########################################################-->
                        <!--###########################################################-->

                        <g:if test="${!attrs.inlineMode}">
                            <asg:link controller="${attrs.controller}" action="show" id="${item?.id}"
                                      class="btn btn-default tip-bottom"
                                      title="${message(code: 'default.button.show.label')}"
                                      update="content-modal"><i
                                    class="icon icon-zoom-in"></i></asg:link>
                        </g:if>
                    </g:if>
                    <g:if test="${(!attrs.inlineMode) && ("edit" in attrs.itemButtons?.split(";"))}">
                        <asg:link controller="${attrs.controller}" action="edit" id="${item?.id}"
                                  class="btn btn-default tip-bottom"
                                  title="${message(code: 'default.button.edit.label')}"><i
                                class="icon icon-pencil"></i></asg:link>
                    </g:if>
                    <g:if test="${!attrs.inlineMode && ("delete" in attrs.itemButtons?.split(";"))}">
                        <asg:link controller="${attrs.controller}" action="delete" id="${item?.id}"
                                  class="btn btn-danger tip-bottom"
                                  title="${message(code: 'default.button.delete.label')}"><i
                                class="icon icon-trash icon-white"></i></asg:link>
                    </g:if>

                <!-- BOTOES PERSONALIZADOS VIA CUSTOMBUTTONS -->
                    <g:each in="${attrs.customButtons}" var="btn">
                        <a title="<g:message code="${btn.label}" default="${btn.label}"/>"
                           class="btn ${btn.class} tip-bottom"
                           href="${btn.href?.toString().replace('%id%', String.valueOf(item?.id))}"
                           data-toggle="${btn["data-toggle"]}"
                           onclick="${btn.onclick}">
                            <i class="${btn.icon}"></i>
                        </a>
                    </g:each>

                    <%
                        boolean validateOnField = grailsApplication.config.grails.plugins.asgard.validateOnField as boolean
                        if (validateOnField) {
                            def erro = ""
                            if ("erros" in attrs.itemButtons?.split(";")) {
                                item.validate()
                                item.errors?.allErrors?.each() {
                                    try {
                                        erro += messageSource.getMessage(it, null) + ' | \n'
                                    } catch (e) {
                                        erro += it.getDefaultMessage()
                                    }
                                }
                            }
                        }
                    %>
                    <g:if test="${erro}">
                        <asg:link controller="${attrs.controller}" action="edit" id="${item?.id}"
                                  class="btn btn-small btn-danger tip-bottom" title="${erro}"><i
                                class="icon icon-warning-sign icon-white"></i> Erros</asg:link>
                    </g:if>
                </td>
            </g:if>
        </tr>
        <%
            index = !item?.id ? old_index : index + 1
        %>

        <g:if test="${!item?.id && attrs.inlineMode}">
            <r:script disposition="defer">
            // Busca modal pelo id e classe só pra ter certeza que está mexendo no elemento certo
            disableModal("${attrs.name}.modal-grid");
            </r:script>
        </g:if>
    </g:each>
    </tbody>
</table>

<g:if test="${attrs.ignoreWidget}">
    </div>
</g:if>
<g:else>
    </div>
    </div>
    <g:hiddenField name="controllerRedirect" class="asg-ignore-check-dirty"/>
    <g:actionSubmit id="btnAdd${attrs.controller}" style="display: none;" value="${message(code: 'default.add.label')}"
                    action="gridAdd"
                    onclick="\$('#controllerRedirect').val('${attrs.controller}'); "/>
    <g:actionSubmit id="btnSave${attrs.controller}" style="display: none;"
                    value="${message(code: 'default.save.label')}"
                    action="save${attrs.controller}"/>

    <g:if test="${!attrs.ignorePagination}">
        <div class="grailspagination">
            <asg:remotePaginate name="${attrs.name}" controller="grid" action="gridAjax" total="${attrs.totalCount}"
                                max="10"/>
        </div>
    </g:if>

    <g:if test="${!attrs.ignoreContainer}">
        </div>
    </g:if>
</g:else>
