package org.example.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class LogoutServletTest {
    @Test void invalidaSessaoEVoltaParaLogin() throws Exception {
        LogoutServlet servlet = new LogoutServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/DEVweb");
        servlet.doGet(request, response);
        verify(session).invalidate();
        verify(response).sendRedirect("/DEVweb/login");
        verify(response).setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    }

    @Test void redirecionaMesmoQuandoNaoHaSessao() throws Exception {
        LogoutServlet servlet = new LogoutServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");
        servlet.doGet(request, response);
        verify(response).sendRedirect("/login");
    }
}
