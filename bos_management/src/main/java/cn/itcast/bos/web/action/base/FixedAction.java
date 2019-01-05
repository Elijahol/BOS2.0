package cn.itcast.bos.web.action.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
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

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.base.FixedAreaService;
import cn.itcast.bos.web.action.common.BaseAction;
import cn.itcast.crm.domain.Customer;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class FixedAction extends BaseAction<FixedArea> {
	@Autowired
	private FixedAreaService fixedAreaService;

	// 保存定区
	@Action(value = "fixedArea_save", results = { @Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String save() {
		// 调用业务层，保存定区
		fixedAreaService.save(model);
		return SUCCESS;
	}

	@Action(value = "fixedArea_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {
		Pageable pageable = new PageRequest(page, rows);
		Specification<FixedArea> specification = new Specification<FixedArea>() {

			@Override
			public Predicate toPredicate(Root<FixedArea> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				if (StringUtils.isNotBlank(model.getId())) {
					Predicate p1 = cb.equal(root.get("id").as(String.class),
							model.getId());
					list.add(p1);
				}
				if (StringUtils.isNotBlank(model.getCompany())) {
					Predicate p2 = cb.like(
							root.get("company").as(String.class),
							"%" + model.getCompany() + "%");
					list.add(p2);
				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};
		Page<FixedArea> pageData = fixedAreaService.findPageData(specification,
				pageable);
		pushPageDataToValueStack(pageData);
		return SUCCESS;
	}

	// 查询未关联定区列表
	@Action(value = "fixedArea_findNoAssociationCustomers", results = { @Result(name = "success", type = "json") })
	public String findNoAssociationCustomers() {
		// 使用webClient调用webService接口
		Collection<? extends Customer> collection = WebClient
				.create("http://localhost:9002/crm_management/services/customerService/noassociationcustomers")
				.accept(MediaType.APPLICATION_JSON)
				.getCollection(Customer.class);
		ActionContext.getContext().getValueStack().push(collection);
		return SUCCESS;
	}

	// 查询关联当前定区列表
	@Action(value = "fixedArea_findHasAssociationCustomers", results = { @Result(name = "success", type = "json") })
	public String findAssociationCustomer() {
		Collection<? extends Customer> collection = WebClient
				.create("http://localhost:9002/crm_management/services/customerService/associationfixedareacustomers/"
						+ model.getId()).accept(MediaType.APPLICATION_JSON)
				.getCollection(Customer.class);
		ActionContext.getContext().getValueStack().push(collection);
		return SUCCESS;
	}

	private String[] customerIds;

	public void setCustomerIds(String[] customerIds) {
		this.customerIds = customerIds;
	}

	// 关联客户
	@Action(value = "fixedArea_associationCustomersToFixedArea", results = { @Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String associationCustomersToFixedArea() {
		String customerIdStr = StringUtils.join(customerIds, ",");
		WebClient
				.create("http://localhost:9002/crm_management/services/customerService/associationcustomerstofixedarea?customerIdStr="
						+ customerIdStr + "&fixedAreaId=" + model.getId()).put(
						null);
		return SUCCESS;
	}

	private Integer courierId;
	private Integer takeTimeId;

	public void setCourierId(Integer courierId) {
		this.courierId = courierId;
	}

	public void setTakeTimeId(Integer takeTimeId) {
		this.takeTimeId = takeTimeId;
	}

	@Action(value = "fixedArea_associationCourierToFixedArea", results = { @Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String associationCourierToFixedArea() {
		fixedAreaService.associationCourierToFixedArea(model, courierId,
				takeTimeId);
		return SUCCESS;
	}
}
