package hexlet.code.repositories;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;

public abstract class BaseRepository<T> {
    @Setter
    static HikariDataSource dataSource;
}
