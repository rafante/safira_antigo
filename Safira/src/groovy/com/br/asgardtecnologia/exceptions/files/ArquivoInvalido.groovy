package com.br.asgardtecnologia.exceptions.files
/**
 * Created by Bruno on 12/07/2016.
 */
public class ArquivoInvalido extends Exception {

    public String nomeArquivo

    public ArquivoInvalido(String nomeArquivo){
        super("Arquivo inválido")
        this.nomeArquivo = nomeArquivo
    }
}
