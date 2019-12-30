package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.base.Utils
import com.br.asgardtecnologia.erp.base.BaseService
import com.br.asgardtecnologia.erp.base.Empresa
import com.br.asgardtecnologia.erp.cadastros.CentroCusto
import com.br.asgardtecnologia.erp.cadastros.FormaPagamento
import com.br.asgardtecnologia.erp.cadastros.ParceiroNegocios
import com.br.asgardtecnologia.erp.cadastros.TipoDocumento
import com.br.asgardtecnologia.erp.config.Periodicidade
import com.br.asgardtecnologia.erp.ev.ParametrosEV
import com.br.asgardtecnologia.erp.financeiro.base.CompensacaoItemFinanceiroBase
import com.br.asgardtecnologia.erp.financeiro.base.FinanceiroBase
import com.br.asgardtecnologia.erp.financeiro.base.ItemFinanceiroBase
import com.br.asgardtecnologia.erp.financeiro.base.ProrrogacaoItemFinanceiroBase

import javax.xml.bind.ValidationException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.regex.Pattern

class FinanceiroBaseService extends BaseService {


    Boolean save(FinanceiroBase financeiroBase, Map params) {
        if (!financeiroBase.hasItens()) {
            if (!params.valorTotal) params.valorTotal = 0
            if (!params.numParcelas) params.numParcelas = 1
            if (!params.primeiroVencimento) params.primeiroVencimento = new Date()
            if (!params.diasPeriodicidade) params.diasPeriodicidade = 30

            financeiroBase.geraParcelas(Utils.ToBigDecimal(params.valorTotal),
                    params.numParcelas.toInteger(),
                    params.primeiroVencimento,
                    Periodicidade.read(params.periodicidade?.id),
                    params.diasPeriodicidade
            )

            def itensOrdenados = financeiroBase?."${financeiroBase.getItensName()}"
            itensOrdenados = itensOrdenados?.sort({ ItemFinanceiroBase a, ItemFinanceiroBase b -> a.dataVencimento?.compareTo(b.dataVencimento) })
            itensOrdenados?.eachWithIndex { itemFinanceiro, index ->
                itemFinanceiro.item = index + 1
            }
        }

        return financeiroBase.save(flush: true)
    }

