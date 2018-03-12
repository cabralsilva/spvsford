package Main;

import java.awt.List;
import java.io.IOException;

import AGVS.FORD.MESH.Baia;
import AGVS.FORD.MESH.MeshModbus;
import AGVS.FORD.MESH.TipoBaia;
import AGVS.FORD.SUPERVISORIO.Servidor;
import AGVS.Serial.SerialAGV;

public class AGVCommunication {

	public static SerialAGV agv;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Sistema de comunica��o AGV");
		MeshModbus mm = new MeshModbus("19.26.136.38", "Segurança", null, false, true, false);
		agv = new SerialAGV();
		agv.conectar();
		new Servidor(Servidor.AGV).start();

		while (true) {
			if (!mm.isBtn2()) {
				//System.out.println("EMERGENCIA");
				try {
					agv.enviar("EM");
					Thread.sleep(2000);
				} catch (Exception e) {

				}
			} else {
				//System.out.println("NORMAL");
			}
		}
	}

}
