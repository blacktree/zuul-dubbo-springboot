package lz.gateway.server.security.config;


public class WebSecurityConfig{
	
}

/*
 * 
 * import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@Configuration
//@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private SecurityCommonProperties securityProperties;
	@Autowired
	private SecurityPathProperties securityPathProperties;



	//定义认证用户信息获取来源，密码校验规则等 
	@Override  
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {  
		//inMemoryAuthentication 从内存中获取  
		auth.inMemoryAuthentication().withUser("chengli").password("123456").roles("USER");  

		//jdbcAuthentication从数据库中获取，但是默认是以security提供的表结构  
		//usersByUsernameQuery 指定查询用户SQL  
		//authoritiesByUsernameQuery 指定查询权限SQL  
		//auth.jdbcAuthentication().dataSource(dataSource).usersByUsernameQuery(query).authoritiesByUsernameQuery(query);  

		//注入userDetailsService，需要实现userDetailsService接口  
		//auth.userDetailsService(userDetailsService);  
	}  

	//定义安全策略
	@Override  
	protected void configure(HttpSecurity http) throws Exception {  
 
		if(securityProperties.getEnabled()) {
			http.csrf().disable()   //https://blog.csdn.net/sinat_28454173/article/details/52251004      后续修改回来加token  http://www.cnblogs.com/yjmyzz/p/customize-CsrfFilter-to-ignore-certain-post-http-request.html
			.authorizeRequests()//配置安全策略  
			.antMatchers("/**","/gateway/**").permitAll()//定义/请求不需要验证  
			.anyRequest().authenticated()//其余的所有请求都需要验证  
			.and()  
			.logout()  
			.permitAll()//定义logout不需要验证  
			.and()  
			.formLogin();//使用form表单登录  
		}  
	 
	}
}
*/