package cn.itcast.bos.service.take_delivery.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.take_delivery.WayBillRepository;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.service.take_delivery.WayBillService;
@Service
@Transactional
public class WayBillServiceImpl implements WayBillService {
	@Autowired
	private WayBillRepository wayBillRepository;
	@Override
	public void save(WayBill wayBill) {
		wayBillRepository.save(wayBill);

	}
	@Override
	public Page<WayBill> findPageData(Pageable pageable) {
		return wayBillRepository.findAll(pageable);
	}

}
