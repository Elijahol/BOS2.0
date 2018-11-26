package cn.itcast.crm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.crm.dao.CustomerRepository;
import cn.itcast.crm.domain.Customer;
@Service
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	private CustomerRepository customerRepository;
	@Override
	public List<Customer> findNoAssociationCustomers() {
		// fixAreaId is null;
		return customerRepository.findByFixedAreaIdIsNull();
	}

	@Override
	public List<Customer> findHasAssociationFixedAreaCustomers(
			String fixedAreaId) {
		// fixAreaId is ?
		return customerRepository.findByFixedAreaId(fixedAreaId);
	}

	@Override
	public void associationCustomersToFixedArea(String customerIdStr,
			String fixedAreaId) {
		String[] ids = customerIdStr.split(",");
		for (String idStr : ids) {
			Integer id = Integer.parseInt(idStr);
			customerRepository.updateFixedAreaId(fixedAreaId,id);
		}

	}

}
