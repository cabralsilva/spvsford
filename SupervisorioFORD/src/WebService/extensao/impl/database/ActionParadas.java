package WebService.extensao.impl.database;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;

import AGVS.Data.Tag;
import AGVS.Serial.DatabaseStatic;
import WebService.extensao.CommandDB;
import WebService.extensao.impl.Tags.TagsValues;
import WebService.http.Dispatcher;
import WebService.http.Request;
import WebService.http.Response;

public class ActionParadas implements CommandDB {

	@Override
	public void execute(Request req, Response resp, Dispatcher disp) throws Exception {
		String html = "Nao foi possivel realizar comando";

		 System.out.println(req.toString());

		if (req.getGetParams().containsKey(TagsValues.paramAction)) {
			if (req.getGetParams().get(TagsValues.paramAction).equals(TagsValues.valueResetParamAction)) {
//				DatabaseStatic.resetParadas();
				html = "OK";
			}else if (req.getGetParams().get(TagsValues.paramAction).equals(TagsValues.valueSalvarParamAction)) {
				System.out.println(req.getGetParams());
				Iterator it = req.getGetParams().entrySet().iterator();
			    while (it.hasNext()) {
			    	Map.Entry pair = (Map.Entry)it.next();
//			        for(Tag tag : DatabaseStatic.lstTags) {
//			        	if (tag.getEpc().equals(pair.getKey().toString())) {
//			        		System.out.println("Localizou Tag: " + tag.getEpc() + " VALUE: " + pair.getValue());
//			        		if(pair.getValue().equals("1")) {
//			        			if (!DatabaseStatic.bufferParadas.contains(tag)) {
//			        				DatabaseStatic.bufferParadas.add(tag);
//			        			}
				        		
//			        		}else {
//			        			if (DatabaseStatic.bufferParadas.contains(tag)) {
//			        				DatabaseStatic.bufferParadas.remove(tag);
//			        			}
//			        		}
//			        		break;
//			        	}
//			        }
			        
			        it.remove(); // avoids a ConcurrentModificationException
			    }
//			    System.out.println(DatabaseStatic.bufferParadas);
				
				html = "OK";
			}
		}

		PrintStream out = resp.getPrintStream();
		out.println(html);
		out.flush();
		resp.flush();

	}

}
