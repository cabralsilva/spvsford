package WebService.extensao.impl.database;

import java.awt.Container;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import AGVS.Controller.Rules.RulesUsuarios;
import AGVS.Data.AGV;
import AGVS.Data.ConfigProcess;
import AGVS.Data.Line;
import AGVS.Data.Tag;
import AGVS.Serial.DatabaseStatic;
import AGVS.Util.Log;
import AGVS.Util.Util;
import WebService.HTML.ConvertPAGinHTML;
import WebService.HTML.PathFilesPAG;
import WebService.HTML.Tags;
import WebService.extensao.Command;
import WebService.extensao.CommandDB;
import WebService.extensao.impl.Login.Login;
import WebService.extensao.impl.Tags.Keys;
import WebService.extensao.impl.Tags.Methods;
import WebService.extensao.impl.Tags.TagsValues;
import WebService.http.Config;
import WebService.http.Dispatcher;
import WebService.http.Request;
import WebService.http.Response;

public class ActionLayoutTV implements CommandDB {
	private static int i = 0;

	@Override
	public void execute(Request req, Response resp, Dispatcher disp) throws Exception {
		String html = "white   ";

		List<Line> lines = ConfigProcess.bd().selecLines();
		List<Tag> tags = ConfigProcess.bd().selecTags();
		List<AGV> agvs = DatabaseStatic.lstAGVS;
		html += "<center>";
		html += "<svg style='background: no-repeat; background-image: url(../TAS/images/background.png); background-size: 65.25vh 86vh;' id='svg' height='86vh' width='67vh' viewBox='0 0 750 1000' preserveAspectRatio='xMinYMin meet'>";
		
		Config config = Config.getInstance();

		for (int i = 0; tags != null && i < tags.size(); i++) {
			Tag t = tags.get(i);
			int r = 22;
			int codigo = t.getCodigo();
			String color = "green";
			for (int j = 0; agvs != null && j < agvs.size(); j++) {
				AGV a = agvs.get(j);
				if (a.getStatusTimeOld() < (new Date().getTime() - 1800000) ) {
					a.setStatus(AGV.statusEmRepouso);
				}
				String fontCor = "white";
				if ((!a.getStatus().equals(AGV.statusManual)) && (!a.getStatus().equals(AGV.statusEmRepouso))) {
					if (a.getTagAtual() != null && t.getEpc() != null && a.getTagAtual().equals(t.getEpc())) {
						if (config.getProperty(Config.PROP_PROJ).equals(ConfigProcess.PROJ_FIAT)) {
							
							switch (a.getStatus()) {
							case AGV.statusFugaRota:
							case AGV.statusEmergenciaRemota:
							case AGV.statusEmergencia:
								color = "red";
								fontCor = "black";
								break;
							case AGV.statusEmCruzamento:
							case AGV.statusEmFila:
							case AGV.statusObstaculo:
								color = "yellow";
								fontCor = "black";
								break;
							case AGV.statusEmEspera:
								color = "rgba(255,192,203,1)";
								fontCor = "black";
								break;
							case AGV.statusEmRepouso:
								color = "white";
								fontCor = "black";
								break;
							case AGV.statusManual:
								color = "rgba(12,103,193,0.8)";
							default:
								color = "rgba(0,255,0,0.7)";
								fontCor = "black";
								break;
							}
							
						}else {
							color = "green";
							if (a.getStatus().equals(AGV.statusParado)) {
								color = "blue";
							}

							if (a.getStatus().equals(AGV.statusFugaRota) || a.getStatus().equals(AGV.statusEmergencia)
									|| a.getStatus().equals(AGV.statusObstaculo)) {
								color = "red";
							}

							if (a.getAtraso() == 1) {
								color = "orange";
							}
						}

						

						codigo = a.getId();

						html += "<g class=\"cursor-rota\">";
						html += "<circle cx = \"" + t.getCoordenadaX() + "\"cy=\"" + t.getCoordenadaY() + "\"r=\"" + 12
								+ "\"fill=\"" + color + "\" stroke=\"black\" stroke-width=\"2\" id=\"C" + t.getEpc() + "\"/>";
						html += "<text x=\"" + t.getCoordenadaX() + "\"y=\"" + t.getCoordenadaY()
								+ "\"dx=\"-12\"dy=\"6\"style=\"fill:"+ fontCor +"; font-size: 20px\"id=\"T" + t.getEpc() + "\">"
								+ codigo + "</text></g>";
					}
				}
			}

		}
		

		html += "</svg>"
				+ "</center>";

		PrintStream out = resp.getPrintStream();
		out.println(html);
		out.flush();
		resp.flush();

	}

}
