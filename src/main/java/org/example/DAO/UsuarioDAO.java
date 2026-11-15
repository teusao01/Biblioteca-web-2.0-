
package org.example.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.example.Model.Conexao;

public class UsuarioDAO {

    public static boolean validarLogin(String usuario, String senha) {
        String sql = "SELECT 1 FROM usuarios WHERE usuario = ? AND senha = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao validar login", e);
        }
    }

    public static String getPerfil(String usuario) {
        String sql = "SELECT perfil FROM usuarios WHERE usuario = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("perfil");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar perfil", e);
        }

        return null;
    }

    public static List<String> listarUsuarios() {
        List<String> usuarios = new ArrayList<>();

        String sql = "SELECT usuario FROM usuarios ORDER BY usuario";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(rs.getString("usuario"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários", e);
        }

        return usuarios;
    }
}
