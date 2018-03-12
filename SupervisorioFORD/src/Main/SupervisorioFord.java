package Main;

import java.io.IOException;
import java.util.Scanner;

import AGVS.Drivers.CLP.PLCComDriver;
import AGVS.FORD.MESH.Baia;
import AGVS.FORD.MESH.MeshModbus;
import AGVS.FORD.MESH.TipoBaia;
import AGVS.FORD.SUPERVISORIO.ClienteAGV;
import AGVS.FORD.SUPERVISORIO.Logic;
import AGVS.FORD.SUPERVISORIO.Reply;
import AGVS.FORD.SUPERVISORIO.Servidor;
import PLCCom.ePLCType;

public class SupervisorioFord {
	public static MeshModbus mm = new MeshModbus("192.168.25.10", "Mesh Area 1", null, false, false, false);
	public static MeshModbus mm2 = new MeshModbus("192.168.25.11", "Mesh Area 2", null, false, false, false);

	public static final String IPAGV1 = "192.168.25.21";
	public static final String IPPLC = "192.168.25.9";

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Iniciando Supervisorio Ford...");
		// ClienteAGV.enviar("<xml>T2</xml>", SupervisorioFord.IPAGV1);
		try {
			new Servidor(Servidor.SUPERVISORIO).start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 1, 0, false,
				ePLCType.S7_300_400_compatibel.name()));
		System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 1, 1, true,
				ePLCType.S7_300_400_compatibel.name()));
		System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 1, 2, false,
				ePLCType.S7_300_400_compatibel.name()));
		System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 1, 3, false,
				ePLCType.S7_300_400_compatibel.name()));

		new Reply();

		new Logic();

		/*
		 * Scanner sc = new Scanner(System.in); while (true) {
		 * 
		 * String msg = sc.nextLine(); if (msg.equals("B")) {
		 * System.err.println("Retorno = " + PLCComDriver.send("192.168.15.10", 102, 52,
		 * 14, 18, ePLCType.S7_300_400_compatibel.name())); } if (msg.equals("C")) {
		 * System.err.println("Retorno = " + PLCComDriver.send("192.168.15.10", 102, 52,
		 * 14, 0, ePLCType.S7_300_400_compatibel.name())); } ClienteAGV.enviar("<xml>" +
		 * msg + "</xml>", IPAGV1); try { Thread.sleep(0); } catch (InterruptedException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * 
		 * }
		 */
	}
}
