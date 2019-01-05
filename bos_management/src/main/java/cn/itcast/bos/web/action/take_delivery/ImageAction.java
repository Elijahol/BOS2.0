package cn.itcast.bos.web.action.take_delivery;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.web.action.common.BaseAction;

import com.opensymphony.xwork2.ActionContext;

/**
 * 处理kindeditor图片上传、管理功能
 * 
 * @author Administrator
 *
 */
@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class ImageAction extends BaseAction<Object> {
	private File imgFile;
	private String imgFileFileName;
	private String imgFileContentType;

	public void setImgFile(File imgFile) {
		this.imgFile = imgFile;
	}

	public void setImgFileFileName(String imgFileFileName) {
		this.imgFileFileName = imgFileFileName;
	}

	public void setImgFileContentType(String imgFileContentType) {
		this.imgFileContentType = imgFileContentType;
	}

	@Action(value = "image_upload", results = {
			@Result(name = "success", type = "json"),
			@Result(name = "input", type = "json") })
	public String upload() {
		String savePath = ServletActionContext.getServletContext().getRealPath(
				"/upload");
		String saveUrl = ServletActionContext.getRequest().getContextPath()
				+ "/upload";
		// 生成随机图片名
		UUID uuid = UUID.randomUUID();
		String ext = imgFileFileName
				.substring(imgFileFileName.lastIndexOf("."));
		String randomFileName = uuid + ext;
		// 通知浏览器文件上传文件
		Map<String, Object> result = new HashMap<String, Object>();
		// 保存图片
		try {
			FileUtils.copyFile(imgFile, new File(savePath + "/"
					+ randomFileName));

			result.put("error", 0);
			result.put("url", saveUrl + "/" + randomFileName);
			ActionContext.getContext().getValueStack().push(result);
			return SUCCESS;
		} catch (IOException e) {
			result.put("error", 1);
			ActionContext.getContext().getValueStack().push(result);
			e.printStackTrace();
			return INPUT;

		}

	}

	@Action(value = "image_manage", results = { @Result(name = "success", type = "json") })
	public String manage() {
		// 根目录路径，可以指定绝对路径
		String rootPath = ServletActionContext.getServletContext().getRealPath(
				"/")
				+ "upload/";
		// 根目录URL
		String rootUrl = ServletActionContext.getRequest().getContextPath()
				+ "/upload/";
		// 遍历目录取文件信息
		List<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();
		// 当前上传目录
		File currentPathFile = new File(rootPath);
		String[] fileTypes = new String[] { "gif", "jpg", "jpeg", "png", "bmp" };
		if (currentPathFile.listFiles() != null) {
			for (File file : currentPathFile.listFiles()) {
				Map<String, Object> hash = new HashMap<String, Object>();
				String fileName = file.getName();
				if (file.isDirectory()) {
					hash.put("is_dir", true);
					hash.put("has_file", (file.listFiles() != null));
					hash.put("filesize", 0L);
					hash.put("is_photo", false);
					hash.put("filetype", "");
				} else {
					String fileExt = fileName.substring(
							fileName.lastIndexOf(".") + 1).toLowerCase();
					hash.put("is_dir", false);
					hash.put("has_file", false);
					hash.put("filesize", file.length());
					hash.put("is_photo",
							Arrays.asList(fileTypes).contains(fileExt));
					hash.put("filetype", fileExt);
				}
				hash.put("filename", fileName);
				hash.put("datetime",
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file
								.lastModified()));
				fileList.add(hash);
			}
		}
		Map<String, Object> result = new HashMap<>();
		result.put("moveup_dir_path", "");
		result.put("current_dir_path", rootPath);
		result.put("current_url", rootUrl);
		result.put("total_count", fileList.size());
		result.put("file_list", fileList);

		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}
}
