package microservice.cloud.inventory.category.infrastructure.persistence.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;

public class SlugToIdMapper implements ResultSetExtractor<Map<String, String>> {

    @Override
    public Map<String, String> extractData(ResultSet rs) throws SQLException {
        Map<String, String> map = new HashMap<>();
        while (rs.next()) {
            map.put(rs.getString("slug"), rs.getString("id"));
        }
        return map;
    }
}
