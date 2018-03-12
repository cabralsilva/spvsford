package WebService.extensao.impl;

import java.io.PrintStream;
import java.util.List;

import AGVS.Data.AGV;
import AGVS.Data.ConfigProcess;
import AGVS.Data.MeshSerial;
import AGVS.Data.PortaMashSerial;
import AGVS.Data.Tag;
import AGVS.Serial.DatabaseStatic;
import AGVS.Util.Util;
import WebService.HTML.ConvertPAGinHTML;
import WebService.HTML.PathFilesPAG;
import WebService.HTML.Tags;
import WebService.extensao.Command;
import WebService.extensao.impl.Login.Login;
import WebService.extensao.impl.Tags.Keys;
import WebService.extensao.impl.Tags.Methods;
import WebService.http.Config;
import WebService.http.Request;
import WebService.http.Response;

public class SupervisorioAGVSCadastroAGVS implements Command{

	private ConvertPAGinHTML cph;
	private String[] files = {PathFilesPAG.cadastroAGVS};
	
	
	@Override
	public void execute(Request req, Response resp) throws Exception {
		
		
		
		// TODO Auto-generated method stub
		
		if(!ConfigProcess.bd().conectarBancoDados()){
			PrintStream out = resp.getPrintStream();
			out.println("<meta http-equiv=\"refresh\" content=1;url=\"/SupervisorioAGVS/Configuracao/BD\">");
			out.flush();
			return;
		}
		if(!Login.login(req, resp, Login.tokenCadastroAGVS)) {
			Login.redirectLoginInvalid(req, resp);
			return;
		}

		Tags tags = new Tags();
		Methods mt = new Methods();
		tags.add(Keys.keyGenerateDashboard, mt.methodGenerateDashboardCadastroAGVS);
		tags.add(Keys.keyGenerateUser, mt.methodGenerateNomeUsuario);
		tags.add(Keys.keyListTipoAGVS, mt.methodListTipoAGVS);
		tags.add(Keys.keyListStatusAGVS, mt.methodListStatusAGVS);
		tags.add(Keys.keyGerarTabelaAGVS, mt.methodGerarTabelaAGVS);
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

	private boolean verifyStatusPort(String port, List<PortaMashSerial> lstPms) {
		for(PortaMashSerial pms : lstPms) {
				if (!pms.getPorta().equals(null) && pms.getPorta().equals(port)) {
					if (pms.getStatus()!=null)
						if (pms.getStatus().equals(pms.getAcionamento())) return true;
					return false;
				}
			
			
		}
		return false;
	}
}
