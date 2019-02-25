package lz.gateway.server.filter.zuul.route;


import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import lz.gateway.server.constant.Constant;
import lz.gateway.server.filter.zuul.BaseFilter;
import lz.gateway.server.filter.zuul.route.CustomRouteLocator.CustomRouteVO;
import lz.gateway.server.forward.IDubboForward;
import lz.gateway.server.forward.IkafkaForward;
import lz.skeleton.common.ServiceRequest;
import lz.skeleton.common.ServiceResponse;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component 
public class ExecFilter extends BaseFilter {
 

	@Autowired
	@Qualifier("dubboForward")
	private IDubboForward dubboForward;

	@Autowired
	@Qualifier("kafkaForward")
	private IkafkaForward kafkaForward;

	@Autowired
	private CustomRouteLocator routeLocator;

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		return (ctx.get(SERVICE_ID_KEY) != null
				&& ctx.sendZuulResponse());
	}

	@Override
	public Object run() throws ZuulException {

		RequestContext ctx = RequestContext.getCurrentContext(); 
		try {

			CustomRouteVO croute=routeLocator.getCustomRout(ctx.get(SERVICE_ID_KEY).toString());
			forward(ctx,croute);
			
		}
		catch (Exception ex) {
			throw new ZuulRuntimeException(ex);
		}
		return null;
	}

 
	private ClientHttpResponse forward(RequestContext ctx, CustomRouteVO croute) throws IOException {

		ServiceRequest sreq=new ServiceRequest((Map) ctx.get(Constant.PARAMS));


		ServiceResponse resp=null;

		if(croute.getType().equals("dubbo")) {
			resp=dubboForward.forward(croute.getService(),croute.getMethod(),sreq);
		}
		if(croute.getType().equals("kafka")) {
			if("asnyc".equals(croute.getTopic())) {
				resp=kafkaForward.forward(croute.getTopic(), sreq);//(croute.getService(),croute.getMethod(),sreq);
			}else {
				resp=kafkaForward.forward(croute.getTopic(),croute.getService(),croute.getMethod(),sreq);
			}
		}

        ctx.put(Constant.SRESP, resp);
		return null;

	}



	protected String getCharset(HttpServletResponse response) {
		String charset = response.getCharacterEncoding();
		if (charset == null) {
			charset = "UTF-8";
			response.setCharacterEncoding(charset);
		}
		return charset;
	}


	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return ROUTE_TYPE;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 10;
	}




}
