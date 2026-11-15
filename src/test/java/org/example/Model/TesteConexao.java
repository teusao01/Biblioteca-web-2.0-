package org.example.Model;

import java.sql.Connection;

public class TesteConexao {

    public static void main(String[] args) {

        try (Connection conexao = Conexao.conectar()) {

            System.out.println("Conexão realizada com sucesso!");
            System.out.println("Banco: " +
                    conexao.getCatalog());

        } catch (Exception e) {
            System.out.println("Erro na conexão:");
            e.printStackTrace();
        }
    }
}