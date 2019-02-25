package lz.gateway.server.filter.zuul;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import lz.gateway.server.exception.WebException;
 
import lz.gateway.server.utils.JsonMapUtils;
import lz.gateway.server.utils.XssShieldUtil;
import com.netflix.client.ClientException;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.util.HTTPRequestUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract class BaseFilter extends ZuulFilter {

	public static final String IGNORED_HEADERS = "ignoredHeaders";

	public void setFailureResponse(RequestContext ctx,int code,String jsonMessage) {
		ctx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由  
		ctx.setResponseStatusCode(code);// 返回错误码  
		ctx.setResponseBody(jsonMessage);// 返回错误内容  
		ctx.set("isSuccess", false); 
		ctx.getResponse().setContentType("application/json;charset=UTF-8");
	}

	protected String getCharset(HttpServletResponse response) {
		String charset = response.getCharacterEncoding();
		if (charset == null) {
			charset = "UTF-8";
			response.setCharacterEncoding(charset);
		}
		return charset;
	}


	protected Map paramByJson(HttpServletRequest request) {
		Map<?,?> params=null;
		StringWriter body=this.readRequestBody(request);
		String reqParamStr=(body==null?null:body.toString());
		if(reqParamStr!=null&&reqParamStr.length()>0) {
			try {
				params=JsonMapUtils.toHashMap(reqParamStr);
			}catch(Exception e) {

			}
		}else {
			params=new HashMap<Object,Object>(16);
		}
		return params;
	}

	protected Map paramByForm(HttpServletRequest request) throws IOException{
		Map  params = formToMap(request);
		return params;
	}



	private Map<?, ?> formToMap(HttpServletRequest request) {
		return  request.getParameterMap();
	}
 

	protected StringWriter readRequestBody(HttpServletRequest request) {

		try {
			BufferedReader br=request.getReader();
			StringWriter out=null;
			char[] buffer=new char[512];
			int bytesRead1;
			for(boolean byteRead =true ;(bytesRead1 = br.read(buffer)) !=-1;out.write(buffer,0,bytesRead1)) {
				if(out == null) {
					out=new StringWriter(512);
				}
			}
			return out;
		}catch(IOException ex) {
			log.error("read request body error:"+ex.getMessage(),ex);
			throw new WebException();
		}

	}

	protected void setResponse(ClientHttpResponse resp)
			throws ClientException, IOException {
		RequestContext.getCurrentContext().set("zuulResponse", resp);
		this.setResponse(resp.getRawStatusCode(),
				resp.getBody() == null ? null : resp.getBody(), resp.getHeaders());
	}

	public void setResponse(int status, InputStream entity,
			MultiValueMap<String, String> headers) throws IOException {
		RequestContext context = RequestContext.getCurrentContext();
		context.setResponseStatusCode(status);
		if (entity != null) {
			context.setResponseDataStream(entity);
		}

		boolean isOriginResponseGzipped = false;
		for (Entry<String, List<String>> header : headers.entrySet()) {
			String name = header.getKey();
			for (String value : header.getValue()) {
				if (name.equalsIgnoreCase(HttpHeaders.CONTENT_ENCODING)
						&& HTTPRequestUtils.getInstance().isGzipped(value)) {
					isOriginResponseGzipped = true;
				}
				if (name.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
					context.setOriginContentLength(value);
				}
				if (isIncludedHeader(name)) {
					context.addZuulResponseHeader(name, value);
				}
			}
		}
		context.setResponseGZipped(isOriginResponseGzipped);
	}

	public boolean isIncludedHeader(String headerName) {
		String name = headerName.toLowerCase();
		RequestContext ctx = RequestContext.getCurrentContext();
		if (ctx.containsKey(IGNORED_HEADERS)) {
			Object object = ctx.get(IGNORED_HEADERS);
			if (object instanceof Collection && ((Collection<?>) object).contains(name)) {
				return false;
			}
		}
		switch (name) {
		case "host":
		case "connection":
		case "content-length":
		case "content-encoding":
		case "server":
		case "transfer-encoding":
		case "x-application-context":
			return false;
		default:
			return true;
		}
	}

	public boolean isJsonRequest(HttpServletRequest req) {
		return req.getMethod().equals("POST")&&MediaType.APPLICATION_JSON.includes(this.getMediaType(req));
	}

	public MediaType getMediaType(HttpServletRequest req) {
		String contentType=req.getContentType();
		if(!StringUtils.hasText(contentType)) {
			contentType="*";
		}
		return StringUtils.hasText(contentType)?MediaType.parseMediaType(contentType):null;
	}
}
