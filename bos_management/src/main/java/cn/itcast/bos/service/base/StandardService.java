package cn.itcast.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.itcast.bos.domain.base.Standard;

/**
 * 收派标准管理
 * @author Administrator
 *
 */
public interface StandardService {
	public void save(Standard standard);
	//分页查询
	public Page<Standard> findPageData(Pageable pageable);
	//查询所有
	public List<Standard> findAll();
	//查询最大重量在A-B之间的收派标准数据
	public List<Standard> findStandardsWithMaxWeightBetween(Integer start, Integer end);
}
