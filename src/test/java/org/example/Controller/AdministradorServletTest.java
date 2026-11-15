package org.example.Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AdministradorServletTest {
    @Test void getBloqueiaQuemNaoEAdministrador() throws Exception {
        AdministradorServlet servlet = new AdministradorServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("perfil")).thenReturn("USUARIO");
        servlet.doGet(request, response);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso permitido somente para administradores.");
    }

    @Test void getDeAdministradorEncaminhaParaPainel() throws Exception {
        AdministradorServlet servlet = new AdministradorServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("perfil")).thenReturn("ADMIN");
        when(session.getAttribute("usuario")).thenReturn("teodoro");
        when(request.getRequestDispatcher("/Administrador.html")).thenReturn(dispatcher);
        servlet.doGet(request, response);
        verify(request).setAttribute("usuario", "teodoro");
        verify(request).setAttribute("perfil", "ADMIN");
        verify(request).setAttribute(eq("totalUsuarios"), anyInt());
        verify(request).setAttribute(eq("totalLivros"), anyInt());
        verify(dispatcher).forward(request, response);
    }


    @Test void getModoDadosRetornaResumoJson() throws Exception {
        AdministradorServlet servlet = new AdministradorServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        java.io.StringWriter body = new java.io.StringWriter();
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("perfil")).thenReturn("ADMIN");
        when(request.getParameter("modo")).thenReturn("dados");
        when(response.getWriter()).thenReturn(new java.io.PrintWriter(body));
        servlet.doGet(request, response);
        verify(response).setContentType("application/json;charset=UTF-8");
        org.junit.jupiter.api.Assertions.assertTrue(body.toString().contains("\"totalUsuarios\":"));
        org.junit.jupiter.api.Assertions.assertTrue(body.toString().contains("\"livros\":["));
    }

    @Test void postCadastraLivroValidoEPermiteRemocao() throws Exception {
        AdministradorServlet servlet = new AdministradorServlet();
        HttpServletRequest createRequest = mock(HttpServletRequest.class);
        HttpServletResponse createResponse = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        String tituloUnico = "LivroTeste-" + java.util.UUID.randomUUID();
        when(createRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("perfil")).thenReturn("ADMIN");
        when(createRequest.getParameter("acao")).thenReturn("cadastrar");
        when(createRequest.getParameter("titulo")).thenReturn(tituloUnico);
        when(createRequest.getParameter("autor")).thenReturn("Autor de Teste");
        when(createRequest.getParameter("categoria")).thenReturn("Teste");
        when(createRequest.getParameter("descricao")).thenReturn("Descricao");
        when(createRequest.getParameter("preco")).thenReturn("15,50");
        when(createRequest.getParameter("imagem")).thenReturn("");
        when(createRequest.getParameter("disponivel")).thenReturn("on");
        when(createRequest.getContextPath()).thenReturn("/DEVweb");
        servlet.doPost(createRequest, createResponse);
        verify(createResponse).sendRedirect("/DEVweb/Administrador");
        org.example.Model.Livro livro = org.example.Model.Livro.listar(tituloUnico).stream().findFirst().orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(15.50, livro.getPreco(), 0.001);

        HttpServletRequest removeRequest = mock(HttpServletRequest.class);
        HttpServletResponse removeResponse = mock(HttpServletResponse.class);
        when(removeRequest.getSession(false)).thenReturn(session);
        when(removeRequest.getParameter("acao")).thenReturn("remover");
        when(removeRequest.getParameter("id")).thenReturn(String.valueOf(livro.getId()));
        when(removeRequest.getContextPath()).thenReturn("/DEVweb");
        servlet.doPost(removeRequest, removeResponse);
        verify(removeResponse).sendRedirect("/DEVweb/Administrador");
        org.junit.jupiter.api.Assertions.assertNull(org.example.Model.Livro.buscar(livro.getId()));
    }

    @Test void postBloqueiaQuemNaoEAdministrador() throws Exception {
        AdministradorServlet servlet = new AdministradorServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getSession(false)).thenReturn(null);
        servlet.doPost(request, response);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso permitido somente para administradores.");
        verify(response, never()).sendRedirect(anyString());
    }

    @Test void postComAcaoDesconhecidaRedirecionaSemAlterarCatalogo() throws Exception {
        AdministradorServlet servlet = new AdministradorServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("perfil")).thenReturn("ADMIN");
        when(request.getParameter("acao")).thenReturn("desconhecida");
        when(request.getContextPath()).thenReturn("/DEVweb");
        servlet.doPost(request, response);
        verify(response).sendRedirect("/DEVweb/Administrador");
    }
}
