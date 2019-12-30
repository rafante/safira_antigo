class ReportsController {

    def print() {
        //tanto o get quanto o post vão trabalhar com esta view (nào tá aqui porque não ta no AsgarBase)
        switch (request.method) {
            case 'GET':
                // Exemplo: /reports/list?domain=com.br.asgardtecnologia.erp.materiais.LDM&reportController=LDM&reportAction=report
                break
            case 'POST':
                forward controller: params.reportController, action: params.reportAction, params: params
                break
        }
    }

}