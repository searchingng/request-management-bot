package uz.everbest.requestmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.everbest.requestmanagement.domain.entity.Company;
import uz.everbest.requestmanagement.domain.entity.Ticket;
import uz.everbest.requestmanagement.domain.entity.User;
import uz.everbest.requestmanagement.domain.enums.MessageText;
import uz.everbest.requestmanagement.domain.enums.TicketStatus;
import uz.everbest.requestmanagement.domain.enums.UserRole;
import uz.everbest.requestmanagement.repository.TicketRepository;
import uz.everbest.requestmanagement.util.KeyboardUtil;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final LanguageService lang;
    private final SendMessageService sendMessageService;
    private final ClientService clientService;
    private final RestService restService;

    public SendMessage sendDefaultMessage(User user, String text) {
        var sendMessage = sendMessageService.getButtonMessage(
                user.getChatId(), text,
                lang.message(MessageText.BUTTON_WORKERS, user.language()),
                lang.message(MessageText.BUTTON_COMPANIES, user.language()),
                lang.message(MessageText.BUTTON_CLIENTS, user.language())
        );

        if (user.getRole() == null) {
            sendMessage = sendMessageService.getButtonMessage(user.getChatId(), text,
                    lang.message(MessageText.BUTTON_LOGIN, user.language())
            );
        } else {

            if (user.getRole().equals(UserRole.WORKER)) {
                sendMessage = sendMessageService.getButtonMessage(user.getChatId(), text);
            }

            if (user.getRole().equals(UserRole.CLIENT)) {
                sendMessage = sendMessageService.getButtonMessage(user.getChatId(), text,
                        lang.message(MessageText.BUTTON_ADD_REQUEST, user.language()),
                        lang.message(MessageText.BUTTON_COMPANIES, user.language()));
            }
        }

        return sendMessage;
    }

    public SendMessage sendDefaultMessage(User user, String text, MessageText category) {
        if (category.equals(MessageText.BUTTON_WORKERS)) {
            return sendMessageService.getButtonMessage(user.getChatId(), text,
                    lang.message(MessageText.BUTTON_WORKERS_ADD, user.language()),
                    lang.message(MessageText.BUTTON_WORKERS_LIST, user.language()),
                    lang.message(MessageText.BUTTON_MAIN_MENU, user.language())
            );
        }

        if (category.equals(MessageText.BUTTON_COMPANIES)) {
            return sendMessageService.getButtonMessage(user.getChatId(), text,
                    lang.message(MessageText.BUTTON_COMPANIES_ADD, user.language()),
                    lang.message(MessageText.BUTTON_COMPANIES_LIST, user.language()),
                    lang.message(MessageText.BUTTON_MAIN_MENU, user.language())
            );
        }

        if (category.equals(MessageText.BUTTON_CLIENTS)) {
            return sendMessageService.getButtonMessage(user.getChatId(), text,
                    lang.message(MessageText.BUTTON_CLIENTS_ADD, user.language()),
                    lang.message(MessageText.BUTTON_CLIENTS_LIST, user.language()),
                    lang.message(MessageText.BUTTON_MAIN_MENU, user.language())
            );
        }
        return sendDefaultMessage(user, text);
    }

    public String workerInfo(User user, User worker) {
        return String.format("<b>%s</b>: %s\n", lang.message(MessageText.INFO_NAME, user.language()), worker.getFullName()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.INFO_PHONE, user.language()), worker.getPhone()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.INFO_ROLE, user.language()), worker.getRole().name()) +
                String.format("<b>%s</b>: <code>%s</code>\n", lang.message(MessageText.INFO_PASSWORD, user.language()), worker.getPassword());
    }

    public String companyInfo(User user, Company company) {
        return String.format("<b>%s</b>: %s\n", lang.message(MessageText.INFO_COMPANY_NAME, user.language()), company.getName()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.COMPANY_REGISTRATED_DATE, user.language()), company.getRegistratedDate()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.COMPANY_REGISTRATED_BY, user.language()), company.getRegistratedBy()) +
                String.format("<b>%s</b>: <code>%s</code>\n", lang.message(MessageText.INFO_COMPANY_INN, user.language()), company.getInn()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.COMPANY_THSHT, user.language()), company.getThsht()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.COMPANY_DBIBT, user.language()), company.getDbibt()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.COMPANY_IFUT, user.language()), company.getIfut()) +
                String.format("<b>%s</b>: %s\n\n", lang.message(MessageText.COMPANY_USTAV_FONDI, user.language()), company.getUstavFondi()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.COMPANY_ADDRESS, user.language()), company.getAddress()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.COMPANY_PHONE, user.language()), company.getPhone()) +
                String.format("<b>%s</b>: %s\n\n", lang.message(MessageText.COMPANY_EMAIL, user.language()), company.getEmail()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.COMPANY_LEADER, user.language()), company.getLeader()) +
                String.format("<b>%s</b>:\n%s\n", lang.message(MessageText.COMPANY_FOUNDERS, user.language()), company.getFounders()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.COMPANY_ACCOUNT_NUMBER, user.language()), company.getAccountNumber()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.COMPANY_BANK_CODE, user.language()), company.getBankCode()) +
                String.format("<b>%s</b>: %s\n", lang.message(MessageText.COMPANY_BANK_NAME, user.language()), company.getBankName());
    }

    public SendMessage confimationMessage(User user, String infoBody, String yesCallBack, String noCallBack) {
        String text = String.format("<b>%s</b>\n", lang.message(MessageText.INFORMATIONS, user.language())) +
                infoBody + "\n" +
                lang.message(MessageText.OPERATION_CONFIRMATION, user.language());

        return sendMessageService.getHorizontalInlineMessage(user.getChatId(), text,
                KeyboardUtil.inline(lang.message(MessageText.YES, user.language()), yesCallBack),
                KeyboardUtil.inline(lang.message(MessageText.NO, user.language()), noCallBack)
        );
    }

    public String clientInfo(User user, User client) {
        String companies = clientService.findByUserId(client.getId())
                .stream()
                .map(clientCompany ->
                        MessageFormat.format("{0}: <i>{1}</i>\nID: <i>{2}</i>\n",
                                lang.message(MessageText.INFO_COMPANY_NAME, user.language()),
                                clientCompany.getCompany().getName(), clientCompany.getClientNumber())
                )
                .collect(Collectors.joining("\n"));

        return MessageFormat.format("{0}\n<b>{1}:</b>\n\n{2}", workerInfo(user, client),
                lang.message(MessageText.BUTTON_COMPANIES, user.language()), companies);
    }

    public String getChatURL(User user) {
        var link = "https://t.me/";
        var chat = restService.getChat(user.getChatId());
        if (chat.getUserName() != null)
            link += chat.getUserName();
        else {
            switch (user.getPhone().length()) {
                case 9 -> link += "+998" + user.getPhone();
                case 12 -> link += "+" + user.getPhone();
                default -> link += user.getPhone();
            }
        }
        return link;
    }

    public String ticketInfo(Ticket ticket) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


        StringBuilder builder = new StringBuilder("<b>Mijoz ID: </b><i>")
                .append(ticket.getFrom().getClientNumber())
                .append("</i>\n<b>Kompaniya: </b>")
                .append(ticket.getFrom().getCompany().getName())
                .append("\n<b>Mijoz: </b><a href=\"")
                .append(getChatURL(ticket.getFrom().getUser()))
                .append("\">")
                .append(ticket.getFrom().getUser().getFullName())
                .append("</a>\n<b>Status: </b>")
                .append(ticket.getStatus().value())
                .append("\n\n<b>Xabar:</b>\n")
                .append(ticket.getBody());

        if (ticket.getStatus().ordinal() >= TicketStatus.ACCEPTED.ordinal()) {
            builder
                    .append("\n<b>Qabul qilgan vaqti: </b>")
                    .append(ticket.getAcceptedAt().format(formatter));

            if (ticket.getStatus().equals(TicketStatus.COMPLETED)) {
                builder.append("\n<b>Tugatilgan vaqti: </b>")
                        .append(ticket.getFinishedAt().format(formatter));
            }
        }

        return builder.toString();
    }

}