    File getArquivoRemessa(itensContaReceber) { //que eh este camarada aqui volta um ai
        File f = new File('remessa.rem')
        if (!f.exists()) {
            f.createNewFile()
        } else {
            f.delete()
            f.createNewFile()
        }
        String arquivo = ""
        ParametrosFinanceiro parametros = ParametrosFinanceiro.get(1)
        int quantLinha = 1
        char LF = (char) 10
        //---------------------------------
        //header - Página 6
        //---------------------------------
        arquivo += espacarDigitos("0", 1) //001 a 001
        arquivo += espacarDigitos('1', 1) //002 a 002
        arquivo += espacarCaracteres('REMESSA', 7) //003 a 009
        arquivo += espacarDigitos('01', 2) //010 a 011
        arquivo += espacarCaracteres('COBRANCA', 15) //012 a 026
        arquivo += espacarDigitos(parametros.contaCorrente.codigoTransmissao, 20) //027 a 046
        arquivo += espacarCaracteres(parametros?.empresa?.nome, 30)
//047 a 076 // sempre que vc tiver pegando uma propriedade do objeto põe o ? pra o caso dele tá null
        arquivo += espacarDigitos('033', 3) //077 a 079
        arquivo += espacarCaracteres('SANTANDER', 15)//080 a 094
        arquivo += espacarDigitos(new Date().format("ddMMyy"), 6) //095 a 100
        arquivo += espacarDigitos('', 16) //101 a 116
        arquivo += espacarCaracteres('', 47)//117 a 163
        arquivo += espacarCaracteres('', 47)//164 a 210
        arquivo += espacarCaracteres('', 47)//211 a 257
        arquivo += espacarCaracteres('', 47)//258 a 304
        arquivo += espacarCaracteres('', 47)//305 a 351
        arquivo += espacarCaracteres('', 33) //352 a 385 - 33 espaços em branco (385-352=33)
        arquivo += espacarCaracteres('', 6)//386 a 391 - espaços em branco 5 espaços em branco (391-386=5)
        arquivo += espacarCaracteres('', 4) //392 a 394
        arquivo += espacarDigitos(quantLinha, 6) //395 a 400 espacarDigitos(quantLinha, 6)
        //aqui termina a linha do arquivo por isso entra o \n que faz saltar uma linha
        arquivo += LF
        quantLinha++

        //---------------------------------
        //registros de movimento - Página 7
        //---------------------------------

        int numeroDeContas = 0

        //Aqui a gente tá dando loop em tudo que é item ou seja, se uma conta tem 10 parcelas, a gente tá gerando uma linha pra cada parcela
        //Porém o Anderson disse que toda conta vai ter sempre só 1 parcela. Mas na prática, não tem problema, podemos deixar o loop por todas
        //as parcelas mesmo porque se na prática só tiver uma parcela mesmo a única coisa que vai acontecer é que vai passar só 1 vez pra cada conta
        //Aqui vc tem uma lista de itens de contas a receber e tá dando um loop nessa lista
        itensContaReceber.each { ItemContaReceber item ->
            //aqui dentro a variavel item representa um ItemContaReceber da lista itensContaReceber que vc tá dando loop e esse item tem uma ContaReceber
            //e essa ContaReceber tem um ParceiroNegocios e esse parceiro é que é o cara que vc quer
            numeroDeContas += 1
            arquivo += espacarDigitos('1', 1) //001 a 001
            //Um exemplo é aqui. Esse segundo intervalo é 01 pra CPF e 02 pra CNPJ. Pra saber se é
            //CNPJ eu olho o campo cnpj_cpf do parceiro de negócios (cliente) da conta. Se o tamanho for 14
            //é CNPJ, se for 11 e CPF
            arquivo += espacarDigitos(item?.contaReceber?.parceiroNegocios?.cnpj_cpf?.size() == 14 ? '02' : '01', 2)//002 a 003
            //o intervalo abaixo é pra CNPJ. Só que o se o campo tiver menos de 14 tem que completar com zeros à esquerda
            String cpf_cnpj = item?.contaReceber?.parceiroNegocios?.cnpj_cpf
            if (cpf_cnpj?.size() < 14) {
                int numeroDeZerosAEsquerda = 14 - cpf_cnpj?.size() ?: 0
                // isso faz a string ficar com 14 caracteres alinhando o que já tem à direita e completando com espaços à esquerda
                cpf_cnpj = cpf_cnpj?.toString()
            }
            arquivo += espacarDigitos(cpf_cnpj, 14) //004 a 017
            arquivo += espacarDigitos(parametros.contaCorrente.agencia, 4)  //018 a 021 Código da agência Beneficiário confirmar se e isso espacarCaracteres(parametros.contaCorrente.agencia, 4)
            arquivo += espacarDigitos(parametros.contaCorrente.conta, 8) //022 a 029 espacarCaracteres(parametros.contaCorrente.conta, 8)
            arquivo += espacarDigitos(parametros.contaCorrente.conta, 8) //030 a 037 PERGUNTAR espacarCaracteres(parametros.contaCorrente.conta, 8)
            arquivo += espacarCaracteres('', 25) //038 a 062
            arquivo += espacarDigitos(parametros.nossoNumero, 8) //063 a 070
            arquivo += espacarDigitos('0', 6) //071 a 076 Data do segundo desconto (Nota 11)
            arquivo += espacarCaracteres('', 1) //077 a 077
            arquivo += espacarDigitos('4', 1) //078 a 078 Informação de multa = 4, senão houver informar zero Verificar página 16
            arquivo += espacarDigitos(item.multa, 4)   //079 a 082 Percentual multa por atraso % //ok aqui
            arquivo += espacarDigitos('0', 2) //083 a 084
            arquivo += espacarDigitos('0', 13) //085 a 097 Valor do título em outra unidade (consultar banco) *VERIFICAR*
            arquivo += espacarCaracteres('', 4)  //098 a 101
            arquivo += espacarDigitos(item.diasVencimento, 6)//102 a 107 Data para cobrança de multa. (Nota 4)
            arquivo += espacarDigitos(parametros.contaCorrente.codigoCarteira, 1) //108 a 108 Código da carteira
            arquivo += espacarDigitos('01', 2)//109 a 110 coonsulta anderson
            arquivo += espacarCaracteres(parametros.nossoNumero, 10)  //111 a 120 *verificar*
            arquivo += espacarDigitos(item.dataVencimento.format("ddMMyy"), 6) //121 a 126 tirar barras yyyMMdd
            arquivo += espacarDigitos(item.valor, 13) //127 a 139 tirar o </br>   "-"  do documentoParcela //aqui na verdade devia vir o valor
            arquivo += espacarDigitos(parametros.contaCorrente.banco.codigo, 3) //140 a 142
            arquivo += espacarDigitos('0', 5)//143 a 147 //voltar aqui
            arquivo += espacarDigitos('06', 2)  //148 a 149
            arquivo += espacarCaracteres('N', 1)//150 a 150
            arquivo += espacarDigitos(item.data_inclusao.format("ddMMyy"), 6)//151 a 156
            arquivo += espacarDigitos('00', 2)//157 158
            arquivo += espacarDigitos('00', 2)//159 160
            arquivo += espacarDigitos(item.juros, 13) //161 173 calcular juros
            arquivo += espacarDigitos('0', 6)//174 179
            arquivo += espacarDigitos(item?.descontos, 13) //180 192 // valorDia?
            arquivo += espacarDigitos('0', 13)//193 205
            arquivo += espacarDigitos('0', 13)//206 218
            arquivo += espacarDigitos(item?.contaReceber?.parceiroNegocios?.cnpj_cpf?.size() == 14 ? '02' : '01', 2) //219 220
            arquivo += espacarDigitos(item?.contaReceber?.parceiroNegocios?.cnpj_cpf, 14) //221 234 verificar se esta certo ate aqui digito
            arquivo += espacarCaracteres(item.contaReceber.parceiroNegocios.nome, 40) //235 274
            arquivo += espacarCaracteres(item.contaReceber.parceiroNegocios.enderecoPrincipal, 40) //275 314 Endereço do pagador Verificar se e isso mesmo cliente.enderecoPrincipal
            arquivo += espacarCaracteres(item.contaReceber.parceiroNegocios.bairro, 12) //315 326
            arquivo += espacarDigitos(item.contaReceber.parceiroNegocios.cep, 5) //327 331
            arquivo += espacarDigitos(item.contaReceber.parceiroNegocios.logradouro, 3) //332 334 Complemento do CEP Verificar onde esta a info logradouro
            arquivo += espacarCaracteres(item.contaReceber.parceiroNegocios.enderecoPrincipal?.municipio, 15) //335 349 inscricao_municipal
            arquivo += espacarCaracteres(item.contaReceber.parceiroNegocios.enderecoPrincipal?.municipio?.estado?.sigla, 2) //350 351 UF estado do pagador estadual
            arquivo += espacarCaracteres('', 30)//352 381
            arquivo += espacarCaracteres('', 1) //382 382
            arquivo += espacarDigitos(parametros.contaCorrente.codigoComplemento, 1)//383 383
            arquivo += espacarDigitos('00', 2)//384 385
            arquivo += espacarCaracteres('', 6) //386 391
            arquivo += espacarDigitos('00', 2)//392 393
            arquivo += espacarCaracteres('', 1)//394 394
            arquivo += espacarDigitos(quantLinha, 6)//395 400

            //aqui termina a linha do arquivo por isso entra o \n que faz saltar uma linha
            arquivo += LF
            quantLinha++


        }

        //---------------------------------
        //trailler - Página 9 398
        //---------------------------------

        arquivo += espacarDigitos('0', 1) //001 001
        arquivo += espacarDigitos(numeroDeContas, 6)  //002 007
        int totalTitulos = 0//008 020
        itensContaReceber.each { ItemContaReceber item ->
            totalTitulos += item.valor
        }
        arquivo += espacarDigitos(totalTitulos, 13)//008 020 No documento esta 11 mas o valor certo e 13
        arquivo += espacarDigitos('0', 374)  //021 394
        arquivo += espacarDigitos(quantLinha, 6) //395 400


        f.write(arquivo)
        return f
    }

