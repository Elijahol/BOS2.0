package cn.itcast.crm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.crm.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	public List<Customer> findByFixedAreaIdIsNull();
	public List<Customer> findByFixedAreaId(String fixedAreaId);
	
	@Query("update Customer set fixedAreaId=?1 where id=?2")
	@Modifying
	public void updateFixedAreaId(String fixedAreaId, Integer id);
	
	
}
