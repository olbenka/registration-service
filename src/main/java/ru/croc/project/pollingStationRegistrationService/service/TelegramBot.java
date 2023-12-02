package ru.croc.project.pollingStationRegistrationService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.croc.project.pollingStationRegistrationService.config.BotConfig;
import ru.croc.project.pollingStationRegistrationService.entities.PollingStation;
import ru.croc.project.pollingStationRegistrationService.entities.RegistrationState;
import ru.croc.project.pollingStationRegistrationService.entities.RegistrationStep;
import ru.croc.project.pollingStationRegistrationService.entities.User;
import ru.croc.project.pollingStationRegistrationService.entities.classes.PollingStationDAO;
import ru.croc.project.pollingStationRegistrationService.entities.classes.UserDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PollingStationDAO pollingStationDAO;
    final BotConfig config;
    private final Map<Long, RegistrationState> userRegistrationStates = new HashMap<>();

    static final String HELP_MESSAGE = "Этот бот предназначен для регистрации избирательных участков \n\n" +
            "Нажмите 'Меню' в левом нижнем углу или отправьте команду /start.";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начать"));
        listOfCommands.add(new BotCommand("/help", "Как пользоваться ботом"));
        listOfCommands.add(new BotCommand("/registration", "Регистрация нового избирательного участка"));
        listOfCommands.add(new BotCommand("/info", "Информация о Ваших зарегистрированных участках"));
        listOfCommands.add(new BotCommand("/delete", "Удаление Ваших участков"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            RegistrationState registrationState = userRegistrationStates.get(chatId);
            if (registrationState != null) {
                handleRegistrationState(chatId, messageText, registrationState);
            } else {
                switch (messageText) {
                    case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/registration":
                        registrationCommandReceived(chatId);
                        break;
                    case "/info":
                        infoCommandReceived(chatId);
                        break;
                    case "/delete":
                        deleteCommandReceived(chatId);
                        break;
                    case "/help":
                        sendMessage(chatId, HELP_MESSAGE);
                        break;
                    default:
                        sendMessage(chatId, "Извините, команда не распознана. \n " +
                                "Выполните команду /start или выберите в 'Меню' в левом нижнем углу.");

                }
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {
        if (userDAO.findById(chatId) == null) {
            User newUser = new User(chatId, name);
            userDAO.registerUser(newUser);
        }

        String answer = "Добро пожаловать, " + name + "!\n" +
                "Это бот-помощник для регистрации избирательных участков. \n" +
                "Выберите дальнейшее действие во вкладке Меню в левом нижнем углу.  ";

        sendMessage(chatId, answer);
    }

    private void registrationCommandReceived(long chatId) {
        RegistrationState registrationState = new RegistrationState();
        userRegistrationStates.put(chatId, registrationState);
        sendMessage(chatId, "Введите уникальный номер участка: ");
        registrationState.setCurrentStep(RegistrationStep.ENTER_UNIQUE_NUMBER);
    }

    private void handleRegistrationState(long chatId, String messageText, RegistrationState registrationState) {
        switch (registrationState.getCurrentStep()) {
            case ENTER_UNIQUE_NUMBER:
                registrationState.setUniqueNumber(messageText);
                long uniqueNumber = Long.parseLong(messageText);
                if (pollingStationDAO.getPollingStationById(uniqueNumber) != null) {
                    sendMessage(chatId, "Участок с таким номером уже зарегистрирован. ");
                    userRegistrationStates.remove(chatId);
                    return;
                }
                sendMessage(chatId, "Введите адрес участка \n" +
                        "(Формат: Регион, населенный пункт, улица, дом): ");
                registrationState.setCurrentStep(RegistrationStep.ENTER_ADDRESS);
                break;
            case ENTER_ADDRESS:
                registrationState.setAddress(messageText);
                sendMessage(chatId, "Введите вместимость участка:");
                registrationState.setCurrentStep(RegistrationStep.ENTER_CAPACITY);
                break;
            case ENTER_CAPACITY:
                try {
                    int capacity = Integer.parseInt(messageText);
                    registrationState.setCapacity(capacity);
                    sendMessage(chatId, "Введите количество урн в участке:");
                    registrationState.setCurrentStep(RegistrationStep.ENTER_URN_COUNT);
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "Некорректный формат. Введите число.");
                }
                break;
            case ENTER_URN_COUNT:
                try {
                    int urnCount = Integer.parseInt(messageText);
                    registrationState.setUrnCount(urnCount);
                    sendMessage(chatId, "Есть ли специальное оборудование для инвалидов? да/нет:");
                    registrationState.setCurrentStep(RegistrationStep.ENTER_SPECIAL_EQUIPMENT);
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "Некорректный формат. Введите число.");
                }
                break;
            case ENTER_SPECIAL_EQUIPMENT:
                boolean hasSpecialEquipment;
                if (messageText.equalsIgnoreCase("да")){
                    hasSpecialEquipment = true;
                } else if (messageText.equalsIgnoreCase("нет")) {
                    hasSpecialEquipment = false;
                } else {
                    sendMessage(chatId, "Ваш выбор не распознан. \n" +
                            "Начните регистрацию заново /registration.");
                    userRegistrationStates.remove(chatId);
                    return;
                }
                registrationState.setHasSpecialEquipment(hasSpecialEquipment);
                sendMessage(chatId, "Введите контактный номер телефона:");
                registrationState.setCurrentStep(RegistrationStep.ENTER_PHONE_NUMBER);
                break;
            case ENTER_PHONE_NUMBER:
                registrationState.setPhoneNumber(messageText);
                sendMessage(chatId, "Введите часы работы:");
                registrationState.setCurrentStep(RegistrationStep.ENTER_WORKING_HOURS);
                break;
            case ENTER_WORKING_HOURS:
                registrationState.setWorkingHours(messageText);

                PollingStation pollingStation = new PollingStation(
                        Long.parseLong(registrationState.getUniqueNumber()),
                        chatId,
                        registrationState.getAddress(),
                        registrationState.getCapacity(),
                        registrationState.getUrnCount(),
                        registrationState.isHasSpecialEquipment(),
                        registrationState.getPhoneNumber(),
                        registrationState.getWorkingHours()
                );
                pollingStationDAO.registerPollingStation(pollingStation);

                userRegistrationStates.remove(chatId);

                sendMessage(chatId, "Участок успешно зарегистрирован!");
                break;
            case DELETE_ENTER_UNIQUE_NUMBER:
                long uniqueNumberToDelete = Long.parseLong(messageText);
                if (pollingStationDAO.getPollingStationById(uniqueNumberToDelete) == null) {
                    sendMessage(chatId, "Участок с таким номером не зарегистрирован. " +
                            "Проверьте информацию с помощью команды /info.");
                    userRegistrationStates.remove(chatId);
                    return;
                }
                pollingStationDAO.deletePollingStation(chatId, uniqueNumberToDelete);
                userRegistrationStates.remove(chatId);
                sendMessage(chatId, "Участок успешно удален.");
                break;
            default:
                sendMessage(chatId, "Извините, команда не распознана");
        }
    }

    private void infoCommandReceived(long chatId) {
        List<PollingStation> userPollingStations = pollingStationDAO.getAllPollingStations(chatId);

        if (userPollingStations.isEmpty()) {
            sendMessage(chatId, "У вас пока нет зарегистрированных участков. Для регистрации выполните команду /registration.");
        } else {
            StringBuilder message = new StringBuilder("Ваши зарегистрированные участки:\n");
            for (PollingStation pollingStation : userPollingStations) {
                message.append(pollingStation.toString()).append("\n\n");
            }
            sendMessage(chatId, message.toString());
        }
    }

    private void deleteCommandReceived(long chatId) {
        List<PollingStation> userPollingStations = pollingStationDAO.getAllPollingStations(chatId);
        if (userPollingStations.isEmpty()) {
            sendMessage(chatId, "У вас пока нет зарегистрированных участков. Для регистрации выполните команду /registration.");
        } else {
            sendMessage(chatId, "Введите уникальный номер участка, который Вы хотите удалить: ");
            RegistrationState registrationState = new RegistrationState();
            registrationState.setCurrentStep(RegistrationStep.DELETE_ENTER_UNIQUE_NUMBER);
            userRegistrationStates.put(chatId, registrationState);
        }
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
