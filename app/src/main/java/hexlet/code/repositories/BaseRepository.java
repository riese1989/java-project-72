package hexlet.code.repositories;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;

import java.sql.SQLException;

public abstract class BaseRepository<T> {
    @Setter
    static HikariDataSource dataSource;

    static void truncate(String tableName) throws SQLException {
        var sql = "DELETE FROM " + tableName;

        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }
}
