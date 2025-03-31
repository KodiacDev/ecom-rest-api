package com.example.shopappangular.components;

import com.example.shopappangular.exceptions.InvalidParamException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value(value = "${jwt.expiration}")
    private int expiration; //save to an environment variable
    @Value(value = "${jwt.secretKey}")
    private String secretKey;

    public String generateToken(com.example.shopappangular.models.User user) throws Exception {
        //properties => claims
        Map<String, Object> claims = new HashMap<>(); // đẩy vào 2 key phoneNumber, userId trong đó string la phone, user là object
        //this.generateSecretKey();
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("userId", user.getId());
        try {
            String token = Jwts.builder()
                    .setClaims(claims) //how to extract claims from this ?
                    .setSubject(user.getPhoneNumber()) //Đặt chủ thể (subject) cho JWT
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L)) //Đặt thời gian hết hạn. *1000 có nghĩa là kiểu miligiay ra giây
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256) //sử dụng hàm getSignInKey() để lấy khóa ký hiệu và sử dụng thuật toán ký hiệu HS256 (HMAC SHA-256).
                    .compact(); //Kết thúc
            return token; //và trả về chuỗi JWT đã được tạo
        } catch (Exception e) {
            //you can "inject" Logger, instead System.out.println
            throw new InvalidParamException("Cannot create jwt token, error: " + e.getMessage());
            //return null;
        }
    }

    private Key getSignInKey() { //hs256 cần khóa ký hiệu dạng mãng byte
        byte[] bytes = Decoders.BASE64.decode(secretKey); //Giải mã chuỗi secretKey từ Base64 thành mảng byte.
        //Keys.hmacShaKeyFor(Decoders.BASE64.decode("TaqlmGv1iEDMRiFp/pHuID1+T84IABfuA0xXh4GhiUI=")); cái này là chuỗi dc mã hóa base64
        return Keys.hmacShaKeyFor(bytes); //Trả về khóa ký hiệu đã tạo.
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256-bit key
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }
    //extractAllClaims(String token) là đối tượng Claims chứa tất cả các thông tin trong JWT.
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)//Nó trả về một đối tượng Jws<Claims> đại diện cho JWT đã được phân tích và xác thực.
                .getBody(); //lấy tt
    }

    public  <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token); //trả về ds ca claims
        return claimsResolver.apply(claims);
    }
    //check expiration
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }
    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public boolean validateToken(String token, UserDetails userDetails) {
        String phoneNumber = extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername())) //Số điện thoại trúng khớp
                && !isTokenExpired(token); //Token chưa hết hạn
    }

}
