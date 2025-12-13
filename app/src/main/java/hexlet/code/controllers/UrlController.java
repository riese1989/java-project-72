package hexlet.code.controllers;

import hexlet.code.models.URL;
import hexlet.code.repositories.UrlRepository;

import java.sql.SQLException;

public final class UrlController {
    public static void create(URL url) throws SQLException {
        UrlRepository.save(url);
    }
}
