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
import AGVS.FORD.MESH.MeshModbus;
import AGVS.FORD.MESH.TipoBaia;
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

public class ActionLayoutParadas implements CommandDB {

	@Override
	public void execute(Request req, Response resp, Dispatcher disp) throws Exception {
		String html = "white   ";

//		List<Tag> tags = DatabaseStatic.lstTags;
//		for (Tag tag : tags) {
//			if (DatabaseStatic.bufferParadas.contains(tag)) {
//				html += "<div style=\"position: absolute; top: "+tag.getCoordenadaY()+"px; left: "+tag.getCoordenadaX()+"px; transform: rotate(0deg) translate(-50%,-50%); /*border: 3px solid #73AD21;*/\">\r\n" + 
//						"	<input type=\"checkbox\" id="+tag.getEpc()+" onchange=\"sendStops(this)\" class=\"pitstops\" name=\"inputiCheckBasicCheckboxes\" data-plugin=\"switchery\" data-color=\"#f96868\" data-secondary-color=\"#73AD21\" data-size=\"large\" checked />" + 
//						"</div>";
//			}else {
//				html += "<div style=\"position: absolute; top: "+tag.getCoordenadaY()+"px; left: "+tag.getCoordenadaX()+"px; transform: rotate(0deg) translate(-50%,-50%); /*border: 3px solid #73AD21;*/\">\r\n" + 
//						"	<input type=\"checkbox\" id="+tag.getEpc()+" onchange=\"sendStops(this)\" class=\"pitstops\" name=\"inputiCheckBasicCheckboxes\" data-plugin=\"switchery\" data-color=\"#f96868\" data-secondary-color=\"#73AD21\" data-size=\"large\"/>" + 
//						"</div>";
//			}
//			
//		}
		
		for(MeshModbus mm : DatabaseStatic.lstMeshModBus) {
			if (mm.getBaia().getTipo() != TipoBaia.ENDPOINT) {
				html += "<div style=\"position: absolute; top: "+mm.getBaia().getCoordenadaY()+"%; left: "+mm.getBaia().getCoordenadaX()+"%; transform: rotate(0deg) translate(-50%,-50%); /*border: 3px solid #73AD21;*/\">\r\n" + 
						"	<input type=\"checkbox\" id=\""+mm.getNome()+"\" onchange=\"buildOrder(this)\" data-tipo=\""+mm.getBaia().getTipo()+"\" data-numero=\""+mm.getBaia().getNumero()+"\" class=\"baia\" name=\"inputiCheck"+mm.getNome()+"\" data-plugin=\"switchery\" data-color=\"#f96868\" data-secondary-color=\"#73AD21\" data-size=\"medium\" />" + 
						"</div>";
			}			
		}
		
		
		
		PrintStream out = resp.getPrintStream();
		out.println(html);
		out.flush();
		resp.flush();

	}

}
