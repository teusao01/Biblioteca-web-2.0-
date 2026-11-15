
package org.example.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/postgres";

    private static final String USUARIO = "postgres";

    private static final String SENHA = "1234";

    public static Connection conectar() throws SQLException {

        try {
            // Força o carregamento do driver PostgreSQL
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Driver PostgreSQL não encontrado no classpath da aplicação.",
                    e
            );
        }

        // Estabelece a conexão com o banco
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
