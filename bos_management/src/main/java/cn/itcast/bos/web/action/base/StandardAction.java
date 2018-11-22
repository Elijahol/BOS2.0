package cn.itcast.bos.web.action.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.StandardService;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@ParentPackage("json-default")
@Namespace("/")
@Actions
@Controller
@Scope("prototype")
public class StandardAction extends ActionSupport implements
		ModelDriven<Standard> {
	// 模型驱动
	private Standard standard = new Standard();
	// 注入service
	@Autowired
	private StandardService standardService;

	@Override
	public Standard getModel() {
		return standard;
	}

	// 添加操作
	@Action(value = "standard_save", results = { @Result(name = "success", type = "redirect", location = "./pages/base/standard.html") })
	public String save() {
		standardService.save(standard);
		return SUCCESS;
	}

	// 属性驱动
	private int page;
	private int rows;

	public void setPage(int page) {
		this.page = page - 1;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	// 分页列表查询
	@Action(value = "standard_pageQuery", results = { @Result(name = "success",
			type = "json") })
	public String pageQuery() {
		// 调用杨文武层，查询数据
		Pageable pageable = new PageRequest(page, rows);
		Page<Standard> standards = standardService.findPageData(pageable);
		// 返回客户端数据包含total，和rows
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", standards.getTotalElements());
		result.put("rows", standards.getContent());
		// 将map转换为json数据返回，使用struts2-json-plugin插件
		// 将map压入值栈顶部，插件会将值栈顶部数据转为json
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}
	//查询所有
	@Action(value="standard_findAll",results={@Result(name="success",type="json")})
	public String findAll(){
		List<Standard> standards = standardService.findAll();
		ActionContext.getContext().getValueStack().push(standards);
		return SUCCESS;
	}
}
