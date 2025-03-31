package com.example.shopappangular.components;

import com.example.shopappangular.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class LocalizationUtils {
    private final MessageSource messageSource; //MessageSource là interface trong Spring Framework dùng để truy xuất các message (thông điệp) từ message bundle (file properties yml chứa các key-value thông điệp).
    private final LocaleResolver localeResolver; //LocaleResolver là interface định nghĩa phương thức để lấy và thiết lập Locale (ngôn ngữ, vùng) cho người dùng.
    public String getLocalizedMessage(String messageKey, Object... params) {//spread operator
        HttpServletRequest request = WebUtils.getCurrentRequest();
        Locale locale = localeResolver.resolveLocale(request);

        return messageSource.getMessage(messageKey, params, locale);
}
}