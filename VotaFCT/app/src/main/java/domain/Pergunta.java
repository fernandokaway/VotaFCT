package domain;

import java.util.ArrayList;

public class Pergunta {
	
	private int id;
	private String titulo;
	private ArrayList<Alternativa> alternativas;
	
	
	
	public Pergunta(int id, String titulo) {
		super();
		this.id = id;
		this.titulo = titulo;
	}
	public Pergunta(int id, String titulo, ArrayList<Alternativa> alternativas) {
		super();
		this.id = id;
		this.titulo = titulo;
		this.alternativas = alternativas;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public ArrayList<Alternativa> getAlternativas() {
		return alternativas;
	}
	public void setAlternativas(ArrayList<Alternativa> alternativas) {
		this.alternativas = alternativas;
	}
	
	

}
