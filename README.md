# Lab 11 — Localisation d'un smartphone et envoi des coordonnées vers un serveur distant

Ce projet met en place un système de géolocalisation mobile connecté à un backend PHP/MySQL. L'application Android capte les coordonnées GPS du téléphone et les transmet automatiquement à un serveur local via des requêtes HTTP.

---

## Présentation

L'idée centrale est de transformer le smartphone en émetteur GPS : à chaque changement de position significatif, l'application envoie les données (latitude, longitude, horodatage, identifiant appareil) à une base de données accessible côté serveur.

---

## Architecture

Le projet est divisé en deux parties distinctes :

### Partie Mobile (Android — Java)

L'application utilise le `LocationManager` d'Android pour écouter les mises à jour GPS. Elle demande les permissions nécessaires au démarrage, puis déclenche l'envoi des coordonnées via la bibliothèque **Volley** (requête POST asynchrone).

Les coordonnées s'affichent à l'écran en temps réel avec le statut de l'envoi.

### Partie Serveur (PHP — XAMPP)

Le serveur expose un point d'entrée HTTP (`savePosition.php`) qui reçoit les données POST et les insère en base via une architecture en couches :

- **`classe/Position.php`** : modèle de données
- **`connexion/Connexion.php`** : gestion de la connexion PDO
- **`dao/IDao.php`** : contrat d'interface CRUD
- **`service/PositionService.php`** : implémentation de la logique d'insertion

---

## Technologies utilisées

- Java (Android SDK), Volley
- PHP 8, PDO, MySQL
- Android Studio, XAMPP / phpMyAdmin

---

## Configuration

**Côté serveur :**
- Démarrer Apache et MySQL via XAMPP
- Créer la base de données `localisation` et la table `position` :

```sql
CREATE TABLE position (
    id INT AUTO_INCREMENT PRIMARY KEY,
    latitude VARCHAR(20),
    longitude VARCHAR(20),
    date_position DATETIME,
    imei VARCHAR(50)
);
```
<img width="1024" height="659" alt="image" src="https://github.com/user-attachments/assets/82c16c81-b64b-42aa-a11b-60cf10abefd5" />

- Copier le dossier `server/` dans `htdocs/localisation/`

**Côté mobile :**
- Ouvrir le projet Android dans Android Studio
- Modifier la variable `SERVER_URL` dans `MainActivity.java` avec l'adresse IP de votre machine
- Lancer l'application sur un émulateur ou un vrai appareil

