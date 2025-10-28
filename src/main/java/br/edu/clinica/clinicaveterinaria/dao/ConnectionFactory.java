package br.edu.clinica.clinicaveterinaria.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class ConnectionFactory {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = ConnectionFactory.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
                    if (input == null) {
                        throw new RuntimeException("config.properties not found");
                    }
                    props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar o arquivo config.properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.password");
        return DriverManager.getConnection(url, user, pass);
    }
}
