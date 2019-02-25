package lz.gateway.server.filter.zuul.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;

@Slf4j
public class FileUploadFilter extends ZuulFilter {

	/**
	 * 是否存储文件流
	 */
	private boolean      storeStream   = false;

	private static long  MAX_FILE_SIZE = 2*1024*1024;

	@Override
	public boolean shouldFilter() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext(); 
		HttpServletRequest httpRequest = (HttpServletRequest) ctx.getRequest();
		boolean isMultipart = ServletFileUpload.isMultipartContent(httpRequest);// 检查输入请求是否为multipart表单数据。
		if (isMultipart == true) {
			try {
				FileItemFactory factory = new DiskFileItemFactory();// 为该请求创建一个DiskFileItemFactory对象，通过它来解析请求。执行解析后，所有的表单项目都保存在一个List中。
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(httpRequest);
				Iterator<FileItem> itr = items.iterator();
				List<Map<String, Object>> contentList = new ArrayList<Map<String, Object>>();
				while (itr.hasNext()) {
					FileItem item = itr.next();
					if (item.getSize() > MAX_FILE_SIZE) {
						throw new ZuulException("file.size.exceed", 0, String.format("最大允许上传文件大小为%dkb",
								MAX_FILE_SIZE / 1024));
					}
					// 检查当前项目是普通表单项目还是上传文件。
					if (!item.isFormField()) { // 如果是普通表单项目，显示表单内容。
						log.info("The upload is a file stream, the name is " + item.getName() + ", the fieldName is "
								+ item.getFieldName());
						byte[] bytes = StreamUtils.copyToByteArray(item.getInputStream());
						//                    UploadFileContentDTO uploadItem = new UploadFileContentDTO();
						//                    uploadItem.setContent(bytes);
						//                    uploadItem.setContentType(item.getContentType());
						//                    uploadItem.setFieldName(item.getFieldName());
						//                    uploadItem.setName(item.getName());
						//                    if (storeStream) {
						//                        storageService.storeStream(null, item.getFieldName(), item.getInputStream());
						//                    }
						//                    contentList.add((Map<String, Object>) Bean2Map.toMap(uploadItem));
					}
					else {
						httpRequest.setAttribute(item.getFieldName(), item.getString());
					}

				}
			} catch (FileUploadException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

}
