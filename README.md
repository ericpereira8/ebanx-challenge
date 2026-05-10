# EBANX Challenge

Spring Boot REST API com banco H2 em memória.

## Requisitos

- Java 26
- Maven 3.x

## Executando a aplicação

```bash
cd ebanx-challenge
./mvnw spring-boot:run
```

A aplicação sobe na porta padrão `8080`.

## Spring Actuator — Health Check

O endpoint de saúde está exposto em:

```
GET http://localhost:8080/actuator/health
```

### Exemplo de resposta (aplicação e banco saudáveis)

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 200000000000,
        "threshold": 10485760,
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Verificando o status do banco

O campo `components.db.status` indica o estado da conexão com o banco H2:

| Valor | Significado |
|-------|-------------|
| `UP`  | Banco acessível e respondendo |
| `DOWN`| Banco inacessível ou com erro |

### Via curl

```bash
curl http://localhost:8080/actuator/health
```

### Via curl (apenas o status do banco)

```bash
curl -s http://localhost:8080/actuator/health | python -m json.tool | grep -A2 '"db"'
```

## Banco de dados

O projeto usa H2 em memória — os dados são perdidos ao reiniciar a aplicação.

| Parâmetro | Valor |
|-----------|-------|
| URL JDBC  | `jdbc:h2:mem:ebanx-challenge-h2` |
| Usuário   | `eric` |
| Senha     | *(vazia)* |