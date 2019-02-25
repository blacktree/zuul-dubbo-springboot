package lz.gateway.server.filter.zuul.pre;


import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;


import lz.gateway.server.constant.Constant;
import lz.gateway.server.exception.WebException;
import lz.gateway.server.filter.zuul.BaseFilter;
import lz.gateway.server.utils.XssShieldUtil;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component 
public class XSSFilter extends BaseFilter {
 
	@Override
	public boolean shouldFilter() {
		 return true;
	}

	@Override
	public Object run() throws ZuulException {

		RequestContext ctx = RequestContext.getCurrentContext(); 
         try {
			forward(ctx);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         return null;
 
	}
 
	private void forward(RequestContext ctx) throws IOException {

		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
		Map params=null;
		if(isJsonRequest(request)) {
			params=paramByJson(request);
		}else {
			params=paramByForm(request);
		}
        ctx.set(Constant.PARAMS,strixXSStoMap(params));
 
	}

	protected Map strixXSStoMap(Map parameters) {
		Map map = new HashMap(16);
		Iterator<Map.Entry<Object, Object>> i = parameters.entrySet()
				.iterator();
		while (i.hasNext()) {
			Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) i
					.next();
			String k = XssShieldUtil.stripXss(entry.getKey().toString());
			if(entry.getValue() instanceof String) {
				 map.put(k, XssShieldUtil.stripXss((String) entry.getValue()));
			}else {
			Object o[] = (Object[]) entry.getValue();
			String v[]=new String[o.length];
			if(v != null){
				for (int j = 0; j < v.length; j++) {
					v[j] = XssShieldUtil.stripXss((String) o[j]);
				}
			}
		 
			try {
				pmap(map, k, v);
			} catch (Throwable th) {
				throw new WebException("Paramter<" + k + "> format error: "
						+ th.getMessage());
			}
			}
		}
		return map;
	}
 

	private void pmap(Map map, String name, String[] value) {
		int idx = name.indexOf('.');
		String cfn = (idx == -1 ? name : name.substring(0, idx));

		int t1 = cfn.indexOf('[');
		if (t1 != -1) {
			int t2 = cfn.indexOf(']', t1);
			int arrIdx = Integer.parseInt(cfn.substring(t1 + 1, t2));
			cfn = cfn.substring(0, t1);

			List list = (List) map.get(cfn);
			if (list == null) {
				list = new ArrayList(8);
				map.put(cfn, list);
			}
			if (list.size() <= arrIdx) {
				for (int i = list.size(); i <= arrIdx; i++) {
					list.add(i, null);
				}
			}

			if (idx == -1) {
				list.set(arrIdx, (value.length > 1 ? value : value[0]));
			} else {
				Map v = (Map) list.get(arrIdx);
				if (v == null) {
					v = new HashMap(8);
					list.set(arrIdx, v);
				}
				pmap(v, name.substring(idx + 1), value);
			}

		} else {
			if (idx == -1) {
				map.put(name, (value.length > 1 ? value : value[0]));
			} else {
				Map v = (Map) map.get(cfn);
				if (v == null) {
					v = new HashMap(8);
					map.put(cfn, v);
				}
				pmap(v, name.substring(idx + 1), value);
			}
		}
	}
 
	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 0;
	}
 


}
