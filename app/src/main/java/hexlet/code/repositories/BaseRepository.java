package hexlet.code.repositories;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;

public class BaseRepository {
    @Setter
    static HikariDataSource dataSource;
}
