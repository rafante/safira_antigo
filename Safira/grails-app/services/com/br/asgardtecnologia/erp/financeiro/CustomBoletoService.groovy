package com.br.asgardtecnologia.erp.financeiro

import com.br.asgardtecnologia.erp.base.Endereco
import com.mrkonno.plugin.jrimum.dsl.BoletoDsl
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeCobranca
import org.jrimum.domkee.financeiro.banco.hsbc.TipoIdentificadorCNR

class CustomBoletoService {

    def geraBoleto() {
        def contas = ContaReceber.list()
        def parametros = ParametrosFinanceiro.get(1)

        contas.each { conta ->
            String nomeSacado = conta.parceiroNegocios.nome
            String documentoSacado = conta.parceiroNegocios.documentoFormatado
            Endereco enderecoSacado = conta.parceiroNegocios.enderecoPrincipal

            def b = BoletoDsl.boleto({
                sacado(nomeSacado, documentoSacado, {
                    endereco({
                        uf(enderecoSacado.uf)
                        logradouro(enderecoSacado.logradouro)
                        localidade(enderecoSacado.bairro)
                    })
                })
                cedente(nomeCedente, documentoCedente, {

                })
            })
        }
//        def b = BoletoDsl.boleto({
//            sacado(sacado.nome, sacado.documento) {
//                endereco {
//                    uf sacado.uf
//                    logradouro sacado.logradouro
//                    localidade sacado.localidade
//                    cep sacado.cep
//                    bairro sacado.bairro
//                    numero sacado.numero
//                }
//            }
//            cedente(cedente.nome, cedente.documento) {
//                endereco {
//                    uf cedente.uf
//                    logradouro cedente.logradouro
//                    localidade cedente.localidade
//                    cep cedente.cep
//                    bairro cedente.bairro
//                    numero cedente.numero
//                }
//            }
//            contaBancaria {
//                banco parametros."banco.id"
//                conta Integer.parseInt(parametros.conta), parametros.contaDigito
//                carteira 6, TipoDeCobranca.SEM_REGISTRO
//                agencia Integer.parseInt(parametros.agencia), parametros.agenciaDigito
//            }
//            dataVencimento parametros.dataVencimento
//            numeroDocumento parametros.numDocumento
//            nossoNumero parametros.nossoNumero
//            digitoNossoNumero parametros.nossoNumeroDigito
//            valor parametros.valorTotal
//            tipoTitulo parametros."tipoDeTitulo.id"
//            parametrosBancarios(["${TipoIdentificadorCNR.class.getName()}": TipoIdentificadorCNR.COM_VENCIMENTO])
//            localPagamento "Pagavel em qualquer Banco"
//            instrucoes """Aceitar ate a data de vencimento
//                                Apos o vencimento aceito apenas nas agencias do HSBC
//                                Cobrar multa de 7% e juros de mora de 0,03% ao dia"""
//        })
//// the bytes property return a array of bytes and it can be used to send in response
//        byte[] bt = b.bytes
//// the pdf method can be used to save to a pdf file
////                    File file = new File("${params.caminho}/${contaReceber.documento}.pdf")
////                    if(!file.canWrite())
////                        throw new Exception("N�o � poss�vel gravar arquivo no caminho especificado")
//        b.pdf("${parametros.caminho}/${parametros.documento}.pdf")
    }
}
