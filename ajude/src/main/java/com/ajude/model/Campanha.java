package com.ajude.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.beans.factory.annotation.Autowired;

import com.ajude.DAO.ComentarioDAO;
import com.ajude.DAO.LikeDAO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.text.Normalizer;
import java.util.regex.Pattern;

@Entity
public class Campanha {

    @Id @GeneratedValue
    private long idCampanha;
    private String nomeCurto;
    //Gerado pelo frontend
    private String url;
    private String descricao;
    private String deadLine;
    private StatusCampanha status;
    private Double meta;
    private Double doacoes;
    @ManyToOne
    private Usuario dono;
	private int likesCount;
	private int comentCount;
	@OneToMany
	private List<Comentario> comentarios;

    public Campanha(){
        super();
    }

    public  Campanha(String nomeCurto,String descricao, String deadLine,Double meta,Usuario dono){
        this.nomeCurto = nomeCurto;
        this.descricao = descricao;
        this.meta = meta;
        this.dono = dono;
        this.url = makeUrl(this.nomeCurto);
        this.status = StatusCampanha.ATIVA;
        this.likesCount = 0;
        this.comentCount = 0;
        this.doacoes = 0.0;
        this.comentarios = new ArrayList<Comentario>();
    }

    private static String makeUrl(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("").replace(" ","-").replaceAll("[!#$%&'()*+,.:;?@[\\\\]_`{|}~]","").toLowerCase();
    }

	public long getId() {
		return idCampanha;
	}

	public String getNomeCurto() {
		return nomeCurto;
	}

	public void setNomeCurto(String nomeCurto) {
		this.nomeCurto = nomeCurto;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDeadLine() {
		return deadLine;
	}

	public void setDeadLine(String deadLine) {
		this.deadLine = deadLine;
	}

	public StatusCampanha getStatus() {
		return status;
	}

	public void setStatus(StatusCampanha status) {
		this.status = status;
	}

	public Double getMeta() {
		return meta;
	}

	public void setMeta(Double meta) {
		this.meta = meta;
	}

	public Usuario getDono() {
		return dono;
	}

	public void setDono(Usuario dono) {
		this.dono = dono;
	}

	public int getLikes() {
		return likesCount;
	}

	public void addLike() {
		this.likesCount++;
	}
	
	public void subLike() {
		this.likesCount--;
	}

	public int getComentariosCount() {
		return this.comentCount;
	}

	public void addComentariosCount() {
		this.comentCount++;
	}
	
	public void subComentariosCount() {
		this.comentCount--;
	}
	
	public Double getDoacoes() {
		return doacoes;
	}
	
	public void setDoacoes(Double doacoes) {
		this.doacoes += doacoes;
	}
	
	public String porcentagemMeta() {
		return Double.toString( (this.getDoacoes() / this.getMeta()) * 100.0 ) + "%";
	}
	
	public List<Comentario> getComentarios() {
		return comentarios;
	}
	
	public void setComentarios(List<Comentario> comentarios) {
		this.comentarios = comentarios;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (idCampanha ^ (idCampanha >>> 32));
		result = prime * result + ((nomeCurto == null) ? 0 : nomeCurto.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Campanha other = (Campanha) obj;
		if (idCampanha != other.idCampanha)
			return false;
		if (nomeCurto == null) {
			if (other.nomeCurto != null)
				return false;
		} else if (!nomeCurto.equals(other.nomeCurto))
			return false;
		return true;
	}  
    
}
