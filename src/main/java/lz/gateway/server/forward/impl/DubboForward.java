package lz.gateway.server.forward.impl;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;

import lz.gateway.server.controller.RestErrorEndpoint;
import lz.gateway.server.forward.IDubboForward;
import lz.skeleton.common.ServiceRequest;
import lz.skeleton.common.ServiceResponse;
import com.netflix.zuul.context.RequestContext;

import lombok.extern.slf4j.Slf4j;

//import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;
//
//import java.io.IOException;
//import java.io.StringWriter;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import org.apache.commons.lang3.tuple.ImmutablePair;
//import org.apache.commons.lang3.tuple.Pair;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.fastjson.JSON;
 

@Slf4j
public class DubboForward implements IDubboForward{

	private final ApplicationConfig applicationConfig;

	private final RegistryConfig registryConfig;

	private Map<String,ReferenceConfig<GenericService>> refCfgMapping;

	public DubboForward(final ApplicationConfig applicationConfig,
			RegistryConfig registryConfig) {
		this.applicationConfig = applicationConfig;
		this.registryConfig = registryConfig;
		this.refCfgMapping=new ConcurrentHashMap<String,ReferenceConfig<GenericService>>();
	}

	@Override
	public ServiceResponse forward(String serviceName,String methodName ,ServiceRequest sreq) {
 
		try {
			ReferenceConfig<GenericService> reference =refCfgMapping.get(serviceName);
			if(reference==null) {
				reference=new ReferenceConfig<GenericService>();
				reference.setApplication(applicationConfig);
				reference.setRegistry(registryConfig);
				reference.setInterface(serviceName);
				reference.setGeneric(true);
				reference.setCheck(false);
				reference.setVersion("dev");
				ReferenceConfigCache cache = ReferenceConfigCache.getCache();
				GenericService genericService = cache.get(reference);
				refCfgMapping.put(serviceName, reference);
			}
			Object response =reference.get().$invoke(methodName, new String[] { "lz.skeleton.common.ServiceRequest" }, new Object[] { sreq});
			 return JSON.parseObject(JSON.toJSONString(response), ServiceResponse.class);
 
		} catch (Throwable e) {
			
			log.error(String.format(
					"service definition is wrong,please check the proto file you update,service is %s, method is %s",
					serviceName, methodName), e);
			return new ServiceResponse("-1","服务不存在"); 
		}
	}

}
