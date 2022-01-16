# verzel motors backend

## requesitos
- postgres rodando na porta 5432
- java
- maven
- bucket AWS s3 
- aws cli 

### 1º - crie um bucket na aws e de acesso acl e configure cors
### 2º - adicione um arquivo application.properties com os seguintes modelo
```
spring.datasource.url=jdbc:postgresql://localhost:5432/motors
spring.datasource.username=<username>
spring.datasource.password=<password>

spring.jpa.hibernate.ddl-auto=update

application.bucket.name=<bucket name do s3>
cloud.aws.credentials.secret-key=<secrekey aws>
cloud.aws.credentials.access-key=<accesskey aws>
cloud.aws.region.static=<region do bucket>
```

### 3º - execute no terminal 
```
    mnv spring-boot:run
```

