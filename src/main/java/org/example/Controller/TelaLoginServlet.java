package org.example.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.Model.Validacao;

import java.io.IOException;

/**
 * Controller responsável exclusivamente pelo fluxo de login.
 */
@WebServlet("/login")
public class TelaLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("usuario") != null) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        request.getRequestDispatcher("/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String usuario = request.getParameter("usuario");
        String senha = request.getParameter("senha");

        if (usuario == null || senha == null
                || usuario.trim().isEmpty() || senha.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/login?erro=campos");
            return;
        }

        usuario = usuario.trim().toLowerCase();

        if (!Validacao.validarLogin(usuario, senha)) {
            response.sendRedirect(request.getContextPath() + "/login?erro=senha");
            return;
        }

        HttpSession sessaoAnterior = request.getSession(false);
        if (sessaoAnterior != null) {
            sessaoAnterior.invalidate();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("usuario", usuario);
        session.setAttribute("perfil", Validacao.getPerfil(usuario));
        session.setMaxInactiveInterval(30 * 60);

        response.sendRedirect(request.getContextPath() + "/Home");
    }
}
