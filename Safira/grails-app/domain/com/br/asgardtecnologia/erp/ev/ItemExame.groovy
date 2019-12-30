package com.br.asgardtecnologia.erp.ev

import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.erp.materiais.Material

class ItemExame extends Persistente{

    Integer item
    static belongsTo = [exame: Exame, material: Material]

    static defaultFilterFields = "material;exame"
    static defaultAutoCompleteFields = "material"

    static constraints = {
        material(nullable: false, unique: ['exame'])
        exame(nullable: false)
    }

    @Override
    String toString() {
        return material.toString()
    }
}
