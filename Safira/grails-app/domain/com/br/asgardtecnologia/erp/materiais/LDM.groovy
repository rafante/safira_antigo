package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.base.Persistente

public class LDM extends Persistente {

    BigDecimal quantidade

    static defaultFilterFields = "material;material.grupo;material.sub_grupo;material.localizacao"

    static belongsTo = [material: Material, material_composicao: Material]

    static constraints = {
        material_composicao(blank: false, nullable: false, asgDefaultFilter: [finalidade: ['\'MATERIA_PRIMA\'', '\'PRODUTO_INTERMEDIARIO\'']])
        quantidade(scale: 2)
    }

    static transients = Persistente.transients + ["custo", "custoTotal"]

    String toString() {
        return material?.toString() + " - " +
                quantidade
    }

    BigDecimal getCusto() {
        return material_composicao?.custo_total
    }

    BigDecimal getCustoTotal() {
        return material_composicao?.custo_total ? material_composicao?.custo_total * quantidade : 0
    }

    BigDecimal getPesoTotal() {
        return material_composicao?.peso ? material_composicao.peso * quantidade : 0
    }

}