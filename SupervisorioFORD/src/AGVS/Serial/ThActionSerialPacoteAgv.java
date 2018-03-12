package AGVS.Serial;

import AGVS.FORD.SUPERVISORIO.ClienteAGV;
import AGVS.Util.Log;

public class ThActionSerialPacoteAgv extends Thread {

	private String pacote = "";

	public ThActionSerialPacoteAgv(String pacote) {

		this.pacote = pacote;
		this.start();
		

	}

	public void run() {
		try {
			ClienteAGV.enviar(pacote, "19.26.136.11");
		} catch (Exception e) {
			new Log(e);
		}
	}

}
