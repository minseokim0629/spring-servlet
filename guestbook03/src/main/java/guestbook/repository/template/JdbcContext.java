package guestbook.repository.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;

public class JdbcContext {
	private DataSource dataSource;
	
	public JdbcContext(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	// 다른 곳에서도 쓸 수 있게 제너릭 타입으로 사용
	public <E> List<E> queryForList(String sql, RowMapper<E> rowMapper) {
		return queryForListWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makeStatement(Connection connection) throws SQLException {
				return connection.prepareStatement(sql);
			}
		},rowMapper);
	};
	
	public int executeUpdate(String sql, Object[] parameters) {
		return executeUpdateWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makeStatement(Connection connection) throws SQLException{
				PreparedStatement pstmt = connection.prepareStatement(sql);
				
				for(int i = 0; i < parameters.length; i++) {
					pstmt.setObject(i+1, parameters[i]);
					//pstmt.setString(1, vo.getName());
					//pstmt.setString(2, vo.getPassword());
					//pstmt.setString(3, vo.getContents());
				}
				
				return pstmt;
			}
		});
	}
	
	private <E> List<E> queryForListWithStatementStrategy(StatementStrategy statementStrategy, RowMapper<E> rowMapper) {
		List<E> result = new ArrayList<>();
		
		try (
				Connection conn = dataSource.getConnection();
				PreparedStatement pstmt = statementStrategy.makeStatement(conn);
				ResultSet rs = pstmt.executeQuery();
			)
			{
				while(rs.next()) {
					E e = rowMapper.mapRow(rs, rs.getRow());
					result.add(e);
				}
			} catch (SQLException e) {
				System.out.println("error:" + e);
			} 
		
		return result;
	}

	private int executeUpdateWithStatementStrategy(StatementStrategy statementStrategy) {
		int count = 0;

		try (
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = statementStrategy.makeStatement(conn);
		)
		{
			count = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error:" + e);
		} 
		return count;                                                                
	}
}
