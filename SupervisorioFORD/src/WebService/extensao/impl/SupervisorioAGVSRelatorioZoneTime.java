package WebService.extensao.impl;

import java.io.PrintStream;

import AGVS.Data.ConfigProcess;
import WebService.HTML.ConvertPAGinHTML;
import WebService.HTML.PathFilesPAG;
import WebService.HTML.Tags;
import WebService.extensao.Command;
import WebService.extensao.impl.Login.Login;
import WebService.extensao.impl.Tags.Keys;
import WebService.extensao.impl.Tags.Methods;
import WebService.http.Request;
import WebService.http.Response;

public class SupervisorioAGVSRelatorioZoneTime implements Command{

	private ConvertPAGinHTML cph;
	private String[] files = {PathFilesPAG.relatoriosZoneTime};
	
	
	@Override
	public void execute(Request req, Response resp) throws Exception {
		// TODO Auto-generated method stub
		if(!ConfigProcess.bd().conectarBancoDados()){
			PrintStream out = resp.getPrintStream();
			out.println("<meta http-equiv=\"refresh\" content=1;url=\"/SupervisorioAGVS/Configuracao/BD\">");
			out.flush();
			return;
		}
		if(!Login.login(req, resp, Login.tokenRelatorioZoneTime)) {
			Login.redirectLoginInvalid(req, resp);
			return;
		}

		Tags tags = new Tags();
		Methods mt = new Methods();
		tags.add(Keys.keyGenerateDashboard, mt.methodGenerateDashboardRelatorioZoneTime);
		tags.add(Keys.keyGenerateUser, mt.methodGenerateNomeUsuario);
		tags.add(Keys.keyGerarTabelaRelatorioZoneTime, mt.methodGerarRelatorioZoneTime);
		tags.add(Keys.keyListAGVs, mt.methodListAGVs);
		tags.add(Keys.keyZoneTime, mt.methodListZoneTime);
		mt.methodGenerateNomeUsuario.setArgs(req.getCookie("name").getValue());
		cph = new ConvertPAGinHTML();
		cph.generateHTML(files, tags);
		
		Home(req, resp);
	}
	
	private void Home(Request req, Response resp) {
		PrintStream out = resp.getPrintStream();
		out.println(cph.getHtml(files[0]));
		out.flush();
	}

}