    /**
     * Método seguro pra espaçar o tanto de caracteres que tem que espaçar e se o objeto estiver nulo, coloca os espaços
     * em branco no lugar de dar exception
     * @param objeto
     * @param quantidadeEspacos
     * @param quantidadeZeros
     * @return
     */
    String espacarCaracteres(objeto, quantidadeEspacos) {
        if (objeto != null) {
            return objeto.toString().replace(",", "").padRight(quantidadeEspacos).substring(0, quantidadeEspacos)


        } else
            return ''.padRight(quantidadeEspacos)
    }

    String espacarDigitos(objeto, quantidadeZeros) {
        return objeto.toString().replaceAll("[^\\d]", "").padLeft(quantidadeZeros).replaceAll(" ", "0").substring(0, quantidadeZeros)
    }

    def compensarParcela(ItemFinanceiroBase itemContaFinanceiroBase, params) throws ValidationException {
        def ret

        // Gambiarra pra pegar as compensações do post :P
        params.each { i, obj ->
            if (((Pattern) ~/(compensacao)\[[0-9]+\]/).matcher(i).matches() && (Utils.ToDouble(obj.valor) > 0)) {

                // Recupera os dados da compensação
                BigDecimal valor = Utils.ToBigDecimal(obj.valor).setScale(2, RoundingMode.HALF_EVEN)
                Date data = Utils.ToDate(params["dataCompensacao" + itemContaFinanceiroBase?.id])
                FormaPagamento formaPagamento = FormaPagamento.read(obj.formaPagamento?.id)
                ContaCorrente contaCorrente = ContaCorrente.read(obj.contaCorrente?.id)

                // Gera um cheque caso necessário
//                ContaCorrente contaBancaria = ContaBancaria.read(obj.cheque?.contaBancaria?.id)
                String numero = obj.cheque?.numero

                Cheque cheque = null
                if (numero && contaCorrente)
                    cheque = new Cheque(numero: numero, contaCorrente: contaCorrente, valor: valor)

                // Executa a compensação
                ret = itemContaFinanceiroBase.compensar(valor, data, formaPagamento, contaCorrente, cheque)
            }
        }

        if (ret != 'compensacaoItemFinanceiroBase.success') return ret

        // Salva o ItemFinanceiro
        if (!itemContaFinanceiroBase.save()) {
            itemContaFinanceiroBase.errors.each {
                return it.fieldError
            }
        } else {
            return 'compensacaoItemFinanceiroBase.success'
        }
    }

