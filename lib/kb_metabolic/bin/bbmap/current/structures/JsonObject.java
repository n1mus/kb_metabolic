package structures;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import stream.ByteBuilder;

public class JsonObject {

	public static void main(String[] args){
		JsonObject bob=new JsonObject("name", "bob");
		JsonObject joe=new JsonObject("name", "joe");
		JsonObject sue=new JsonObject("name", "sue");
		JsonObject dan=new JsonObject("name", "dan");
		bob.add("joe", joe);
		bob.add("sue", sue);
		joe.add("dan", dan);
		bob.add("a",1);
		bob.add("b",2);
		bob.add("c","3");
		bob.add("a","4");
		dan.add("e",5);
		dan.add("f","6");
		sue.add("g","7");

		System.out.println("dan:\n"+dan);
		System.out.println("sue:\n"+sue);
		System.out.println("joe:\n"+joe);
		System.out.println("bob:\n"+bob);
		
		ArrayList<JsonObject> list=new ArrayList<JsonObject>();
		list.add(joe);
		list.add(sue);
		list.add(dan);
		System.out.println("list:\n"+toString(list));
	}
	
	public JsonObject(){}
	
	public JsonObject(String key, Object value){
		add(key, value);
	}
	
//	public JsonObject(String name_){
//		name=name_;
//	}
//	
//	public JsonObject(String name_, String key, Object value){
//		name=name_;
//		add(key, value);
//	}

	public void add(String key0, Object value){
		if(value!=null && value.getClass()==JsonObject.class){
			add(key0, (JsonObject)value);
			return;
		}
		int x=2;
		String key=key0;
		if(smap==null){smap=new LinkedHashMap<String, Object>(8);}
		while(smap.containsKey(key)){
			key=key0+" "+x;
			x++;
		}
		smap.put(key, value);
	}

	public void add(final String key0, JsonObject value){
		int x=2;
		String key=key0;
		if(jmap==null){jmap=new LinkedHashMap<String, JsonObject>(8);}
		while(jmap.containsKey(key)){
			key=key0+" "+x;
			x++;
		}
		jmap.put(key, value);
	}
	
	public static String toString(ArrayList<JsonObject> list) {
		ByteBuilder sb=new ByteBuilder();
		int commas=list.size()-1;
		for(JsonObject j : list){
			j.append(0, sb);
			if(commas>0){
				sb.append(",\n");
			}
			commas--;
		}
		return sb.toString();
	}
	
	public ByteBuilder toText(){
		return toText(null, 0);
	}
	
	public ByteBuilder toText(ByteBuilder sb, int level){
		if(sb==null){sb=new ByteBuilder();}
		append(level, sb);
		return sb;
	}
	
	public String toString(String name){
		ByteBuilder sb=new ByteBuilder();
		sb.append('{').append('\n');
		for(int i=0; i<padmult; i++){sb.append(' ');}
		sb.append('"').append(name).append('"').append(':').append(' ');
		toText(sb, 1);
		sb.append('\n').append('}');
		return sb.toString();
	}
	
	public static String toString(Object[] array){
		ByteBuilder sb=new ByteBuilder();
		appendArray(sb, array, 0);
		return sb.toString();
	}
	
	@Override
	public String toString(){
		return toText(null, 0).toString();
	}
	
	public void append(int level, ByteBuilder sb){
		int pad=padmult*level;
		int pad2=padmult*(level+1);
		
		sb.append("{\n");
		
		int commas=(smap==null ? 0 : smap.size())+(jmap==null ? 0 : jmap.size())-1;
		
		if(smap!=null){
			for(Entry<String, Object> e : smap.entrySet()){
				String key=e.getKey();
				Object value=e.getValue();
				for(int i=0; i<pad2; i++){sb.append(' ');}
				
				appendEntry(sb, key, value, level);
				
				if(commas>0){sb.append(",\n");}
				else{sb.append('\n');}
				commas--;
			}
		}
		
		if(jmap!=null){
			for(Entry<String, JsonObject> e : jmap.entrySet()){
				String key=e.getKey();
				JsonObject value=e.getValue();
				for(int i=0; i<pad2; i++){sb.append(' ');}
				appendKey(sb, key);
				
				value.append(level+1, sb);
				if(commas>0){sb.append(",\n");}
				else{sb.append("\n");}
				commas--;
			}
		}
		
		for(int i=0; i<pad; i++){sb.append(' ');}
		sb.append('}');
	}
	
	private static void appendEntry(ByteBuilder sb, String key, Object value, int level){
		appendKey(sb, key);
		appendValue(sb, value, level);
	}
	
	private static void appendKey(ByteBuilder sb, String key){
		sb.append('"').append(key).append("\": ");
	}
	
	private static void appendValue(ByteBuilder sb, Object value, int level){
		final Class<?> c=(value==null ? null : value.getClass());
		if(c==null || value==null){
			sb.append("null");
		}else if(c==String.class){
			sb.append("\"").append(value.toString()).append('"');
		}else if(c==Double.class && decimals>=0){
			sb.appendFast(((Double)value).doubleValue(), decimals);
		}else if(c==Float.class && decimals>=0){
			sb.appendFast(((Float)value).floatValue(), decimals);
		}else if(c==JsonObject.class){
			((JsonObject)value).append(level+1, sb);
		}else if(c.isArray()){
			appendArray(sb, (Object[])value, level);
		}else{//long, int, boolean, null
			sb.append(value.toString());
		}
	}
	
	private static void appendArray(ByteBuilder sb, Object[] array, int level){
		int commas=(array==null ? 0 : array.length)-1;
		sb.append('[');
		if(array!=null){
			for(Object value : array){
				appendValue(sb, value, level);
				if(commas>0){sb.append(',').append(' ');}
				commas--;
			}
		}
		sb.append(']');
	}
	
//	public String name;
	public LinkedHashMap<String, Object> smap;
	public LinkedHashMap<String, JsonObject> jmap;

	private static int decimals=-1;
	private static String decimalFormat="%."+decimals+"f";
	public static synchronized void setDecimals(int d){
		if(d!=decimals){
			d=decimals;
			decimalFormat="%."+decimals+"f";
		}
	}
	
	public static final int padmult=3;
	
}
