package ru.service;

import io.kubernetes.client.openapi.models.V1Secret;
import ru.configs.Aliace;
import ru.configs.CustomSecret;
import ru.configs.OpenShiftConfigs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class CustomSecretListBuilder {

    OpenShiftConfigs osconfig;

    public CustomSecretListBuilder(OpenShiftConfigs osconfig) {
        this.osconfig = osconfig;
    }

    private List<CustomSecret> customSecretsList = new ArrayList<>();

    /**
     * Из списка secret'ов формирует список кастомных сикретов, в которых хранятся name, namespace и список aliase'ов из хранилища
     * @param standardSecrets - список всех секретов с хранилищами
     * @return customSecretList - список кастомных secret'ов
     * @throws IOException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     */
    public List<CustomSecret> buildCustomSecretList(List<V1Secret> standardSecrets) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        for (V1Secret secret : standardSecrets) {
            String name = secret.getMetadata().getName();
            String namespace = secret.getMetadata().getNamespace();
            String base64jks = Base64.getEncoder().encodeToString(secret.getData().get("keystore.jks"));
            getJksFie(base64jks);
            extractAliases(name, namespace);
        }
        return this.customSecretsList;
    }

    /**
     * Открывает хранилище, формирует список алиасов хранилища с их именами и датами истечения срока действия,
     * создает кастомный секрет и добавляет в итоговый список
     * @param name - имя секрета
     * @param namespace
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     */
    private void extractAliases(String name, String namespace) throws KeyStoreException, NoSuchAlgorithmException, CertificateException {
        List<Aliace> aliaceList = new ArrayList<>();
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyStore.load(new FileInputStream("./keystore.jks"), osconfig.getPasswords()[0].toCharArray());
            Enumeration aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                Date certExpiryDate = ((X509Certificate) keyStore.getCertificate(alias)).getNotAfter();
                Aliace aliace = new Aliace(alias, certExpiryDate);
                aliaceList.add(aliace);
            }
            this.customSecretsList.add(new CustomSecret(name, namespace, aliaceList));
        } catch (NullPointerException | IOException e) {
            System.out.println("Пароль для " + name + " в " +
                    namespace + " не подошел!");
        }
    }

    /**
     * Из строки в base64 кодировке формирует файл хранилища
     * @param base64jks - закодированное хранилище
     * @throws IOException
     */
    private void getJksFie(String base64jks) throws IOException {
        File file = new File("./keystore.jks");
        FileOutputStream fos = new FileOutputStream(file);
        byte[] decoder = Base64.getDecoder().decode(base64jks);
        fos.write(decoder);
    }
}
