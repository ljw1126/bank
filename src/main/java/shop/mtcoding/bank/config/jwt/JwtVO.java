package shop.mtcoding.bank.config.jwt;

/*
 - SECRET은 노출되면 안된다
 - Refresh Token 구현은 우선 x
 */
public interface JwtVO {
    public static final String SECRET = "시크릿키"; // HS256 대칭키

    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 일주일
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";

}
