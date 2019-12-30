<%@ page import="org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes" %>
<%
    if (!controller) controller = params.controller
    if (!action) action = params.action
    if (!id) id = params.id

    def title = params.reportController ?
            message(code: params.reportController + '.' + params.reportAction + '.label') : g.message(code: controller + ".label")
%>

<style>
.asg-fixed-top {
    background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#fff), to(#eceaef));
    background-image: -webkit-linear-gradient(top, #fff 0, #eceaef 100%);
    background-image: -moz-linear-gradient(top, #fff 0, #eceaef 100%);
    background-image: -ms-linear-gradient(top, #fff 0, #eceaef 100%);
    background-image: -o-linear-gradient(top, #fff 0, #eceaef 100%);
    background-image: linear-gradient(top, #fff 0, #eceaef 100%);
    top: -13px;
    box-shadow: 1px 1px 5px #888;
}

.action-group-left {
    display: none;
    position: fixed;
    left: 0px;
    top: 0px;
    height: 50px;
    width: 50px;
    padding-left: 10px;
}

.page-header {
    margin: 0;
}

.page-header .asg-fixed-top h1 {
    padding-left: 220px;
    /*text-align:center;*/
}

.tabulation {
    padding-left: 20px !important;
}

</style>

<div id="content-header">

    <g:if test="${action}">
        <div class="page-header">
            <div id="action-group">
                <div id="action-group-left" class="action-group-left">
                    <img src="${resource(dir: 'img', file: 'logo.png')}">
                </div>

                <h1 id="action-title">
                    <g:message code="default.${action}.label" args='["${title}"]'/>
                    <g:if test="${id}">(ID: ${id})</g:if>
                </h1>

                <div class="btn-group asg-group">
                    <g:if test="${grailsApplication.getArtefactByLogicalPropertyName("Domain", controllerName)}">
                        <g:if test="${action in ['create', 'edit'] && !('save' in hiddenActionButtons)}">
                            <a href="javascript:void(0);"
                               id="btnSave"
                               onclick="submitMainForm();"
                               class="btn tip-bottom"
                               title="${g.message(code: 'default.save.label')} (F10)" data-asg-bind-key="121">
                                <i class="icon icon-ok icon-white"></i>
                            </a>
                        </g:if>

                        <g:if test="${!('create' in hiddenActionButtons)}">
                            <asg:link class="btn tip-bottom" controller="${controller}" action="create"
                                      title="${g.message(code: 'default.button.create.label')} (F9)"
                                      data-asg-bind-key="120">
                                <i class="icon icon-file"></i>
                            </asg:link>
                        </g:if>

                        <g:if test="${action in ['show'] && !('edit' in hiddenActionButtons)}">
                            <asg:link class="btn tip-bottom" controller="${controller}" action="edit"
                                      id="${id}"
                                      title="${g.message(code: 'default.button.edit.label')} (F8)"
                                      data-asg-bind-key="119">
                                <i class="icon icon-pencil icon-white"></i>
                            </asg:link>
                        </g:if>

                        <g:if test="${action in ['edit'] && !('show' in hiddenActionButtons)}">
                            <asg:link class="btn tip-bottom" controller="${controller}" action="show"
                                      id="${id}"
                                      title="${g.message(code: 'default.button.show.label')} (F8)"
                                      data-asg-bind-key="119">
                                <i class="icon icon-zoom-in"></i>
                            </asg:link>
                        </g:if>

                        <g:if test="${!('list' in hiddenActionButtons)}">
                            <asg:link class="btn tip-bottom" controller="${controller}" action="list"
                                      title="${g.message(code: 'default.button.list.label')} (F7)"
                                      data-asg-bind-key="118">
                                <i class="icon icon-list"></i>
                            </asg:link>
                        </g:if>


                        <g:each in="${customButtons}" var="btn">
                            <asg:link class="btn tip-bottom" controller="${btn.controller ?: controller}"
                                      id="${btn.id ?: id}"
                                      action="${btn.action}"
                                      title="${btn.title}"
                                      target="${btn.target ?: '_self'}">
                                <i class="icon ${btn['icon']}"></i>
                                ${btn.showText ? btn.title : ""}
                            </asg:link>
                        </g:each>

                        <g:if test="${action in ['show', 'edit']}">

                        %{--<asg:link class="btn tip-bottom" action="copy" id="${id}"--}%
                        %{--title="${g.message(code: 'default.button.copy.label')}">--}%
                        %{--<i class="icon icon-copy icon-white"></i>--}%
                        %{--</asg:link>--}%

                        %{--<asg:link class="btn tip-bottom" action="prev" id="${id}"--}%
                        %{--title="${g.message(code: 'default.button.prev.label')}">--}%
                        %{--<i class="icon icon-arrow-left icon-white"></i>--}%
                        %{--</asg:link>--}%

                        %{--<asg:link class="btn tip-bottom" action="next" id="${id}"--}%
                        %{--title="${g.message(code: 'default.button.next.label')}">--}%
                        %{--<i class="icon icon-arrow-right icon-white"></i>--}%
                        %{--</asg:link>--}%

                            <g:if test="${!('delete' in hiddenActionButtons)}">
                                <asg:link class="btn tip-bottom" action="delete" id="${id}"
                                          title="${g.message(code: 'default.button.delete.label')}">
                                    <i class="icon icon-trash icon-white"></i>
                                </asg:link>
                            </g:if>
                        </g:if>
                    </g:if>
                </div>
            </div>

        </div>
    </g:if>

</div>