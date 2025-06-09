# Sistema de Estacionamento

## 🚀 Objetivo

A aplicação recebe dados georreferenciados de vagas e setores via API externa (`/garage`) e inicializa automaticamente
essas informações no banco de dados. A partir disso, a aplicação será capaz de processar eventos de entrada, ocupação e
saída de veículos, aplicando as regras de negócio fornecidas, como controle de faturamento e precificação dinâmica.

## ⚙️ Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- Spring WebFlux (WebClient)
- Springdoc OpenAPI (Swagger)
- Docker
- MySQL
- JUnit + Mockito

## ▶️ Como rodar o projeto

### 1. Suba o simulador

```bash
docker run -d --network="host" cfontes0estapar/garage-sim:1.0.0
```

### 2. Configure o ambiente

Crie um arquivo `.env` na raiz com as credenciais do banco:

```
DB_USER=XXXX
DB_PASSWORD=XXXXX
```

A conexão com o banco está definida como:

```
spring.datasource.url=jdbc:mysql://localhost:3306/estacionamento_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

### 3. Rode o projeto

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:3003`.

## 📄 Documentação da API

A documentação dos endpoints será gerada automaticamente por Swagger e poderá ser acessada em:

```
http://localhost:3003/swagger-ui.html
```

## 📥 Inicialização automática

Durante o boot da aplicação, um componente (`GarageStartupLoader`) realiza uma chamada ao endpoint `/garage`,
recuperando a configuração dos setores e vagas do estacionamento.

Os dados são persistidos em banco de dados com validação para evitar duplicidades. Toda a lógica está desacoplada em
`services`, `clients`, `repositories` e `models`, com uso de boas práticas de design.

## 📁 Estrutura do projeto

```
src/
├── controller/       # Exposição dos endpoints REST
├── dto/              # Objetos de entrada e saída da API (Request/Response)
├── enums/            # Enumerações para tipos fixos (ex: EventType)
├── exception/        # Exceções customizadas e tratamento global
├── integration/      # Integração com sistemas externos (ex: simulador Estapar)
├── model/            # Entidades JPA que representam tabelas do banco
├── repository/       # Interfaces Spring Data para acesso aos dados
├── scheduler/        # Tarefas que rodam automaticamente (ex: carregamento inicial)
└── service/          # Regras de negócio e orquestração de persistência
```

## 🧪 Cobertura de testes

Cobertura com JUnit 5 e Mockito:

| Pacote               | Cobertura |
|----------------------|-----------|
| `controller`         | ✅ 100%    |
| `service`            | ✅ 95%     |
| `dto`                | ✅ 100%    |
| `enums`              | ✅ 100%    |
| `exception`          | ✅ 100%    |
| `integration.client` | ✅ 100%    |
| `scheduler`          | ✅ 100%    |

🧼 Os testes validam comportamento, regras de negócio e tratamento de erros.
---

✅ Projeto finalizado com boas práticas de arquitetura, separação de responsabilidades, documentação via Swagger e testes
automatizados.