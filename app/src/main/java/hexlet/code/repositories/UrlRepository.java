package hexlet.code.repositories;

import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import lombok.Getter;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UrlRepository extends BaseRepository<Url> {
    @Getter
    private static List<Url> data = new ArrayList<>();

    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";

        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, url.getName());
            preparedStatement.setTimestamp(2, url.getCreatedAt());
            preparedStatement.executeUpdate();

            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    url.setId(generatedKeys.getLong(1));
                }
            }
        }

        data.add(url);
    }

    public static void truncate() throws SQLException {
        var sql = "DELETE FROM urls";

        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }

        data.clear();
    }
}
