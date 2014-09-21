package com.indoorlocating.server;

import net.sf.json.*;

import java.util.*;
import java.sql.*;

public class Logic 
{	
	final static int WIFI_LEVEL_FLOOR=-90;
	final static int WIFI_LOWER_BOUND=-90;
	final static int WIFI_NOTEXISTS_WEIGHT=-200;
	final static double EXP_BASE=10;
	
	/**
	 * ͨ���ַ�����ʽ��JSONArray��ʾ��ǿ���������HashMap��ʽ��ǿ������
	 * @param vector �ַ�����ʽ��JSONArray��ʾ��ǿ������
	 * @return HashMap��ʽ��ǿ������
	 * @throws SQLException
	 */
	private static HashMap<Integer,WIFI_MES> getVectorHMByVectorString(String vector)
		throws SQLException
	{
		HashMap<Integer,WIFI_MES> hashMap=new HashMap<Integer,WIFI_MES>();
		JSONArray jsonArray=JSONArray.fromObject(vector);
		Iterator<JSONObject> i=(Iterator<JSONObject>)jsonArray.iterator();
		while (i.hasNext())
		{
			JSONObject jsonObject=i.next();
			hashMap.put(DBInterface.getWidByBssid(jsonObject.getString("bssid")), new WIFI_MES((double)jsonObject.getInt("level"),-1));
		}
		return hashMap;
	}
	
	/**
	 * ͨ���ص�label���HashMap��ʽ��ǿ������
	 * @param label �ص�label
	 * @param floor ����wifi�ȵ��ǿ������ 
	 * @return HashMap��ʽ��ǿ������
	 * @throws SQLException
	 */
	private static HashMap<Integer,WIFI_MES> getVectorHMByLabel(String label,int floor)
		throws SQLException
	{
		return DBInterface.getVectorHMByLabel(label,floor);
	}
	
	/**
	 * ����wifi�ȵ�id��ǿ�ȼ��㵱ǰ�㵽wifi��վ�ľ���
	 * @param wid �ȵ�id
	 * @param level wifiǿ�ȣ�dbm��
	 * @return ��ǰ�㵽wifi��վ�ľ��루cm��
	 * @throws SQLException
	 */
	private static double getDistance(int freq,double level)
		throws SQLException
	{
	    double exp = (27.55 - (20 * Math.log10(freq)) + Math.abs(level) + 10) / 20.0;
	    return Math.pow(10.0, exp);
	}
	
	/**
	 * ������ƶ�ϵ��
	 * @param ��������������غ�ά��
	 * @param ����������ά��
	 * @return ���ƶ�ϵ��
	 */
	private static double getSimWeight(int same,int all)
	{
		return same==0?Double.MAX_VALUE:Math.pow(Math.asin(Math.pow(1-(double)same/all,2)), 0.8)+1;
	}
	
	/**
	 * ��ò���������������������ƶ�����ֵ
	 * @param testVector HashMap��ʽ�Ĳ�������
	 * @param sampleVector HashMap��ʽ�Ĳ�������
	 * @return ���ƶ�����ֵ
	 * @throws SQLException
	 */
	private static double getEval(HashMap<Integer,WIFI_MES> testVector, HashMap<Integer,WIFI_MES> sampleVector)
		throws SQLException
	{
		double result=0;
		int all=0,same=0;

		Iterator<Map.Entry<Integer,WIFI_MES>> iterM=testVector.entrySet().iterator();
		while (iterM.hasNext())
		{
			Map.Entry<Integer,WIFI_MES> entry=iterM.next();
			if (entry.getValue().level>=WIFI_LEVEL_FLOOR) all++;
		}
		
		Set<Integer> intersection=new HashSet<Integer>();
		intersection.addAll(testVector.keySet());
		intersection.retainAll(sampleVector.keySet());
		
		Iterator<Integer> iterI=intersection.iterator();
		
		while (iterI.hasNext())
		{
			int wid=iterI.next();
			if (testVector.get(wid).level>=WIFI_LEVEL_FLOOR) 
			{
				same++;
				result+=(getDistance(sampleVector.get(wid).freq,testVector.get(wid).level)-getDistance(sampleVector.get(wid).freq,sampleVector.get(wid).level))
					   *(getDistance(sampleVector.get(wid).freq,testVector.get(wid).level)-getDistance(sampleVector.get(wid).freq,sampleVector.get(wid).level));
			}
		}
		//System.out.println(same+"    "+all+"    "+getSimWeight(same,all)+"    "+result/same);
		return same==0?Double.MAX_VALUE:result/same*getSimWeight(same,all);
	}
	
	/**
	 * ����λ����
	 * @param vector �ַ�����ʽ��JSONArray��ʾ��ǿ������
	 * @return JSON��ʽ�Ķ�λ���
	 */
	public static JSONObject getLocation(String vector)
	{
		JSONObject result=new JSONObject();
		try
		{
			ArrayList<String> labelArrayList=DBInterface.getAllLabelAL();
			HashMap<Integer,WIFI_MES> testVector=getVectorHMByVectorString(vector);
			//System.out.println(testVector);
			Iterator<String> i=labelArrayList.iterator();
			double maxEval=Double.MAX_VALUE;
			String maxLabel="#NULL#";
			while (i.hasNext())
			{
				String label=i.next();
				double tmp;
				//System.out.println(label+"   "+getEval(testVector,getVectorHMByLabel(label,WIFI_LOWER_BOUND)));
				if ((tmp=getEval(testVector,getVectorHMByLabel(label,WIFI_LOWER_BOUND)))<maxEval)
				{
					maxEval=tmp;
					maxLabel=label;
				}
			}
			
			result.put("flag", 1);
			result.put("label", maxLabel);
			//System.out.println("ANSWER: "+maxLabel);
			return result;
		}
		catch(Exception e)
		{
			System.out.println("ERROR: "+e);
			e.printStackTrace();
			result.put("flag",0);
			return result;
		}
	}
	
	/**
	 * �����������
	 * @param label �ص��ǩ
	 * @param vector �ַ�����ʽ��JSONArray��ʾ��ǿ������
	 * @return JSON��ʽ�Ĳ��������
	 */
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
				int freq=jsonObject.getInt("freq");
				if (!DBInterface.bssidExists(bssid)) DBInterface.createWifi(bssid,freq);
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
	public WIFI_MES(double level,int freq)
	{
		this.level=level;
		this.freq=freq;
	}
	double level;
	int freq;
	
	@Override
	public String toString()
	{
		return "[level:"+level+" freq:"+freq+"]";
	}
}