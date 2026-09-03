package com.nbh.edushare.common.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@Profile("seed")
public class SeedRunner implements CommandLineRunner {

    private final DataSource dataSource;

    public SeedRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("db/seed_user.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("db/seed_category.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("db/seed_chat.sql"));
        }
    }
}