package AGVS.FORD.SUPERVISORIO;

import AGVS.Data.ConfigProcess;
import AGVS.Drivers.CLP.PLCComDriver;
import AGVS.Util.Util;
import Main.SupervisorioFord;
import PLCCom.ePLCType;
import WebService.http.Config;

public class Logic extends Thread {

	public Logic() {
		System.out.println("Start Logic");
		this.start();
	}

	public void run() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {

			if (!SupervisorioFord.mm.isBtn3()) {
				System.out.println("EMERGENCIA");
				ClienteAGV.enviar("<xml>AEMERGENCIA</xml>", SupervisorioFord.IPAGV1);
			}

			if (SupervisorioFord.mm2.isBtn1()) {
				System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 4, 6, true,
						ePLCType.S7_300_400_compatibel.name()));
			} else {
				System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 4, 6, false,
						ePLCType.S7_300_400_compatibel.name()));
			}

			if (SupervisorioFord.mm.isBtn2()) {
				System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 4, 5, true,
						ePLCType.S7_300_400_compatibel.name()));
			} else {
				System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 4, 5, false,
						ePLCType.S7_300_400_compatibel.name()));
			}

			if (SupervisorioFord.mm.isBtn1()) {
				System.out.println("ROTA");
				if (SupervisorioFord.mm2.isBtn1())
					ClienteAGV.enviar("<xml>T1</xml>", SupervisorioFord.IPAGV1);
				else
					ClienteAGV.enviar("<xml>T4</xml>", SupervisorioFord.IPAGV1);

			}

			int[] a = { 0 };
			if (PLCComDriver.request(SupervisorioFord.IPPLC, 102, 52, 7, a, ePLCType.S7_300_400_compatibel.name())
					.equals(PLCComDriver.msgOk)) {
				ClienteAGV.enviar("<xml>AEMERGENCIA</xml>", SupervisorioFord.IPAGV1);
			}

			int[] b = { 1 };
			if (PLCComDriver.request(SupervisorioFord.IPPLC, 102, 52, 7, b, ePLCType.S7_300_400_compatibel.name())
					.equals(PLCComDriver.msgOk)) {
				ClienteAGV.enviar("<xml>AEMERGENCIA</xml>", SupervisorioFord.IPAGV1);
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void FordAction(String pctXML) {
		System.out.println("Action");
		Config config = Config.getInstance();
		if (config.getProperty(Config.PROP_PROJ).equals(ConfigProcess.PROJ_FORD)) {
			int ac = Integer.parseInt(Util.localizarStrXML(pctXML, "<a>", "</a>"));
			
			
			System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 1, 0, false,
					ePLCType.S7_300_400_compatibel.name()));
			System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 1, 1, false,
					ePLCType.S7_300_400_compatibel.name()));
			System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 1, 2, false,
					ePLCType.S7_300_400_compatibel.name()));
			System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 1, 3, false,
					ePLCType.S7_300_400_compatibel.name()));
			switch (ac) {

				case 1:
					System.out.println("Init Dexindexa Action");
					System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 1, 2, true,
							ePLCType.S7_300_400_compatibel.name()));
					break;
				case 2:
					System.out.println("Complet Dexindexa Action");
					System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 1, 3, true,
							ePLCType.S7_300_400_compatibel.name()));
					break;
				case 3:
					System.out.println("Init Indexa Action");
					System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 1, 0, true,
							ePLCType.S7_300_400_compatibel.name()));
					break;
				case 4:
					System.err.println("Retorno = " + PLCComDriver.sendBit(SupervisorioFord.IPPLC, 102, 52, 1, 1, true,
							ePLCType.S7_300_400_compatibel.name()));
					System.out.println("Complet Indexa Action");
					break;
	
				default:
					break;
			}
			
			//armazenar informações de actions no BD (falta)
			
		}
		
	}
}
