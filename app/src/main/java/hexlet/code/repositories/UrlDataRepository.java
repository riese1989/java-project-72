package hexlet.code.repositories;

import hexlet.code.entities.UrlData;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

public class UrlDataRepository extends BaseRepository {
    public static List<UrlData> getUrlData() throws SQLException {
        var sql = """
                SELECT
                    urls.id AS id,
                    urls.name,
                    url_checks.status_code,
                    url_checks.created_at AS last_check
                FROM urls
                LEFT JOIN url_checks ON urls.id = url_checks.url_id
                AND url_checks.created_at = (
                    SELECT MAX(created_at)
                    FROM url_checks AS inner_uc
                    WHERE inner_uc.url_id = urls.id
                )
                ORDER BY id;
                """;

        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<UrlData>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var name = resultSet.getString("name");
                var codeAnswer = resultSet.getInt("status_code");
                var dateLastCheck = resultSet.getTimestamp("last_check");

                var urlData = UrlData.builder()
                        .id(id)
                        .name(name)
                        .dateLastCheck(ofNullable(dateLastCheck).map(Timestamp::toLocalDateTime).orElse(null))
                        .codeAnswer(codeAnswer);


                result.add(urlData.build());
            }
            return result;
        }
    }
}
