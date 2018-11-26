package cn.itcast.bos.web.action.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;

import cn.itcast.bos.domain.base.Area;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public abstract class BaseAction<T> extends ActionSupport implements
		ModelDriven<T> {
	// 模型驱动
	protected T model;

	@Override
	public T getModel() {
		return model;
	}
	//构造器完成model实例化
	public BaseAction() {
		//狗仔子类Action对象，获取继承父类型的泛型
		//AreaAction extends BaseAction<Area>
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		//获取类型第一个泛型参数
		ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
		Class<T> modelClass = (Class<T>) parameterizedType
				.getActualTypeArguments()[0];
		try {
			model = modelClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			System.out.println("模型构造失败...");
		}
	}

	// 接受分页查询参数
	protected int page;
	protected int rows;

	public void setPage(int page) {
		this.page = page-1;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
	protected void pushPageDataToValueStack(Page<T> pageData) {
		// 封装datagrid需要数据格式
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("total", pageData.getTotalElements());
		res.put("rows", pageData.getContent());
		ActionContext.getContext().getValueStack().push(res);
	}
}
