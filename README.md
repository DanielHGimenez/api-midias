# API Medias

## Como rodar localmente
Para rodar localmente você deve ter:
- JDK 8
- MySQL 5.x
- Apache Maven
- Uma conta da GCP com acesso ao serviço de storage

### Configuração
Para executar a aplicação algumas configurações são necessarias.
**1.** Configure as **credenciais do GCP** nas váriaveis de ambiente, conforme a [documentação](https://cloud.google.com/docs/authentication/production#passing_variable).
**2.** Configure um **buckets** no Storage da GCP para ser usado em execução, conforme a [documentação](https://cloud.google.com/storage/docs/creating-buckets?hl=pt-br).
**3.** Configure um **banco de dados** localmente, conforme a [documentação](https://dev.mysql.com/doc/refman/5.7/en/installing.html), ou se preferir use um hospedado em um servidor.
**4.** Por fim, configure a aplicação preenchendo os campos comentados do arquivo interno: **application.yml**.

### Execução
para executar a aplicação execute o seguinte comando dentro da pasta raiz da aplicação:
```
mvn spring-boot:run
```

## Testes
### Configuração
Para executar os testes da aplicação algumas configurações serão necessarias.
**1.** Configure as **credenciais do GCP** nas váriaveis de ambiente, conforme a [documentação](https://cloud.google.com/docs/authentication/production#passing_variable).
**2.** Configure um **buckets** no Storage da GCP para ser usado nos testes, conforme a [documentação](https://cloud.google.com/storage/docs/creating-buckets?hl=pt-br).
**3.** Por fim, configure a aplicação preenchendo os campos comentados do arquivo interno: **application-test.yml**.

### Execução
Para executar os testes execute o seguinte comando dentro da pasta raiz da aplicação:
```
mvn test
```