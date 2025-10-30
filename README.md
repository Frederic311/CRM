
<h2>✨Customer Relation System✨


## Stack

<!-- Author's badge style -->
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Angular](https://img.shields.io/badge/angular-DD0031.svg?style=for-the-badge&logo=angular&logoColor=white)
![Java](https://img.shields.io/badge/java-007396.svg?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring_boot-6DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![Docker](https://img.shields.io/badge/docker-2496ED.svg?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/maven-C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white)
![GitLab CI](https://img.shields.io/badge/GitLab_CI-FCA121.svg?style=for-the-badge&logo=gitlab&logoColor=white)

## Description

CRM – Gestions des candidatures étudiantes

Développement d’une solution CRM modulaire en architecture microservices pour gérer le processus d’admission :

- Angular (web) pour l’administration des dossiers.
- Backend Spring Boot avec base de données MySQL.
- Conteneurisation via Docker, intégration continue avec GitLab CI/CD.

## Structure du dépôt

- `CRM_Backend/` – microservices backend (auth-service, student management, task management, notification, service discovery, api gateway, ...)
- `Front/CRM/` – application Angular (UI)
- `uploads/` – fichiers et ressources uploadées

## Démarrage rapide

1. Lancer MySQL et créer la base de données requise (ex: `authdb`).
2. Depuis chaque service backend :

```powershell
# depuis le dossier du service (ex: CRM_Backend/auth-service)
./mvnw -DskipTests clean package
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

3. Depuis `Front/CRM/` :

```powershell
npm install
npm start
```

## Notes

- Les fichiers de configuration pour les profils (ex: `application-dev.yml`) contiennent les paramètres de connexion à la base de données et les propriétés de Spring Boot.
- Swagger UI est exposé pour chaque microservice (ex: `http://localhost:8080/api/v1/swagger-ui/index.html` pour le service `auth-service` si démarré sur le port 8080).


---

File created by assistant: `README_updated.md` — if this looks good I can try to replace `README.md` in-place.





