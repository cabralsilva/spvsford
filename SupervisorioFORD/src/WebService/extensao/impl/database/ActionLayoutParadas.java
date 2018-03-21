package WebService.extensao.impl.database;

import java.io.PrintStream;
import java.util.List;

import AGVS.Data.AGV;
import AGVS.Data.Tag;
import AGVS.FORD.MESH.Baia;
import AGVS.FORD.MESH.MeshModbus;
import AGVS.FORD.MESH.TipoBaia;
import AGVS.Serial.DatabaseStatic;
import WebService.extensao.CommandDB;
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
		for (int k = 0; DatabaseStatic.lstMeshModBus != null && k < DatabaseStatic.lstMeshModBus.size(); k++) {
			MeshModbus mm = DatabaseStatic.lstMeshModBus.get(k);
			for (int i = 0; mm.getLstBaia() != null && i < mm.getLstBaia().size(); i++) {
				Baia baia = mm.getLstBaia().get(i);
				if (baia.getTipo() != TipoBaia.HOME1 && baia.getTipo() != TipoBaia.HOME2) {
					html += "<div style=\"position: absolute; top: " + baia.getCoordenadaY()+"%; left: "+baia.getCoordenadaX()+"%; transform: rotate(0deg) translate(-50%,-50%); /*border: 3px solid #73AD21;*/\">\r\n" + 
							"<button id=\"" + baia.getNome()+"\"3 type=\"button\" class=\"btn btn-floating btn-success btn-sm\" onclick=\"buildOrder(this)\" data-tipo=\""+baia.getTipo()+"\" data-numero=\""+baia.getNumero()+"\" name=\"btn"+baia.getNome()+"\">"
								+ "<i class=\"icon wb-plus\" aria-hidden=\"true\"></i></button>"+
							"</div>";
					
					
				}
			}						
		}
		
		List<Tag> tags = DatabaseStatic.lstTags;		
		List<AGV> agvs = DatabaseStatic.lstAGVS;
		int r = 11;
		String color = "green";
		for (Tag tag : tags) {
			int codigo;
			for(AGV agv : agvs) {
				
				String fontCor = "white";
				if (!agv.getStatus().equals(AGV.statusManual) && !agv.getStatus().equals(AGV.statusEmRepouso)) {
					if (agv.getTagAtual() != null && tag.getEpc() != null && agv.getTagAtual().equals(tag.getEpc())) {
						switch (agv.getStatus()) {
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
						codigo = agv.getId();
						html += "<div style=\"position: absolute; "
								+ "font-size: 14px; "
								+ "height: "+(2*r)+"px; "
								+ "width: " +(2*r)+"px; "
								+ "top: "+tag.getCoordenadaY()+"px; left: "+tag.getCoordenadaX()+"px; "
								+ "transform: translate(-50%,-50%); "
								+ "border: 1px solid #73AD21; border-radius: 50%; "
								+ "background-color: "+color+"; color: "+fontCor+"; \">\r\n" +  
									
									"<center>"+codigo+"</center>"+
							"</div>";
					}
				}
			}	
		}
		
		
		
		PrintStream out = resp.getPrintStream();
		out.println(html);
		out.flush();
		resp.flush();

	}

}
