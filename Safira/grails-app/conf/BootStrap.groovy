import com.br.asgardtecnologia.erp.base.ParametrosGerais
import com.br.asgardtecnologia.erp.base.Widget
import com.br.asgardtecnologia.erp.cadastros.FormaPagamento
import com.br.asgardtecnologia.erp.financeiro.TipoConta
import com.br.asgardtecnologia.erp.materiais.ParametrosMateriais
import com.br.asgardtecnologia.erp.security.Perfil
import com.br.asgardtecnologia.erp.security.Usuario
import com.br.asgardtecnologia.erp.security.UsuarioPerfil
import groovy.sql.Sql

class BootStrap {

    def securityService
    def dataSource

    def init = { servletContext ->
        // Troca null para ''
        org.codehaus.groovy.runtime.NullObject.metaClass.toString = { return '' }


        if (!Usuario.count()) {
            println "Criando o Deus Admin ..."

            def adminPerfil = new Perfil(authority: 'ROLE_ADMIN').save(flush: true)
            def userPerfil = new Perfil(authority: 'ROLE_USER').save(flush: true)

            def adminUsuario = new Usuario(username: 'admin', enabled: true, password: 'admin')
            adminUsuario.save(flush: true)

            UsuarioPerfil.create(adminUsuario, adminPerfil, true)
            UsuarioPerfil.create(adminUsuario, userPerfil, true)

            println "... Deus Admin criado"
        }

        if (!ParametrosGerais.count()) (new ParametrosGerais()).save()
        if (!ParametrosMateriais.count()) (new ParametrosMateriais()).save()

        // Prepara os Widgets (caso não existam)
        if (!Widget.count()) {
            (new Widget(template: "/widgets/contaPagarVencendoHoje", title: "Títulos vencendo hoje", icon: "icon-list", span: 6)).save()
            (new Widget(template: "/widgets/contaPagarVencidos", title: "Títulos vencidos", icon: "icon-list", span: 6)).save()
        }

        if (!TipoConta.count()) {
            TipoConta bancaria = new TipoConta(descricao: "Bancária")
            bancaria.save(flush: true)
            TipoConta adClientes = new TipoConta(descricao: "Adiantamento Clientes")
            adClientes.save(flush: true)
            TipoConta adFornecedor = new TipoConta(descricao: "Adiantamento Fornecedor")
            adFornecedor.save(flush: true)
            TipoConta aplFinanceira = new TipoConta(descricao: "Aplicação Financeira")
            aplFinanceira.save(flush: true)
            TipoConta investimentos = new TipoConta(descricao: "Investimentos")
            investimentos.save(flush: true)
            TipoConta caixaInterno = new TipoConta(descricao: "Caixa Interno")
            caixaInterno.save(flush: true)
        }

        FormaPagamento transferenciaBancaria = FormaPagamento.findByDescricao("Transferência entre contas")
        if (!transferenciaBancaria) {
            transferenciaBancaria = new FormaPagamento(descricao: "Transferência entre contas")
            transferenciaBancaria.save(flush: true)
        }

        securityService.preparaPerfis()
    }


    def destroy = {
    }
}
