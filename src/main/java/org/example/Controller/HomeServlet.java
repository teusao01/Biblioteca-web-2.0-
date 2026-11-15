package org.example.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.Model.Livro;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Controller responsável pela Home/catálogo e pelas ações de favoritos.
 * Continua sendo uma única responsabilidade de tela: catálogo de livros.
 */
@WebServlet("/Home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String usuario = (String) session.getAttribute("usuario");

        if ("dados".equals(request.getParameter("modo"))) {
            enviarDados(response, usuario, request.getParameter("busca"));
            return;
        }

        request.setAttribute("usuario", usuario);
        request.setAttribute("perfil", session.getAttribute("perfil"));
        request.getRequestDispatcher("/Home.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        String usuario = (String) session.getAttribute("usuario");
        String acao = request.getParameter("acao");

        if ("favorito".equals(acao)) {
            try {
                Livro.alternarFavorito(usuario, Integer.parseInt(request.getParameter("id")));
            } catch (NumberFormatException ignored) {
                // ID inválido: nenhuma alteração é feita.
            }
        }

        String busca = request.getParameter("busca");
        response.sendRedirect(request.getContextPath() + "/Home"
                + (busca == null || busca.trim().isEmpty() ? "" : "?busca=" + java.net.URLEncoder.encode(busca, java.nio.charset.StandardCharsets.UTF_8)));
    }

    private void enviarDados(HttpServletResponse response, String usuario, String busca) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");

        List<Livro> livros = Livro.listar(busca);
        try (PrintWriter out = response.getWriter()) {
            out.print("[");
            for (int i = 0; i < livros.size(); i++) {
                Livro livro = livros.get(i);
                if (i > 0) out.print(",");
                out.print("{\"id\":" + livro.getId()
                        + ",\"titulo\":\"" + json(livro.getTitulo())
                        + "\",\"autor\":\"" + json(livro.getAutor())
                        + "\",\"categoria\":\"" + json(livro.getCategoria())
                        + "\",\"descricao\":\"" + json(livro.getDescricao())
                        + "\",\"preco\":" + String.format(java.util.Locale.US, "%.2f", livro.getPreco())
                        + ",\"imagem\":\"" + json(livro.getImagem())
                        + "\",\"disponivel\":" + livro.isDisponivel()
                        + ",\"favorito\":" + Livro.isFavorito(usuario, livro.getId()) + "}");
            }
            out.print("]");
        }
    }

    private String json(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n");
    }
}
