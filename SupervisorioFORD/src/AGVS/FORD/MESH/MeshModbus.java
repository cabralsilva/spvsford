package AGVS.FORD.MESH;
//test3.java 7/23/97 - JAVA program to read registers via gateway

//compile as
//javac test3.java
//run as
//java test3 aswales1.modicon.com 1 2 3

import java.io.*;
import java.net.*;
import java.util.*;

import AGVS.Drivers.CLP.PLCComDriver;
import AGVS.FORD.SUPERVISORIO.ClienteAGV;
import Main.AGVCommunication;
import Main.SupervisorioFord;
import PLCCom.ePLCType;

public class MeshModbus extends Thread {

	private boolean on;
	private String ip;
	private boolean btn1;
	private boolean btn2;
	private boolean btn3;
	private long time = System.currentTimeMillis();
	private String nome;
	private Baia baia;
	private List<Baia> lstBaia;
	

	public MeshModbus(String ip, String nome, Baia lstBaia, boolean btn1, boolean btn2, boolean btn3) {
		this.nome = nome;
		this.ip = ip;
		this.btn1 = btn1;
		this.btn2 = btn2;
		this.btn3 = btn3;
		this.on = true;
		this.baia = lstBaia;
		this.start();
	}

	public boolean isBtn1() {
		return btn1;
	}

	public void setBtn1(boolean btn1) {
		this.btn1 = btn1;
	}

	public boolean isBtn2() {
		return btn2;
	}

	public void setBtn2(boolean btn2) {
		this.btn2 = btn2;
	}

	public boolean isBtn3() {
		return btn3;
	}

	public void setBtn3(boolean btn3) {
		this.btn3 = btn3;
	}
	
	

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Baia getBaia() {
		return baia;
	}

	public void setBaia(Baia baia) {
		this.baia = baia;
	}
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public List<Baia> getLstBaia() {
		return lstBaia;
	}

	public void setLstBaia(List<Baia> lstBaia) {
		this.lstBaia = lstBaia;
	}

	public void run() {
		System.out.println("Iniciando " + this.nome);
		while (on) {

			try {
				//System.out.println("Tempo ciclo " + this.nome + ": " + (System.currentTimeMillis() - time));
				time = System.currentTimeMillis();
				String ip_adrs = this.ip;
				int unit = 4;
				int reg_no = 0;
				int num_regs = 20;

				// set up socket
				Socket es = new Socket();
				es.connect(new InetSocketAddress(ip_adrs, 502), 500);
				// Socket es = new Socket(ip_adrs, 502);

				es.setSoTimeout(1000);

				OutputStream os = es.getOutputStream();

				FilterInputStream is = new BufferedInputStream(es.getInputStream());

				byte obuf[] = new byte[261];
				byte ibuf[] = new byte[261];
				int c = 0;
				int i;

				for (i = 0; i < 5; i++)
					obuf[i] = 0;
				obuf[5] = 6;
				obuf[6] = (byte) unit;
				obuf[7] = 3;
				obuf[8] = (byte) (reg_no >> 8);
				obuf[9] = (byte) (reg_no & 0xff);
				obuf[10] = (byte) (num_regs >> 8);
				obuf[11] = (byte) (num_regs & 0xff);

				// send request
				os.write(obuf, 0, 12);

				// read response
				i = is.read(ibuf, 0, 261);
				if (i < 9) {
					if (i == 0) {
						System.err.println("unexpected close of connection at remote end");
					} else {
						System.err.println("response was too short - " + i + " chars");
					}
				} else if (0 != (ibuf[7] & 0x80)) {
					System.err.println("MODBUS exception response - type " + ibuf[8]);
				} else if (i != (9 + 2 * num_regs)) {
					System.err.println("incorrect response size is " + i +

							" expected" + (9 + 2 * num_regs));
				} else {
					for (i = 0; i < num_regs; i++) {
						int w = (ibuf[9 + i + i] << 8) + ibuf[10 + i + i];

						if (i == 0) {
							if (w >= 1024) {
								this.btn3 = true;
//								System.out.println(this.getIp() + ": EMERGENCIA LIVRE BTN3");
							} else {
								this.btn3 = false;
							}
						}

						if (i == 5) {
							if (w == 256) {
								this.btn1 = true;
								this.btn2 = false;
								System.out.println(this.getIp() + ": ROTA NORMAL BTN 1");
								this.baia.doOrderSample();
//								for(Baia baia : this.lstBaia) {
//									if (baia.getTipo().equals(TipoBaia.ORDER)) {
//										baia.doOrderSample();
//									}
//								}
							} else if (w == 512) {
								this.btn1 = false;
								this.btn2 = true;
								System.out.println(this.getIp() + ": SENSOR BAIA BTN2 ON");
							} else if (w == 768) {
								this.btn1 = true;
								this.btn2 = true;
								System.out.println(this.getIp() + ": BTN1 ON -- BTN2 ON");
//								System.out.println(this.getIp() + ": ");
							} else {
								this.btn1 = false;
								this.btn2 = false;
							}
						}
					}
					//System.out.println(this.nome + ": Btn1:" + this.btn1 + "Btn2:" + this.btn2 + "Btn3:" + this.btn3);
				}

				// close down
				es.close();
				Thread.sleep(250);
			} catch (Exception e) {
//				System.out.println("exception :" + e);
				try {
					Thread.sleep(1);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

}