package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;


public class UrlCheckRepository extends BaseRepository {

    public static void save(UrlCheck urlCheck) throws SQLException {
        var sql = "INSERT INTO Url_checks (url_id, status_code, h1, title, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, urlCheck.getUrlId());
            statement.setInt(2, urlCheck.getStatusCode());
            statement.setString(3, urlCheck.getH1());
            statement.setString(4, urlCheck.getTitle());
            statement.setString(5, urlCheck.getDescription());
            Date currentDate = new Date();
            Timestamp timestamp = new Timestamp(currentDate.getTime());
            statement.setTimestamp(6, timestamp);
            statement.executeUpdate();

            var generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static List<UrlCheck> searchAll(Long urlId)  {
        var sql = "SELECT * FROM Url_checks WHERE url_id = ? ORDER BY id";
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, urlId);
            var resUrlChecks = statement.executeQuery();
            var urlChecks = new ArrayList<UrlCheck>();
            while (resUrlChecks.next()) {
                var createdAt = resUrlChecks.getTimestamp("created_at");
                var title = resUrlChecks.getString("title");
                var h1 = resUrlChecks.getString("h1");
                var description = resUrlChecks.getString("description");
                var code = resUrlChecks.getInt("status_code");
                var id = resUrlChecks.getLong("id");
                var urlCheck = new UrlCheck(code, title, h1, description, urlId, createdAt);
                urlCheck.setId(id);
                urlChecks.add(urlCheck);
            }
            return urlChecks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Long, UrlCheck> getLastChecks() throws SQLException {
        var sql = "SELECT DISTINCT ON (url_id) * FROM url_checks ORDER BY url_id DESC, id DESC";
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            var resUrls = statement.executeQuery();
            var urlsLastCheck = new LinkedHashMap<Long, UrlCheck>();
            while (resUrls.next()) {
                var urlId = resUrls.getLong("url_id");
                var statusCode = resUrls.getInt("status_code");
                var h1 = resUrls.getString("h1");
                var title = resUrls.getString("title");
                var description = resUrls.getString("description");
                var createdAt = resUrls.getTimestamp("created_at");
                var urlCheck = new UrlCheck(statusCode, title, h1, description, urlId, createdAt);
                urlCheck.setUrlId(urlId);
                urlsLastCheck.put(urlId, urlCheck);
            }
            return urlsLastCheck;
        }
    }
}
