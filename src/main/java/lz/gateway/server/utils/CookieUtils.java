package  lz.gateway.server.utils;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/***
 * Cookie操作工具类
 *
 */
public class CookieUtils {
	
 
	
	/**
	 * 清除Cookie，即将Cookie maxAge置为0
	 * @param cookieName cookie名
	 * @param domain cookie域名限定,为null则不进行限定
	 * @param path 请求路径限定，为null则不进行限定
	 * @param cookieValue cookie值
	 * @return
	 */
	public static Cookie deleteCookie(String cookieName,String domain,String path,String cookieValue){
		Cookie c=createCookie(cookieName,cookieValue,domain,path);
		c.setMaxAge(0);
		return c;
	}
	
	/***
	 * 新建会话级的Cookie
	 * @param cookieName cookie名
	 * @param cookieValue cookie值
	 * @param domain cookie域名限定,为null则不进行限定
	 * @param path 请求路径限定，为null则不进行限定
	 * @return
	 */
	public static  Cookie createCookie(String cookieName,String cookieValue,String domain,String path){
		Cookie c=new Cookie(cookieName,cookieValue);
		//添加域名限定
		if(domain!=null && (domain=domain.trim()).length()>0){
			//c.setDomain(domain);
		}
		//添加路径限定
		if(path!=null && (path=path.trim()).length()>0){
			c.setPath(path);
		}else{
			//c.setPath("/"); 
		    //fortify : 可通过相同域中的其他应用程序访问路径范围过大的 cookie。
		}
		//cookie有效作用域为会话级(内存)
		c.setMaxAge(-1);
		c.setSecure(false);
	 
		return c;
	}
	
	
	
	
	public static String getCookieValue(HttpServletRequest request,String cookieName){
		String cookieStr=request.getHeader("Cookie");
		
		if(cookieStr==null)return null;
		//
		int idx=cookieStr.indexOf(cookieName);
		if(idx==-1)return null;
		idx=cookieStr.indexOf('=',idx);
		if(idx==-1)return null;
		int idx2=cookieStr.indexOf(';',idx);
		String value=null;
		if(idx2==-1){
			value=cookieStr.substring(idx+1);
		}else{
			value=cookieStr.substring(idx+1,idx2);
		}
		try {
			String charset=request.getCharacterEncoding();
			if(charset==null){
				charset="UTF-8";
			}
			return java.net.URLDecoder.decode(value.trim(), charset);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}


	
}
