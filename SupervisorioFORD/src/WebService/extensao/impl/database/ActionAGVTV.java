package WebService.extensao.impl.database;

import java.io.PrintStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import AGVS.Data.AGV;
import AGVS.Data.ConfigProcess;
import AGVS.Data.LogZoneTime;
import AGVS.Data.Tag;
import AGVS.Serial.DatabaseStatic;
import AGVS.Util.Util;
import WebService.extensao.CommandDB;
import WebService.http.Config;
import WebService.http.Dispatcher;
import WebService.http.Request;
import WebService.http.Response;

public class ActionAGVTV implements CommandDB {

	@Override
	public void execute(Request req, Response resp, Dispatcher disp) throws Exception {
		String html = "";
		List<AGV> agvs = DatabaseStatic.lstAGVS;
		Collections.sort(agvs);
		Config config = Config.getInstance();
		String bgColor = "white   ";
		
		//ALERTA PELO PLANO DE FUNDO DA TELA DO SUPERVISÓRIO
		for (AGV agv : agvs) {
			if (agv.getStatus().equals(AGV.statusObstaculo)) {
				bgColor = "yellow  ";
				break;
			}
		}
		for (AGV agv : agvs) {
			if (agv.getStatus().equals(AGV.statusEmergencia) || agv.getStatus().equals(AGV.statusEmergenciaRemota) || agv.getStatus().equals(AGV.statusFugaRota)) {
				bgColor = "red     ";
				break;
			}
		}
		//FIM ALERTA PLANO DE FUNDO DA TELA

		for (AGV agv : agvs) {
			//TEMPO SEM ENVIAR STATUS > 10 MINUTOS, STATUS = 'EM REPOUSO'
			if ((agv.getStatusTimeOld() < (new Date().getTime() - 60000)) && (!agv.getStatus().equals(AGV.statusEmRepouso))){
				agv.setStatus(AGV.statusEmRepouso);
				ConfigProcess.bd().updateCurrencyStatusAGV(agv.getId(), AGV.statusEmRepouso, System.currentTimeMillis());
			}
			
			String fontCor = "white";
			String bgLabelStatus = "gray-600' ";
			////////////////////////////////////////////////////////
			switch (agv.getStatus()) {
				case AGV.statusRodando:
					html += "<div class='col-md-2' style='margin-right: 0px; width: 16.6%; height: 100%; border: #cdcdcd 1px solid; border-radius: 10px; background-color: rgba(23,202,23,0.7);'>";
					fontCor = "black";
					bgLabelStatus = "green-600' ";
					break;
				case AGV.statusEmEspera:
					html += "<div class='col-md-2' style='margin-right: 0px; width: 16.6%; height: 100%; border: #cdcdcd 1px solid; border-radius: 10px; background-color: rgba(255,192,203,1);'>";
					fontCor = "black";
					bgLabelStatus = "pink-600' ";
					break;
				case AGV.statusEmRepouso:
					html += "<div class='col-md-2' style='margin-right: 0px; width: 16.6%; height: 100%; border: #cdcdcd 1px solid; border-radius: 10px; background-color: white;'>";
					fontCor = "black";
					bgLabelStatus = "blue-300' ";
					break;
				case AGV.statusEmCruzamento:
				case AGV.statusEmFila:
					html += "<div class='col-md-2' style='margin-right: 0px; width: 16.6%; height: 100%; border: #cdcdcd 1px solid; border-radius: 10px; background-color: yellow;'>";
					fontCor = "black";
					bgLabelStatus = "yellow-600' ";
					break;
				case AGV.statusObstaculo:
					//VERIFICA SE ESTÁ A MAIS DE 5 MINUTOS COM OBSTÁCULO A FRENTE
					if ((agv.getStatusTimeOld() < (new Date().getTime() - 600000)) && (!agv.getStatusOld().equals(AGV.statusObstaculo))) {
						html += "<div class='col-md-2' style='margin-right: 0px; width: 16.6%; height: 100%; border: #cdcdcd 1px solid; border-radius: 10px; background-color: red;'>";
						bgColor = "red     ";
						bgLabelStatus = "red-600' ";
					}else {
						html += "<div class='col-md-2' style='margin-right: 0px; width: 16.6%; height: 100%; border: #cdcdcd 1px solid; border-radius: 10px; background-color: yellow;'>";
						fontCor = "black";
						bgLabelStatus = "yellow-600' ";
					}
					break;
				case AGV.statusEmergencia:
				case AGV.statusEmergenciaRemota:
				case AGV.statusFugaRota:
					html += "<div class='col-md-2' style='margin-right: 0px; width: 16.6%; height: 100%; border: #cdcdcd 1px solid; border-radius: 10px; background-color: red;'>";
					bgLabelStatus = "red-600' ";
					break;
				case AGV.statusManual:
					html += "<div class='col-md-2' style='margin-right: 0px; width: 16.6%; height: 100%; border: #cdcdcd 1px solid; border-radius: 10px; background-color: rgba(12,103,193,0.8);'>";
					bgLabelStatus += "blue-600' ";
					break;
				default:
			
					break;
			}
			
			html += "<table width=\"100%\" style=\"border-spacing: 5px;\">" + "<tr>" + "	<td>";
			switch (agv.getTipo()) {
				case "Carregador":
					html += "<img class='img-rounded' src='/TAS/images/icon-agv-carregador.jpg' alt='...'> ";
					break;
				case "Rebocador":
					System.out.println("REBOCADOR");
					html += "<img class='img-rounded' src='/TAS/images/icon-agv-rebocador.jpg' alt='...'> ";
					break;
				case AGV.tipoCarregadorLifter:
					html += "<img width='100px' heigth='100px' class='img-rounded' src='/TAS/images/icon-agv-carregador-lifter.jpg' alt='...'> ";
					break;
				default:
					break;
			}
			html += "</td>" + "<td>" + "<center><span style='font-size: 32px; color: " + fontCor
					+ "; font-weight: bold;'>" + agv.getNome() + "</span></center>" + "</td>" + "</tr>";
			
			//ZONAS DE TEMPO
			boolean agvInZone = false;
			if (DatabaseStatic.logZoneTimes != null) {
				for (LogZoneTime lzt : DatabaseStatic.logZoneTimes) {
					if (lzt.getAgv().getId() == agv.getId()
							&& !(lzt.getZoneTime().getTagStart().equals(lzt.getZoneTime().getTagEnd()))) {
						agvInZone = true;
						SimpleDateFormat formatador = new SimpleDateFormat("HH:mm:ss");
						Date data = formatador.parse("00:00:00");
						Time timeZero = new Time(data.getTime());
						long interval = lzt.getZoneTime().getLimiteTempo().getTime() - timeZero.getTime();
						long current = (lzt.getCurrentTime().getTime() - timeZero.getTime());
						long percent = 0;
						if (!lzt.isBurst()) {
							percent = 100 - (current * 100 / interval);
						}

						html += "<tr><td colspan=\"2\"><center><span style='font-size: 18px;'>#"
								+ lzt.getZoneTime().getDescricao() + "</span><br></center></td></tr>" +

								"<tr><td style=\"padding: 5px;\"><center><span style='font-size: 12px;'>Tempo</span>"
								+ "<div class='counter-number-group margin-bottom-0'  style='line-height: 1;'>"
								+ "<span class='counter-number'>" + lzt.getCurrentTime() + "</span>" + "</div>"
								+ "<div class='progress progress-xs margin-bottom-0'>";
						if (!lzt.isBurst()) {
							html += "<div class='progress-bar progress-bar-info bg-green-600' aria-valuenow='" + percent
									+ "' aria-valuemin='0' aria-valuemax='100' style='width: " + percent
									+ "%' role='progressbar'><span class='sr-only'>" + percent + "%</span>" + "</div>";
						} else {

							html += "<div class='progress-bar progress-bar-info bg-red-600' aria-valuenow='100' aria-valuemin='0' aria-valuemax='100' style='width: 100%' role='progressbar'>"
									+ "<span class='sr-only'>100%</span>" + "</div>";
						}

						html += "</center></td>" + "<td><center><span style='font-size: 12px;'>Obstaculos</span>"
								+ "<div class='counter-number-group margin-bottom-0'  style='line-height: 1;'>"
								+ "<span class='counter-number'>"
								+ Util.getDurationConvertString(lzt.getTimeLostObstacle()) + "</span>" + "</div>"
								+ "</center></td>" + "</tr>";
					}
				}
			}
			if (!agvInZone) {
				html += "<tr><td colspan=\"2\"><center><span style='font-size: 14px;'>#Don't is in ZONE TIME#</span><br></center></td></tr>";
			}

			html += "</table>";
			//FIM ZONAS DE TEMPO
			
			//LABEL STATUS
			html += "<div class='col-md-offset-1 col-md-8 bg-"+bgLabelStatus+" style='font-size: 18px; font-weight: bold; color: " + fontCor
					+ "; height: 30px; border-radius: 2px; position: absolute; bottom: 10px; margin-right: 0;'>"
					+ "<center>" + agv.getStatus() + "</center>" + "</div>" + "</div>";
		
		}

		PrintStream out = resp.getPrintStream();
		out.println(bgColor + html);
		out.flush();
		resp.flush();

	}
}
