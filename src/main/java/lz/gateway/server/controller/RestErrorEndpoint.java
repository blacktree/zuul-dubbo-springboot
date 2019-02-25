package lz.gateway.server.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import lz.skeleton.common.ServiceResponse;
 
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class RestErrorEndpoint implements ErrorController {


	@Autowired
    private ErrorAttributes errorAttributes;
	
    private static final String PATH = "/error";
    
	@Override
	public String getErrorPath() {
		// TODO Auto-generated method stub
		return PATH;
	}

	@RequestMapping(value = PATH,  produces = {MediaType.APPLICATION_JSON_VALUE})
	ServiceResponse error(HttpServletRequest request, HttpServletResponse response) {
		ServletWebRequest requestAttributes = new ServletWebRequest(request);
		 Map<String, Object> map =errorAttributes.getErrorAttributes(requestAttributes,true);
	 
		 log.error(map.get("trace").toString());
		return new ServiceResponse("网关层有未捕获异常","500","服务出现问题");
    }
 
 
 
 

}
