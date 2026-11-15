
package org.example.Model;

import java.util.List;
import org.example.DAO.UsuarioDAO;

public final class Validacao {

    private Validacao() {
    }

    public static boolean validarLogin(String usuario, String senha) {
        if (usuario == null || senha == null) {
            return false;
        }

        return UsuarioDAO.validarLogin(
                usuario.trim().toLowerCase(), senha
        );
    }

    public static String getPerfil(String usuario) {
        if (usuario == null) return null;

        return UsuarioDAO.getPerfil(
                usuario.trim().toLowerCase()
        );
    }

    public static List<String> listarUsuarios() {
        return UsuarioDAO.listarUsuarios();
    }
}
