package lz.gateway.server.filter.zuul.pre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
 
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import com.alibaba.fastjson.JSON;
import lz.gateway.server.filter.zuul.route.CustomRouteLocator;
import lz.gateway.server.filter.zuul.route.CustomRouteLocator.CustomRouteVO;
import lz.skeleton.common.ServiceResponse;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component 
public class RateLImiterFilter extends ZuulFilter {

	@Autowired
	private CustomRouteLocator routeLocator;
	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		return  ctx.sendZuulResponse();
	}

	@Override
	public Object run() throws ZuulException {
		
		RequestContext ctx = RequestContext.getCurrentContext();
		CustomRouteVO croute=routeLocator.getCustomRout(ctx.get("path").toString());
		
		if(!croute.tryAcquire()) { 
			ctx.getResponse().setContentType("application/json;charset=UTF-8");
            ctx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由  
            ctx.setResponseStatusCode(401);// 返回错误码  
            ctx.setResponseBody(JSON.toJSONString(new ServiceResponse("-1","服务繁忙，稍后再试")));// 返回错误内容  
            ctx.set("isSuccess", false);  
		}
		
		
		return null;
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 5;
	}

}
