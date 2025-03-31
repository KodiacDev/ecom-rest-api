package com.example.shopappangular.filters;

import com.example.shopappangular.components.JwtTokenUtils;
import com.example.shopappangular.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
@Component
@RequiredArgsConstructor

public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtil;

    @Override
    protected void doFilterInternal(@NonNull  HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (isBypassToken(request)) {
                filterChain.doFilter(request, response); //enable bypass
                return;
            }
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
            final String token = authHeader.substring(7); //vì đầu của token có chuỗi "Bearer " nên phải bắt đầu tù 7 để cắt chuỗi
            final String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
            if (phoneNumber != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response); //enable bypass
        } catch (Exception e) {
            e.printStackTrace(); // In stack trace để xem chi tiết lỗi
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }

    }
    private boolean isBypassToken(@NonNull HttpServletRequest request) { //ko cần quyền hay nói là ko cần kiểm tra khi request tới
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/roles", apiPrefix), "GET"),
                Pair.of(String.format("%s/healthcheck/health", apiPrefix), "GET"),
                Pair.of(String.format("%s/roles", apiPrefix), "GET"),
                //Pair.of(String.format("%s/orders", apiPrefix), "GET"),
                Pair.of(String.format("%s/products", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST")
        );

        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();
        //tại sao phải làm riêng cái GET này ở đây vì
        //khi ta bỏ /%s/orders" GET ở trên thì cái GET của get-orders-by-keyword của trang thống kê bên ADMIN sẽ bị lọt theo.
        //nên ta làm đều kiện if ở đây
        if (requestPath.startsWith(String.format("/%s/orders", apiPrefix))
                && requestMethod.equals("GET")) {
            // Check if the requestPath matches the desired pattern
            if (requestPath.matches(String.format("/%s/orders/\\d+", apiPrefix))) {
                return true;
            }
            // If the requestPath is just "%s/orders", return true
            if (requestPath.equals(String.format("/%s/orders", apiPrefix))) {
                return true;
            }
        }
        for (Pair<String, String> bypassToken : bypassTokens) {
            if (requestPath.contains(bypassToken.getFirst())
                    && requestMethod.equals(bypassToken.getSecond())) {
                return true;
            }
        }

        return false;
    }


}