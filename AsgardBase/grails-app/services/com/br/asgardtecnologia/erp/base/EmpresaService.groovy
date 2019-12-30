package com.br.asgardtecnologia.erp.base

public class EmpresaService extends BaseService {

    Boolean beforeSave(Empresa empresa) {
        empresa.endereco_comercial?.save()
        empresa.endereco_cobranca?.save()
        empresa.endereco_entrega?.save()

        return super.beforeSave(empresa)
    }

}