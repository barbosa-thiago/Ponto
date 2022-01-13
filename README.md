# Ponto

### API de controle de ponto para colaboradores

#### Descrição:

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

#### Endpoints:

##### Uma lista mais detalhada dos endpoints podera ser vista na documentação do swagger após  rodar a aplicação:  

```http://localhost:8080/swagger-ui.html```  

- localhost:8080/usuario (post)
    - json exemplo:
    
```
{
    "username": "johny",
    "password": "john",
    "name": "john"
}
```
 
- localhost:8080/schedules (post) 
    - json exemplo:
    
```
{
    "dateTime": "2022-01-03T08:00:00"
}  
```

### Rodando a api:

- o programa requer um arquivo ```.env``` ou similar para guardar as credenciais usadas, o arquivo  
deve conter os valores como no arquivo modelo ```.envTemplate``` na raiz do projeto. O arquivo  
precisa ainda ser reconhecido nas variáveis de ambiente:

- obs: O valor de **KEYCLOAK_CLIENT_SECRET** deverá ser preenchido mais adiante.

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
- em ```localhost:8280```, logar no keycloak com as credenciais do arquivo **.env**
- logado no keycloak, criar uma realm chamada **"usuarios"**
- na realm *usuarios*, vá em *clients* e crie um client chamado *ponto-public*
- mais uma vez em clients, criar ***outro client*** chamado **ponto** e configurar:
    - **Access Type** para confidential
    - **Authorization Enabled** para "on"
    - **Valid Redirect Urls** preencher com "*"
    - Na aba **Service Account Roles**, em **Client Roles**, selecione **realm-management** e adicione  
    a role **manage-users**
    - Ainda nas configurações do client *ponto*, ir na aba *credentials*, copiar o valor de **client secret**  
    e preencher no arquivo *.env*.


#### Segurança:

O programa requer autenticação para o endpoint que persiste os "pontos", o token pode   
ser adquirido com a seguinte requisição: 

```
curl -XPOST 'http://localhost:8180/auth/realms/usuarios/protocol/openid-connect/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=<user-username>' \
--data-urlencode 'password=<user-password>' \
--data-urlencode \
'client_id=public-client'
```

ou rodando um Pre-request-script no Postman

### ToDo list:

- Proteger endpoint para criar usuario para ser acessado somente por admin
- crud métodos restantes

### Possíveis soluções:

- criar domínios do tipo PrimeiroPonto, SegundoPonto, TerceiroPonto etc que estendam o domínio Ponto  
usando @MappedSuperClass para uma melhor organização dos dados 