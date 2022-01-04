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

####Segurança:

O programa requer autenticação para o endpoint que persiste os "pontos", o token pode   
ser adquirido com a seguinte requisição: 

```
curl -XPOST 'http://localhost:8180/auth/realms/application-users/protocol/openid-connect/token' -H 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'grant_type=password' --data-urlencode 'username=thiago@barbosa' --data-urlencode 'password=thiago' --data-urlencode 'client_id=public-client'
```

ou rodando um Pre-request-script no Postman