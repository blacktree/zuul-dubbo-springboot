package lz.gateway.server.filter.zuul.after;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import com.alibaba.fastjson.JSONObject;
import lz.gateway.server.constant.Constant;
import lz.gateway.server.filter.zuul.BaseFilter;
import lz.skeleton.common.ServiceResponse;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class FinalFilter extends BaseFilter {

	@Override
	public boolean shouldFilter() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext(); 
		ClientHttpResponse response=null;
		try {

			HttpServletResponse httpResponse = (HttpServletResponse) ctx.getResponse();
			String charset = getCharset(httpResponse);

			ServiceResponse resp=(ServiceResponse) ctx.get(Constant.SRESP);
			//		byte[] data=null;
			if(resp!=null) {
				//			processResponse(resp);
				final byte[] data=getResponseJsonBytes(resp, charset);

				return new ClientHttpResponse() {

					@Override
					public HttpStatus getStatusCode() throws IOException {
						return HttpStatus.OK;
					}

					@Override
					public int getRawStatusCode() throws IOException {
						return 200;
					}

					@Override
					public String getStatusText() throws IOException {
						return HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();
					}

					@Override
					public void close() {

					}

					@Override
					public InputStream getBody() throws IOException {
						return new ByteArrayInputStream(data);
					}

					@Override
					public HttpHeaders getHeaders() {
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
						return headers;
					}
				};
			}
			super.setResponse(response);
		}catch(Exception e) {
			throw new ZuulException(e, 0, null);
		}
		return response;
	}

	private byte[] getResponseJsonBytes(ServiceResponse resp, String charset) throws UnsupportedEncodingException {
		String respString = JSONObject.toJSONString(resp);

		return respString.getBytes(charset);
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return POST_TYPE;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 100000;
	}

}
