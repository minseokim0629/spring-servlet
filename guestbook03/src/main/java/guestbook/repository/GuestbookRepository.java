package guestbook.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import guestbook.repository.template.JdbcContext;
import guestbook.repository.template.StatementStrategy;
import guestbook.vo.GuestbookVo;

@Repository
public class GuestbookRepository {
	private JdbcContext jdbcContext;
	private DataSource dataSource;
	
	public GuestbookRepository(JdbcContext jdbcContext, DataSource dataSource) {
		this.jdbcContext = jdbcContext;
		this.dataSource = dataSource;
	}
	
	public int insert(GuestbookVo vo) {
		return jdbcContext.executeUpdate(
		"insert into guestbook values(null, ?, ?, ?, now())",
		new Object[] {vo.getName(), vo.getPassword(), vo.getContents()});
		/*
		 int count = 0;


		try (
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("insert into guestbook values(null, ?, ?, ?, now())");
		)
		{
			pstmt.setString(1, vo.getName());
			pstmt.setString(2, vo.getPassword());
			pstmt.setString(3, vo.getContents());

			count = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error:" + e);
		} 
		return count;
				 */
	}
	
	public List<GuestbookVo> findAll() {
		List<GuestbookVo> result = new ArrayList<>();
		try (
			// 주로 생성되는 코드를 적는다
			// pstmt에 binding하는 코드가 있으면 rs를 올리기 어렵지만 binding 하는 코드가 없으니 rs를 올렸다
			Connection conn = dataSource.getConnection(); 
			PreparedStatement pstmt = conn.prepareStatement("select id, name, contents, date_format(reg_date, '%Y-%m-%d %h:%i:%s') from guestbook order by reg_date desc");
			ResultSet rs = pstmt.executeQuery();
		) {
			while (rs.next()) {
				Long id = rs.getLong(1);
				String name = rs.getString(2);
				String contents = rs.getString(3);
				String reg_date = rs.getString(4);
				GuestbookVo vo = new GuestbookVo();

				vo.setId(id);
				vo.setName(name);
				vo.setContents(contents);
				vo.setRegDate(reg_date);
				
				result.add(vo);
			}
			rs.close();

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
		return result;
	}
	
	public int deleteByIdAndPassword(Long id, String password) {
		return jdbcContext.executeUpdate("delete from guestbook where id=? and password=?", new Object[] {id, password});
		/*int count = 0;

		try (
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("delete from guestbook where id=? and password=?");
		)
		{
			pstmt.setLong(1, id);
			pstmt.setString(2, password);

			count = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error:" + e);
		} 
		return count;*/
	}
}
