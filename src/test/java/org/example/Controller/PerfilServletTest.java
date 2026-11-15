package org.example.Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class PerfilServletTest {
    @Test void getEncaminhaParaPerfilEDefineFavoritos() throws Exception {
        PerfilServlet servlet = new PerfilServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn("gabriela");
        when(session.getAttribute("perfil")).thenReturn("USUARIO");
        when(request.getRequestDispatcher("/Perfil.html")).thenReturn(dispatcher);
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("usuario"), eq("gabriela"));
        verify(request).setAttribute(eq("perfil"), eq("USUARIO"));
        verify(request).setAttribute(eq("livrosSalvos"), anyList());
        verify(request).setAttribute(eq("quantidadeSalvos"), anyInt());
        verify(dispatcher).forward(request, response);
    }

    @Test void modoDadosRetornaJson() throws Exception {
        PerfilServlet servlet = new PerfilServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        java.io.StringWriter body = new java.io.StringWriter();
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn("gabriela");
        when(session.getAttribute("perfil")).thenReturn("USUARIO");
        when(request.getParameter("modo")).thenReturn("dados");
        when(response.getWriter()).thenReturn(new java.io.PrintWriter(body));
        servlet.doGet(request, response);
        verify(response).setContentType("application/json;charset=UTF-8");
        org.junit.jupiter.api.Assertions.assertTrue(body.toString().contains("\"usuario\":\"gabriela\""));
        org.junit.jupiter.api.Assertions.assertTrue(body.toString().contains("\"salvos\":["));
    }
}
