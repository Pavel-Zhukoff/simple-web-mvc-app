package ru.pavel_zhukoff.models;

import ru.pavel_zhukoff.Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuotesModel extends Model {

    public List<String> getQuotes() {
        List<String> result = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = this.getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT text FROM quotes;");
            while (resultSet.next()) {
                result.add(resultSet.getString("text"));
            }
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
