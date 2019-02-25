package lz.gateway.server.filter.zuul.pre;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
 

import org.springframework.stereotype.Component;

import lz.gateway.server.filter.zuul.BaseFilter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import lombok.extern.slf4j.Slf4j;


//@Slf4j
//@Component 
public class LoginFilter extends BaseFilter {

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		return (checkIsLoginOper(ctx)
		          && ctx.sendZuulResponse());
	}

	private boolean checkIsLoginOper(RequestContext ctx) {
		String path=(String) ctx.get("path");
		if(path.equals("login")&&path.equals("logout")){
			return true;
		}
		return false;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		//如果是登陆，则验证及获取用户信息权限及处理session
		
        ctx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由  
        if(false) {
            ctx.setResponseStatusCode(401);// 返回错误码  
        }else {
        	ctx.setResponseStatusCode(200);// 返回错误码  
        }
		//如果是退出登陆，则删除session
		
		return null;
	}

	@Override
	public String filterType() {
		return PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 10;
	}

}
