package lz.gateway.server.filter.zuul.pre;

import javax.servlet.http.HttpServletRequest;

import org.jboss.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UrlPathHelper;

import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;

import lz.gateway.server.constant.Constant;
import lz.gateway.server.filter.zuul.BaseFilter;
import lz.gateway.server.filter.zuul.route.CustomRouteLocator;
import lz.gateway.server.filter.zuul.route.CustomRouteLocator.CustomRouteVO;
import lz.gateway.server.utils.CookieUtils;
import lz.skeleton.common.ServiceResponse;
import com.netflix.client.ClientException;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.util.HTTPRequestUtils;

//import io.netty.util.internal.StringUtil;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.REQUEST_URI_KEY;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

@Slf4j
@Component 
public class PermCheckFilter extends BaseFilter {

	@Autowired
	private CustomRouteLocator routeLocator;
	
	private final UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	/**
	 * Zuul context key for a collection of ignored headers for the current request.
	 * Pre-filters can set this up as a set of lowercase strings.
	 */
	public static final String IGNORED_HEADERS = "ignoredHeaders";
	
	@Override
	public boolean shouldFilter() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();  
        HttpServletRequest request = ctx.getRequest();  
   
	 
        /*
            ctx.setSendZuulResponse(true);// 对该请求进行路由  
            ctx.setResponseStatusCode(200);  
            ctx.set("isSuccess", true);// 设值，让下一个Filter看到上一个Filter的状态  
            return null;  
        }
         * 
         * 
        ctx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由  
        ctx.setResponseStatusCode(401);// 返回错误码  
        ctx.setResponseBody("{\"result\":\"名字  is not correct!\"}");// 返回错误内容  
        ctx.set("isSuccess", false); 
        ctx.getResponse().setContentType("application/json;charset=UTF-8");
        return null;  
       */
        
//        ctx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由  
//        ctx.setResponseStatusCode(401);// 返回错误码  
//        ctx.setResponseBody("{\"result\":\"名字  is not correct!\"}");// 返回错误内容  
//        ctx.set("isSuccess", false); 
//        ctx.getResponse().setContentType("application/json;charset=UTF-8");
//        return null;  
      
		ClientHttpResponse response=null;
		try {
		 
			String path=urlPathHelper.getPathWithinApplication(ctx.getRequest());
			ctx.set("path",path);
			
//			String uri=(String) ctx.get(REQUEST_URI_KEY);

			CustomRouteVO croute=routeLocator.getCustomRout(path);
			ctx.set(Constant.KEY_TOKEN,croute.getToken());
			if(croute.getPermCheck()!=null&&croute.getPermCheck()==true) {
				checkSessionAndPerm(response, ctx, croute);
			}
			 
	        return null;
		 
		}
		catch (Exception ex) {
			throw new ZuulRuntimeException(ex);
		}
 
    /*
        String username = request.getParameter("username");// 获取请求的参数  
        if(null != username && username.equals("chhliu")) {// 如果请求的参数不为空，且值为chhliu时，则通过  
            ctx.setSendZuulResponse(true);// 对该请求进行路由  
            ctx.setResponseStatusCode(200);  
            ctx.set("isSuccess", true);// 设值，让下一个Filter看到上一个Filter的状态  
            return null;  
        }else{  
            ctx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由  
            ctx.setResponseStatusCode(401);// 返回错误码  
            ctx.setResponseBody(JSON.toJSONString(new ServiceResponse("-1","需要登陆才能访问")));// 返回错误内容  
            ctx.set("isSuccess", false);  
            return null;  
        }  
      */  
       
	}

	private void checkSessionAndPerm(ClientHttpResponse response, RequestContext ctx, CustomRouteVO croute) {
		boolean sessionExist=checkSession(ctx.getRequest());
		if(sessionExist) {
			checkPerm();
		}else {
            super.setFailureResponse(ctx, 401, JSON.toJSONString(new ServiceResponse("需要登陆才能访问","-1")));
		}
	}
	
	
	private void checkPerm() {
		// TODO Auto-generated method stub
		
	}

	private boolean  checkSession(HttpServletRequest request) {
		String sessionId=CookieUtils.getCookieValue(request,Constant.KEY_SESSION);
		if(StringUtils.isNullOrEmpty(sessionId))
			return false;
		else {
			//check redis session
			return true;
		}
	}

	/*
	private ClientHttpResponse checkServiceNeedSession(ClientHttpResponse response, RequestContext ctx,
			CustomRouteVO croute) {
			boolean sessionExist=checkSession(ctx.getRequest());
			if(!sessionExist)
				response=new ClientHttpResponse() {

				@Override
				public HttpStatus getStatusCode() throws IOException {
					return HttpStatus.OK;
				}

				@Override
				public int getRawStatusCode() throws IOException {
					return 200;
				}

				@Override
				public String getStatusText() throws IOException {
					return HttpStatus.UNAUTHORIZED.getReasonPhrase();
				}

				@Override
				public void close() {

				}

				@Override
				public InputStream getBody() throws IOException {
					ServiceResponse badresp=new ServiceResponse("-1","需要登陆才能访问");
					return new ByteArrayInputStream(JSONObject.toJSONString(badresp).getBytes());
				}

				@Override
				public HttpHeaders getHeaders() {
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
					return headers;
				}
			};
			
			//检查角色是否符合
			
			
			
			
		return response;
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
	*/
	
	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 1;
	}

}