    def compensarBloco(itensFinanceiroBase, String entity, params) {
        // Previne formaPagamento vazio
        if (!params.formaPagamento.id || (params.formaPagamento.id && params.formaPagamento.id.toString() == 'null')) {
            return 'baixaBloco.formaPagamento.empty'
        }

        // Previne contaCaixa vazio
        else if (!params.contaCorrente.id || params.contaCorrente?.id.toString() == 'null') {
            return 'baixaBloco.contaCorrente.empty'
        }

        // formaPagamento e contaCorrente ok, continuar
        else {
            FormaPagamento formaPagamento = FormaPagamento.read(params.formaPagamento.id)
            ContaCorrente contaCorrente = ContaCorrente.read(params.contaCorrente.id)

            // Realiza as compensações para cada um dos itens
            itensFinanceiroBase.each {
                def itemFinanceiroBase = it

                // Checa se realmente precisa fazer uma compensação
                if (itemFinanceiroBase.valorCompensado < itemFinanceiroBase.valor) {
                    // Executa a compensação
                    def ret = itemFinanceiroBase.compensar(new Date(), formaPagamento, contaCorrente)
                    if (ret != 'compensacaoItemFinanceiroBase.success') return ret
                }
            }

            return 'baixaBloco.success'
        }
    }

    def estornarParcela(CompensacaoItemFinanceiroBase compensacaoItemFinanceiroBase, params) {
        return compensacaoItemFinanceiroBase.estornar(params.justificativaEstorno)
    }

