package  lz.gateway.server.utils;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;

public class JsonMapUtils {

	@SuppressWarnings("unchecked")
	public static Map toMap(String json) {
		return JSON.parseObject(json);
	}

	@SuppressWarnings("unchecked")
	public static Map toHashMap(String json) {
		JSONObject result = JSON.parseObject(json);
		return _toHashMap(result);
	}

	@SuppressWarnings("unchecked")
	private static Map _toHashMap(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;
		HashMap datas = new HashMap(jsonObject.size());

		Iterator i = jsonObject.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			Object v = entry.getValue();
			if (v != null && v instanceof JSONAware) {
				if (v instanceof JSONObject) {
					v = _toHashMap((JSONObject) v);
				} else if (v instanceof JSONArray) {
					v = _toObjectArray((JSONArray) v);
				}
			}
			datas.put(entry.getKey(), v);
		}

		return datas;
	}

	private static Object _toObjectArray(JSONArray json) {
		Object arr = Array.newInstance(Object.class, json.size());

		for (int i = 0; i < json.size(); i++) {
			Object v = json.get(i);
			if (v != null && v instanceof JSONAware) {
				if (v instanceof JSONObject) {
					v = _toHashMap((JSONObject) v);
				} else if (v instanceof JSONArray) {
					v = _toObjectArray((JSONArray) v);
				}
			}
			Array.set(arr, i, v);
		}
		return arr;

	}

}
