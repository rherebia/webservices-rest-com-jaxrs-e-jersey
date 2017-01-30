package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
import br.com.alura.loja.modelo.Projeto;
import junit.framework.Assert;

public class ClienteTest {
	
	private HttpServer server;
	private WebTarget target;
	private Client client;
	
	@Before
	public void iniciaServidor() {
		server = Servidor.inicializaServidor();
		
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());
		
		client = ClientBuilder.newClient(config);
		target = client.target("http://localhost:8080");
	}
	
	@After
	public void paraServidor() {
		server.stop();
	}

	@Test
	public void testQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
		Carrinho carrinho = target.path("/carrinhos/1").request().get(Carrinho.class);
		
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}
	
	@Test
	public void testQueBuscarUmProjetoTrazOProjetoEsperado() {
		Projeto projeto = target.path("/projetos/1").request().get(Projeto.class);
		
		Assert.assertEquals("Minha loja", projeto.getNome());
	}
	
	@Test
	public void testQueSuportaNovosCarrinhos() {
        Carrinho carrinho = new Carrinho();
        carrinho.adiciona(new Produto(314L, "Tablet", 999, 1));
        carrinho.setRua("Rua Vergueiro");
        carrinho.setCidade("Sao Paulo");
        
        Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);

        Response response = target.path("/carrinhos").request().post(entity);
        Assert.assertEquals(201, response.getStatus());
        
        String location = response.getHeaderString("Location");
        Carrinho carrinhoResponse = client.target(location).request().get(Carrinho.class);
        
        Assert.assertEquals("Tablet", carrinhoResponse.getProdutos().get(0).getNome());
	}
}
