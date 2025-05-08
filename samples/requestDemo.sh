#!/bin/bash

printf "\n\n--- RUNNING APPLICATION ---\n"
sleep 1
java -jar ../target/juniorHomework-1.0.jar & SERVER_PID=$!

sleep 6

printf "\n\n--- IMPORTING STATEMENT1.CSV ---\n"
sleep 1
curl --location 'http://localhost:8080/api/statements/import' \
--form 'file=@"statement1.csv"'

sleep 2

printf "\n\n--- IMPORTING STATEMENT2.CSV ---\n"
sleep 1
curl --location 'http://localhost:8080/api/statements/import' \
--form 'file=@"statement2.csv"'

sleep 2

printf "\n\n--- GETTING BALANCE FOR LT444555666777888999 FROM 2024-01-01 TO 2024-01-10 ---\n"
sleep 1
curl --location 'http://localhost:8080/api/statements/getBalance?accountNumber=LT444555666777888999&from=2024-01-01&to=2024-01-10'
printf "\n"

sleep 2

printf "\n--- GETTING BALANCE FOR LT222333444555666777 FROM 2024-01-01 TO 2024-01-10 ---\n"
sleep 1
curl --location 'http://localhost:8080/api/statements/getBalance?accountNumber=LT222333444555666777&from=2024-01-01&to=2024-01-10'
printf "\n"

sleep 2

printf "\n--- EXPORTING STATEMENTS FROM 2024-01-01 TO 2024-01-10 ---\n"
sleep 1
curl --location 'http://localhost:8080/api/statements/export?from=2024-01-01&to=2024-01-10'

sleep 2

printf "\n--- STOPPING APPLICATION ---\n"
sleep 1
kill $SERVER_PID