package uz.everbest.requestmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.everbest.requestmanagement.domain.entity.Client;
import uz.everbest.requestmanagement.domain.entity.Company;
import uz.everbest.requestmanagement.domain.entity.Ticket;
import uz.everbest.requestmanagement.domain.entity.User;
import uz.everbest.requestmanagement.domain.enums.*;
import uz.everbest.requestmanagement.util.KeyboardUtil;
import uz.everbest.requestmanagement.util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BotService {


    private final RestService restService;
    private final UserService userService;
    private final CompanyService companyService;
    private final ClientService clientService;
    private final TicketService ticketService;
    private final LanguageService lang;
    private final SendMessageService sendMessageService;
    private final MessageService messageService;

    @Value("${telegram.channel}")
    private String CHANNEL_CHAT_ID;

    public void update(Update update) {

        if (update.hasMessage()) {
            doWithMessage(update.getMessage());
        }

        if (update.hasCallbackQuery()) {
            doWithCallBack(update.getCallbackQuery());
        }

    }

    private void doWithMessage(Message message) {
        var tgUser = message.getFrom();
        var chatId = tgUser.getId();
        var user = userService.findByChatId(chatId);

        user = recordUser(user, tgUser);

        if (message.hasText()) {
            var text = message.getText();

            if (text.startsWith("/")) {
                switch (text) {
                    case "/start" -> {

                        if (user.getRole() != null) {
                            restService.sendMessage(chatId,
                                    messageService.sendDefaultMessage(user, lang
                                            .message(MessageText.GREETING, user.language()) + " <b>" + user.getFullName() + "</b>"));
                            return;
                        }

                        restService.sendMessage(chatId,
                                sendMessageService.getButtonMessage(chatId,
                                        lang.message(MessageText.GREETING, user.language()),
                                        lang.message(MessageText.BUTTON_LOGIN, user.language())
                                )
                        );
                    }

                    case "/lang" -> restService.sendMessage(
                                chatId,
                                sendMessageService.getVerticalInlineMessage(
                                        chatId,
                                        lang.message(MessageText.BUTTON_SELECT_LANGUAGE, user.language()),
                                        KeyboardUtil.inline(lang.en(MessageText.UZBEK), "lang-en"),
                                        KeyboardUtil.inline(lang.ru(MessageText.RUSSIAN), "lang-ru")
                                )
                    );

                    case "/logout" -> {
                        if (user.getRole() != null) {
                            user = userService.logout(user);
                            restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                                    lang.message(MessageText.YOU_ARE_NOT_LOGGED_IN, user.language())));
                            return;
                        }
                    }
                }
                return;
            }

            if (lang.equals(text, MessageText.BUTTON_LOGIN)) {

                if (user.getRole() != null) {
                    restService.sendMessage(chatId,
                            messageService.sendDefaultMessage(user, lang
                                    .message(MessageText.YOU_ARE_ALREADY_LOGGED_IN, user.language()) + " <b>" + user.getFullName() + "</b>"));
                    return;
                }

                user.setLastAction("login");
                userService.save(user);

                restService.sendMessage(chatId,
                        sendMessageService.getMessage(chatId,
                                lang.message(MessageText.REQUEST_PASSWORD, user.language()))
                );
                return;
            }

            urgent(user);

            if (user.getRole() != null && (user.getRole().equals(UserRole.ADMIN) || user.getRole().equals(UserRole.MODERATOR))) {

                if (lang.equals(text, MessageText.BUTTON_WORKERS)) {
                    restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                            lang.message(MessageText.BUTTON_WORKERS, user.language()),
                            MessageText.BUTTON_WORKERS
                    ));
                    return;
                }

                if (lang.equals(text, MessageText.BUTTON_COMPANIES)) {
                    restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                            lang.message(MessageText.BUTTON_COMPANIES, user.language()),
                            MessageText.BUTTON_COMPANIES
                    ));
                    return;
                }

                if (lang.equals(text, MessageText.BUTTON_CLIENTS)) {
                    restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                            lang.message(MessageText.BUTTON_CLIENTS, user.language()),
                            MessageText.BUTTON_CLIENTS
                    ));
                    return;
                }

                if (lang.equals(text, MessageText.BUTTON_MAIN_MENU)) {
                    user.setLastAction("mainMenu");
                    userService.save(user);
                    restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                            lang.message(MessageText.MAIN_MENU, user.language())));
                    return;
                }

                if (lang.equals(text, MessageText.BUTTON_WORKERS_ADD)) {
                    User worker = userService.save(
                            User.builder().position("Worker").build()
                    );

                    user.setLastAction("request-" + worker.getId() + "-name");
                    userService.save(user);
                    restService.sendMessage(chatId,
                            sendMessageService.getMessage(chatId,
                                    lang.message(MessageText.REQUEST_WORKER_NAME, user.language()))
                    );
                    return;
                }

                if (lang.equals(text, MessageText.BUTTON_COMPANIES_ADD)) {
                    Company company = companyService.save(new Company());
                    user.setLastAction("requestCompany-" + company.getId() + "-name");
                    userService.save(user);

                    restService.sendMessage(chatId, sendMessageService
                            .getMessage(chatId, lang.message(MessageText.REQUEST_COMPANY_NAME, user.language())));
                    return;
                }

                if (lang.equals(text, MessageText.BUTTON_CLIENTS_ADD)) {
                    User client = userService.save(
                            User.builder().position("Client").build());

                    user.setLastAction("request-" + client.getId() + "-name");
                    userService.save(user);
                    restService.sendMessage(chatId,
                            sendMessageService.getMessage(chatId,
                                    lang.message(MessageText.REQUEST_WORKER_NAME, user.language()))
                    );
                    return;
                }

                if (lang.equals(text, MessageText.BUTTON_WORKERS_LIST)) {
                    User finalUser = user;

                    Function<User, SendMessage> workerToSendMessage = worker -> sendMessageService.getVerticalInlineMessage(
                            chatId, messageService.workerInfo(finalUser, worker),
                            KeyboardUtil.inline(lang.message(MessageText.BUTTON_DELETE, finalUser.language()),
                                    "confirmationDeleteWorker-" + worker.getId()));

                    var workersMessages = new java.util.ArrayList<>(
                            userService.findByRole(UserRole.WORKER)
                                    .stream().map(workerToSendMessage).toList());

                    if (user.getRole().equals(UserRole.ADMIN)) {
                        workersMessages.addAll(0, userService.findByRole(UserRole.MODERATOR)
                                .stream().map(workerToSendMessage).toList());
                    }

                    sendMessageList(user, workersMessages, lang.message(MessageText.NOT_FOUND_WORKERS, user.language()));

                    return;
                }

                if (lang.equals(text, MessageText.BUTTON_COMPANIES_LIST)) {
                    User finalUser1 = user;
                    Function<Company, SendMessage> companyToSendMessage = company -> sendMessageService.getVerticalInlineMessage(
                            chatId, messageService.companyInfo(finalUser1, company),
                            KeyboardUtil.inline(lang.message(MessageText.BUTTON_DELETE, finalUser1.language()),
                                    "confirmationDeleteCompany-" + company.getId()));

                    var companyMessages = companyService.getActiveCompanies()
                            .stream().map(companyToSendMessage).toList();

                    sendMessageList(user, companyMessages, lang.message(MessageText.NOT_FOUND_COMPANIES, user.language()));

                    return;
                }

                if (lang.equals(text, MessageText.BUTTON_CLIENTS_LIST)) {
                    User finalUser = user;

                    Function<User, SendMessage> clientToSendMessage = client -> sendMessageService.getVerticalInlineMessage(
                            chatId, messageService.clientInfo(finalUser, client),
                            KeyboardUtil.inline(lang.message(MessageText.BUTTON_DELETE, finalUser.language()),
                                    "confirmationDeleteClient-" + client.getId()));

                    var clientsMessages = new java.util.ArrayList<>(
                            userService.findByRole(UserRole.CLIENT)
                                    .stream().map(clientToSendMessage).toList());

                    sendMessageList(user, clientsMessages, lang.message(MessageText.NOT_FOUND_CLIENTS, user.language()));

                    return;
                }
            }

            if (user.getRole() != null && user.getRole().equals(UserRole.CLIENT)) {

                if (lang.equals(text, MessageText.BUTTON_ADD_REQUEST)) {

                    List<InlineKeyboardButton> clientsButtons = new ArrayList<>(clientService.findByUserId(user.getId())
                            .stream()
                            .map(client -> KeyboardUtil.inline(client.getCompany().getName(), "chooseTicket-" + client.getId()))
                            .toList());

                    clientsButtons.add(KeyboardUtil.inline(lang.message(MessageText.BUTTON_CANCEL, user.language()), "chooseTicket-0"));

                    Message response = restService.sendMessage(chatId, sendMessageService.getVerticalInlineMessage(chatId,
                            lang.message(MessageText.REQUEST_CHOOSE_PROFILE, user.language()),
                            clientsButtons.toArray(InlineKeyboardButton[]::new)));

                    user.setLastAction(response.getMessageId().toString());
                    userService.save(user);

                    return;
                }

                if (lang.equals(text, MessageText.BUTTON_COMPANIES)) {
                    var finalUser = user;
                    Function<Client, SendMessage> companyToSendMessage = client -> sendMessageService.getMessage(
                            chatId, messageService.companyInfo(finalUser, client.getCompany()));

                    var companies = clientService.findByUserId(user.getId())
                            .stream()
                            .map(companyToSendMessage)
                            .toList();

                    sendMessageList(user, companies, lang.message(MessageText.NOT_FOUND_COMPANIES, user.language()));
//                    messageService.sendDefaultMessage(user, );
                    return;
                }

            }

            if (user.getLastAction() != null) {
                if (user.getLastAction().startsWith("request-")) {
                    workerRequests(chatId, user, text);
                    return;
                }

                if (user.getLastAction().startsWith("requestCompany-")) {
                    companyRequests(chatId, user, text);
                    return;
                }

                if (user.getLastAction().startsWith("requestTicket-")) {
                    if (UserRole.CLIENT.equals(user.getRole())) {
                        ticketRequest(user, text);
                    }
                    return;
                }

                if (user.getLastAction().equals("login")) {
                    User account = userService.findByPassword(text);
                    if (account != null && account.getChatId() == null) {

                        user = userService.authorize(user, account);
                            restService.sendMessage(chatId,
                                messageService.sendDefaultMessage(user,lang
                                        .message(MessageText.SUCCESSFULLY_LOGGED_IN, user.language()) + " <b>" + user.getFullName() + "</b>"));
                    } else {
                        restService.sendMessage(chatId, sendMessageService
                                .getMessage(chatId, lang.message(MessageText.WRONG_PASSWORD, user.language())));
                    }
                }
            }
            return;
        }


        if (user.getLastAction().split("-")[2].equals("files")) {
            if (message.hasDocument()) {
                ticketRequest(user, InputMediaType.DOCUMENT.name() + ":" + message.getDocument().getFileId());
            }

            if (message.hasPhoto()) {
                ticketRequest(user, InputMediaType.PHOTO.name() + ":" + message.getPhoto().get(0).getFileId());
            }

            if (message.hasVideo()) {
                ticketRequest(user, InputMediaType.VIDEO.name() + ":" + message.getVideo().getFileId());
            }

            if (message.hasVoice()) {
                ticketRequest(user, InputMediaType.VOICE.name() + ":" + message.getVoice().getFileId());
            }

            if (message.hasAudio()) {
                ticketRequest(user, InputMediaType.AUDIO.name() + ":" + message.getAudio().getFileId());
            }
        }

    }

    private void doWithCallBack(CallbackQuery callback) {
        var tgUser = callback.getFrom();
        var chatId = tgUser.getId();
        var user = userService.findByChatId(chatId);
        var data = callback.getData();

        urgent(user);
        user = recordUser(user, tgUser);

        if (data.startsWith("lang")) {
            String langCode = data.split("-")[1];
            user.setLangCode(LanguageCode.getByCode(langCode));
            userService.save(user);
            restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                    lang.message(MessageText.COMMAND_COMPLETED, user.language())));
        }

        if (data.startsWith("role")) {
            String roleName = data.split("-")[1];
            workerRequests(chatId, user, roleName);
            return;
        }


        if (data.startsWith("confirmationDeleteWorker-")) {
            var userId = Long.parseLong(data.split("-")[1]);
            User worker = userService.findById(userId);

            Message response = restService.sendMessage(chatId,
                    messageService.confimationMessage(user, messageService.workerInfo(user, worker),
                                    "deleteWorker-" + userId, "deleteWorker-0"));

            user.setLastAction(response.getMessageId().toString());
            userService.save(user);
            return;
        }

        if (data.startsWith("deleteWorker-")) {
            var userId = Long.parseLong(data.split("-")[1]);
            if (userId != 0) {
                userService.deactive(userId);
                restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                        lang.message(MessageText.COMMAND_COMPLETED, user.language()),
                        MessageText.BUTTON_WORKERS
                ));
            } else {
                restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                        lang.message(MessageText.COMMAND_CANCELLED, user.language()),
                        MessageText.BUTTON_WORKERS
                ));
            }

            Integer messageId = Integer.valueOf(user.getLastAction());
            restService.deleteMessage(chatId, messageId);
        }

        if (data.startsWith("confirmationDeleteCompany-")) {
            var companyId = Long.parseLong(data.split("-")[1]);
            Company company = companyService.findById(companyId);

            Message response = restService.sendMessage(chatId,
                    messageService.confimationMessage(user, messageService.companyInfo(user, company),
                                    "deleteCompany-" + companyId, "deleteCompany-0"));

            user.setLastAction(response.getMessageId().toString());
            userService.save(user);
            return;
        }

        if (data.startsWith("deleteCompany-")) {
            var companyId = Long.parseLong(data.split("-")[1]);
            if (companyId != 0) {
                companyService.delete(companyId);
                restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                        lang.message(MessageText.COMMAND_COMPLETED, user.language()),
                        MessageText.BUTTON_COMPANIES
                ));
            } else {
                restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                        lang.message(MessageText.COMMAND_CANCELLED, user.language()),
                        MessageText.BUTTON_COMPANIES
                ));
            }

            Integer messageId = Integer.valueOf(user.getLastAction());
            restService.deleteMessage(chatId, messageId);
            return;
        }

        if (data.startsWith("confirmationDeleteClient-")) {
            var userId = Long.parseLong(data.split("-")[1]);
            User client = userService.findById(userId);

            Message response = restService.sendMessage(chatId,
                    messageService.confimationMessage(user, messageService.clientInfo(user, client),
                            "deleteClient-" + userId, "deleteClient-0"));

            user.setLastAction(response.getMessageId().toString());
            userService.save(user);
            return;
        }

        if (data.startsWith("deleteClient-")) {
            Integer messageId = Integer.valueOf(user.getLastAction());
            restService.deleteMessage(chatId, messageId);

            var userId = Long.parseLong(data.split("-")[1]);

            if (userId != 0) {
                userService.deactive(userId);
                clientService.findByUserId(userId).forEach(client -> {
                    client.setIsActive(false);
                    clientService.save(client);
                });

                restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                        lang.message(MessageText.COMMAND_COMPLETED, user.language()),
                        MessageText.BUTTON_CLIENTS
                ));
            } else {
                restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                        lang.message(MessageText.COMMAND_CANCELLED, user.language()),
                        MessageText.BUTTON_CLIENTS
                ));
            }

            return;
        }

        if (data.startsWith("clientCompanyConfirmation-") || data.startsWith("clientCompany-")) {
            workerRequests(chatId, user, data.split("-")[1]);
            return;
        }

        if (data.startsWith("chooseTicket-")) {
            restService.deleteMessage(user.getChatId(), Integer.parseInt(user.getLastAction()));
            Long clientId = Long.parseLong(data.split("-")[1]);

            if (clientId.equals(0L)) {
                restService.sendMessage(user.getChatId(), messageService.sendDefaultMessage(user,
                        lang.message(MessageText.COMMAND_CANCELLED, user.language())));
                return;
            }
            restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                    lang.message(MessageText.REQUEST_MESSAGE_BODY, user.language())));

            Ticket ticket = Ticket.builder()
                    .status(TicketStatus.NEW)
                    .fromId(clientId)
                    .build();
            ticket = ticketService.save(ticket);

            user.setLastAction(String.join("-", "requestTicket", ticket.getId().toString(), "name"));
            userService.save(user);
            return;
        }

        if (data.startsWith("ticket-")) {

            if (user.getRole() == null || !user.getRole().equals(UserRole.WORKER)) {
                restService.sendToast(callback.getId(), "Siz Xodim sifatida tizimga kirmagansiz!");
                return;
            }

            String[] commands = data.split("-");
            Ticket ticket = ticketService.findById(Long.parseLong(commands[1]));

            switch (commands[2]) {
                case "accept" -> {
                    if (ticket.getAcceptedBy() != null) {
                        restService.sendToast(callback.getId(), "Ticket allaqachon qabul qilingan");
                        return;
                    }

                    ticket.setAcceptedById(user.getId());
                    ticket.setAcceptedAt(LocalDateTime.now());
                    ticket.setStatus(TicketStatus.ACCEPTED);
                    ticket = ticketService.save(ticket);

                    restService.editMessageText(ticket.getMessageId(), sendMessageService.getHorizontalInlineMessage(
                            Long.parseLong(CHANNEL_CHAT_ID), messageService.ticketInfo(ticket),
                            KeyboardUtil.inline("COMPLETE", "ticket-" + ticket.getId() + "-complete")
                    ));
                }

                case "complete" -> {
                    if (ticket.getAcceptedBy() != null && !ticket.getAcceptedById().equals(user.getId())) {
                        restService.sendToast(callback.getId(), "Ticket siz qabul qilmagansiz!");
                        return;
                    }

                    ticket.setFinishedAt(LocalDateTime.now());
                    ticket.setStatus(TicketStatus.COMPLETED);
                    ticket = ticketService.save(ticket);

                    restService.editMessageText(ticket.getMessageId(), sendMessageService.getMessage(
                            Long.parseLong(CHANNEL_CHAT_ID), messageService.ticketInfo(ticket)));
                }
            }
        }

    }

    private void ticketRequest(User user, String data) {
        String[] commands = user.getLastAction().split("-");
        Long ticketId = Long.parseLong(commands[1]);
        Ticket ticket = ticketService.findById(ticketId);

        switch (commands[2]) {
            case "name" -> {
                ticket.setBody(data);
                ticketService.save(ticket);

                restService.sendMessage(user.getChatId(), sendMessageService.getButtonMessage(user.getChatId(),
                        lang.message(MessageText.REQUEST_UPLOAD_DOCUMENT, user.language()),
                        lang.message(MessageText.BUTTON_SEND_REQUEST, user.language())));

                user.setLastAction(String.join("-", "requestTicket", ticketId.toString(), "files"));
                userService.save(user);
            }

            case "files" -> {

                if (lang.equals(data, MessageText.BUTTON_SEND_REQUEST)) {

                    restService.sendMessage(Long.valueOf(CHANNEL_CHAT_ID), sendMessageService.getMessage(
                            Long.valueOf(CHANNEL_CHAT_ID),
                            String.format("-------<b> Ticket </b>#%03d -------", ticketService.todaysTicketCount())));

                    if (ticket.getFilesIds() != null) {
                        String[] filesIds = ticket.getFilesIds().split("---");

                        Arrays.stream(filesIds)
                                .forEach(str -> {
                                    String[] split = str.split(":");
                                    restService.execute(CHANNEL_CHAT_ID, InputMediaType.valueOf(split[0]).sendMedia(split[1]));
                                });
                    }

                    Message channelResponse = restService.sendMessage(Long.valueOf(CHANNEL_CHAT_ID), sendMessageService.getHorizontalInlineMessage(
                            Long.valueOf(CHANNEL_CHAT_ID),
                            messageService.ticketInfo(ticket),
                            KeyboardUtil.inline("ACCEPT", "ticket-" + ticket.getId() + "-accept")));

                    ticket.setMessageId(channelResponse.getMessageId());
                    ticketService.save(ticket);

                    restService.sendMessage(user.getChatId(), messageService
                            .sendDefaultMessage(user, lang.message(MessageText.COMMAND_COMPLETED, user.language())));


                    user.setLastAction("sent request");
                    userService.save(user);

                    return;
                }

                ticket.setFilesIds(ticket.getFilesIds() == null ? data : ticket.getFilesIds() + "---" + data);
                ticketService.save(ticket);

                user.setLastAction(String.join("-", "requestTicket", ticketId.toString(), "files"));
                userService.save(user);
            }
        }

    }

    private void companyRequests(Long chatId, User user, String text) {
        String[] commands = user.getLastAction().split("-");
        Long companyId = Long.valueOf(commands[1]);
        Company company = companyService.findById(companyId);

        switch (commands[2]) {
            case "name" -> {
                company.setName(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-RegistratedDate");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_REGISTRATED_DATE, user.language())));

            }

            case "RegistratedDate" -> {
                company.setRegistratedDate(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-RegistratedBy");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_REGISTRATED_BY, user.language())));

            }

            case "RegistratedBy" -> {
                company.setRegistratedBy(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-Inn");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.REQUEST_COMPANY_INN, user.language())));

            }

            case "Inn" -> {
                company.setInn(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-Thsht");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_THSHT, user.language())));
            }

            case "Thsht" -> {
                company.setThsht(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-Dbibt");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_DBIBT, user.language())));

            }

            case "Dbibt" -> {
                company.setDbibt(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-Ifut");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_IFUT, user.language())));

            }

            case "Ifut" -> {
                company.setIfut(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-UstavFondi");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_USTAV_FONDI, user.language())));

            }

            case "UstavFondi" -> {
                company.setUstavFondi(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-Address");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_ADDRESS, user.language())));

            }

            case "Address" -> {
                company.setAddress(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-Phone");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_PHONE, user.language())));

            }

            case "Phone" -> {
                company.setPhone(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-Email");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_EMAIL, user.language())));

            }

            case "Email" -> {
                company.setEmail(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-Leader");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_LEADER, user.language())));

            }

            case "Leader" -> {
                company.setLeader(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-Founders");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_FOUNDERS, user.language())));

            }

            case "Founders" -> {
                company.setFounders(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-AccountNumber");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_ACCOUNT_NUMBER, user.language())));
            }

            case "AccountNumber" -> {
                company.setAccountNumber(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-BankCode");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_BANK_CODE, user.language())));

            }

            case "BankCode" -> {
                company.setBankCode(text);
                companyService.save(company);

                user.setLastAction("requestCompany-" + company.getId() + "-BankName");
                userService.save(user);
                restService.sendMessage(chatId, sendMessageService.getMessage(chatId,
                        lang.message(MessageText.COMPANY_BANK_NAME, user.language())));

            }

            case "BankName" -> {
                company.setBankName(text);
                company.setStatus(CompanyStatus.ACTIVE);
                companyService.save(company);

                user.setLastAction("company-" + companyId + "-registrated");
                userService.save(user);
                restService.sendMessage(chatId, messageService.sendDefaultMessage(user,
                        lang.message(MessageText.COMMAND_COMPLETED, user.language()),
                        MessageText.BUTTON_COMPANIES));
            }
        }

    }

    public void workerRequests(Long chatId, User user, String text){
        String[] commands = user.getLastAction().split("-");

        Long workerId = Long.valueOf(commands[1]);
        User worker = userService.findById(workerId);
        SendMessage answerMessage;

        switch (commands[2]) {
            case "name" -> {
                worker.setFullName(text);
                userService.save(worker);

                user.setLastAction("request-" + workerId + "-phone");
                userService.save(user);
                answerMessage = sendMessageService.getMessage(chatId,
                        lang.message(MessageText.REQUEST_PHONE, user.language()));
            }

            case "phone" -> {
                worker.setPhone(text);
                userService.save(worker);

                if (worker.getPosition() != null && worker.getPosition().equals("Client")) {

                    List<InlineKeyboardButton> companyButtons = companyService.getExceptsOfClient(workerId)
                            .stream()
                            .map(company -> KeyboardUtil.inline(company.getName(), "clientCompanyConfirmation-" + company.getId()))
                            .toList();

                    var response = restService.sendMessage(chatId, sendMessageService.getVerticalInlineMessage(chatId,
                            lang.message(MessageText.REQUEST_CLIENT_COMPANIES, user.language()),
                            companyButtons.toArray(InlineKeyboardButton[]::new)
                    ));

                    user.setLastAction(String.join("-", "request", workerId.toString(),
                            "clientCompanyConfirmation", response.getMessageId().toString()));
                    userService.save(user);
                    return;
                }


                List<InlineKeyboardButton> buttons = new ArrayList<>();
                buttons.add(KeyboardUtil.inline(UserRole.WORKER.name(), "role-" + UserRole.WORKER.name()));
                if (user.getRole().equals(UserRole.ADMIN)) {
                    buttons.add(KeyboardUtil.inline(UserRole.MODERATOR.name(), "role-" + UserRole.MODERATOR.name()));
                }

                answerMessage = sendMessageService.getHorizontalInlineMessage(chatId,
                        lang.message(MessageText.REQUEST_ROLE, user.language()),
                        buttons.toArray(InlineKeyboardButton[]::new)
                );

                user.setLastAction("request-" + workerId + "-role");
                userService.save(user);
            }

            case "role" -> {
                UserRole role = UserRole.valueOf(text.toUpperCase());
                String password = PasswordUtil.generatePassword(text.toLowerCase() + worker.getId());


                worker.setRole(role);
                worker.setPassword(password);
                userService.save(worker);

                user.setLastAction("registrated-" + worker.getId() + "-worker");
                userService.save(user);

                answerMessage = messageService.sendDefaultMessage(user,
                        lang.message(MessageText.SUCCESSFULLY_REGISTRATED, user.language()) + ". [<code>" + password + "</code>]",
                        MessageText.BUTTON_WORKERS);
            }

            case "clientCompanyConfirmation" -> {
                if (lang.equals(text, MessageText.BUTTON_CLIENTS_FINISH)) {
                    worker.setRole(UserRole.CLIENT);
                    worker.setPassword(PasswordUtil.generatePassword("client" + workerId));
                    userService.save(worker);

                    restService.deleteMessage(chatId, Integer.parseInt(commands[3]));
                    restService.sendMessage(chatId, messageService.sendDefaultMessage(user, lang.message(MessageText.SUCCESSFULLY_REGISTRATED,
                            user.language()) + ". [<code>" + worker.getPassword() + "</code>]", MessageText.BUTTON_CLIENTS));
                    return;
                }

                Company company = companyService.findById(Long.parseLong(text));

                var message = restService.sendMessage(chatId, messageService.confimationMessage(user, messageService.companyInfo(user,
                        company), "clientCompany-" + company.getId(), "clientCompany-0"));

                user.setLastAction(String.join("-", "request",
                        workerId.toString(), "clientCompany", commands[3], message.getMessageId().toString()));
                userService.save(user);
                return;
            }

            case "clientCompany" -> {
                Long companyId = Long.parseLong(text);
                restService.deleteMessage(chatId, Integer.parseInt(commands[4]));

                user.setLastAction("request-" + workerId + "-clientCompanyConfirmation-" + commands[3]);
                userService.save(user);

                if (companyId == 0) {
                    answerMessage = sendMessageService.getButtonMessage(
                            chatId, lang.message(MessageText.COMMAND_CANCELLED, user.language()),
                            lang.message(MessageText.BUTTON_CLIENTS_FINISH, user.language()));
                    break;
                }

                Company company = companyService.findById(companyId);
                Integer count = clientService.countByCompanyId(companyId) + 1;

                Client client = Client.builder()
                        .clientNumber(PasswordUtil.generateClientNumber(company.getInn(), count))
                        .userId(workerId)
                        .isActive(true)
                        .companyId(companyId)
                        .build();

                clientService.save(client);

                List<List<InlineKeyboardButton>> companyButtons = companyService.getExceptsOfClient(workerId)
                        .stream()
                        .map(c -> List.of(KeyboardUtil.inline(c.getName(), "clientCompanyConfirmation-" + c.getId())))
                        .toList();

                restService.editReplyMarkup(chatId, Integer.parseInt(commands[3]),
                        KeyboardUtil.inlineMarkup(companyButtons));

                answerMessage = sendMessageService.getButtonMessage(chatId,
                        lang.message(MessageText.COMMAND_COMPLETED, user.language()),
                        lang.message(MessageText.BUTTON_CLIENTS_FINISH, user.language())
                );
            }

            default -> {
                return;
            }
        }
        restService.sendMessage(chatId, answerMessage);
    }

    public void sendMessageList(User user, List<SendMessage> messages, String notFound) {
        if (!messages.isEmpty()) {
            List<String> messagesIds = messages.stream()
                    .map(message -> restService.sendMessage(user.getChatId(), message).getMessageId().toString())
                    .toList();

            user.setLastAction("urgentList-" + String.join(":", messagesIds));
            userService.save(user);
        } else {
            restService.sendMessage(user.getChatId(), sendMessageService.getButtonMessage(user.getChatId(), notFound));
        }
    }

    public void urgent(User user) {
        if (user.getLastAction() != null) {
            if (user.getLastAction().startsWith("urgentList-")) {
                String[] messagesIds = user.getLastAction().split("-")[1].split(":");
                Stream.of(messagesIds)
                        .forEach(messageId -> restService.deleteMessage(user.getChatId(), Integer.valueOf(messageId)));

                user.setLastAction("urgent-completed");
                userService.save(user);
            }
        }
    }

    private User recordUser(User user, org.telegram.telegrambots.meta.api.objects.User tgUser) {
        if (user.getLangCode() == null) {
            user.setLangCode(tgUser.getLanguageCode().equalsIgnoreCase("ru") ?
                    LanguageCode.RU : LanguageCode.EN);

        }

        if (user.getFullName() == null) {
            tgUser.getFirstName();
            String fullName = ((tgUser.getLastName() == null || tgUser.getLastName().equals("null")) ? "" : tgUser.getLastName() + " ")
                    + (tgUser.getFirstName().equals("null") ? "" : tgUser.getFirstName());

            user.setFullName(fullName.trim());
        }
        return userService.save(user);
    }

}
