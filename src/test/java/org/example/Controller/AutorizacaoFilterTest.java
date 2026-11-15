package org.example.Controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class AutorizacaoFilterTest {
    private AutorizacaoFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @BeforeEach void setup() {
        filter = new AutorizacaoFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        when(request.getContextPath()).thenReturn("/DEVweb");
    }

    @Test void redirecionaRaizParaLogin() throws Exception {
        when(request.getRequestURI()).thenReturn("/DEVweb/");
        filter.doFilter(request, response, chain);
        verify(response).sendRedirect("/DEVweb/login");
        verifyNoInteractions(chain);
    }

    @Test void permiteLoginSemSessao() throws Exception {
        when(request.getRequestURI()).thenReturn("/DEVweb/login");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test void impedeAcessoDiretoAHtmlProtegido() throws Exception {
        when(request.getRequestURI()).thenReturn("/DEVweb/Home.html");
        filter.doFilter(request, response, chain);
        verify(response).sendRedirect("/DEVweb/login");
        verifyNoInteractions(chain);
    }

    @Test void redirecionaParaLoginQuandoSessaoNaoExiste() throws Exception {
        when(request.getRequestURI()).thenReturn("/DEVweb/Home");
        when(request.getSession(false)).thenReturn(null);
        filter.doFilter(request, response, chain);
        verify(response).sendRedirect("/DEVweb/login");
        verify(response).setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        verifyNoInteractions(chain);
    }

    @Test void bloqueiaUsuarioComumNaRotaDeAdministrador() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(request.getRequestURI()).thenReturn("/DEVweb/Administrador");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn("gabriela");
        when(session.getAttribute("perfil")).thenReturn("USUARIO");
        filter.doFilter(request, response, chain);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso permitido somente para administradores.");
        verifyNoInteractions(chain);
    }

    @Test void permiteRotaProtegidaComSessaoValida() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(request.getRequestURI()).thenReturn("/DEVweb/Home");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn("gabriela");
        when(session.getAttribute("perfil")).thenReturn("USUARIO");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        verify(response).setHeader("Pragma", "no-cache");
    }

    @Test void retorna404ParaRotaProtegidaDesconhecidaComSessaoValida() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(request.getRequestURI()).thenReturn("/DEVweb/rota-inexistente");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn("gabriela");
        when(session.getAttribute("perfil")).thenReturn("USUARIO");
        filter.doFilter(request, response, chain);
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
        verifyNoInteractions(chain);
    }
}
