package hexlet.code.repositories;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

public abstract class BaseRepository {
    @Getter
    @Setter
    static HikariDataSource dataSource;
}
