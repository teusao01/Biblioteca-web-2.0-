package org.example.Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.Model.Livro;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class HomeServletTest {
    @Test void getEncaminhaParaHomeComDadosDaSessao() throws Exception {
        HomeServlet servlet = new HomeServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn("gabriela");
        when(session.getAttribute("perfil")).thenReturn("USUARIO");
        when(request.getRequestDispatcher("/Home.html")).thenReturn(dispatcher);
        servlet.doGet(request, response);
        verify(request).setAttribute("usuario", "gabriela");
        verify(request).setAttribute("perfil", "USUARIO");
        verify(dispatcher).forward(request, response);
    }


    @Test void getModoDadosRetornaCatalogoEmJson() throws Exception {
        HomeServlet servlet = new HomeServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        java.io.StringWriter body = new java.io.StringWriter();
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn("gabriela");
        when(request.getParameter("modo")).thenReturn("dados");
        when(response.getWriter()).thenReturn(new java.io.PrintWriter(body));
        servlet.doGet(request, response);
        verify(response).setContentType("application/json;charset=UTF-8");
        org.junit.jupiter.api.Assertions.assertTrue(body.toString().startsWith("["));
        org.junit.jupiter.api.Assertions.assertTrue(body.toString().contains("\"titulo\":"));
        org.junit.jupiter.api.Assertions.assertTrue(body.toString().contains("\"favorito\":"));
    }

    @Test void postFavoritoRedirecionaParaHome() throws Exception {
        HomeServlet servlet = new HomeServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn("teste-home");
        when(request.getParameter("acao")).thenReturn("favorito");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getContextPath()).thenReturn("/DEVweb");
        servlet.doPost(request, response);
        verify(response).sendRedirect("/DEVweb/Home");
        // Limpa o estado global criado pelo teste.
        Livro.alternarFavorito("teste-home", 1);
    }

    @Test void postComBuscaCodificaTermoNaUrl() throws Exception {
        HomeServlet servlet = new HomeServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn("gabriela");
        when(request.getParameter("acao")).thenReturn("buscar");
        when(request.getParameter("busca")).thenReturn("ficção científica");
        when(request.getContextPath()).thenReturn("");
        servlet.doPost(request, response);
        verify(response).sendRedirect("/Home?busca=fic%C3%A7%C3%A3o+cient%C3%ADfica");
    }
}
