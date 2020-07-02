package com.moe.x4jdm.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import java.util.Map;
import java.util.Set;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.NativeString;
import org.mozilla.javascript.ConsString;

public class JavascriptUtil
{
	public static JSONObject toJsonObject(NativeObject nativeObject)
    {
        JSONObject object = new JSONObject();
        Set<Map.Entry<Object, Object>> entrySet = nativeObject.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet)
        {
            try
            {
				if (entry.getValue() instanceof ConsString)
				{
					object.put(entry.getKey().toString(),entry.getValue().toString());
				}else
                if (entry.getValue() instanceof String)
                {
                    object.put(entry.getKey().toString(), entry.getValue().toString());
                }
                else if (entry.getValue() instanceof NativeArray)
                {
                    object.put(entry.getKey().toString(), toJsonArray((NativeArray) entry.getValue()));
                }
                else if (entry.getValue() instanceof NativeObject)
                {
                    object.put(entry.getKey().toString(), toJsonObject((NativeObject) entry.getValue()));
                }else{
					object.put(entry.getKey().toString(), entry.getValue().toString());
				}
            }
            catch (JSONException e)
            {
                try
                {
                    object.put(entry.getKey().toString(), entry.getValue().toString());
                }
                catch (JSONException ignored)
                {
                }
            }
        }


        return object;
    }

    public static JSONArray toJsonArray(NativeArray nativeArray)
    {
        JSONArray array = new JSONArray();

        for (Object o : nativeArray)
        {
            if (o instanceof NativeObject)
            {
                array.add(toJsonObject((NativeObject) o));
            }
            else if (o instanceof NativeArray)
            {
                array.add(toJsonArray((NativeArray) o));
            }
            else
            {
                try
                {
                    array.add(JSONObject.parse(o.toString()));
                }
                catch (JSONException e)
                {
                    array.add(o.toString());
                }
            }
        }

        return array;
    }
}
