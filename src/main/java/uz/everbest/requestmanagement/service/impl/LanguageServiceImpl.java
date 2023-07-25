package uz.everbest.requestmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import uz.everbest.requestmanagement.domain.enums.LanguageCode;
import uz.everbest.requestmanagement.domain.enums.MessageText;
import uz.everbest.requestmanagement.service.LanguageService;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final ResourceBundleMessageSource messageSource;

    @Override
    public String message(String key, String code) {
        return messageSource.getMessage(key, null, new Locale(code));
    }

    @Override
    public String message(MessageText key, LanguageCode code) {
        return messageSource.getMessage(key.name(), null,
                code.locale() == null ? LanguageCode.EN.locale() : code.locale());
    }

    @Override
    public String en(MessageText key) {
        return message(key, LanguageCode.EN);
    }

    @Override
    public String ru(MessageText key) {
        return message(key, LanguageCode.RU);
    }

    @Override
    public boolean equals(String text, MessageText key) {
        return text.equalsIgnoreCase(ru(key)) ||
                text.equalsIgnoreCase(en(key));
    }

}
