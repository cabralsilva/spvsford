package WebService.extensao.impl.database;

import java.io.PrintStream;
import java.util.List;

import AGVS.Data.ConfigProcess;
import AGVS.Data.MeshSerial;
import AGVS.Data.PLCPheriferical;
import AGVS.Data.PortaMashSerial;
import AGVS.Data.PortaSaidaMeshSerial;
import AGVS.Serial.DatabaseStatic;
import WebService.extensao.CommandDB;
import WebService.http.Config;
import WebService.http.Dispatcher;
import WebService.http.Request;
import WebService.http.Response;

public class ActionMeshTV implements CommandDB {

	private boolean verifyStatusPort(String port, List<PortaMashSerial> lstPms) {
		for(PortaMashSerial pms : lstPms) {
			if (!pms.getPorta().equals(null) && pms.getPorta().equals(port)) {
				if (pms.getStatus()!=null) {
					if (pms.getStatus().equals(pms.getAcionamento())) {
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}
	
	@Override
	public void execute(Request req, Response resp, Dispatcher disp) throws Exception {

		
		Config config = Config.getInstance();
		String html = "        ";//reservado para a cor do plano de fundo 8 caracteres
		html += "<div class=\"row\" style=\"margin-right: 0px; width: 100%; height: 15%;\">\r\n" + 
				"	<center><span style=\"font-size: 20px;\">ESTADOS ENTRADAS MESH</span></center>\r\n" + 
				"</div>	\r\n" + 
				"<div class=\"row\" style=\"height: 80%; border: #cdcdcd 1px solid; border-radius: 10px; background-color: white;\">\r\n" + 
				"	<div class=\"col-md-3\" style=\"margin-right: 0px; width: 10%; height: 80%;padding-right: 0px;\">\r\n" + 
				"		<center><span style=\"font-size: 15px;\">&nbsp</span></center>\r\n" + 
				"		<div class=\"col-md-12\" style=\"font-weight: bold; color: black; font-size: 25px; height: 40%; background-color: white;padding-left: 0px;padding-right: 0px;\">\r\n" + 
				"			<span style=\"float: right;position: relative; top: 50%; transform: translateY(-50%);\">IN</span>	\r\n" + 
				"		</div>\r\n" + 
				"		<div class=\"col-md-12\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; background-color: white\">\r\n" + 
				"		</div>\r\n" + 
				"		<div class=\"col-md-12\" style=\"font-weight: bold; color: black; font-size: 25px; height: 40%; background-color: white;padding-left: 0px;padding-right: 0px;\">\r\n" + 
				"			<span style=\"float: right;position: relative; top: 50%; transform: translateY(-50%);\">OUT</span>	\r\n" + 
				"		</div>\r\n" + 
				"	</div>\r\n";
				for(MeshSerial ms : DatabaseStatic.mashs) {
//					System.out.println("Mesh ID:: " + ms.getId());
					if (ms.getId() == 1 ) {
						html += "<div class=\"col-md-3\" style=\"height: 80%; width: 30%; \">\r\n" + 
								"	<center><span style=\"font-size: 15px;\">Linha 1</span></center>\r\n";
								if(!verifyStatusPort("5", ms.getLstPms())) {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: white;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">ON</div>\r\n" + 
											"	</div>\r\n";
								}else {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: white;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">OFF</div>\r\n" + 
											"	</div>\r\n";
								}
								
								if(!verifyStatusPort("6", ms.getLstPms())) {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: white;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">ON</div>\r\n" + 
											"	</div>\r\n";
								}else {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: white;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">OFF</div>\r\n" + 
											"	</div>\r\n";
								}
								
								
								html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; background-color: white\">\r\n" + 
										"	</div>\r\n";
								
								if(!verifyStatusPort("5", ms.getLstPms())) {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: green;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">PLAY</div>\r\n" + 
											"	</div>\r\n";
								}else {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: red;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">STOP</div>\r\n" + 
											"	</div>\r\n";
								}
								
								if(!verifyStatusPort("6", ms.getLstPms())) {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: green;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">PLAY</div>\r\n" + 
											"	</div>\r\n";
								}else {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: red;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">STOP</div>\r\n" + 
											"	</div>\r\n";
								}
								
								
								html += "</div>\r\n";
					}else if (ms.getId() == 4){
						html += "<div class=\"col-md-3\" style=\"height: 80%; width: 30%; \">\r\n" + 
								"	<center><span style=\"font-size: 15px;\">Linha 2</span></center>\r\n";
								if(verifyStatusPort("5", ms.getLstPms())) {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: green;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">PLAY</div>\r\n" + 
											"	</div>\r\n";
								}else {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: red;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">STOP</div>\r\n" + 
											"	</div>\r\n";
								}
								
								if(verifyStatusPort("6", ms.getLstPms())) {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: green;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">PLAY</div>\r\n" + 
											"	</div>\r\n";
								}else {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: red;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">STOP</div>\r\n" + 
											"	</div>\r\n";
								}
								html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; background-color: white\">\r\n" + 
										"	</div>\r\n"; 

						for(MeshSerial msin : DatabaseStatic.mashs) {
							if (msin.getId() == 2) {
								if(!verifyStatusPort("5", msin.getLstPms())) {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: green;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">PLAY</div>\r\n" + 
											"	</div>\r\n";
								}else {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: red;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">STOP</div>\r\n" + 
											"	</div>\r\n";
								}
								
								if(!verifyStatusPort("6", msin.getLstPms())) {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: green;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">PLAY</div>\r\n" + 
											"	</div>\r\n";
								}else {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: red;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">STOP</div>\r\n" + 
											"	</div>\r\n";
								}
							}
						}
							
						html += "</div>\r\n";
					}else if (ms.getId() == 5){
						html += "<div class=\"col-md-3\" style=\"height: 80%; width: 30%; \">\r\n" + 
								"	<center><span style=\"font-size: 15px;\">Linha 3</span></center>\r\n";
						if(verifyStatusPort("5", ms.getLstPms())) {
							html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: green;\">\r\n" + 
									"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">PLAY</div>\r\n" + 
									"	</div>\r\n";
						}else {
							html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: red;\">\r\n" + 
									"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">STOP</div>\r\n" + 
									"	</div>\r\n";
						}
						
						if(verifyStatusPort("6", ms.getLstPms())) {
							html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: green;\">\r\n" + 
									"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">PLAY</div>\r\n" + 
									"	</div>\r\n";
						}else {
							html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: red;\">\r\n" + 
									"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">STOP</div>\r\n" + 
									"	</div>\r\n";
						}
								 
						html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; background-color: white\">\r\n" + 
								"	</div>\r\n"; 
						for(MeshSerial msin : DatabaseStatic.mashs) {
							if (msin.getId() == 3) {
								if(verifyStatusPort("5", msin.getLstPms())) {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: green;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">PLAY</div>\r\n" + 
											"	</div>\r\n";
								}else {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: red;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">STOP</div>\r\n" + 
											"	</div>\r\n";
								}
								
								if(verifyStatusPort("6", msin.getLstPms())) {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: green;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">PLAY</div>\r\n" + 
											"	</div>\r\n";
								}else {
									html += "	<div class=\"col-md-offset-3 col-md-6\" style=\"font-weight: bold; color: black; font-size: x-medium; text-align: center;margin-right: 4px; height: 20%; border: #cdcdcd 1px solid; border-radius: 50px; background-color: red;\">\r\n" + 
											"		<div style=\"position: relative; top: 50%; transform: translateY(-50%);\">STOP</div>\r\n" + 
											"	</div>\r\n";
								}
							}
						}
							
						html += "</div>\r\n";
					}
				}
				html += "</div>\r\n";
				
		PrintStream out = resp.getPrintStream();
		out.println(html);
		out.flush();
		resp.flush();

	}
}
