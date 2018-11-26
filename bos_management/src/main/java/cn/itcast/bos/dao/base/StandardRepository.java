package cn.itcast.bos.dao.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.bos.domain.base.Standard;

public interface StandardRepository extends JpaRepository<Standard, Integer> {
	// 根据收派标准名称查询
	public List<Standard> findByName(String name);

	@Query(value = "from Standard where name=?", nativeQuery = false)
	// nativeQuery为false配置JPQL，为true 配置SQL
	public List<Standard> queryName(String name);

	@Query
	public List<Standard> queryName2(String name);

	@Query(value = "update Standard set minLength=?2 where id=?1")
	@Modifying
	public void updateMinLength(Integer id, Integer minLength);
	//最大重量在 start-end 公斤的收派标准数据
	@Query(value = "from Standard where maxWeight between ?1 and ?2")
	public List<Standard> findStandardsWithMaxWeightBetween(Integer start,
			Integer end);
	
	//根据收派标准名称删除
//	public void deleteByName(String name);
}
