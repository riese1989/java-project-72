package hexlet.code.util;

public class NamedRoutes {
    public static String urlsPath() {
        return "/urls";
    }

    public static String checkUrlPath(Long id) {
        return "/urls/" + id + "/checks";
    }

    public static String urlDataPath(Long id) {
        return "urls/" + id;
    }
}
