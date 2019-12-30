package com.br.asgardtecnologia.erp

import groovy.sql.Sql

class CorrecoesService {

    def dataSource

    def correcaoDecimaisNulos(){
        final Sql sql = new Sql(dataSource)

        //Busca todas as tabelas do banco
        String query = "select table_name from information_schema.tables where table_type = 'base table' and table_schema = 'safira';"
        final tabelas = sql.rows(query)

        //Loop em todas as tabelas para buscar todas as colunas de cada uma
        tabelas.each { tabela ->
            query = "select column_name, data_type from information_schema.columns where table_name = '${tabela["table_name"]}' and data_type = 'decimal'"
            final colunasDecimais = sql.rows(query)

            //Loop em todas as colunas decimais pra setar 0 onde tiver null
            colunasDecimais.each { coluna ->
                query = "update ${tabela['table_name']} set ${coluna['column_name']}=0 where ${coluna['column_name']} is null"
                sql.execute(query)
            }
        }
    }

    def correcaoBooleanosNulos(){
        final Sql sql = new Sql(dataSource)

        //Busca todas as tabelas do banco
        String query = "select table_name from information_schema.tables where table_type = 'base table' and table_schema = 'safira';"
        final tabelas = sql.rows(query)

        //Loop em todas as tabelas para buscar todas as colunas de cada uma
        tabelas.each { tabela ->
            query = "select column_name, data_type from information_schema.columns where table_name = '${tabela["table_name"]}' and data_type = 'bit'"
            final colunasDecimais = sql.rows(query)

            //Loop em todas as colunas decimais pra setar 0 onde tiver null
            colunasDecimais.each { coluna ->
                query = "update ${tabela['table_name']} set ${coluna['column_name']}=0 where ${coluna['column_name']} is null"
                sql.execute(query)
            }
        }
    }

    def eliminaColunas = { Map<String, String> tabelaColuna ->
        tabelaColuna.each { chaveValor ->
            final Sql sql = new Sql(dataSource)

            //Seleciona a constraint
            String query = "select column_name, constraint_name from information_schema.key_column_usage "
            query += "where table_name = '${chaveValor.key}' and column_name = '${chaveValor.value}';"

            final constraints = sql.rows(query)

            if (constraints.size()) {
                String constraint = constraints.first()['constraint_name']
                query = "alter table ${chaveValor.key} drop foreign key $constraint;"
                sql.execute(query)
            }

            query = "select column_name from information_schema.columns where table_name = '${chaveValor.key}' and column_name = '${chaveValor.value}'"
            final colunas = sql.rows(query)

            if (colunas.size()) {
                print("Removendo a coluna ${chaveValor.value} da tabela ${chaveValor.key}")
                query = "alter table ${chaveValor.key} drop column `${chaveValor.value}`"
                sql.execute(query)
            }
        }
    }
}
