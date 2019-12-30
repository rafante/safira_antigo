package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios

public class MaterialLote extends Persistente {

    String lote
    Date validade_lote
    BigDecimal estoque = 0

    static defaultValueField = "lote"
    static defaultFilterFields = "lote;fornecedor"
    static defaultAutoCompleteFields = "lote"

    static belongsTo = [material: Material, fornecedor: ParceiroNegocios]

    static constraints = {
        material()
        fornecedor()
        lote()
        validade_lote()
    }

    String toString() {
        return "Lote: " + this.lote + "| Estoque: " + this.estoque + " | Forn:" + fornecedor.toString() + " | Venc:" + this.validade_lote?.format("dd/MM/yy")
    }

}