
package org.example.Model;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoTest {

    @Test
    void aceitaLoginValido() {
        assertTrue(Validacao.validarLogin("gabriela", "1234"));
    }

    @Test
    void aceitaUsuarioComEspacosEVariacaoDeMaiusculas() {
        assertTrue(Validacao.validarLogin("  GABRIELA ", "1234"));
    }

    @Test
    void rejeitaSenhaIncorreta() {
        assertFalse(Validacao.validarLogin("gabriela", "senha-errada"));
    }

    @Test
    void rejeitaUsuarioDesconhecido() {
        assertFalse(Validacao.validarLogin("naoexiste", "1234"));
    }

    @Test
    void rejeitaUsuarioOuSenhaNulos() {
        assertFalse(Validacao.validarLogin(null, "1234"));
        assertFalse(Validacao.validarLogin("gabriela", null));
    }

    @Test
    void retornaPerfilCorreto() {
        assertEquals("ADMIN", Validacao.getPerfil("MATEUS"));
        assertEquals("USUARIO", Validacao.getPerfil(" gabriela "));
    }

    @Test
    void retornaNuloParaPerfilDeUsuarioInexistenteOuNulo() {
        assertNull(Validacao.getPerfil("desconhecido"));
        assertNull(Validacao.getPerfil(null));
    }

    @Test
    void listaUsuariosConhecidosSemExporMapaInterno() {
        List<String> usuarios = Validacao.listarUsuarios();

        assertTrue(usuarios.containsAll(
                List.of("gabriela", "athaides", "mateus")
        ));

        usuarios.clear();

        assertTrue(Validacao.listarUsuarios().contains("gabriela"));
    }
}
