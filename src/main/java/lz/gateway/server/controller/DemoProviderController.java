package lz.gateway.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;

@RestController
public class DemoProviderController {

    @RequestMapping("/unexport")
    public String unexport(@RequestParam String name) {
    	
    	ApplicationConfig app=new ApplicationConfig();
    	app.setName("dubbo-provider-demo");
    	
    	RegistryConfig registry=new RegistryConfig();
    	registry.setAddress("zookeeper://127.0.0.1:2181");
    	ReferenceConfig<GenericService> ref=new ReferenceConfig<GenericService>();
    	ref.setApplication(app);
    	ref.setRegistry(registry);
    	ref.setGeneric(true);
    	ref.setInterface(name);
    	ReferenceConfigCache cache=ReferenceConfigCache.getCache();
    	
    	
    	ProviderConfig prd=new ProviderConfig();
    	prd.setApplication(app);
    	prd.setRegistry(registry);
    	
    	
    	ServiceConfig<GenericService> scfg=new ServiceConfig<GenericService>();
    	
    	scfg.setInterface(name);
    	
    	scfg.setRef(cache.get(ref));
    	scfg.setProvider(prd);
    	scfg.unexport();
        return "finish";
    }
    
    
    @RequestMapping("/export")
    public String export(@RequestParam String name) {
    	
    	ApplicationConfig app=new ApplicationConfig();
    	app.setName("dubbo-provider-demo");
    	
    	RegistryConfig registry=new RegistryConfig();
    	registry.setAddress("zookeeper://127.0.0.1:2181");
    	ReferenceConfig<GenericService> ref=new ReferenceConfig<GenericService>();
    	ref.setApplication(app);
    	ref.setRegistry(registry);
    	ref.setGeneric(true);
    	ref.setInterface(name);
    	ReferenceConfigCache cache=ReferenceConfigCache.getCache();
    	
    	
    	ProviderConfig prd=new ProviderConfig();
    	prd.setApplication(app);
    	prd.setRegistry(registry);
    	
    	
    	ServiceConfig<GenericService> scfg=new ServiceConfig<GenericService>();
    	
    	scfg.setInterface(name);
    	
    	scfg.setRef(cache.get(ref));
    	scfg.setProvider(prd);
 
    	scfg.export();
        return "finish";
    }
}
