package org.example.Controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controle central de autenticacao e autorizacao.
 * Somente /login e arquivos estaticos podem ser acessados sem sessao.
 * As telas HTML protegidas nunca podem ser abertas diretamente pela URL.
 */
@WebFilter("/*")
public class AutorizacaoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String caminho = req.getRequestURI().substring(req.getContextPath().length());

        // Impede que o usuario abra qualquer View HTML diretamente pela URL.
        // As Views protegidas somente podem ser entregues por seus Servlets.
        if (caminho.endsWith(".html") && !caminho.equals("/login.html")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (caminho.equals("/") || caminho.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (rotaPublica(caminho)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);

        if (!sessaoValida(session)) {
            limparCache(resp);
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        limparCache(resp);

        // Autorizacao do administrador no servidor.
        if (caminho.equals("/Administrador")
                && !"ADMIN".equals(session.getAttribute("perfil"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Acesso permitido somente para administradores.");
            return;
        }

        if (!rotaProtegidaConhecida(caminho)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean rotaPublica(String caminho) {
        return caminho.equals("/login")
                || caminho.equals("/login.html")
                || caminho.equals("/Style.css")
                || caminho.startsWith("/css/")
                || caminho.startsWith("/img/")
                || caminho.startsWith("/images/");
    }

    private boolean sessaoValida(HttpSession session) {
        return session != null
                && session.getAttribute("usuario") != null
                && session.getAttribute("perfil") != null;
    }

    private boolean rotaProtegidaConhecida(String caminho) {
        return caminho.equals("/Home")
                || caminho.equals("/Perfil")
                || caminho.equals("/Usuarios")
                || caminho.equals("/Administrador")
                || caminho.equals("/logout");
    }

    private void limparCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
