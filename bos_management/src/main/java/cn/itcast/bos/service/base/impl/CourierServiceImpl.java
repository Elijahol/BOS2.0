package cn.itcast.bos.service.base.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.CourierRepository;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.service.base.CourierService;

@Service
@Transactional
public class CourierServiceImpl implements CourierService {
	@Autowired
	private CourierRepository courierRepository;
	
	@Override
	public void save(Courier courier) {
		courierRepository.save(courier);

	}

	@Override
	public Page<Courier> findPageData(Specification<Courier> specification,Pageable pageable) {
		return courierRepository.findAll(specification, pageable);
	}

	@Override
	public void delBatch(String ids) {
		String[] idArray = ids.split(",");
		for (String idStr : idArray) {
			Integer id = Integer.parseInt(idStr);
			courierRepository.updateTag(id,'1');
		}
	}

	@Override
	public void restoreBatch(String ids) {
		String[] idArray = ids.split(",");
		for (String idStr : idArray) {
			Integer id = Integer.parseInt(idStr);
			courierRepository.updateTag(id,null);
		}
	}
	

}
