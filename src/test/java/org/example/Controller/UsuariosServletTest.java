package org.example.Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class UsuariosServletTest {
    @Test void getEncaminhaParaPaginaComUsuariosDaSessao() throws Exception {
        UsuariosServlet servlet = new UsuariosServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn("teodoro");
        when(session.getAttribute("perfil")).thenReturn("ADMIN");
        when(request.getRequestDispatcher("/Usuarios.html")).thenReturn(dispatcher);
        servlet.doGet(request, response);
        verify(request).setAttribute("usuario", "teodoro");
        verify(request).setAttribute("perfil", "ADMIN");
        verify(request).setAttribute(eq("usuarios"), anyList());
        verify(dispatcher).forward(request, response);
    }

    @Test void modoDadosRetornaJsonDeUsuarios() throws Exception {
        UsuariosServlet servlet = new UsuariosServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        java.io.StringWriter body = new java.io.StringWriter();
        when(request.getParameter("modo")).thenReturn("dados");
        when(response.getWriter()).thenReturn(new java.io.PrintWriter(body));
        servlet.doGet(request, response);
        verify(response).setContentType("application/json;charset=UTF-8");
        org.junit.jupiter.api.Assertions.assertTrue(body.toString().startsWith("["));
        org.junit.jupiter.api.Assertions.assertTrue(body.toString().contains("\"usuario\":"));
    }
}
