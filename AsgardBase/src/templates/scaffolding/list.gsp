<% import grails.persistence.Event %>
<%=packageName%>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label', default: '${className}')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body><div class="container-fluid">
    <div class="col-xs-12">

        <div class="col-12 col-sm-3" style="display:none">
        </div>

        <div id="page" class="col-12">

            <g:if test="\${flash.message}">
                <bootstrap:alert class="alert-info">\${flash.message}</bootstrap:alert>
            </g:if>
            <% excludedProps = Event.allEvents.toList() << 'id' << 'version'
            allowedNames = domainClass.persistentProperties*.name << '<< 'empresa' << 'usuario'
            props = domainClass.properties.findAll { allowedNames.contains(it.name) && !excludedProps.contains(it.name) && it.type != null && !Collection.isAssignableFrom(it.type) }
            Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))

            def fieldsFilter = '\${['
            def fieldsGrid = ""
            props.eachWithIndex { p, i ->
                if (i < 6) {
                    fieldsFilter += '[name:"' + p.name + '"], '
                    fieldsGrid += p.name + ";"
                }
            }

            fieldsFilter = fieldsFilter[0..-3] + ']}'
            fieldsGrid = fieldsGrid[0..-2]

            %>
            <asg:filter domain="${domainClass.fullName}"
                        params="\${params}"
                        fields="${fieldsFilter}"/>

            <asg:grid controller="${domainClass.propertyName}" list="\${${propertyName}List}"
                      fields="${fieldsGrid}"
                      headerButtons="create" itemButtons="show;edit;delete;erros"/>

        </div>
    </div>
    </div></body>
</html>
