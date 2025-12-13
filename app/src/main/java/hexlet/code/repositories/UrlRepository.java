package hexlet.code.repositories;

import hexlet.code.models.URL;

import java.sql.SQLException;
import java.sql.Statement;

public class UrlRepository extends BaseRepository {

    public static void save(URL url) throws SQLException {
        var sql = "INSERT INTO urls (name) VALUES (?)";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, url.getName());
            preparedStatement.executeUpdate();
        }
    }
}
