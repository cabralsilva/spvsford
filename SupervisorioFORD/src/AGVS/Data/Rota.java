package AGVS.Data;

import java.util.List;

import AGVS.FORD.MESH.Baia;

public class Rota {

	private String nome;
	private int numero;
	
	private List<Baia> lstBaia;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}
	
	

	public List<Baia> getLstBaia() {
		return lstBaia;
	}

	public void setLstBaia(List<Baia> lstBaia) {
		this.lstBaia = lstBaia;
	}

	public Rota(String nome, int numero, List<Baia> lstBaia) {
		super();
		this.nome = nome;
		this.numero = numero;
		this.lstBaia = lstBaia;
	}

}
