package lz.gateway.server.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.web.ZuulHandlerMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lz.skeleton.common.ServiceResponse;

@RestController
public class RefreshEndpoint {
 
	@Autowired
	ZuulHandlerMapping zuulHandlerMapping;  

	@Autowired  
	RefreshableRouteLocator customRouteLocator;


	@GetMapping("/refreshRoute")  
	public String refresh() {  
		customRouteLocator.refresh();
		return "refresh success";
	}  


	@RequestMapping("/watchRoute")  
	public List<String> watchNowRoute() {  
		Map<String, Object> handlerMap = zuulHandlerMapping.getHandlerMap();  
		List<String> ls=new ArrayList<String> ();
		for (Iterator i = handlerMap.keySet().iterator(); i.hasNext();) {  
			ls.add((String) i.next());  
		}  

		return ls;  
	}  
 
}
