package ru.service;


import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.util.Config;
import ru.configs.OpenShiftConfigs;

import java.util.ArrayList;
import java.util.List;

public class SecretsGetter {
    private List<V1Secret> secretsList = new ArrayList<>();

    /**
     * Формирует и возвращает список секретов, в которых лежит jks хранилище в base64 кодировке
     * @param osconfig - конфигурация OS, в которой описаны хост и namespace'ы, из которых необходимо получить secret'ы
     * @return secretsList - список secret'ов с jks хранилищами
     */
    public List<V1Secret> getJksSecretList(OpenShiftConfigs osconfig) {
        try {
            String token = osconfig.getToken();
            String host = osconfig.getHost();
            String[] namespaces = osconfig.getNamespaces();
            ApiClient client = Config.fromToken(host, token, false);
            CoreV1Api api = new CoreV1Api(client);
            for (String namespace : namespaces) {
                List<V1Secret> list = getAllSecrets(api, namespace);
                scanJksSecrets(list);
            }
        } catch (ApiException | NullPointerException r) {
            System.out.println(r);
        }
        return secretsList;
    }

    /**
     * Из списка со всеми secret'ами выбирает те, в которых есть jks хранилище и добавляет в список secretsList
     * @param list - список всех secret'ов
     */
    private void scanJksSecrets(List<V1Secret> list) {
        for (V1Secret secret : list) {
            if(secret.getData() != null) {
                if (secret.getData().containsKey("keystore.jks")) {
                    secretsList.add(secret);
                }
            }
        }
    }

    /**
     * Формирует список всех secret'ов из заданного namespace'а
     * @param api - API, через которое происходит взаимодействие с OS
     * @param namespace - namespace, из которого необходимо взять secret'ы
     * @return возвращает список всех secret'ов из namespace'а
     * @throws ApiException
     */
    private List<V1Secret> getAllSecrets(CoreV1Api api, String namespace) throws ApiException {
        V1SecretList secretList = api.listNamespacedSecret(namespace, null, null,
                null, null, null, null, null, null,
                null, null);
        return secretList.getItems();
    }
}
