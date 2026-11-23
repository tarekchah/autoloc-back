# AutoLoc API — Pipeline CI Jenkins

Pipeline déclarative (`Jenkinsfile`) :

1. **Déclencheur** : webhook GitHub (`githubPush()`) — chaque push sur `main` lance un build.
2. **Checkout** : récupération du projet depuis GitHub.
3. **Tests unitaires** : `mvn clean test` (tests des services, JUnit 5 + Mockito).
4. **Livrable** : `mvn package -DskipTests` → `target/autoloc-api-0.0.1-SNAPSHOT.jar`.
