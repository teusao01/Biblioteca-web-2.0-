package org.example.Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class TelaLoginServletTest {
    private TelaLoginServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach void setup() {
        servlet = new TelaLoginServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        when(request.getContextPath()).thenReturn("/DEVweb");
    }

    @Test void getEncaminhaParaPaginaDeLoginQuandoNaoAutenticado() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getRequestDispatcher("/login.html")).thenReturn(dispatcher);
        servlet.doGet(request, response);
        verify(dispatcher).forward(request, response);
        verify(response).setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    }

    @Test void getRedirecionaUsuarioJaAutenticadoParaHome() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn("gabriela");
        servlet.doGet(request, response);
        verify(response).sendRedirect("/DEVweb/Home");
    }

    @Test void postRejeitaCamposVazios() throws Exception {
        when(request.getParameter("usuario")).thenReturn(" ");
        when(request.getParameter("senha")).thenReturn("1234");
        servlet.doPost(request, response);
        verify(response).sendRedirect("/DEVweb/login?erro=campos");
        verify(request, never()).getSession(true);
    }

    @Test void postRejeitaSenhaIncorreta() throws Exception {
        when(request.getParameter("usuario")).thenReturn("gabriela");
        when(request.getParameter("senha")).thenReturn("errada");
        servlet.doPost(request, response);
        verify(response).sendRedirect("/DEVweb/login?erro=senha");
        verify(request, never()).getSession(true);
    }

    @Test void postAutenticaUsuarioCriaSessaoERedireciona() throws Exception {
        HttpSession antiga = mock(HttpSession.class);
        HttpSession nova = mock(HttpSession.class);
        when(request.getParameter("usuario")).thenReturn(" GABRIELA ");
        when(request.getParameter("senha")).thenReturn("1234");
        when(request.getSession(false)).thenReturn(antiga);
        when(request.getSession(true)).thenReturn(nova);
        servlet.doPost(request, response);
        verify(antiga).invalidate();
        verify(nova).setAttribute("usuario", "gabriela");
        verify(nova).setAttribute("perfil", "USUARIO");
        verify(nova).setMaxInactiveInterval(1800);
        verify(response).sendRedirect("/DEVweb/Home");
    }
}