    def prorrogarParcela(ItemFinanceiroBase itemFinanceiroBase, entity, params) {
        ProrrogacaoItemFinanceiroBase prorrogacaoItemFinanceiroBase = this.class.classLoader.loadClass('com.br.asgardtecnologia.erp.financeiro.ProrrogacaoItem' + entity).newInstance()

        prorrogacaoItemFinanceiroBase.data = Utils.ToDate(params.data_holder)
        prorrogacaoItemFinanceiroBase.justificativa = params.justificativa
        prorrogacaoItemFinanceiroBase['itemConta' + entity] = itemFinanceiroBase

        if (!prorrogacaoItemFinanceiroBase.save(flush: true)) {
            prorrogacaoItemFinanceiroBase.errors.each {
                println "** ERROR PRORROGACAOITEM **"
                println it.fieldError.code
                println it.fieldError.field
                println params.contaCorrente.id
                return it.fieldError
            }
        }

        itemFinanceiroBase.dataVencimento = prorrogacaoItemFinanceiroBase.data
        itemFinanceiroBase.save(flush: true)

        return 'prorrogacaoItemFinanceiroBase.success'
    }

    def fromXML(String xml, referencia, vencimento) {
        def rootNode = new XmlSlurper().parseText(xml)
        TipoDocumento nf = TipoDocumento.findByDescricao("NF")
        ParametrosEV parametrosEV = ParametrosEV.get(1)
        CentroCusto matriz = CentroCusto.get(1)
        ParceiroNegocios cliente = ParceiroNegocios.get(1)
        int salvos = 0
        rootNode.Nfse.each {
            def nfse = it//
            if (!nfse.toString().isEmpty()) {
                ContaReceber contaReceber = new ContaReceber()
                contaReceber.dataEmissao = new Date()
                contaReceber.dataDocumento = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(nfse.DataEmissao.toString())
                contaReceber.tipoDocumento = nf
                contaReceber.descricao = nfse.Servico.Discriminacao.toString() + " Cod Verificação " + nfse.IdentificacaoNfse.CodigoVerificacao.toString()
                contaReceber.parceiroNegocios = ParceiroNegocios.findByCnpj_cpf(nfse.TomadorServico.IdentificacaoTomador.CpfCnpj.toString()) ?: cliente
                contaReceber.referencia = referencia
                contaReceber.planoContas = parametrosEV.planoContas
                contaReceber.contaCorrente = parametrosEV.contaCorrente
                contaReceber.centroCusto = matriz
                BigDecimal total = new BigDecimal(nfse.Servico.Valores.ValorLiquidoNfse.toString())
                contaReceber.valorTotal = total
                save(contaReceber, [primeiroVencimento: vencimento, valorTotal: total, periodicidade: [id: 1]])
                salvos++
            }
        }
        return salvos
    }

    def fromXMLFatura(String xml, referencia) {
        def rootNode = new XmlSlurper().parseText(xml)
        ParametrosEV parametrosEV = ParametrosEV.get(1)
        PlanoContas planoContas = parametrosEV.planoContas
        CentroCusto centroCusto = parametrosEV.centroCusto
        def retorno = [:]

        int salvos = 0
        def tipoDoc = TipoDocumento.findByDescricao("Nota Fiscal")
        def item = 1
        retorno.itens = []
        rootNode.Fatura.each { fatura ->
            if (!fatura.toString().isEmpty()) {
                def codigo = fatura.Cd_Fatura.toString()
                //def parceiro = ParceiroNegocios.findByCnpj_cpf(fatura.CNPJ_Associacao.toString())
                def parceiro = ParceiroNegocios.findByNome(fatura.Convenio.toString())
                def dataInicio = new SimpleDateFormat("dd/MM/yyyy").parse(fatura.Data_Inicio.toString())
                def dataFim = new SimpleDateFormat("dd/MM/yyyy").parse(fatura.Data_Fim.toString())
                def dataVencimento = new SimpleDateFormat("dd/MM/yyyy").parse(fatura.Data_Vencimento.toString())
                def documento = fatura.Cd_Fatura.toString()
                ContaReceber contaReceber = ContaReceber.findByDocumento(documento) //aqui já tá até criando
                if (!contaReceber) {
                    contaReceber = new ContaReceber()
                }
                contaReceber.documento = documento
                contaReceber.dataEmissao = new SimpleDateFormat("dd/MM/yyyy").parse(fatura.Data_Gerada.toString())
                contaReceber.dataDocumento = new SimpleDateFormat("dd/MM/yyyy").parse(fatura.Data_Gerada.toString())
                contaReceber.tipoDocumento = tipoDoc
                contaReceber.descricao = "Fatura ${codigo}"
                contaReceber.parceiroNegocios = parceiro
                contaReceber.referencia = "Período ${fatura.Data_Inicio.toString()} a ${fatura.Data_Fim.toString()}"
                contaReceber.centroCusto = centroCusto
                contaReceber.planoContas = planoContas
                contaReceber.contaCorrente = null
                contaReceber.valorTotal = new BigDecimal(fatura.Valor_Total.toString().replaceAll(",", "."))

                save(contaReceber, [primeiroVencimento: dataVencimento, valorTotal: contaReceber.valorTotal, periodicidade: [id: 1]])
                retorno.itens << [numero: item, conta: contaReceber]
                if (!contaReceber.hasErrors())
                    salvos++ //se salvou sem erro, aumenta 1 no número de contas salvas para mostrar para o usuário
                item++
            }
        }
        retorno.salvos = salvos
        return retorno
    }

