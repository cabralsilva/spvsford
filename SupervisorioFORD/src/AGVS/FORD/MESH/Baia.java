package AGVS.FORD.MESH;

import java.util.ArrayList;
import java.util.List;

import AGVS.Data.AGV;
import AGVS.Data.Rota;
import AGVS.Serial.DatabaseStatic;

public class Baia {
	private int id;
	private String nome;
	private int numero;
	private int coordenadaX;
	private int coordenadaY;
	
	private boolean sensorRack;
	private boolean isFree;
	
	private TipoBaia tipo;

	
	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public int getCoordenadaX() {
		return coordenadaX;
	}

	public void setCoordenadaX(int coordenadaX) {
		this.coordenadaX = coordenadaX;
	}

	public int getCoordenadaY() {
		return coordenadaY;
	}

	public void setCoordenadaY(int coordenadaY) {
		this.coordenadaY = coordenadaY;
	}

	public TipoBaia getTipo() {
		return tipo;
	}

	public void setTipo(TipoBaia tipo) {
		this.tipo = tipo;
	}
	

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Baia(int id, String nome, int numero, int coordenadaX, int coordenadaY, TipoBaia tipo, boolean sensorRack, boolean free) {
		super();
		this.id = id;
		this.nome = nome;
		this.numero = numero;
		this.coordenadaX = coordenadaX;
		this.coordenadaY = coordenadaY;
		this.tipo = tipo;
		this.sensorRack = sensorRack;
		this.isFree = free;
	}

	
	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public boolean isSensorRack() {
		return sensorRack;
	}

	public void setSensorRack(boolean sensorRack) {
		this.sensorRack = sensorRack;
	}
	
	
	@Override
	public String toString() {
		return "Baia [id=" + id + ", nome=" + nome + ", numero=" + numero + ", coordenadaX=" + coordenadaX
				+ ", coordenadaY=" + coordenadaY + ", sensorRack=" + sensorRack + ", isFree=" + isFree + ", tipo="
				+ tipo + "]";
	}

	public String doOrderSample() {
		List<Baia> lstBaiaRoute = new ArrayList<Baia>();
		if (this.sensorRack && this.isFree) {
			lstBaiaRoute.add(this);
			
			for (MeshModbus mm : DatabaseStatic.lstMeshModBus) {//VERIFICAR SE AS BAIAS DE DELIVERY ESTÃO PRONTAS
				if (mm.getBaia().getTipo() == TipoBaia.DELIVERY1) {
					if(mm.getBaia().isFree && !mm.getBaia().sensorRack) {
						lstBaiaRoute.add(mm.getBaia());
					}else {
						return "Baia de delivery sendo usada ou ocupada. Sensor: " + this.sensorRack + " - Free: " + this.isFree;
					}
				}
				
				if (mm.getBaia().getTipo() == TipoBaia.DELIVERY2) {
					if(mm.getBaia().isFree && mm.getBaia().sensorRack) {
						lstBaiaRoute.add(mm.getBaia());
					}else {
						return "Baia de delivery 2 sendo usada ou sem rack de retorno";
					}
				}
			}
			
			lstBaiaRoute.add(this);
			
			
			
			//BLOQUEAR AS BAIAS QUE ESTÃO NESTA ROTA PARA Q AS NOVAS ROTAS NÃO AS UTILIZEM ENQUANTO NÃO FOR FINALIZADA
			for(MeshModbus mm : DatabaseStatic.lstMeshModBus) {
				if (lstBaiaRoute.contains(mm.getBaia())) {
					mm.getBaia().setFree(false);
				}
			}
			
			for (AGV agv : DatabaseStatic.lstAGVS) {
				
				if (agv.getId() == 1) {
					for (MeshModbus mm : DatabaseStatic.lstMeshModBus) {
						if (mm.getNome().equals("Home 1")) lstBaiaRoute.add(mm.getBaia());
					}
					Rota routeOrder = new Rota("Pedido", 1, lstBaiaRoute);
					AGV.sendRota(routeOrder, agv.getEnderecoIP(), agv.getMac64());
				}
			}
		}else {
			return "Nenhum rack detectado na baia de origem ou a baia de origem está sendo usada. Sensor: " + this.sensorRack + " - Free: " + this.isFree;
		}
		
		return "";
	}
}
