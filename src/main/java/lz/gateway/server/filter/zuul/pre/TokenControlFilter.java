package lz.gateway.server.filter.zuul.pre;

import lz.gateway.server.constant.Constant;
import lz.gateway.server.session.Session;
import lz.gateway.server.session.SessionManager;
import lz.gateway.server.utils.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component 
public class TokenControlFilter  extends ZuulFilter {

	@Autowired
	private SessionManager sessionManager;
	
	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		return  ctx.sendZuulResponse();
	}
	

	@Override
	public Object run() throws ZuulException {
		// TODO Auto-generated method stub
		RequestContext ctx = RequestContext.getCurrentContext();

		if("1".equals(ctx.get(Constant.KEY_TOKEN).toString())) {

			//判断是否要做防重

		}else if ("2".equals(ctx.get(Constant.KEY_TOKEN).toString())) {
			// 获取当前请求的TOKEN
			String tokenKey=(String) ((Map)ctx.get(Constant.PARAMS)).get(Constant.KEY_TOKEN);
			 if (tokenKey==null||tokenKey.length()<1)  {
				 //报错
			 }
			 
				HttpServletRequest req =   ctx.getRequest();
//				HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
				 Session session=sessionManager.getSession(CookieUtils.getCookieValue(req,Constant.KEY_SESSION));
	            // 根据当前TOKEN，从缓存服务器中获取TOKEN的信息，并设置TOKEN为已提交属性
	            Object tokenInServer =session.getAttribute(Constant.KEY_TOKEN);
		   		 if (tokenInServer==null||((String)tokenInServer).length()<1)  {
					 //报错 没有
				 }
		   		 
	              if (tokenInServer.equals(tokenKey)) {
//	            	  说明是正常的
		            }
		            else {
                   //	 说明是重复提交，不处理
		            }
	              
			   // 先尝试锁住属性，获取session中的token值，获取完成之后，直接删除token属性，然后释放锁(只在成功获取锁的地方释放锁，未成功获取锁的线程直接不管，正常线程获取锁即可)
	            if (session.lock(Constant.KEY_TOKEN)) {
//	                tokenInServer = session.getAttribute(Constant.KEY_TOKEN, String.class);
	                session.removeAttribute(Constant.KEY_TOKEN);
	                session.unlock(Constant.KEY_TOKEN);
	            }
 
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
		return 15;
	}

}
