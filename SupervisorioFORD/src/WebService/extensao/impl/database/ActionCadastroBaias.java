package WebService.extensao.impl.database;

import java.awt.Container;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;

import AGVS.Controller.Rules.RulesUsuarios;
import AGVS.Data.ConfigProcess;
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
import WebService.http.Dispatcher;
import WebService.http.Request;
import WebService.http.Response;

public class ActionCadastroBaias implements CommandDB {
	private static int i = 0;

	@Override
	public void execute(Request req, Response resp, Dispatcher disp) throws Exception {
		String html = "Nao foi possivel realizar comando";

		if (req.getGetParams().containsKey(TagsValues.paramAction)) {
			if (req.getGetParams().get(TagsValues.paramAction).equals(TagsValues.valueDeleteParamAction)) {
				if (req.getGetParams().containsKey(TagsValues.paramID)) {
					if (ConfigProcess.bd().deleteBaia(Integer.parseInt(req.getGetParams().get(TagsValues.paramID)))) {
						ConfigProcess.bd().insertLogUsuarios(System.currentTimeMillis(),
								req.getCookies().get(Login.strKeyName).getValue(),
								"Excluiu BAIA " + req.getGetParams().get(TagsValues.paramID), Login.strExcluir);
						html = "OK";
					}

				}

			} else if(req.getGetParams().get(TagsValues.paramAction).equals(TagsValues.valueSalvarParamAction)){
				if (req.getGetParams().containsKey(TagsValues.paramNome) && req.getGetParams().containsKey(TagsValues.paramNumeroRota)) {
					if (req.getGetParams().containsKey(TagsValues.paramX) && req.getGetParams().containsKey(TagsValues.paramY)) {
						int idInsert;
						if ((idInsert = ConfigProcess.bd().insertBaia(
								req.getGetParams().get(TagsValues.paramNome),
								Integer.parseInt(req.getGetParams().get(TagsValues.paramNumeroRota)), 
								Integer.parseInt(req.getGetParams().get(TagsValues.paramX)),
								Integer.parseInt(req.getGetParams().get(TagsValues.paramY)),
								Integer.parseInt(req.getGetParams().get(TagsValues.paramTipoBaia))))>0) {
							ConfigProcess.bd().insertLogUsuarios(System.currentTimeMillis(),
									req.getCookies().get(Login.strKeyName).getValue(),
									"Adicionou BAIA " + req.getGetParams().get(TagsValues.paramNome),
									Login.strAdicionar);
							html = "ID"+((int)Math.log10(idInsert) + 1)+idInsert;
							System.out.println(html);
						}
					}		
				}
			} else if(req.getGetParams().get(TagsValues.paramAction).equals(TagsValues.valueEditParamAction)) {
				int idInsert;
				if (ConfigProcess.bd().updateBaia(
						Integer.parseInt(req.getGetParams().get(TagsValues.paramID)),
						req.getGetParams().get(TagsValues.paramNome),
						Integer.parseInt(req.getGetParams().get(TagsValues.paramNumeroRota)),
						Integer.parseInt(req.getGetParams().get(TagsValues.paramX)),
						Integer.parseInt(req.getGetParams().get(TagsValues.paramY)),
						Integer.parseInt(req.getGetParams().get(TagsValues.paramTipoBaia)))) {
					ConfigProcess.bd().insertLogUsuarios(System.currentTimeMillis(),
							req.getCookies().get(Login.strKeyName).getValue(),
							"Alterou BAIA " + req.getGetParams().get(TagsValues.paramID),
							Login.strAlterar);
					idInsert = Integer.parseInt(req.getGetParams().get(TagsValues.paramID));
					html = "ID"+((int)Math.log10(idInsert) + 1)+idInsert;
				}
			}

		}

		PrintStream out = resp.getPrintStream();
		out.println(html);
		out.flush();
		resp.flush();

	}

}
