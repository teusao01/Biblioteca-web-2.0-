package org.example.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Model responsável pelos livros da livraria e pelos favoritos de cada usuário.
 * Os dados ficam em memória para não introduzir banco de dados ou uma nova arquitetura.
 */
public final class Livro {

    private static final List<Livro> LIVROS = new ArrayList<>();
    private static final java.util.Map<String, List<Integer>> FAVORITOS = new java.util.HashMap<>();
    private static final AtomicInteger PROXIMO_ID = new AtomicInteger(4);

    private final int id;
    private final String titulo;
    private final String autor;
    private final String categoria;
    private final String descricao;
    private final double preco;
    private final String imagem;
    private boolean disponivel;

    static {
        LIVROS.add(new Livro(1, "Dom Casmurro", "Machado de Assis", "Romance",
                "Um dos grandes clássicos da literatura brasileira.", 39.90, "https://covers.openlibrary.org/b/isbn/9788535910837-L.jpg", true));
        LIVROS.add(new Livro(2, "1984", "George Orwell", "Ficção",
                "Um clássico sobre vigilância, poder e liberdade.", 44.90, "https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg", true));
        LIVROS.add(new Livro(3, "O Hobbit", "J. R. R. Tolkien", "Fantasia",
                "A aventura de Bilbo Bolseiro pela Terra-média.", 49.90, "https://covers.openlibrary.org/b/isbn/9780547928227-L.jpg", true));
    }

    private Livro(int id, String titulo, String autor, String categoria,
                  String descricao, double preco, String imagem, boolean disponivel) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.descricao = descricao;
        this.preco = preco;
        this.imagem = imagem;
        this.disponivel = disponivel;
    }

    public static synchronized Livro cadastrar(String titulo, String autor, String categoria,
                                               String descricao, double preco, String imagem,
                                               boolean disponivel) {
        Livro livro = new Livro(PROXIMO_ID.getAndIncrement(), titulo.trim(), autor.trim(),
                categoria.trim(), descricao.trim(), preco,
                imagem == null || imagem.trim().isEmpty()
                        ? "https://via.placeholder.com/300x430?text=Livro" : imagem.trim(),
                disponivel);
        LIVROS.add(livro);
        return livro;
    }

    public static synchronized boolean remover(int id) {
        Iterator<Livro> iterator = LIVROS.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().id == id) {
                iterator.remove();
                for (List<Integer> favoritos : FAVORITOS.values()) {
                    favoritos.remove(Integer.valueOf(id));
                }
                return true;
            }
        }
        return false;
    }

    public static synchronized List<Livro> listar(String busca) {
        String termo = busca == null ? "" : busca.trim().toLowerCase();
        List<Livro> resultado = new ArrayList<>();
        for (Livro livro : LIVROS) {
            if (termo.isEmpty()
                    || livro.titulo.toLowerCase().contains(termo)
                    || livro.autor.toLowerCase().contains(termo)
                    || livro.categoria.toLowerCase().contains(termo)) {
                resultado.add(livro);
            }
        }
        return resultado;
    }

    public static synchronized Livro buscar(int id) {
        for (Livro livro : LIVROS) if (livro.id == id) return livro;
        return null;
    }

    public static synchronized void alternarFavorito(String usuario, int id) {
        if (usuario == null || buscar(id) == null) return;
        List<Integer> favoritos = FAVORITOS.computeIfAbsent(usuario, k -> new ArrayList<>());
        Integer valor = Integer.valueOf(id);
        if (favoritos.contains(valor)) favoritos.remove(valor);
        else favoritos.add(valor);
    }

    public static synchronized boolean isFavorito(String usuario, int id) {
        return usuario != null
                && FAVORITOS.getOrDefault(usuario, Collections.emptyList()).contains(id);
    }

    public static synchronized List<Livro> listarFavoritos(String usuario) {
        List<Livro> resultado = new ArrayList<>();
        if (usuario == null) return resultado;
        for (Integer id : FAVORITOS.getOrDefault(usuario, Collections.emptyList())) {
            Livro livro = buscar(id);
            if (livro != null) resultado.add(livro);
        }
        return resultado;
    }

    public static synchronized int quantidadeFavoritos(String usuario) {
        return listarFavoritos(usuario).size();
    }

    public static synchronized int total() {
        return LIVROS.size();
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getCategoria() { return categoria; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    public String getImagem() { return imagem; }
    public boolean isDisponivel() { return disponivel; }
}
