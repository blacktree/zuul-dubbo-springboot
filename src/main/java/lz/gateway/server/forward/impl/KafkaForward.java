package lz.gateway.server.forward.impl;

import org.springframework.stereotype.Component;

import lz.gateway.server.forward.IkafkaForward;
import lz.skeleton.common.ServiceRequest;
import lz.skeleton.common.ServiceResponse;

@Component
public class KafkaForward implements IkafkaForward {

	@Override
	public ServiceResponse forward(String topic, String serviceName, String methodName, ServiceRequest sreq) {
		
		ServiceResponse resp =new ServiceResponse();
		
		return resp;
	}

	@Override
	public ServiceResponse forward(String topic, ServiceRequest sreq) {
		// TODO Auto-generated method stub
		ServiceResponse resp =new ServiceResponse();
		return resp;
	}

}
