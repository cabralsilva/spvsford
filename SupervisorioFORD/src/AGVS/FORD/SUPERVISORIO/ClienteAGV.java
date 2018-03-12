package AGVS.FORD.SUPERVISORIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClienteAGV {
	public static void enviar(String xml, String ip) {

		int envio = 0;

		while (envio < 5) {
			try {
				envio++;
				enviarAux(xml, ip);
				envio = 5;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static void enviarAux(String xml, String ip) throws Exception {

		Socket sk = new Socket();
		sk.connect(new InetSocketAddress(ip, 27015), 500);
		InputStream input = sk.getInputStream();
		OutputStream output = sk.getOutputStream();
		byte[] b = xml.getBytes();
		output.write(b.length);
		output.write(b);
		System.out.println("Enviado: " + xml);
		input.close();
		output.close();
		sk.close();
	}
}
