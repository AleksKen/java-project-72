package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlCheckRepository extends BaseRepository {

    public static void save(UrlCheck urlCheck) throws SQLException {
        var sql = "INSERT INTO Url_checks (url_id, status_code, h1, title, description, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, urlCheck.getUrlId());
            statement.setInt(2, urlCheck.getStatusCode());
            statement.setString(3, urlCheck.getH1());
            statement.setString(4, urlCheck.getTitle());
            statement.setString(5, urlCheck.getDescription());
            statement.setTimestamp(6, urlCheck.getCreatedAt());
            statement.executeUpdate();

            var generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }


    public static Optional<UrlCheck> find(Long id) throws SQLException {
        var sql = "SELECT * FROM Url_checks WHERE id = ?";
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resUrlChecks = statement.executeQuery();
            if (resUrlChecks.next()) {
                var createdAt = resUrlChecks.getTimestamp("created_at");
                var title = resUrlChecks.getString("title");
                var h1 = resUrlChecks.getString("h1");
                var description = resUrlChecks.getString("description");
                var code = resUrlChecks.getInt("status_code");
                var urlId = resUrlChecks.getLong("uri_id");

                var urlCheck = new UrlCheck(code, title, h1, description, urlId, createdAt);
                urlCheck.setId(id);
                return Optional.of(urlCheck);
            }
            return Optional.empty();
        }
    }

    public static List<UrlCheck> getEntities() throws SQLException {
        var sql = "SELECT * FROM Url_checks";
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            var resUrlChecks = statement.executeQuery();
            var urlChecks = new ArrayList<UrlCheck>();
            while (resUrlChecks.next()) {
                var createdAt = resUrlChecks.getTimestamp("created_at");
                var title = resUrlChecks.getString("title");
                var h1 = resUrlChecks.getString("h1");
                var description = resUrlChecks.getString("description");
                var code = resUrlChecks.getInt("status_code");
                var urlId = resUrlChecks.getLong("uri_id");
                var id = resUrlChecks.getLong("id");
                var urlCheck = new UrlCheck(code, title, h1, description, urlId, createdAt);
                urlCheck.setId(id);
                urlChecks.add(urlCheck);
            }
            return urlChecks;
        }
    }
}
