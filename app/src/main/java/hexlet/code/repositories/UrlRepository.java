package hexlet.code.repositories;

import hexlet.code.models.Url;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class UrlRepository extends BaseRepository {
    private static List<Url> urls = new LinkedList<>();

    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name) VALUES (?)";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, url.getName());
            preparedStatement.executeUpdate();

            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    url.setId(generatedKeys.getLong(1));
                }
            }
        }

        urls.add(url);
    }

    public static List<Url> showAll() {
        return urls;
    }

    public static void truncate() throws SQLException {
        var sql = "TRUNCATE TABLE urls";

        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }

        urls.clear();
    }
}
