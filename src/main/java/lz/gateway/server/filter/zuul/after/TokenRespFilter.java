package lz.gateway.server.filter.zuul.after;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import lz.gateway.server.constant.Constant;
import lz.gateway.server.session.Session;
import lz.gateway.server.session.SessionManager;
import lz.gateway.server.utils.CookieUtils;
import lz.skeleton.common.ServiceResponse;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component 
public class TokenRespFilter  extends ZuulFilter {

	@Autowired
	private SessionManager sessionManager;
	
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		return  ctx.sendZuulResponse();
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest req =   ctx.getRequest();
		HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
		 Session session=sessionManager.getSession(CookieUtils.getCookieValue(req,Constant.KEY_SESSION));
		//判断下1步是否要做防重
		if("1".equals(ctx.get(Constant.KEY_TOKEN).toString())) {
          
			 // 每次重新生成TOKEN，无需判断原来是否已有
            String tokenKey = TokenUtil.getRandomString();

            // 将TOKEN放到redis缓存中
            session.setAttribute(Constant.KEY_TOKEN, tokenKey);
            ServiceResponse resp=(ServiceResponse) ctx.get(Constant.SRESP);
            resp.setToken(tokenKey);
		}
		return null;
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return POST_TYPE;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

}
