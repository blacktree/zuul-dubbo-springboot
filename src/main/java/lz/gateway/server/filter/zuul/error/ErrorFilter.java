package lz.gateway.server.filter.zuul.error;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.ERROR_TYPE;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import lz.gateway.server.filter.zuul.pre.PermCheckFilter;
import lz.skeleton.common.ServiceResponse;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
 
@Slf4j
@Component
public class ErrorFilter extends ZuulFilter {
 
    @Override
    public String filterType() {
        //异常过滤器
        return ERROR_TYPE;
    }
 
    @Override
    public int filterOrder() {
        //优先级，数字越大，优先级越低
        return 0;
    }
 
    @Override
    public boolean shouldFilter() {
        //是否执行该过滤器，true代表需要过滤
        return true;
    }
 
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
 
        log.info("进入异常过滤器");
        Throwable throwable = ctx.getThrowable();
  
		ctx.getResponse().setContentType("application/json;charset=UTF-8");
        ctx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由  
        ctx.setResponseStatusCode(401);// 返回错误码  
        ctx.set("responseCode", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ctx.set("result", throwable.getCause());
        ctx.set("responseMessage", throwable.getMessage());
        return null;
 
    }
 

}