    def validaXml(xml, referencia) {
        def rootNode = new XmlSlurper().parseText(xml)
        int index = 0
        def erros = []
        def noPartners = []
        ParametrosEV parametrosEV = ParametrosEV.get(1)
        if (!parametrosEV) {
            erros << "Necessário cadastrar parametro EV"
        } else if (!parametrosEV.planoContas) {
            erros << "ParametrosEV não possui plano de contas cadastrado"
        } else if (!parametrosEV.contaCorrente) {
            erros << "ParametrosEV não possui conta caixa cadastrada"
        } else if (referencia?.length() > 255) {
            erros << "Campo Referência muito longo"
        } else if (!rootNode.Nfse.size()) {
            erros << "Não há tags Nfse no documento informado"
        } else {
            rootNode.Nfse.each {
                index++
                def nfse = it
                if (!nfse.toString().isEmpty()) {
                    if (nfse.DataEmissao.toString().isEmpty()) {
                        erros << "Nfse ${nfse.IdentificacaoNfse.Numero}: Não possui a tag DataEmissao"
                    }
                    try {
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(nfse.DataEmissao.toString())
                    } catch (ParseException e) {
                        erros << "Nfse ${nfse.IdentificacaoNfse.Numero}: DataEmissao com formato inválido"
                    }
                    /*String descricao = nfse.Servico.Discriminacao.toString() + " Cod Verificação " + nfse.IdentificacaoNfse.CodigoVerificacao.toString()
                    if (descricao.length() > 255) {
                        erros << "Nfse ${nfse.IdentificacaoNfse.Numero}: Descrição muito longa - tag Discriminação"
                    }*/
                    //Lê o cpf ou cnpj dependendo do que tiver vindo no xml
                    def cpf = nfse.TomadorServico.IdentificacaoTomador.CpfCnpj?.Cpf?.toString()
                    def cnpj = nfse.TomadorServico.IdentificacaoTomador.CpfCnpj?.Cnpj?.toString()

                    //busca no banco algum parceiro com o cpf (se tiver vindo o cpf) ou o cpnj (se tiver vindo o cnpj)
                    def parceiro = ParceiroNegocios.findByCnpj_cpf(cpf ?: cnpj)
                    // cpf ?: cnpj significa use o cpf, se ele tiver null use o cnpj
                    if (!parceiro) { //se não encontrou nenhum parceiro, cria um
                        if (!noPartners.contains()) {
                            parceiro = new ParceiroNegocios(cnpj_cpf: cpf ?: cnpj)
                            parceiro.nome = 'Criado pelo importador de NFe'
                            boolean saveDeuCerto = parceiro.save()
                            erros << "Nfse ${nfse.IdentificacaoNfse.Numero}: Não existe parceiro de negócios com cpf/cnpj igual a ${cpf ?: cnpj}. Foi criado um e o id dele é ${parceiro.id}"
                        }
                    }
                }
            }
        }
        return [valido: !erros.size(), erros: erros]
    }

