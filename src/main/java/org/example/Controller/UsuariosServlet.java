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

@WebServlet("/Usuarios")
public class UsuariosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if ("dados".equals(request.getParameter("modo"))) {
            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.print("[");
                List<String> usuarios = Validacao.listarUsuarios();
                for (int i = 0; i < usuarios.size(); i++) {
                    if (i > 0) out.print(",");
                    String nome = usuarios.get(i);
                    out.print("{\"usuario\":\"" + json(nome) + "\",\"salvos\":" + Livro.quantidadeFavoritos(nome) + "}");
                }
                out.print("]");
            }
            return;
        }

        request.setAttribute("usuario", session.getAttribute("usuario"));
        request.setAttribute("perfil", session.getAttribute("perfil"));
        request.setAttribute("usuarios", Validacao.listarUsuarios());

        request.getRequestDispatcher("/Usuarios.html").forward(request, response);
    }

    private String json(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
