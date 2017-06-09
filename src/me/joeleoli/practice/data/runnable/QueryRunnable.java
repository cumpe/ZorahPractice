package me.joeleoli.practice.data.runnable;

import me.joeleoli.practice.manager.ManagerHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryRunnable extends BukkitRunnable {

    private String statement;
    private QueryCallback<ResultSet, SQLException> callback;

    public QueryRunnable(String statement, QueryCallback<ResultSet, SQLException> callback) {
        this.statement = statement;
        this.callback = callback;
    }

    @Override
    public void run() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = ManagerHandler.getStorageBackend().getPoolManager().getConnection();
            preparedStatement = connection.prepareStatement(statement);
            resultSet = preparedStatement.executeQuery();
            callback.call(resultSet, null);
        }
        catch (SQLException e) {
            callback.call(null, e);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}