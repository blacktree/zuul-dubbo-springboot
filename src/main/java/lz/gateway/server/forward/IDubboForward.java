package lz.gateway.server.forward;

import org.springframework.http.client.ClientHttpResponse;

import lz.skeleton.common.ServiceRequest;
import lz.skeleton.common.ServiceResponse;
import com.netflix.zuul.context.RequestContext;

public interface IDubboForward {

//	public ServiceResponse forward(RequestContext ctx);

	public ServiceResponse forward(String serviceName,String methodName,ServiceRequest sreq);
}
