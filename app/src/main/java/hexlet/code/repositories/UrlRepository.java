package hexlet.code.repositories;

import hexlet.code.models.Url;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class UrlRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";

        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, url.getName());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(url.getCreatedAt()));
            preparedStatement.executeUpdate();

            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    url.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public static Optional<Url> getById(long id) throws SQLException {
        var sql = "SELECT id, name, created_at FROM urls WHERE id = ?";

        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                var createdAt = resultSet.getTimestamp("created_at");

                return Optional.of(Url.builder()
                        .id(resultSet.getLong("id"))
                        .name(resultSet.getString("name"))
                        .createdAt(ofNullable(createdAt).map(Timestamp::toLocalDateTime).orElse(null))
                        .build());
            }
        }

        return Optional.empty();
    }
}
