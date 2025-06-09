# Prerequisites
 - [Docker](https://www.docker.com/products/docker-desktop/) to build and run
 - [Git](https://git-scm.com/downloads) for cloning repository
 - [Postman](https://www.postman.com) for testing endpoints (Optional)

# Get started

1. Clone the repository

```
git clone https://github.com/Kybartas/juniorHomework
```

2. Navigate to the project

```
cd juniorHomework
```

3. Build and run with Docker compose

```
docker compose up -d --build
```

4. Test the api

[Postman collection](https://www.postman.com/kristijonaskybartas/workspace/juniorhomework/collection/44482661-e5878220-e9aa-4c92-8530-65eaddfd6ae7?action=share&creator=44482661)

5. Stop the application

```
docker compose down
```

# Endpoints

Optional fields are in brackets [...]

### POST /api/statements/import

Import statement CSV file, example:

```
curl --location 'http://localhost:8080/api/statements/import' \
--form 'file=@"juniorHomework/samples/000.csv"'
```

### GET /api/statements/export?accountNumber={string}[&from={date}][&to={date}]

Export stored data to CSV, example:

```
curl --location 'http://localhost:8080/api/statements/export?accountNumber=000&from=2025-01-01&to=2025-01-30'
```

### GET /api/statements/generateCSV?numberOfAccounts={number}&transactionsPerAccount={number}[&directory={string}]

Generate random CSV statement files for api testing, example:

```
curl --location 'http://localhost:8080/api/statements/generateCSV?numberOfAccounts=2&transactionsPerAccount=30'
```

### GET /api/accounts/getBalance?accountNumber={number}[&from={date}][&to={date}]

Get balance of an account, example:

```
curl --location 'http://localhost:8080/api/accounts/getBalance?accountNumber=000&from=2025-01-01&to=2025-01-30'
```