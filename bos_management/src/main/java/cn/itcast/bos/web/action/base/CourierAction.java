package cn.itcast.bos.web.action.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.service.base.CourierService;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class CourierAction extends ActionSupport implements
		ModelDriven<Courier> {
	private Courier courier = new Courier();

	@Override
	public Courier getModel() {
		return courier;
	}

	// 注入service
	@Autowired
	private CourierService courierService;

	// 保存快递员
	@Action(value = "courier_save", results = { @Result(name = "success", location = "./pages/base/courier.html", type = "redirect") })
	public String save() {
		courierService.save(courier);
		return SUCCESS;
	}

	private int page;
	private int rows;

	// 分页查询方法
	public void setPage(int page) {
		this.page = page - 1;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	@Action(value = "courier_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {
		// 封装Pageable对象
		Pageable pageable = new PageRequest(page, rows);
		// 根据查询条件 构造Specification 条件查询对象（类似Hibernate的QBC查询）
		Specification<Courier> specification = new Specification<Courier>() {
			/**
			 * 构造条件查询方法，如果方法返回null，代表无条件查询 Root 参数 获取条件表达式 name=?, age=?
			 * CriteriaQuery 参数，构造简单查询条件返回， 提供where方法 CriteriaBuilder 参数
			 * 构造Predicate对象， 条件对象，构造复杂查询效果
			 */
			@Override
			public Predicate toPredicate(Root<Courier> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				// 当前查询Root根对象Courier
				List<Predicate> list = new ArrayList<Predicate>();
				// 表单查询（查询当前对象对应数据表）
				if (StringUtils.isNotBlank(courier.getCourierNum())) {
					// 进行快递员工号查询
					// courierNum=？
					Predicate p1 = cb.equal(
							root.get("courierNum").as(String.class),
							courier.getCourierNum());
					list.add(p1);
				}
				if (StringUtils.isNotBlank(courier.getCompany())) {
					// 进行公司查询，模糊查询
					// company like %?%
					Predicate p2 = cb.like(
							root.get("company").as(String.class),
							"%" + courier.getCompany() + "%");
					list.add(p2);
				}
				if (StringUtils.isNotBlank(courier.getType())) {
					// 进行快递员类型查询，等值查询
					Predicate p3 = cb.equal(root.get("type").as(String.class),
							courier.getType());
					list.add(p3);
				}
				// 多表查询（查询当前对象关联 对应数据表）
				// 使用Courier（Root），关联Standard
				Join<Object, Object> standardRoot = root.join("standard",
						JoinType.INNER);
				if (courier.getStandard() != null
						&& StringUtils.isNotBlank(courier.getStandard()
								.getName())) {
					// 进行收派标准名称 模糊查询
					// standard.name like %?%
					Predicate p4 = cb.like(
							standardRoot.get("name").as(String.class), "%"
									+ courier.getStandard().getName() + "%");
					list.add(p4);
				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};
		// 调用业务查询数据，返回Page
		Page<Courier> pageData = courierService.findPageData(specification,
				pageable);
		// 将返回Page对象转换为json
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", pageData.getTotalElements());
		result.put("rows", pageData.getContent());
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}

	private String ids;

	public void setIds(String ids) {
		this.ids = ids;
	}

	// courier_delBatch
	@Action(value = "courier_delBatch", results = { @Result(name = "success", location = "./pages/base/courier.html", type = "redirect") })
	public String delBatch() {
		courierService.delBatch(ids);
		return SUCCESS;
	}

	// courier_restoreBatch
	@Action(value = "courier_restoreBatch", results = { @Result(name = "success", location = "./pages/base/courier.html", type = "redirect") })
	public String restoreBatch() {
		courierService.restoreBatch(ids);
		return SUCCESS;
	}
	@Action(value="courier_findnoassociation",results={@Result(name="success",type="json")})
	public String findnoassociation(){
		List<Courier> couriers = courierService.findNoAssociation();
		ActionContext.getContext().getValueStack().push(couriers);
		return SUCCESS;
	}
}
