# Projet RH - ProjectApp / ProjetApi / ProjetBatch

## 1) Fonctionnalités

### 1.1 Côté RH (ProjectApp)
- Gestion employés
  - Lister / rechercher (id, prénom, nom, email)
  - Ajouter un employé (création d'un mot de passe automatique, le mot de passe est envoyé sur le mail de création pour pouvoir se connecter)
  - Modifier un employé (prénom, nom, email, poste)
  - Supprimer un employé (nettoyage : congés + heures + bulletins avant suppression)
- Gestion bulletins
  - Voir les bulletins d'un employé
  - Télécharger un PDF existant
  - Lancer la génération des bulletins via le module Batch
- Gestion congés
  - Visualiser toutes les demandes (tri : EN_ATTENTE en premier)
  - Valider / refuser une demande

### 1.2 Côté Employé (ProjectApp)
- Mes bulletins : liste + téléchargement PDF
- Mes heures : déclaration mensuelle (S1..S4) + total calculé
- Mes congés
  - Créer une demande (CP/RTT, dates, motif)
  - Voir historique + statut (EN_ATTENTE, VALID, REFUS)
  - Affichage des soldes (soldeCP / soldeRTT)
- News : page "Infos Groupe" (contenu statique côté app)

### 1.3 Batch (ProjetBatch)
- Job 1 : Génération des bulletins
  - Lit tous les employés, récupère les heures et les congés validés.
  - Calcule le brut/net et génère un PDF.

## 2) Base de données (PostgreSQL)
L'application utilise plusieurs tables interconnectées : `poste`, `employee`, `hours_declaration`, `leave_request` et `payslip`.


## 3) Structure des projets
* **ProjectApp (Port 8081) :** Interface utilisateur (Thymeleaf/Spring Security).
* **ProjetApi (Port 8080) :** Cœur du système (Services, Repositories, Logique métier).
* **ProjetBatch (Port 8082) :** Traitements automatisés et génération de PDF.

## 4) Lancement en local

### Accès direct :
- **App :** [http://localhost:8081](http://localhost:8081)
- **API :** [http://localhost:8080](http://localhost:8080)
- **Batch :** [http://localhost:8082](http://localhost:8082)

### Guide de première connexion :
1. Une fois sur le **localhost:8081**, utilisez les identifiants RH pour commencer à gérer le personnel (les identifiants sont aussi rappelés en bas de la page web).
   * **Email :** `rh@bank.local`
   * **Mot de passe :** `rh123`
2. **Création d'employé :** Vous pouvez créer un nouvel employé. **Note importante :** le mot de passe de l'employé sera envoyé à l'adresse email renseignée lors de la création. Utilisez donc une **vraie adresse mail** pour tester la connexion.
3. **Espace Employé :** Déconnectez-vous du compte RH, puis reconnectez-vous avec l'email et le mot de passe reçu par mail pour accéder à l'espace personnel de l'employé fraîchement créé.

## 5) Bonus / Blagues
- **Easter egg** : Un incontournable de notre site, vous êtes obligés de le tester !
- **ChatBot IA** : Il conclut souvent (après une analyse très sérieuse) que le projet mérite **20/20**.

## 6) Auteurs
- Marius Babin
- Baptiste Durand
