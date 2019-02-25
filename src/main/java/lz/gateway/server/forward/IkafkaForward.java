package lz.gateway.server.forward;

import lz.skeleton.common.ServiceRequest;
import lz.skeleton.common.ServiceResponse;

public interface IkafkaForward {

	public ServiceResponse forward(String topic,String serviceName,String methodName,ServiceRequest sreq);
	
	public ServiceResponse forward(String topic,ServiceRequest sreq);
	
}
