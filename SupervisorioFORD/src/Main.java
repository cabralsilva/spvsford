import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import AGVS.Data.AGV;
import AGVS.Data.ConfigProcess;
import AGVS.FORD.MESH.MeshModbus;
import AGVS.FORD.SUPERVISORIO.ClienteAGV;
import AGVS.Serial.DatabaseStatic;
import AGVS.Serial.Serial;
import AGVS.Util.Log;
import AGVS.Util.Util;
import WebService.http.Servidor;

public class Main {

	public static void main(String[] args) {
		try {
			new AGVS.FORD.SUPERVISORIO.Servidor(AGVS.FORD.SUPERVISORIO.Servidor.SUPERVISORIO).start();
			System.out.println("Iniciando Supervisorio FORD");
			ConfigProcess.serial.conectar();
			Servidor serv = new Servidor();
			serv.start();
			System.out.println("Started server");
			new DatabaseStatic();
			ConfigProcess.verificaUsuarios();
			ConfigProcess.initSystem();
			System.out.println("Supervisorio Iniciado.");
		} catch (Exception e) {
			new Log(e);
		}
	}
}
