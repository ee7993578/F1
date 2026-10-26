package com.edufox.schoolerp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Auth auth = new Auth();
    private SuperAdmin superAdmin = new SuperAdmin();
    private Cors cors = new Cors();
    private MasterSettings masterSettings = new MasterSettings();

    @Getter @Setter
    public static class Auth {
        private String jwtSecret;
        private long accessTokenMinutes = 30;
        private boolean databaseReadOnly = true;
        private boolean otpEnabled = true;
    }

    @Getter @Setter
    public static class SuperAdmin {
        private String identifier = "7830402482";
        private String password = "Raza@123";
    }

    @Getter @Setter
    public static class Cors {
        private String allowedOrigins = "http://localhost:5173";
    }

    @Getter @Setter
    public static class MasterSettings {
        private boolean writeEnabled = false;
    }
}