    def validaXmlFatura(xml, referencia) { //sacou?
        def rootNode = new XmlSlurper().parseText(xml)
        //Aqui ele usa essa classe XmlSlurper que pega uma string de xml e converte num objeto tipo aquela parada da data que nós vimos ontem
        int index = 0
        def erros = []
        def noPartners = []
        ParametrosEV parametrosEV = ParametrosEV.get(1)
        if (!parametrosEV) {
            erros << "Necessário cadastrar parametro EV"
        } else if (!parametrosEV.planoContas) {
            erros << "ParametrosEV não possui plano de contas cadastrado"
        } else if (!parametrosEV.contaCorrente) {
            erros << "ParametrosEV não possui conta caixa cadastrada"
        } else if (referencia?.length() > 255) {
            erros << "Campo Referência muito longo"
        } else if (!parametrosEV.centroCusto) {
            erros << "Cadastrar um centro de custo nos parametros EV"
        } else if (!rootNode.Fatura.size()) {
            erros << "Não há tags Fatura no documento informado"
        } else {
            rootNode.Fatura.each { fatura ->
                index++
                if (!fatura.toString().isEmpty()) {
                    //valida documento
                    if (!fatura.Cd_Fatura) {
                        erros << "Fatura: ${index} não contém a tag obrigatória Cd_Fatura"
                    }
                    def codigo = fatura.Cd_Fatura.toString()
                    //verifica se existem as tags de data/hora
                    if (!fatura.Data_Gerada || !fatura.Hora_Gerada) {
                        //se não existe mostra mensagem de erro
                        erros << "Fatura ${codigo} não contém a(s) tag(s) Data_Gerada e Hora_Gerada"
                    } else {
                        //se existe, verifica se a data é válida
                        try {
                            Date dataEmissao = new SimpleDateFormat("dd/MM/yyyy").parse(fatura.Data_Gerada.toString())
                        } catch (ParseException e) {
                            erros << "Fatura ${codigo}: Data_Gerada com formato inválido"
                        }
                    }
                    //valida se possui tipo de documento com descrição "Nota Fiscal", se não tem, cria
                    def tipoDocNotaFiscal = TipoDocumento.findByDescricao("Nota Fiscal")
                    if (!tipoDocNotaFiscal) {
                        tipoDocNotaFiscal = new TipoDocumento(descricao: "Nota Fiscal")
                        tipoDocNotaFiscal.save(flush: true)
                    }
                    //valida parceiro de negócios
                    def cpfCnpj = fatura.CNPJ?.toString()//e que eu não achei onde ele busca o cliente hahah
                    if (!cpfCnpj) {
                        erros << "Fatura ${codigo} não possui a tag CNPJ_Associacao"
                    } else {
                        def parceiro = ParceiroNegocios.findByCnpj_cpf(cpfCnpj)
                        if (!parceiro) {
                            def nome = fatura.Convenio?.toString()
                            parceiro = new ParceiroNegocios(cnpj_cpf: cpfCnpj, nome: nome)
                            parceiro.save(flush: true)
                        }
                    }
                    //valida referencia
                    def dataInicio = fatura.Data_Inicio?.toString()
                    def dataFim = fatura.Data_Fim?.toString()
                    if (!dataInicio && !dataFim) {
                        erros << "Fatura ${codigo} não possui as tags Data_Inicio e Data_Fim"
                    }
                    //valida tag de valor total
                    def totalStr = fatura.Valor_Total?.toString()
                    if (!totalStr) {
                        erros << "Fatura ${codigo} não possui tag Valor_total"
                    } else {
                        try {
                            totalStr = totalStr.replaceAll(",", ".")
                            BigDecimal total = new BigDecimal(totalStr)
                        } catch (e) {
                            erros << "Fatura ${codigo}: Valor total: ${totalStr} inválido"
                        }
                    }
                    //valida data de vencimento
                    def dataVencimento = fatura.Data_Vencimento?.toString()
                    if (!dataVencimento) {
                        erros << "Fatura ${codigo} não possui tag Data_Vencimento"
                    } else {
                        try {
                            new SimpleDateFormat("dd/MM/yyyy").parse(dataVencimento)
                        } catch (e) {
                            erros << "Fatura ${codigo}: data de vencimento: ${dataVencimento} inválida"
                        }
                    }
                }
            }
        }
        return [valido: !erros.size(), erros: erros]
    }
}