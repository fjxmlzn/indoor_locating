package com.indoorlocating.server;

import net.sf.json.*;

import org.omg.CORBA.*;

public class Logic 
{
	public static JSONObject GetLocation(String vector)
	{
		JSONObject result=new JSONObject();
		JSONArray allVector=DBInterface.GetAllVector();
		StringHolder label=new StringHolder();
		if (DBInterface.GetLabelById(allVector.getJSONObject(1).getInt("id"),label))
		{
			result.put("flag",1);
			result.put("label",label.value);
			return result;
		}
		else
		{
			result.put("flag",0);
			return result;
		}

	}
}
