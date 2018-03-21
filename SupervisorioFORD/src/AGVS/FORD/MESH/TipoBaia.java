package AGVS.FORD.MESH;

import java.util.Comparator;

public enum TipoBaia {
	HOME1(1),
	ORDER(2),
	SPECIAL(3),
	DELIVERY1(4),
	RECOVERY1(5),
	DELIVERY2(6),
	RECOVERY2(7),
	HOME2(8);
	
	public int iTipo;

	private TipoBaia(int iTipo) {
		this.iTipo = iTipo;
	}

	public int getiTipo() {
		return iTipo;
	}

	public void setiTipo(int iTipo) {
		this.iTipo = iTipo;
	}
	
	public static TipoBaia getById(int id) {
	    for(TipoBaia e : values()) {
	        if(e.iTipo == id) return e;
	    }
	    return null;
	}
	
//	public class ComparatorTipoBaia implements Comparator<TipoBaia>
//	{
//	    public int compare(TipoBaia o1, TipoBaia o2)
//	    {
//	    	return o1.toString().compareTo(o2.toString());
//	    }
//	}
}

