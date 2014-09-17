package com.indoorlocating.server;

import net.sf.json.*;

import java.util.*;
import java.sql.*;

public class Logic 
{	
	final static int WIFI_LEVEL_FLOOR=-100;
	final static int WIFI_NOTEXISTS_WEIGHT=-200;
	
	private static ArrayList<WIFI_MES> getVectorALByVectorString(String vector)
		throws SQLException
	{
		ArrayList<WIFI_MES> arrayList=new ArrayList<WIFI_MES>();
		JSONArray jsonArray=JSONArray.fromObject(vector);
		Iterator<JSONObject> i=(Iterator<JSONObject>)jsonArray.iterator();
		while (i.hasNext())
		{
			JSONObject jsonObject=i.next();
			arrayList.add(new WIFI_MES(DBInterface.getWidByBssid(jsonObject.getString("bssid")), jsonObject.getInt("level")));
		}
		return arrayList;
	}
	
	private static ArrayList<WIFI_MES> getVectorALByLabel(String label,int floor)
		throws SQLException
	{
		return DBInterface.getVectorALByLabel(label,floor);
	}
	
	private static double getDis(ArrayList<WIFI_MES> testVector, ArrayList<WIFI_MES> sampleVector)
	{
		double result=0;
		Collections.sort(testVector,new comparator_WIFI_MES());
		Collections.sort(sampleVector,new comparator_WIFI_MES());
		int i=0,j=0,count=0;
		while (j!=sampleVector.size())
		{
			if (i>=testVector.size() || testVector.get(i).wid>sampleVector.get(j).wid)
			{
				//result+=(WIFI_NOTEXISTS_WEIGHT-sampleVector.get(j).level)*(WIFI_NOTEXISTS_WEIGHT-sampleVector.get(j).level);
				j++;
			}else
			if (testVector.get(i).wid==sampleVector.get(j).wid)
			{
				result+=(testVector.get(i).level-sampleVector.get(j).level)*(testVector.get(i).level-sampleVector.get(j).level);
				i++;j++;count++;
			}else
			i++;
		}
		return count==0?Double.MAX_VALUE:result/count;
	}
	
	public static JSONObject getLocation(String vector)
	{
		JSONObject result=new JSONObject();
		try
		{
			ArrayList<String> labelArrayList=DBInterface.getAllLabelAL();
			ArrayList<WIFI_MES> testVector=getVectorALByVectorString(vector);
			Iterator<String> i=labelArrayList.iterator();
			double maxDis=Double.MAX_VALUE;
			String maxLabel="#NULL#";
			while (i.hasNext())
			{
				String label=i.next();
				double tmp;
				System.out.println(label+"   "+getDis(testVector,getVectorALByLabel(label,WIFI_LEVEL_FLOOR)));
				if ((tmp=getDis(testVector,getVectorALByLabel(label,WIFI_LEVEL_FLOOR)))<maxDis)
				{
					maxDis=tmp;
					maxLabel=label;
				}
			}
			
			result.put("flag", 1);
			result.put("label", maxLabel);
			return result;
		}
		catch(Exception e)
		{
			System.out.println("ERROR: "+e);
			result.put("flag",0);
			return result;
		}
	}
	
	public static JSONObject processInput(String label,String vector)
	{
		JSONObject result=new JSONObject();
		try
		{
			JSONArray jsonArray=JSONArray.fromObject(vector);
			if (!DBInterface.labelExists(label)) DBInterface.createLocation(label);
			int lid=DBInterface.getLidByLabel(label);
			Iterator<JSONObject> i=(Iterator<JSONObject>)jsonArray.iterator();
			while (i.hasNext())
			{
				JSONObject jsonObject=i.next();
				String bssid=jsonObject.getString("bssid");
				int level=jsonObject.getInt("level");
				if (!DBInterface.bssidExists(bssid)) DBInterface.createWifi(bssid);
				int wid=DBInterface.getWidByBssid(bssid);
				DBInterface.createSample(lid,wid,level);
			}
			result.put("flag", 1);
			return result;
		}
		catch(Exception e)
		{
			System.out.println("ERROR: "+e);
			result.put("flag",0);
			return result;
		}
	}
}

class WIFI_MES
{
	public int wid;
	public double level; 
	
	public WIFI_MES(int wid,double level)
	{
		this.wid=wid;
		this.level=level;
	}
	
	@Override
	public String toString()
	{
		return "["+wid+" "+level+"]";
	}
}

class comparator_WIFI_MES implements Comparator<WIFI_MES>
{
	public int compare(WIFI_MES a,WIFI_MES b)
	{
		if (a.wid==b.wid) return 0;
		return a.wid<b.wid?-1:1;
	}
}