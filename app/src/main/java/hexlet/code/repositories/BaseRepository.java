package hexlet.code.repositories;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

public abstract class BaseRepository<T> {
    @Getter
    @Setter
    static HikariDataSource dataSource;
}
