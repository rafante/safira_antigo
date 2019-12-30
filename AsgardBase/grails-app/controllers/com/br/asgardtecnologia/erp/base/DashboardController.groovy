package com.br.asgardtecnologia.erp.base

import grails.plugins.springsecurity.Secured

class DashboardController {

    @Secured('IS_AUTHENTICATED_FULLY')
    def index() {
        def widgetList = Widget.list()
        [widgetList: widgetList]
    }

}
