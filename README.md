#Ponto

###API de controle de ponto para colaboradores

####Descrição:

Este Programa para controle de pontos usa as tecnologias:
- Keycloak
- Spring Security
- Mysql
- Hibernate
- JPA
- Docker

Portas:
- programa: 8080
- comunicação com banco de dados: 3306 (rodando no docker ou nativamente)
- comunicação com keycloak: 8280 (docker ou nativamente)

####Endpoints:

- localhost:8080/usuario
    - json exemplo:
    
```
{
    "username": "johny",
    "password": "john",
    "name": "john"
}
```
 
- localhost:8080/batidas 
    - url exemplo:
    
```
http://localhost:8081/batidas?date=2020-12-05T10:15:19
```

###Rodando a api:

- o programa requer um arquivo ```.env``` ou similar para guardar as credenciais usadas, o arquivo  
deve conter os valores como no modelo a seguir e ser reconhecido nas variáveis de ambiente:
```
MYSQL_ROOT_USER=***
MYSQL_ROOT_PASSWORD=***
MYSQL_USER=***
MYSQL_PASSWORD=***
KEYCLOAK_USER=***
KEYCLOAK_PASSWORD=***
KEYCLOAK_CLIENT_SECRET=886c8812-ff41-4e6b-9584-4d2a9eb27896
```

Uma das maneiras de passar o arquivo para o Environment é usando o plugin *EnvFile* no Intellij

- com isso, da raiz do projeto, rodar o comando: ```docker-compose up```
- em ```localhost:8280```, logar com as credenciais do keycloak
- logado no keycloak, criar uma realm chamada "usuarios"
- em *usuarios*, criar um client chamado *ponto-public*
- em *usuarios*, criar outro client chamado *ponto-public* e configurar:
    - **Access Type** para confidential
    - **Authorization Enabled** para "on"
    - **Valid Redirect Urls** preencher com "*"
    - Ainda nas configurações do client *ponto*, ir na aba *credentials*, copiar o valor de *client secret*  
    e preencher no arquivo *.env*


####Segurança:

O programa requer autenticação para o endpoint que persiste os "pontos", o token pode   
ser adquirido com a seguinte requisição: 

```
curl -XPOST 'http://localhost:8180/auth/realms/usuarios/protocol/openid-connect/token' -H 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'grant_type=password' --data-urlencode 'username=thiago@barbosa' --data-urlencode 'password=thiago' --data-urlencode 'client_id=public-client'
```

ou rodando um Pre-request-script no Postman

###ToDo list:

- Regras de negocio: Horario de almoço
- Proteger endpoint para criar usuario para ser acessado somente por admin
- teste de integração
- mudar PontoController.save pra receber requestBody e não RequestParam