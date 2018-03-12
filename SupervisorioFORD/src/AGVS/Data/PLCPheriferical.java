package AGVS.Data;

public class PLCPheriferical {
	private String descricao;
	private String status;
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public PLCPheriferical(String descricao, String status) {
		super();
		this.descricao = descricao;
		this.status = status;
	}
	
	
}
