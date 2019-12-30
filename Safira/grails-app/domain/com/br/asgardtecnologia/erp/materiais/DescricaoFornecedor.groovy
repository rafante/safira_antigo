package com.br.asgardtecnologia.erp.materiais

import com.br.asgardtecnologia.base.Persistente
import com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios

/*Classe de informações de descrições de acordo 
 *com o Fornecedor. Utlizada para ordens de compra 
 *e importação de entrada de materiais.*/

public class DescricaoFornecedor extends Persistente {

    String codigo
    String descricao

    static belongsTo = [
            material: Material,
            fornecedor: ParceiroNegocios]

    static constraints = {

        material()
        fornecedor(asgDefaultFilter: [fornecedor: true])

        /**
         * C�digo do material no fornecedor
         */
        codigo()
        /**
         * Descri��o do material de acordo com o fornecedor.
         */
        descricao()
    }

    String toString() {
        return descricao
    }


}