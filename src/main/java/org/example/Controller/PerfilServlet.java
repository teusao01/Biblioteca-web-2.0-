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

@WebServlet("/Perfil")
public class PerfilServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String usuario = (String) session.getAttribute("usuario");

        if ("dados".equals(request.getParameter("modo"))) {
            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"usuario\":\"" + json(usuario) + "\",\"perfil\":\"" + json(String.valueOf(session.getAttribute("perfil"))) + "\",\"salvos\":[");
                var salvos = Livro.listarFavoritos(usuario);
                for (int i = 0; i < salvos.size(); i++) {
                    Livro livro = salvos.get(i);
                    if (i > 0) out.print(",");
                    out.print("{\"titulo\":\"" + json(livro.getTitulo()) + "\",\"autor\":\"" + json(livro.getAutor())
                            + "\",\"categoria\":\"" + json(livro.getCategoria()) + "\",\"imagem\":\"" + json(livro.getImagem())
                            + "\",\"preco\":" + String.format(java.util.Locale.US, "%.2f", livro.getPreco()) + "}");
                }
                out.print("]}");
            }
            return;
        }

        request.setAttribute("usuario", usuario);
        request.setAttribute("perfil", session.getAttribute("perfil"));
        request.setAttribute("livrosSalvos", Livro.listarFavoritos(usuario));
        request.setAttribute("quantidadeSalvos", Livro.quantidadeFavoritos(usuario));

        request.getRequestDispatcher("/Perfil.html").forward(request, response);
    }

    private String json(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
