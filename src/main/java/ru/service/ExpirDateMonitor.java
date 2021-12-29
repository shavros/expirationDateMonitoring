package ru.service;

import io.kubernetes.client.openapi.models.V1Secret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.configs.CustomSecret;
import ru.configs.OpenShiftConfigs;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

@Component
public class ExpirDateMonitor {
    @Autowired
    OpenShiftConfigs osconf;

    /**
     *
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws IOException
     *
     * Через определенный промежуток времени показывает информацию по сроку действия сертификатов
     */
    @Scheduled(fixedRate = 20000)
    public void monitorExpirDates() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        SecretsGetter getter = new SecretsGetter();
        List<V1Secret> standardSecrets = getter.getJksSecretList(osconf);
        CustomSecretListBuilder builder = new CustomSecretListBuilder(osconf);
        List<CustomSecret> customSecretsList = builder.buildCustomSecretList(standardSecrets);
        InfoPrinter printer = new InfoPrinter();
        printer.printInfo(customSecretsList);
    }
}
