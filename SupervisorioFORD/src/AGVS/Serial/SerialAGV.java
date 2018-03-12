/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AGVS.Serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import AGVS.Data.ComandoMashSerial;
import AGVS.Data.ConfigProcess;
import AGVS.Data.Cruzamento_OLD;
import AGVS.Data.MeshSerial;
import AGVS.Data.PortaMashSerial;
import AGVS.Data.Tag;
import AGVS.Data.TagsRotas;
import AGVS.Util.Log;
import AGVS.Util.Util;
import WebService.http.Config;

/**
 *
 * @author Luiz
 */
public class SerialAGV implements SerialPortEventListener {

	private InputStream in;
	private SerialPort serialPort;
	private OutputStream out;
	private boolean conectado = false;
	private int frameIDPK = 1;
	private static Semaphore semp = new Semaphore(1);
	private int[] pacoteInt = new int[255];
	private int id = 0;
	private String pacote = "";

	public boolean getConectado() {
		return this.conectado;
	}

	public void conectar() {
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier("/dev/ttyS0");
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Erro: Porta em uso");
			} else {
				CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

				if (commPort instanceof SerialPort) {
					serialPort = (SerialPort) commPort;
					serialPort.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
					System.out.println("Conexao Serial Start OK !");
					in = serialPort.getInputStream();
					out = serialPort.getOutputStream();

					serialPort.addEventListener(this);
					serialPort.notifyOnDataAvailable(true);
					conectado = true;
					System.out.println("Conectado");
				} else {
					System.out.println("Error: Somente as portas seriais s�o tratadas.");
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			// TODO: handle exception
		}
	}

	public void enviar(String xml) throws Exception {

		out.write(0xFF & 0xFF);
		out.flush();
		for (int i = 0; i < xml.getBytes().length; i++) {
			out.flush();
			out.write(xml.getBytes()[i]);
			out.flush();
		}
		out.write(0xEF & 0xFF);
		out.flush();
	}

	public void serialEvent(SerialPortEvent arg0) {
		try {
			while (in.available() > 0) {
				try {
					char ler = (char) in.read();
					System.out.print(ler);
					pacote = pacote + ler;
					System.out.println(" Pacote: " + pacote);
					System.out.println("Procurando");
					int pos1 = pacote.indexOf("<xml>") + "<xml>".length();
					System.out.println(pos1);
					int pos2 = pacote.indexOf("</xml>");
					System.out.println(pos2);
					pacote = pacote.substring(pos1, pos2);
					System.out.println(" Pacote: " + pacote);
					new ThActionSerialPacoteAgv(pacote);
					pacote = "";
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			}
		} catch (Exception e) {
			System.out.println("COM:" + e.getMessage());
		}

	}

}
