package pl.sda.demo.configuration;

public class SecurityConstants {
    public static final String SECRET = "SecretKey";
    public static final long EXPIRATION_TIME = 3_600_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/register";
}
