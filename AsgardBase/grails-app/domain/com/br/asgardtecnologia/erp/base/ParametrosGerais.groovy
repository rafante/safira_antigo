package com.br.asgardtecnologia.erp.base

import com.br.asgardtecnologia.base.Persistente

public class ParametrosGerais extends Persistente {

    Boolean cpfCnpjObrigatorio = true

    Boolean ignoreAjaxLoadingFilter = true
    Boolean ignoreAjaxLoadingGrid = true
    Boolean ignoreAjaxLoadingSelect = true

    static constraints = {
        cpfCnpjObrigatorio()
        ignoreAjaxLoadingFilter()
        ignoreAjaxLoadingGrid()
        ignoreAjaxLoadingSelect()
    }

}
