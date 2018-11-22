package cn.itcast.bos.service.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.Courier;

public interface CourierService {

	public void save(Courier courier);
	//无条件分页查询
	public Page<Courier> findPageData(Specification<Courier> specification,Pageable pageable);

}
