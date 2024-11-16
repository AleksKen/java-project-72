package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, url.getName());
            statement.setTimestamp(2, url.getCreatedAt());
            statement.executeUpdate();

            var generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Optional<Url> search(String name) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";
        try (var connection = dataSource.getConnection();
        var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            var resUrls = statement.executeQuery();
            if (resUrls.next()) {
                var createdAt = resUrls.getTimestamp("created_at");
                var id = resUrls.getLong("id");
                var url = new Url(name, createdAt);
                url.setId(id);
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    public static List<Url> getEntities() throws SQLException {
        var sql = "SELECT * FROM urls";
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            var resUrls = statement.executeQuery();
            var urls = new ArrayList<Url>();
            while (resUrls.next()) {
                var createdAt = resUrls.getTimestamp("created_at");
                var id = resUrls.getLong("id");
                var name = resUrls.getString("name");
                var url = new Url(name, createdAt);
                url.setId(id);
                urls.add(url);
            }
            return urls;
        }
    }
}
