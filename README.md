# Expiration date monitoring
Сервис мониторит срок действия сертификатов, хранящихся в jks хранилищах,
которые загружены в kubernetes в качестве secret'ов. <br>

С заданной переодичностью (monitor.delay в application.yml) происходит
подключение к проекту в k8s (openshift.host, openshift.namespaces), парсинг
всех секретов из проекта, отбор только тех, в которых лежат jks хранилища, 
и попытка открыть их заданными паролями (openshift.passwords). После чего 
информация о дате истечения срока действия сертификата и оставшемся времени для 
каждого secret'a выводится на консоль.

Для работы сервиса в проекте kubernetes должен быть создан ServiceAccount 
с повешенной на него ролью (Role), позволяющей читать secret'ы. 
Токен от этого аккаунта необходимо указать в openshift.token в application.yml.