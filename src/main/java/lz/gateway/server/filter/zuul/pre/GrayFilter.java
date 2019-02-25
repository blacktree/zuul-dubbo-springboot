package lz.gateway.server.filter.zuul.pre;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.rpc.RpcContext;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;


@Component 
public class GrayFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		// TODO Auto-generated method stub
		
		RpcContext.getContext().set("gray", "true");
		
		return null;
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return "pre";
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

}
