package guestbook.service;

import java.util.List;

import org.springframework.stereotype.Service;

import guestbook.repository.GuestbookLogRepository;
import guestbook.repository.GuestbookRepository;
import guestbook.vo.GuestbookVo;

@Service
public class GuestbookService {
	// 수정 못하게 final로 구현 (안전성)
	private final GuestbookRepository guestbookRepository;
	private final GuestbookLogRepository guestbookLogRepository;
	
	public GuestbookService(GuestbookRepository guestbookRepository, GuestbookLogRepository guestbookLogRepository) {
		this.guestbookRepository = guestbookRepository;
		this.guestbookLogRepository = guestbookLogRepository;
	}
	
	public List<GuestbookVo> getContentsList(){
		return guestbookRepository.findAll();
	}
	
	public void deleteContents(Long id, String password) {
		GuestbookVo vo = guestbookRepository.findById(id);
		if(vo == null) {
			return;
		}
		
		int count = guestbookRepository.deleteByIdAndPassword(id, password);
		
		//password가 잘못되었을 경우 안지워질 수 있다
		if(count == 1) {
			guestbookLogRepository.update(vo.getRegDate());
		}
	}
	
	public void addContents(GuestbookVo vo) {
		int count = guestbookLogRepository.update();
		if(count==0) {
			guestbookLogRepository.insert();
		}
		
		guestbookRepository.insert(vo);
	}
}
