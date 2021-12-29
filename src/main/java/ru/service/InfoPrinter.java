package ru.service;

import ru.configs.Aliace;
import ru.configs.CustomSecret;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InfoPrinter {

    /**
     * Для каждого сертификата вычисляет количетсво дней до истеченя срока действия и печатает информацию в консоль
     * @param customSecretsList - список кастомных секретов
     */
    public void printInfo(List<CustomSecret> customSecretsList) {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();
        for (CustomSecret secret: customSecretsList) {
            System.out.println(secret.getName() + " в " + secret.getNamespace() + ":");
            for(Aliace aliace: secret.getAliaceList()) {
                long dateDiff = aliace.getExpirationDate().getTime() - today.getTime();
                long expiresIn = dateDiff / (24 * 60 * 60 * 1000);
                System.out.println("Certifiate: " + String.format("%1$30s", aliace.getName()) + "\tExpires On: " + aliace.getExpirationDate() + "\tFormated Date: " +
                        ft.format(aliace.getExpirationDate()) + "\tExpires In: "+ expiresIn);
            }
        }
    }
}
