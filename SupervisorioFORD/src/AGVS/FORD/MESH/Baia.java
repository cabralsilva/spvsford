package AGVS.FORD.MESH;

import java.util.ArrayList;
import java.util.Iterator;
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
	
	private boolean sensorRack1;
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
	
	

	public boolean isSensorRack1() {
		return sensorRack1;
	}

	public void setSensorRack1(boolean sensorRack1) {
		this.sensorRack1 = sensorRack1;
	}

	public Baia(int id, String nome, int numero, int coordenadaX, int coordenadaY, TipoBaia tipo, boolean sensorRack1, boolean free) {
		super();
		this.id = id;
		this.nome = nome;
		this.numero = numero;
		this.coordenadaX = coordenadaX;
		this.coordenadaY = coordenadaY;
		this.tipo = tipo;
		this.sensorRack1 = sensorRack1;
		this.isFree = free;
	}

	
	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public boolean issensorRack1() {
		return sensorRack1;
	}

	public void setsensorRack1(boolean sensorRack1) {
		this.sensorRack1 = sensorRack1;
	}
	
	

	@Override
	public String toString() {
		return "Baia [id=" + id + ", nome=" + nome + ", numero=" + numero + ", coordenadaX=" + coordenadaX
				+ ", coordenadaY=" + coordenadaY + ", sensorRack1=" + sensorRack1 
				+ ", isFree=" + isFree + ", tipo=" + tipo + "]";
	}

	public String doOrderSample(MeshModbus mesh) {
		List<Baia> lstBaiaRoute = new ArrayList<Baia>();
		if (mesh.isBtn2() && this.isFree) {
			lstBaiaRoute.add(this);
			for (int k = 0; DatabaseStatic.lstMeshModBus != null && k < DatabaseStatic.lstMeshModBus.size(); k++) {
				MeshModbus mm = DatabaseStatic.lstMeshModBus.get(k);
				for (int i = 0; mm.getLstBaia() != null && i < mm.getLstBaia().size(); i++) {
					Baia rec = mm.getLstBaia().get(i);
					if(rec.getTipo() == TipoBaia.DELIVERY1) {
						if(rec.isFree && !mesh.isBtn1()) {
							lstBaiaRoute.add(rec);
							for (int j = 0; mm.getLstBaia() != null && j < mm.getLstBaia().size(); j++) {
								Baia resti = mm.getLstBaia().get(j);
								if (resti.getTipo() == TipoBaia.RECOVERY1) {
									if (resti.isFree && mm.isBtn2()) {
										lstBaiaRoute.add(resti);
									}else {
										return "Baia de restituição sendo usada ou sem rack. Sensor " + mesh.isBtn2() + " - Livre: " + resti.isFree;
									}
								}
							}
						}else {
							return "Baia de entrega sendo usada ou ocupada. Sensor: " + mesh.isBtn1() + " - Livre: " + rec.isFree;
						}
					}
				}
			}
			
			lstBaiaRoute.add(this);
			
			
			
			for (AGV agv : DatabaseStatic.lstAGVS) {
				if (agv.getId() == 1) {
					for (Baia home : DatabaseStatic.lstBaia) {
						if(home.getTipo() == TipoBaia.HOME1) {
							lstBaiaRoute.add(home);
						}
					}
					Rota routeOrder = new Rota("Pedido", 1, lstBaiaRoute);
					AGV.sendRota(routeOrder, agv.getEnderecoIP(), agv.getMac64());
					
					
					//BLOQUEAR AS BAIAS QUE ESTÃO NESTA ROTA PARA Q AS NOVAS ROTAS NÃO AS UTILIZEM ENQUANTO NÃO FOR FINALIZADA
					for(MeshModbus mm : DatabaseStatic.lstMeshModBus) {
						if (lstBaiaRoute.contains(mm.getBaia())) {
							mm.getBaia().setFree(false);
						}
					}
				}
			}
		}else {
			return "Nenhum rack detectado na baia solicitante ou a baia está sendo usada. Sensor: " + mesh.isBtn2() + " - Livre: " + this.isFree;
		}
		
		return "";
	}
}
