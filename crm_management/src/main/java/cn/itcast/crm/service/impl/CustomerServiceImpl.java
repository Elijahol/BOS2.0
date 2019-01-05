package cn.itcast.crm.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.crm.dao.CustomerRepository;
import cn.itcast.crm.domain.Customer;
import cn.itcast.crm.service.CustomerService;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	private CustomerRepository customerRepository;
	public List<Customer> findNoAssociationCustomers() {
		return customerRepository.findByFixedAreaIdIsNull();
	}

	public List<Customer> findHasAssociationFixedAreaCustomers(
			String fixedAreaId) {
		return customerRepository.findByFixedAreaId(fixedAreaId);
	}

	public void associationCustomersToFixedArea(String customerIdStr,
			String fixedAreaId) {
		//接触关联动作
		customerRepository.clearFixedAreaId(fixedAreaId);
		if (StringUtils.isBlank(customerIdStr)) {
			return;
		}
		String[] idsStr = customerIdStr.split(",");
		for (String idStr : idsStr) {
			Integer id = Integer.parseInt(idStr);
			customerRepository.updateFixedAreaId(fixedAreaId, id);
		}
	}

	public void regist(Customer customer) {
		customerRepository.save(customer);
		
	}

	public Customer findByTelephone(String telephone) {
		return customerRepository.findByTelephone(telephone);
	}

	public void updateType(String telephone) {
		customerRepository.updateType(telephone);
	}

	public Customer findByTelephoneAndPassword(String telephone, String password) {
		return customerRepository.findByTelephoneAndPassword(telephone,password);
	}

	public String findFixedAreaIdByAddress(String address) {
		return customerRepository.findFixedAreaIdByAddress(address);
	}

	

}
