package com.ajude.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.ajude.model.*;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ajude.DAO.CampanhaDAO;
import com.ajude.DAO.ComentarioDAO;
import com.ajude.DAO.LikeDAO;


@Service
public class CampanhaService {
	
	@Autowired
	private CampanhaDAO<Campanha ,Long> campanhasDAO;
    @Autowired
    private ComentarioDAO<Comentario, Long> comentariosDAO;
    @Autowired
    private LikeDAO<Like, Long> likesDAO;
	@Autowired
	private UsuarioService usuarioService;
	//@Autowired
	//private ComentarioRespostaDAO<ComentarioResposta,Long> comentariosRespostaDAO;


	public Campanha cadastrarCampanha(CampanhaDTO c, String token) {
		Usuario user = usuarioService.recuperaUsuarioToken(token);
		if(user != null) {
			Campanha novaCampanha = c.makeCampanha(user);
			campanhasDAO.save(novaCampanha);
			return  novaCampanha;
		}
		else {
			return null;
		}
	}

	public Campanha recuperaCampanha(long id) {
		try {
			Campanha camp = campanhasDAO.findById(id).get();
			ArrayList<Comentario> c = new ArrayList<Comentario>();
			for (Comentario coment : this.comentariosDAO.findAll()) {
				if (coment.getCampanha().equals(camp)) {
					c.add(coment);
				}
			}
			//camp.setComentarios(c);
			return camp;
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean Like(String token, long id) {
		Campanha c = this.recuperaCampanha(id);
		Usuario u = this.usuarioService.recuperaUsuarioToken(token);
		if (c == null || u == null) {
			return false;
		}
		else {
			if(this.verificaLike(u, c)) {
				this.removerLikeCampanha(c, this.getLikeId(u, c));
			}
			else {
				this.darLikeCampanha(u, c);
			}
			return true;
		}
	}
	
	private void darLikeCampanha(Usuario u, Campanha c) {
		c.addLike();
		this.likesDAO.save(new Like(u, c));
		
		this.campanhasDAO.save(c);
	}
	
	private void removerLikeCampanha(Campanha c, long id) {
		c.subLike();
		this.likesDAO.deleteById(id);
		this.campanhasDAO.save(c);
	}
	
	public boolean fazerComentarioCampanha(String token, long id, Comentario comentario) {
		Campanha c = this.recuperaCampanha(id);
		Usuario usuario = usuarioService.recuperaUsuarioToken(token);
		if (usuario == null) {
			return false;
		}
		else {
			Comentario coment = new Comentario(c, usuario, comentario.getComentario());
			System.out.println(comentario.getComentario());
			this.comentariosDAO.save(coment);
			c.addComentariosCount();
			this.campanhasDAO.save(c);
			return true;
		}
	}
	
	//ccontador de comentarios na campnha nao implemntado aqui, a discutir...
	public boolean removerComentarioCampanha(String token, long idComent) {
		Usuario user = usuarioService.recuperaUsuarioToken(token);
		Optional<Comentario> comentario = comentariosDAO.findById(idComent);
		if (user != null && comentario.isPresent() && comentario.get().getUsuario().equals(user) ) {
			this.comentariosDAO.deleteById(idComent);
			return true;
		}
		else {
			return false;
		}
	}
	
	public Comentario responderComentarioCampanha(long idComentario,  Comentario resposta,String token) {
		Optional<Comentario> coment = comentariosDAO.findById(idComentario);
		Usuario usuario = usuarioService.recuperaUsuarioToken(token);
		
		
		if (coment.isPresent() && usuario != null) {
			Comentario novaResposta = new Comentario(usuario,resposta.getComentario(),coment.get());
			comentariosDAO.save(novaResposta);
			return novaResposta;
		}
		else {
			return null;
		}
	}
	public boolean removerRespostaComentarioCampanha(long idResposta,String token) {
		Usuario usuario = usuarioService.recuperaUsuarioToken(token);
		Optional<Comentario> resposta = comentariosDAO.findById(idResposta);
	
		
		if ( usuario != null && resposta != null && resposta.get().getComentarioPai().getUsuario().equals(usuario)) {
			comentariosDAO.deleteById(idResposta);
			return true;
		}
		else {
			return false;
		}
	}
	
	// Tem que ver se precisa referenciar o usuario que doou, acho que sim mas já sao 23:00hr e to cansado vou deixar assim por enquanto
	public boolean fazerDoacaoCampanha(long idCamp, Double valor) {
		Campanha c = recuperaCampanha(idCamp);
		if (c == null) {
			return false;
		}
		else {
			c.setDoacoes(valor);
			this.campanhasDAO.save(c);
			return true;
		}
	}
	
	private boolean verificaLike(Usuario u, Campanha c) {
		for (Like like : this.likesDAO.findAll()) {
			if(like.getUser().equals(u) && like.getCampanha().equals(c)) {
				return true;
			}
		}
		return false;
	}
	
	private long getLikeId(Usuario u, Campanha c) {
		long id = -1;
		for (Like like : this.likesDAO.findAll()) {
			if(like.getUser().equals(u) && like.getCampanha().equals(c)) {
				id = like.getId();
				break;
			}
		}
		return id;
	}

	public Collection<Campanha> recuperarCampanhas() {
		return this.campanhasDAO.findAll();
	}
	
	public Collection<Like> like() {
		return this.likesDAO.findAll();
	}
	
	public Collection<Comentario> comentarios(){
		return this.comentariosDAO.findAll();
	}



}
