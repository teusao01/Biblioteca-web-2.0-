package org.example.Model;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class LivroTest {
    private Livro cadastrarLivroUnico() {
        String marca = UUID.randomUUID().toString();
        return Livro.cadastrar("Teste " + marca, "Autor " + marca, "CategoriaTeste",
                "Descricao de teste", 12.50, "", true);
    }

    @Test void possuiLivrosIniciais() {
        assertTrue(Livro.total() >= 3);
        assertNotNull(Livro.buscar(1));
        assertEquals("Dom Casmurro", Livro.buscar(1).getTitulo());
    }

    @Test void cadastraLivroEExpõeSeusDados() {
        Livro livro = cadastrarLivroUnico();
        assertNotNull(Livro.buscar(livro.getId()));
        assertEquals("Autor " + livro.getTitulo().substring("Teste ".length()), livro.getAutor());
        assertEquals("CategoriaTeste", livro.getCategoria());
        assertEquals("Descricao de teste", livro.getDescricao());
        assertEquals(12.50, livro.getPreco(), 0.001);
        assertTrue(livro.isDisponivel());
        assertNotNull(livro.getImagem());
        assertTrue(livro.getId() > 0);
    }

    @Test void usaImagemPadraoQuandoImagemEstaVazia() {
        Livro livro = cadastrarLivroUnico();
        assertTrue(livro.getImagem().contains("placeholder"));
    }

    @Test void listaTodosQuandoBuscaEstaVaziaOuNula() {
        assertTrue(Livro.listar("").size() >= 3);
        assertTrue(Livro.listar(null).size() >= 3);
    }

    @Test void pesquisaPorTituloAutorOuCategoriaSemDiferenciarMaiusculas() {
        Livro livro = cadastrarLivroUnico();
        assertTrue(Livro.listar(livro.getTitulo().toUpperCase()).stream().anyMatch(l -> l.getId() == livro.getId()));
        assertTrue(Livro.listar(livro.getAutor()).stream().anyMatch(l -> l.getId() == livro.getId()));
        assertTrue(Livro.listar("categoriateste").stream().anyMatch(l -> l.getId() == livro.getId()));
    }

    @Test void retornaNuloAoBuscarIdInexistente() {
        assertNull(Livro.buscar(-999));
    }

    @Test void removeLivroExistenteERetornaVerdadeiro() {
        Livro livro = cadastrarLivroUnico();
        assertTrue(Livro.remover(livro.getId()));
        assertNull(Livro.buscar(livro.getId()));
    }

    @Test void removerIdInexistenteRetornaFalso() {
        assertFalse(Livro.remover(-999));
    }

    @Test void alternaFavoritoEListaFavoritosDoUsuario() {
        Livro livro = cadastrarLivroUnico();
        String usuario = "teste-" + UUID.randomUUID();
        Livro.alternarFavorito(usuario, livro.getId());
        assertTrue(Livro.isFavorito(usuario, livro.getId()));
        assertEquals(1, Livro.quantidadeFavoritos(usuario));
        assertTrue(Livro.listarFavoritos(usuario).stream().anyMatch(l -> l.getId() == livro.getId()));
        Livro.alternarFavorito(usuario, livro.getId());
        assertFalse(Livro.isFavorito(usuario, livro.getId()));
        assertEquals(0, Livro.quantidadeFavoritos(usuario));
        Livro.remover(livro.getId());
    }

    @Test void ignoraFavoritoComUsuarioNuloOuLivroInexistente() {
        Livro livro = cadastrarLivroUnico();
        assertDoesNotThrow(() -> Livro.alternarFavorito(null, livro.getId()));
        assertDoesNotThrow(() -> Livro.alternarFavorito("usuario", -999));
        assertFalse(Livro.isFavorito(null, livro.getId()));
        assertTrue(Livro.listarFavoritos(null).isEmpty());
        assertEquals(0, Livro.quantidadeFavoritos(null));
        Livro.remover(livro.getId());
    }

    @Test void removerLivroTambemRemoveDosFavoritos() {
        Livro livro = cadastrarLivroUnico();
        String usuario = "teste-" + UUID.randomUUID();
        Livro.alternarFavorito(usuario, livro.getId());
        assertTrue(Livro.remover(livro.getId()));
        assertFalse(Livro.isFavorito(usuario, livro.getId()));
        assertFalse(Livro.listarFavoritos(usuario).stream().anyMatch(l -> l.getId() == livro.getId()));
    }
}
