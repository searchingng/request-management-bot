package uz.everbest.requestmanagement.service;

import uz.everbest.requestmanagement.domain.enums.LanguageCode;
import uz.everbest.requestmanagement.domain.enums.MessageText;

public interface LanguageService {

    String message(String key, String code);

    String message(MessageText key, LanguageCode code);

    String en(MessageText key);

    String ru(MessageText key);

    boolean equals(String text, MessageText key);

}
