package domain;

public class Alternativa {
	private int id;
	private String titulo;
	
	public Alternativa(int id, String titulo) {
		super();
		this.id = id;
		this.titulo = titulo;
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
	
	
}
