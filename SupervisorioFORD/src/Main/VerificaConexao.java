package Main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import AGVS.Util.Util;

public class VerificaConexao {
	public static void main(String[] args) throws InterruptedException {

		if (true) {
			InetAddress address = null;
			try {
				address = InetAddress.getByName("192.168.25.21");
			} catch (UnknownHostException e) {
				System.out.println("Cannot lookup host " + args[0]);
				return;
			}
			try {
				if (address.isReachable(5000)) {
					long nanos = 0;
					long millis = 0;
					long iterations = 0;
					while (true) {
						iterations++;
						try {
							nanos = System.nanoTime();
							address.isReachable(500); // this invocation is the offender
							nanos = System.nanoTime() - nanos;
						} catch (IOException e) {
							System.out.println("Failed to reach host");
						}
						millis = Math.round(nanos / Math.pow(10, 6));
						if (millis < 2) {
							System.err.println(
									Util.getDateTimeFormatoBR(System.currentTimeMillis()) + " Sem conexão");
						} else {
							System.out.println(
									Util.getDateTimeFormatoBR(System.currentTimeMillis()) + ": Resposta do IP: "
											+ address.getHostAddress() + " com de tempo=" + millis + " ms");
						}
						try {
							Thread.sleep(Math.max(0, 1000 - millis));
						} catch (InterruptedException e) {
							break;
						}
						//Thread.sleep(500);
					}
					System.out.println("Iterations: " + iterations);
				} else {
					System.out.println("Host " + address.getHostName() + " is not reachable even once.");
				}
			} catch (IOException e) {
				System.out.println("Network error.");
			}
		} else {
			System.out.println("Usage: java isReachableTest <host>");
		}
	}
}
