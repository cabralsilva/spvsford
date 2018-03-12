package AGVS.FORD.SUPERVISORIO;

import Main.SupervisorioFord;

public class Reply extends Thread {

	public Reply() {
		System.out.println("Start Reply");
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

			
			if (Controle_old.menssagemOld != null) {
				Controle_old.Supervisorio(Controle_old.menssagemOld);
			}

			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
