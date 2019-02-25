package lz.gateway.server.filter.zuul.route;

//import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.cloud.netflix.zuul.util.RequestUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.StringUtils;

import com.google.common.util.concurrent.RateLimiter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class CustomRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator{

	public final static Logger logger = LoggerFactory.getLogger(CustomRouteLocator.class);
	private String dispatcherServletPath = "/";
	private JdbcTemplate jdbcTemplate;

	private String zuulServletPath;
	private ZuulProperties properties;
	private LinkedHashMap<String, CustomRouteVO> customRoutesMap;


	public CustomRouteLocator(String servletPath, ZuulProperties properties) {
		super(servletPath, properties);
		this.properties = properties;
		logger.info("servletPath:{}",servletPath);
		this.zuulServletPath = properties.getServletPath();
		customRoutesMap = new LinkedHashMap<String, CustomRouteVO>();
	}

	@Override
	public void refresh() {
		doRefresh();
	}

	@Override
	protected Map<String, ZuulRoute> locateRoutes() {
		LinkedHashMap<String, ZuulRoute> routesMap = new LinkedHashMap<String, ZuulRoute>();
		//从application.properties中加载路由信息
		routesMap.putAll(super.locateRoutes());
		//从db中加载路由信息
		locateRoutesFromDB();
		//优化一下配置
		LinkedHashMap<String, ZuulRoute> values = new LinkedHashMap<>();
		for (Map.Entry<String, CustomRouteVO> entry : customRoutesMap.entrySet()) {
			String path = entry.getKey();
			// Prepend with slash if not already present.
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			values.put(path,new ZuulRoute(path));
		}
		return values;
	}

	private void locateRoutesFromDB(){
		customRoutesMap.clear();
		List<CustomRouteVO> results =  jdbcTemplate.query("select * from gateway_api_route where enable = 1 ",new BeanPropertyRowMapper<>(CustomRouteVO.class));

		for (CustomRouteVO result : results) {
			if(org.apache.commons.lang3.StringUtils.isBlank(result.getApiPath()) || org.apache.commons.lang3.StringUtils.isBlank(result.getType()) ){
				continue;
			}
			if(result.getRateLimit()>0) {
				RateLimiter rateLimiter =RateLimiter.create(result.getRateLimit());
				result.setRateLimiter(rateLimiter);
			}
			customRoutesMap.put(result.getApiPath(), result);
		}
	}


	public LinkedHashMap<String, CustomRouteVO> getCustomRoutesMap() {
		return customRoutesMap;
	}

	public CustomRouteVO getCustomRout(String path) {
		return customRoutesMap.get(path);
	}

	@Override
	public Route getMatchingRoute(final String path) {

		String servicePath=getSevicePath(path);
		Route r= getSimpleMatchingRoute(servicePath);

		if(r==null) {
			r=new Route("dubbo", path, path, path, null, null);
		}
		return r;

	}

	private String getSevicePath(final String path) {
		return path.substring(0, path.lastIndexOf("/"));
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public static class CustomRouteVO {

		private String apiPath;

		private String type;

		private String topic;

		private String service;

		private String method;

		private boolean isMock;

		private Boolean permCheck;

		private String mockResp;

		private int timeOut;

		private int rateLimit;
		
		private int token;

		private RateLimiter rateLimiter;

		public String getApiPath() {
			return apiPath;
		}

		public void setApiPath(String apiPath) {
			this.apiPath = apiPath;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getService() {
			return service;
		}

		public void setService(String service) {
			this.service = service;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public boolean isMock() {
			return isMock;
		}

		public void setMock(boolean isMock) {
			this.isMock = isMock;
		}



		public String getMockResp() {
			return mockResp;
		}

		public void setMockResp(String mockResp) {
			this.mockResp = mockResp;
		}

		public int getRateLimit() {
			return rateLimit;
		}

		public void setRateLimit(int rateLimit) {
			this.rateLimit = rateLimit;
		}

		public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}

		public Boolean getPermCheck() {
			return permCheck;
		}

		public void setPermCheck(Boolean permCheck) {
			this.permCheck = permCheck;
		}

		public RateLimiter getRateLimiter() {
			return rateLimiter;
		}

		public void setRateLimiter(RateLimiter rateLimiter) {
			this.rateLimiter = rateLimiter;
		}

		public boolean tryAcquire() {
			if(rateLimit<=0)
				return true;
			return rateLimiter.tryAcquire(1, timeOut, TimeUnit.MICROSECONDS);
		}

		public int getToken() {
			return token;
		}

		public void setToken(int token) {
			this.token = token;
		}

	}

}
