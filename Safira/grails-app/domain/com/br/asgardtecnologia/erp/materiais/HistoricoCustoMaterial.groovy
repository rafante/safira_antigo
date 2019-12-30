package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.base.Persistente

public class HistoricoCustoMaterial extends Persistente {
    Date data
    BigDecimal custo_total

    static belongsTo = [material: Material, entradaMaterial: EntradaMaterial]

    static constraints = {
        material()
        data()
        entradaMaterial()
        custo_total()
    }

}