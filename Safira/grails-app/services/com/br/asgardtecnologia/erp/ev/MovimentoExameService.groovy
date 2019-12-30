package com.br.asgardtecnologia.erp.ev

import com.br.asgardtecnologia.erp.base.BaseService
import com.br.asgardtecnologia.erp.cadastros.Setor
import com.br.asgardtecnologia.erp.materiais.Recipiente

class MovimentoExameService extends BaseService {

    def exameService

    def save(object, params = [:]){
        super.save(object, params)
    }

    def exameFromXMLItem(it, result) {
        Long codExame = Long.parseLong(it.CodExame.toString())
        Exame exame = Exame.findByCodExameAndMnemonico(codExame, it.Mnemonico.toString())
        if (!exame)
            exame = new Exame()
        exame.codExame = codExame
        exame.mnemonico = it.Mnemonico.toString()
        exame.descricao = it.Descricao.toString()
        Setor setor = Setor.findByDescricao(it.Setor.toString())
        if (!setor) {
            setor = new Setor(descricao: it.Setor.toString())
            if (!setor.save())
                result.errors << setor.getErrors()
        }
        exame.setor = setor
        Long codRecipiente = Long.parseLong(it.CodRecipiente.toString())
        Recipiente recipiente = Recipiente.findByCodEv(codRecipiente)
        if (!recipiente) {
            recipiente = new Recipiente(codEv: codRecipiente, descricao: it.DescricaoRecipiente.toString())
            if (!recipiente.save())
                result.errors << recipiente.getErrors()
        }
        exame.recipiente = recipiente
        exame.precoCusto = new BigDecimal(it.PrecoCusto.toString().replaceAll(",", "."))
        exame.precoVenda = new BigDecimal(it.PrecoVenda.toString().replaceAll(",", "."))
        exame.ativo = it.Ativo.equals("S")
        if (!exame.save())
            result.errors << exame.getErrors()
        else
            result.exames << exame
        return  exame
    }

    def movimentosFromXML(xml) {
        def result = [exames: [], errors: [], movimentos: []]
        def rootNode = new XmlSlurper().parseText(xml)
        rootNode.Atendimento.each { atendimento ->
            atendimento.Exame.each { it ->
                def exame = exameFromXMLItem(it, result)
                Date data = Date.parse("dd/MM/yyyy", it.Data.toString())
                BigDecimal quantiade = new BigDecimal(it.Quantidade.toString().replaceAll(",", "."))
                def movimento = new MovimentoExame(exame: exame, dataMovimento: data, quantidade: quantiade)
                result.movimentos << movimento
            }
        }

        return result
    }

    def validaXml(xml) {
        def rootNode = new XmlSlurper().parseText(xml)
        int index = 0
        def erros = []
        if (!rootNode)
            ParametrosEV parametrosEV = ParametrosEV.get(1)
        if (rootNode.Atendimento.size() == 0)
            erros << "XML informado não possui a tag Atendimentos"
        if (rootNode.Atendimento.Exame.size() == 0)
            erros << "Tag Atendimento sem nenhum exame"
        if (!erros.size()) {
            rootNode.Atendimento.Exame.each {
                index++
                validaExame(it, index, erros)
                validaMovimento(it, index, erros)
            }
        }

        return [valido: !erros.size(), erros: erros]
    }

    def validaMovimento(it, index, erros) {
        try {
            BigDecimal quantiade = new BigDecimal(it.Quantidade.toString().replaceAll(",", "."))
        } catch (NumberFormatException e) {
            erros << "Item ${index}: Tag Quantidade possui valor inválido"
        }
        Date data = Date.parse("dd/MM/yyyy", it.Data.toString())
        if (!data)
            erros << "Item ${index}: Tag Data possui valor inválido"
    }

    def validaExame(it, index, erros) {
        def codExame, mnemonico
        if (it.CodExame.toString().isEmpty())
            erros << "Item ${index}: Não possui tag CodExame"
        try {
            codExame = Long.parseLong(it.CodExame.toString())
        } catch (NumberFormatException e) {
            erros << "Item ${index}: Tag CodExame possui valor inv�lido."
        }
        if (it.Mnemonico.toString().isEmpty())
            erros << "Item ${index}: Não possui tag Mnemonico"
        mnemonico = it.Mnemonico.toString()
        def exame = Exame.findByCodExame(codExame)
        if (exame && !exame.mnemonico.equals(mnemonico))
            erros << "Item ${index}: Já existe um exame com o código ${codExame}, porém o mnemônico está diferente. Esperado: ${exame.mnemonico}. Encontrado: ${mnemonico}"
        exame = Exame.findByMnemonico(mnemonico)
        if (exame && exame.codExame != codExame)
            erros << "Item ${index}: Já existe um exame com o mnemônico ${mnemonico}, porém o código do exame está diferente. Esperado: ${exame.codExame}. Encontrado: ${codExame}"
        if (it.Setor.toString().isEmpty())
            erros << "Item ${index}: Não possui tag Setor"
        if (it.CodRecipiente.toString().isEmpty())
            erros << "Item ${index}: Não possui tag CodRecipiente"
        if (it.DescricaoRecipiente.toString().isEmpty())
            erros << "Item ${index}: Não possui tag DescricaoRecipiente"
        if (it.PrecoCusto.toString().isEmpty())
            erros << "Item ${index}: Não possui tag PrecoCusto"
        try {
            BigDecimal precoCusto = new BigDecimal(it.PrecoCusto.toString().replaceAll(",", "."))
        } catch (NumberFormatException e) {
            erros << "Item ${index}: Tag PrecoCusto possui valor inv�lido"
        }
        if (it.PrecoVenda.toString().isEmpty())
            erros << "Item ${index}: Não possui tag PrecoVenda"
        try {
            long precoVenda = new BigDecimal(it.PrecoVenda.toString().replaceAll(",", "."))
        } catch (NumberFormatException e) {
            erros << "Item ${index}: Tag PrecoVenda possui valor inv�lido"
        }
        if (it.Ativo.toString().isEmpty())
            erros << "Item ${index}: Não possui tag Ativo"
    }

    def saveList(ArrayList<MovimentoExame> movimentos) {
        def resultados = [salvos: 0, erros: []]
        for (MovimentoExame it : movimentos) {
            if (!it.exame.recipiente.id)
                it.exame.recipiente.save()
            if (!it.exame.setor.id)
                it.exame.setor.save()
            if (!it.exame.id || !exameService.save(it.exame)) {
                resultados.erros << [item: it.exame.codExame, erros: it.errors]
            }else{
                if(!it.id && !save(it)){
                    resultados.erros << [item: it.exame.codExame, erros: it.errors]
                }else{
                    resultados.salvos++
                }
            }
        }
        return resultados
    }
}
