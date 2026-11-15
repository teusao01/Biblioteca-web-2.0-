package org.example.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.Model.Livro;
import org.example.Model.Validacao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/Administrador")
public class AdministradorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !"ADMIN".equals(session.getAttribute("perfil"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Acesso permitido somente para administradores.");
            return;
        }

        if ("dados".equals(request.getParameter("modo"))) {
            enviarDados(response);
            return;
        }

        request.setAttribute("usuario", session.getAttribute("usuario"));
        request.setAttribute("perfil", session.getAttribute("perfil"));
        request.setAttribute("totalUsuarios", Validacao.listarUsuarios().size());
        request.setAttribute("totalLivros", Livro.total());
        request.getRequestDispatcher("/Administrador.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !"ADMIN".equals(session.getAttribute("perfil"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Acesso permitido somente para administradores.");
            return;
        }

        String acao = request.getParameter("acao");

        if ("cadastrar".equals(acao)) {
            cadastrarLivro(request);
        } else if ("remover".equals(acao)) {
            removerLivro(request);
        }

        response.sendRedirect(request.getContextPath() + "/Administrador");
    }

    private void cadastrarLivro(HttpServletRequest request) {
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");
        String categoria = request.getParameter("categoria");
        String descricao = request.getParameter("descricao");
        String precoTexto = request.getParameter("preco");
        String imagem = request.getParameter("imagem");
        boolean disponivel = request.getParameter("disponivel") != null;

        try {
            double preco = Double.parseDouble(precoTexto.replace(",", "."));

            if (valido(titulo) && valido(autor) && valido(categoria) && preco >= 0) {
                Livro.cadastrar(titulo, autor, categoria,
                        descricao == null ? "" : descricao,
                        preco, imagem, disponivel);
            }
        } catch (Exception ignored) {
            // Formulário inválido: não cadastra.
        }
    }

    private void removerLivro(HttpServletRequest request) {
        try {
            Livro.remover(Integer.parseInt(request.getParameter("id")));
        } catch (NumberFormatException ignored) {
            // ID inválido: nenhuma alteração.
        }
    }

    private void enviarDados(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.print("{\"totalUsuarios\":" + Validacao.listarUsuarios().size() + ",\"livros\":[");

            List<Livro> livros = Livro.listar("");

            for (int i = 0; i < livros.size(); i++) {
                Livro livro = livros.get(i);

                if (i > 0) {
                    out.print(",");
                }

                out.print("{\"id\":" + livro.getId()
                        + ",\"titulo\":\"" + json(livro.getTitulo())
                        + "\",\"autor\":\"" + json(livro.getAutor())
                        + "\",\"categoria\":\"" + json(livro.getCategoria())
                        + "\",\"preco\":" + String.format(java.util.Locale.US, "%.2f", livro.getPreco())
                        + ",\"disponivel\":" + livro.isDisponivel() + "}");
            }

            out.print("]}");
        }
    }

    private boolean valido(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    private String json(String valor) {
        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
