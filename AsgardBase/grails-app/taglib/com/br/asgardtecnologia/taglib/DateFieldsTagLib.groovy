package com.br.asgardtecnologia.taglib

class DateFieldsTagLib {

    static namespace = "asg"

    def labeledCalculatedDate = { attrs, body ->
        attrs.class = "asg-calculated-date"

        def attrsDias = [name: attrs.name + "_dias_calc", class: "asg-dias-calc"]
        attrsDias["data-date-field"] = attrs.name

        def bodyOut = "<div class='row'>"

        bodyOut += "<div class='col-12 col-sm-4'>"
        bodyOut += '<div class="input-group input-group-sm">'
        bodyOut += asg.decimal(attrsDias)
        bodyOut += '<span class="input-group-addon">' + g.message(code: 'dias.label') + '</span>'
        bodyOut += '</div>'
        bodyOut += "</div>"

        bodyOut += "<div class='col-12 col-sm-8'>"
        bodyOut += asg.datePicker(attrs, body)
        bodyOut += "</div>"

        bodyOut += "</div>" // row

        out << "<div class='col-12 col-sm-${attrs.span ?: 12}'>"
        out << asg.labeled(attrs, bodyOut)
        out << "</div>"
    }

}