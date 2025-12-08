package hexlet.code.repositories;

import com.zaxxer.hikari.HikariDataSource;

public class BaseRepository {
    private static HikariDataSource hikariDataSource;

    public static void setDataSource(HikariDataSource dataSource) {
        hikariDataSource = dataSource;
    }
}
