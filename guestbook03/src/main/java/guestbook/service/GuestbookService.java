package guestbook.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import guestbook.repository.GuestbookLogRepository;
import guestbook.repository.GuestbookRepository;
import guestbook.vo.GuestbookVo;

@Service
public class GuestbookService {
	@Autowired
	private PlatformTransactionManager transactionManager;
	@Autowired
	private DataSource dataSource;
	
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
		//TX:BEGIN
		// 이부분을 AOP로 뺴내서 처리할 수 있는데, spring에 이미 이 부분이 구현되어 있어서 알아서 처리해줌
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			GuestbookVo vo = guestbookRepository.findById(id);
			if(vo == null) {
				return;
			}
			
			int count = guestbookRepository.deleteByIdAndPassword(id, password);
			//password가 잘못되었을 경우 안지워질 수 있다
			if(count == 1) {
				guestbookLogRepository.update(vo.getRegDate());
			}
			
			//TX:END(SUCCESS)
			transactionManager.commit(txStatus);
		} catch(Throwable e) {
			//TX:END(FAIL)
			transactionManager.rollback(txStatus);
			
			throw new RuntimeException(e);
		}
	}
	
	public void addContents(GuestbookVo vo) {
		// 트랜잭션 동기(Connection)초기화
		TransactionSynchronizationManager.initSynchronization();
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			//TX:BEGIN
			conn.setAutoCommit(false);
			
			int count = guestbookLogRepository.update();
			if(count==0) {
				guestbookLogRepository.insert();
			}
			
			guestbookRepository.insert(vo);
			
			//TX:END(SUCCESS)
			conn.commit();
		} catch (Throwable e) {
			//TX:END(FAIL)
			try {
				conn.rollback();
				throw new RuntimeException(e);
			} catch(SQLException ignore) {
				
			}
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
		
	}
}
