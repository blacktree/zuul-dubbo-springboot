package lz.gateway.server.security.config;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

 
@Component
@ConfigurationProperties(prefix = "gateway.security.path")
public class SecurityPathProperties {
	
	private static final Pattern SERVICE_PATTERN = Pattern.compile("(?<name>^.+)-(?<version>v.+$)");
	
	private static final String ROUTE_PATTERN = "${version}/${name}";
	
	@Autowired  
	private Environment env;
	
	/**
	 * 登录路径
	 */
	private String loginPath;

	/**
	 * 需认证的path
	 */
	private String authPathPattern;

	/**
	 * 管理员路径
	 */
	private String managePathPattern;

	/**
	 * token刷新路径
	 */
	private String refleshTokenPath;
	
	/**
	 * 白名单
	 */
	private Map<String,WhiteService> whiteServices = new LinkedHashMap<>();
	
 
	@PostConstruct
	public void init() {
		for (Entry<String, WhiteService> entry : this.whiteServices.entrySet()) {
			WhiteService value = entry.getValue();
			if (!StringUtils.hasText(value.getServiceId())) {
				value.serviceId = entry.getKey();
			}
			if (!StringUtils.hasText(value.getId())) {
				value.id = entry.getKey();
			}
			
			if (CollectionUtils.isNotEmpty(value.getPathPattern())) {
				String zuulPrefix = StringUtils.isEmpty(env.getProperty("zuul.prefix")) ? "" : env.getProperty("zuul.prefix");
				Matcher matcher = SERVICE_PATTERN.matcher(value.getServiceId());
				String route = matcher.replaceFirst(ROUTE_PATTERN);
				List<String> newPatterns = new LinkedList<>();
				
				for(String pattern: value.getPathPattern()) {
					newPatterns.add(zuulPrefix + "/" + route + pattern);
				}
				value.setPathPattern(newPatterns);
			}
		}
	}
	
	public static class WhiteService {
		private List<String> pathPattern;
		private String id;
		private String serviceId;
		public List<String> getPathPattern() {
			return pathPattern;
		}
		public void setPathPattern(List<String> pathPattern) {
			this.pathPattern = pathPattern;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getServiceId() {
			return serviceId;
		}
		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}
	}

	public String getLoginPath() {
		return loginPath;
	}

	public void setLoginPath(String loginPath) {
		this.loginPath = loginPath;
	}

	public String getAuthPathPattern() {
		return authPathPattern;
	}

	public void setAuthPathPattern(String authPathPattern) {
		this.authPathPattern = authPathPattern;
	}

	public String getManagePathPattern() {
		return managePathPattern;
	}

	public void setManagePathPattern(String managePathPattern) {
		this.managePathPattern = managePathPattern;
	}

	public String getRefleshTokenPath() {
		return refleshTokenPath;
	}

	public void setRefleshTokenPath(String refleshTokenPath) {
		this.refleshTokenPath = refleshTokenPath;
	}

	public Map<String, WhiteService> getWhiteServices() {
		return whiteServices;
	}

	public void setWhiteServices(Map<String, WhiteService> whiteServices) {
		this.whiteServices = whiteServices;
	}

}
