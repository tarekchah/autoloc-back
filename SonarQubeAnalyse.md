# Analyse SonarQube for IDE : AutoLoc

**Résultat : 61 problèmes dans 26 fichiers.**

Sommaire : 19 à impact important, 13 à impact moyen, 29 à impact faible.

## Suivi des corrections

Légende : ✅ corrigé et vérifié (`./mvnw clean verify` OK) · ⏳ à faire · ⛔ volontairement non corrigé.

| Alerte | Impact | Statut |
|---|---|---|
| Imports inutilisés (7) | Faible | ✅ (`AgenceTests`: `Fail.fail` retiré aussi) |
| Champ `Repository` renommé `repository` | Faible | ✅ |
| `ID` inutilisé dans `ICheckGenericService` | Moyen | ✅ (paramètre retiré, aucune classe ne l'utilisait) |
| Instruction vide `;;` dans `VehiculeServiceImpl` | Moyen | ✅ |
| Constructeur privé dans `Utils` | Moyen | ✅ (classe `final`) |
| `System.out` remplacé par un logger dans `RapportConsole` | Moyen | ✅ (SLF4J, l'en-tête apparaît dans les logs) |
| `LocalDateTime.now()` sans fuseau (3) | Moyen | ✅ (`ZoneId.systemDefault()`, comportement identique) |
| Tests sans assertion (6) dans `AgenceTests` | Moyen | ⏳ (tests JUnit 4 non exécutés par Maven) |
| `@ApiResponses` superflu (12) | Faible | ⏳ |
| Noms de génériques `ID` (5) | Faible | ⛔ convention de cours, risque large pour peu de gain |
| Noms de beans `STANDARD`, `WEEKEND`, `LONGUE_DUREE` (3) | Faible | ⛔ le nom du bean sert de clé (`offres.get(offre.name())`) |
| Mot de passe en clair (2) | Important | ✅ sorti du code : `application-local.properties` (ignoré par git) ou variable `DB_PASSWORD`. Le mot de passe actuel a été exposé dans l'éditeur : pensez à le changer. |
| Entités JPA exposées par les contrôleurs (17) | Important | ⏳ (demande la création de DTO) |

---

## 1. Liste brute (par fichier, telle que fournie par Sonar)

### AgenceController.java
- Replace this persistent entity with a simple POJO or DTO object. (x3)
- Remove the 'ApiResponses' wrapper from this annotation group (x4)

### AgenceTests.java
- Add at least one assertion to this test case. (x6)
- Remove this unused import 'org.assertj.core.api.Fail.fail'.

### BasicAgenceServiceImpl.java
- Remove this unused import 'tn.esprit.autoloc.calcul.VehiculeOffre'.
- Rename this field "Repository" to match the regular expression '^[a-z][a-zA-Z0-9]*$'.

### ClientController.java
- Remove the 'ApiResponses' wrapper from this annotation group
- Replace this persistent entity with a simple POJO or DTO object. (x2)

### ContratController.java
- Remove the 'ApiResponses' wrapper from this annotation group
- Replace this persistent entity with a simple POJO or DTO object. (x2)

### EmployeController.java
- Remove the 'ApiResponses' wrapper from this annotation group
- Replace this persistent entity with a simple POJO or DTO object. (x2)

### EquipementController.java
- Remove the 'ApiResponses' wrapper from this annotation group
- Replace this persistent entity with a simple POJO or DTO object. (x2)

### GenericServiceImpl.java
- Remove this unused import 'tn.esprit.autoloc.calcul.VehiculeOffre'.
- Remove this unused import 'tn.esprit.autoloc.domain.Agence'.
- Rename this generic name to match the regular expression '^[A-Z][0-9]?$'.

### GlobalExceptionHandler.java
- Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method. (x3)

### IAgenceService.java
- Remove this unused import 'tn.esprit.autoloc.calcul.VehiculeOffre'.
- Remove this unused import 'java.util.List'.

### ICheckGenericService.java
- ID is not used in the interface.
- Rename this generic name to match the regular expression '^[A-Z][0-9]?$'.

### IDeleteService.java
- Rename this generic name to match the regular expression '^[A-Z][0-9]?$'.

### IGenericService.java
- Rename this generic name to match the regular expression '^[A-Z][0-9]?$'.
- Remove this unused import 'java.util.List'.

### IReadService.java
- Rename this generic name to match the regular expression '^[A-Z][0-9]?$'.

### IWriteService.java
- Rename this generic name to match the regular expression '^[A-Z][0-9]?$'.

### LongueDureeOffre.java
- Rename this bean to match the regular expression '^[a-z][a-zA-Z0-9]*$'.

### MaintenanceController.java
- Remove the 'ApiResponses' wrapper from this annotation group
- Replace this persistent entity with a simple POJO or DTO object. (x2)

### PaiementController.java
- Remove the 'ApiResponses' wrapper from this annotation group

### RapportConsole.java
- Replace this use of System.out by a logger.

### ReservationController.java
- Remove the 'ApiResponses' wrapper from this annotation group
- Replace this persistent entity with a simple POJO or DTO object. (x2)

### StandardOffre.java
- Rename this bean to match the regular expression '^[a-z][a-zA-Z0-9]*$'.

### Utils.java
- Add a private constructor to hide the implicit public one.

### VehiculeController.java
- Remove the 'ApiResponses' wrapper from this annotation group
- Replace this persistent entity with a simple POJO or DTO object. (x2)

### VehiculeServiceImpl.java
- Remove this empty statement.

### WeekendOffre.java
- Rename this bean to match the regular expression '^[a-z][a-zA-Z0-9]*$'.

### application.properties
- Make sure this database password gets changed and removed from the code.
- Revoke and change this secret, as it is compromised.

---

## 2. Classement par impact

# Impact Important

*Sécurité et risques de conception : 19 problèmes.*

### Secret en clair dans le code (2)
- `application.properties` : Make sure this database password gets changed and removed from the code.
- `application.properties` : Revoke and change this secret, as it is compromised.

### Entités JPA exposées directement par l'API (17)
Les contrôleurs reçoivent et renvoient les entités persistantes (risque de *mass assignment* et de fuite de données).
- `AgenceController.java` : Replace this persistent entity with a simple POJO or DTO object. (x3)
- `ClientController.java` (x2)
- `ContratController.java` (x2)
- `EmployeController.java` (x2)
- `EquipementController.java` (x2)
- `MaintenanceController.java` (x2)
- `ReservationController.java` (x2)
- `VehiculeController.java` (x2)

---

# Impact Moyen

*Fiabilité et maintenabilité : 13 problèmes.*

### Tests sans assertion (6)
- `AgenceTests.java` : Add at least one assertion to this test case. (x6)

### Date sans fuseau horaire (3)
- `GlobalExceptionHandler.java` : Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method. (x3)

### Autres (4)
- `RapportConsole.java` : Replace this use of System.out by a logger.
- `Utils.java` : Add a private constructor to hide the implicit public one.
- `VehiculeServiceImpl.java` : Remove this empty statement.
- `ICheckGenericService.java` : ID is not used in the interface.

---

# Impact Faible

*Style et conventions : 29 problèmes.*

### Imports inutilisés (7)
- `AgenceTests.java` : `org.assertj.core.api.Fail.fail`
- `BasicAgenceServiceImpl.java` : `tn.esprit.autoloc.calcul.VehiculeOffre`
- `GenericServiceImpl.java` : `tn.esprit.autoloc.calcul.VehiculeOffre`
- `GenericServiceImpl.java` : `tn.esprit.autoloc.domain.Agence`
- `IAgenceService.java` : `tn.esprit.autoloc.calcul.VehiculeOffre`
- `IAgenceService.java` : `java.util.List`
- `IGenericService.java` : `java.util.List`

### Annotation `@ApiResponses` superflue (12)
- `AgenceController.java` (x4)
- `ClientController.java`, `ContratController.java`, `EmployeController.java`, `EquipementController.java`, `MaintenanceController.java`, `PaiementController.java`, `ReservationController.java`, `VehiculeController.java` (x1 chacun)

### Noms de génériques (6)
Sonar attend `^[A-Z][0-9]?$` : `ID` est jugé trop long. Un nom comme `K` ou `I` conviendrait.
- `GenericServiceImpl.java`, `ICheckGenericService.java`, `IDeleteService.java`, `IGenericService.java`, `IReadService.java`, `IWriteService.java`

### Noms de beans (3)
Les beans `STANDARD`, `WEEKEND` et `LONGUE_DUREE` sont écrits en majuscules.
- `StandardOffre.java`, `WeekendOffre.java`, `LongueDureeOffre.java`

> **À ne pas corriger sans adapter le code :** `VehiculeServiceImpl.calculTarif` retrouve la stratégie par `offres.get(offre.name())`, donc le nom du bean doit rester égal au nom de l'enum.

### Nom de champ (1)
- `BasicAgenceServiceImpl.java` : le champ `Repository` doit commencer par une minuscule.
