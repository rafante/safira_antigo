package com.br.asgardtecnologia.erp.financeiro
import com.br.asgardtecnologia.erp.cadastros.FormaPagamento
import com.br.asgardtecnologia.erp.cadastros.TipoDocumento
import com.br.asgardtecnologia.erp.ev.ParametrosEV

import java.text.ParseException
import java.text.SimpleDateFormat

class MovimentoFinanceiroService {

    def tagsPrincipais = [
            [name: "Cod_Atendimento"],
            [name: "Unidade"],
            [name: "Data_Pagamento"],
            [name: "Valor_Total"],
            [name: "Forma_Pgto"],
    ]

    def fromXML(String xml) {
        def rootNode = new XmlSlurper().parseText(xml)
        def atendimentos = [unidades: [], formasPagamento: [], atendimentos: []]
        def unidades = []
        def formasPagamento = []
        for (def it : rootNode.Atendimento) {
            def atendimento = [:]
            atendimento.codAtendimento = it.Cod_Atendimento.toString()
            atendimento.unidade = it.Unidade?.toString()
            unidades << atendimento.unidade
            atendimento.dataPagamento = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(it.Data_Pagamento.toString() + " " + (it.Hora_Pagamento.toString() ?: "00:00:00"))
            atendimento.observacao = it.Observacao.toString()
            atendimento.valorTotal = new BigDecimal(it.Valor_Total.toString().replaceAll(",", "."))
            atendimento.formaPagamento = FormaPagamento.findByDescricao(it.Forma_Pgto.toString())
            formasPagamento << atendimento.formaPagamento
            atendimentos.atendimentos << atendimento
        }
        atendimentos.unidades = unidades.unique()
        atendimentos.formasPagamento = formasPagamento.unique()
        return atendimentos
    }

    def validaXml(xml) {
        def rootNode = new XmlSlurper().parseText(xml)
        int index = 0
        def erros = []
        ParametrosEV parametrosEV = ParametrosEV.get(1)
        if (!parametrosEV) {
            erros << "Não existe parametro ev cadastrado"
        } else if (!parametrosEV.planoContas) {
            erros << "parametros ev não possui plano de contas"
        } else if (!parametrosEV.contaCaixa) {
            erros << "parametros ev não possui conta caixa"
        } else if (!rootNode.Atendimento.size()) {
            erros << "Documento apresentado não possui elementos da tag Atendimento"
        } else {
            rootNode.Atendimento.each {
                index++
                def atendimento = it
                if (atendimento.Cod_Atendimento.toString().isEmpty())
                    erros << "Item ${index}: Não possui tag Cod_Atendimento"
                if (atendimento.Unidade.toString().isEmpty())
                    erros << "Item ${index}: Não possui tag Unidade"
                if (atendimento.Data_Pagamento.toString().isEmpty())
                    erros << "Item ${index}: Não possui tag Data_Pagamento"
                try {
                    def dataPagamento = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(it.Data_Pagamento.toString() + " " + (it.Hora_Pagamento.toString() ?: "00:00:00"))
                } catch (ParseException e) {
                    erros << "Item ${index}: Formato da tag Data_Pagamento / Hora_Pagamento inválido"
                }
                if (atendimento.Valor_Total.toString().isEmpty())
                    erros << "Item ${index}: Não possui tag Valor_Total"
                try {
                    def valorTotal = new BigDecimal(atendimento.Valor_Total.toString().replaceAll(",", "."))
                } catch (Exception e) {
                    erros << "Item ${index}: Valor Total com formato inválido"
                }
                if (atendimento.Forma_Pgto.toString().isEmpty())
                    erros << "Item ${index}: Não possui tag Forma_Pgto"
                else {
                    if (!FormaPagamento.findByDescricao(atendimento.Forma_Pgto.toString())) {
                        erros << "Item ${index}: Forma de pagamento inexistente"
                    }
                }
            }
        }

        return [valido: !erros.size(), erros: erros]
    }

    def saveList(Map atendimentosMap = [atendimentos: [], unidades: [], formasPagamento: []]) {
        try {
            def salvos = 0
            ParametrosEV parametrosEV = ParametrosEV.get(1)
            for (def unidade : atendimentosMap.unidades) {
                for (def formaPagamento : atendimentosMap.formasPagamento) {
                    def ats = atendimentosMap.atendimentos.findAll {
                        it.unidade.equals(unidade) && it.formaPagamento == formaPagamento
                    }
                    if (ats.size()) {
                        salvos++
                        def atendimento = ats.first()
                        def valor = ats.valorTotal.sum()
                        MovimentoFinanceiro movimentoFinanceiro = new MovimentoFinanceiro()
                        TipoDocumento tipoDocumento = TipoDocumento.findByDescricao(atendimento.formaPagamento.descricao)
                        if (!tipoDocumento) {
                            tipoDocumento = new TipoDocumento(descricao: atendimento.formaPagamento.descricao)
                            tipoDocumento.save()
                        }
                        movimentoFinanceiro.tipoDocumento = tipoDocumento
                        movimentoFinanceiro.descricao = "Movimento de caixa recepção - " + atendimento.unidade
                        movimentoFinanceiro.numeroDocumento = atendimento.codAtendimento
                        movimentoFinanceiro.debito_credito = TipoMovimentoFinanceiro.CREDITO
                        movimentoFinanceiro.dataDocumento = atendimento.dataPagamento
                        movimentoFinanceiro.dataEmissao = atendimento.dataPagamento
                        movimentoFinanceiro.contaCaixa = parametrosEV.contaCaixa
                        movimentoFinanceiro.planoContas = parametrosEV.planoContas
                        movimentoFinanceiro.valor = valor
                        movimentoFinanceiro.save()
                    }
                }
            }
            return salvos
        } catch (RuntimeException e) {
            return 0
        }
    }
}
