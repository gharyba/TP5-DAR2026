# TP5 – Java RMI et JNDI

Faculté des Sciences de Sfax – LSI3 – DAR 2026-2027
Enseignante des TP : Mme Zeineb DAOUD

## 1. Présentation

Ce TP utilise Java RMI pour appeler une méthode d'un objet qui s'exécute dans une autre JVM. Le registre RMI associe un nom au service distant. Dans l'activité 5-2, JNDI sert à enregistrer et retrouver ce service dans le registre.

## 2. Organisation

```text
Activite5-1/
  RMIConversion_Server/src/rmiService/ (IConversion, ConversionImpl)
  RMIConversion_Server/src/rmiServer/ConversionServer.java
  RMIConversion_Client/src/rmiClient/ConversionClient.java
  RMIConversion_Client/lib/conversionClient.jar
Activite5-2/
  RMIBanque_Server/src/metier/Compte.java
  RMIBanque_Server/src/rmiService/ (IBanque, BanqueImpl)
  RMIBanque_Server/src/rmiServer/BanqueServer.java
  RMIBanque_Server/src/jndi.properties
  RMIBanque_Client/src/rmiClient/BanqueClient.java
  RMIBanque_Client/src/jndi.properties
  RMIBanque_Client/lib/banqueClient.jar
docs/test-results/ : sorties des essais effectués
docs/screenshots/ : emplacement des captures à faire
```

Les JAR sont produits à partir du projet serveur. Le client ne possède pas les sources de l'interface distante. `conversionClient.jar` contient seulement `IConversion.class`. `banqueClient.jar` contient seulement `IBanque.class` et `Compte.class`, en plus du manifeste automatique.

## 3. Compilation

Ouvrir PowerShell dans `TP5-DAR2026`. Avec JDK 21 :

### Activité 5-1

```powershell
$s = 'Activite5-1/RMIConversion_Server'
$c = 'Activite5-1/RMIConversion_Client'
New-Item -ItemType Directory -Force "$s/bin", "$c/bin", "$c/lib" | Out-Null
javac -encoding UTF-8 -d "$s/bin" (Get-ChildItem "$s/src" -Recurse -Filter '*.java').FullName
jar cf "$c/lib/conversionClient.jar" -C "$s/bin" rmiService/IConversion.class
javac -encoding UTF-8 -cp "$c/lib/conversionClient.jar" -d "$c/bin" (Get-ChildItem "$c/src" -Recurse -Filter '*.java').FullName
jar tf "$c/lib/conversionClient.jar"
```

Dans un premier terminal, depuis la racine du projet :

```powershell
java -cp 'Activite5-1/RMIConversion_Server/bin' rmiServer.ConversionServer
```

Dans un second terminal :

```powershell
java -cp 'Activite5-1/RMIConversion_Client/bin;Activite5-1/RMIConversion_Client/lib/conversionClient.jar' rmiClient.ConversionClient
```

Arrêter le serveur avec `Ctrl+C` avant de lancer celui de l'activité 5-2. Les deux utilisent le port 1099. Si ce port est déjà occupé, arrêter l'autre registre ou serveur RMI avant de recommencer.

### Activité 5-2, version finale JNDI

```powershell
$s = 'Activite5-2/RMIBanque_Server'
$c = 'Activite5-2/RMIBanque_Client'
New-Item -ItemType Directory -Force "$s/bin", "$c/bin", "$c/lib" | Out-Null
javac -encoding UTF-8 -d "$s/bin" (Get-ChildItem "$s/src" -Recurse -Filter '*.java').FullName
Copy-Item "$s/src/jndi.properties" "$s/bin/jndi.properties"
jar cf "$c/lib/banqueClient.jar" -C "$s/bin" rmiService/IBanque.class -C "$s/bin" metier/Compte.class
javac -encoding UTF-8 -cp "$c/lib/banqueClient.jar" -d "$c/bin" (Get-ChildItem "$c/src" -Recurse -Filter '*.java').FullName
Copy-Item "$c/src/jndi.properties" "$c/bin/jndi.properties"
jar tf "$c/lib/banqueClient.jar"
```

Premier terminal :

```powershell
java -cp 'Activite5-2/RMIBanque_Server/bin' rmiServer.BanqueServer
```

Second terminal :

```powershell
java -cp 'Activite5-2/RMIBanque_Client/bin;Activite5-2/RMIBanque_Client/lib/banqueClient.jar' rmiClient.BanqueClient
```

Dans Eclipse, on peut aussi ajouter le JAR du dossier `lib` au classpath du projet client via **Properties > Java Build Path > Libraries**.

## 4. Activité 5-1 : conversion

`IConversion` déclare `convertirMontant(double)`. `ConversionImpl` fait le calcul sur le serveur. `ConversionServer` crée le registre sur le port 1099 et publie l'objet sous le nom `conversion`. `ConversionClient` retrouve cette référence et appelle `convertirMontant(500)`.

Le cours de Mme Amal ABID utilise le taux simplifié **1 euro = 3 dinars** pour son exemple de conversion. Ce taux est uniquement pédagogique, pas un cours de change réel. L'essai exécuté a renvoyé **1500.0 dinars** pour 500 euros.

## 5. Activité 5-2 : banque

`Compte` est sérialisable et contient un code, un solde et une date de création. `IBanque` déclare `creerCompte(Compte)` et `getInfoCompte(int)`. `BanqueImpl` conserve les comptes dans une `Map` en mémoire ; ses méthodes sont `synchronized` pour protéger les accès simultanés. Les comptes disparaissent à l'arrêt du serveur.

Le client crée le compte 101 avec un solde de 1000.0, le consulte, essaie de le recréer, puis cherche le compte 999. Les essais RMI direct et JNDI ont donné respectivement : création, informations du compte, refus du doublon et compte introuvable. Les sorties réelles figurent dans `docs/test-results/`.

L'énoncé 5-2 nomme `banqueClient.jar` à l'export, mais indique `conversionClient.jar` dans une instruction d'import. Pour le projet banque, le JAR utilisé est `banqueClient.jar`.

## 6. Utilisation de JNDI

La première version bancaire a été compilée et testée avec `Registry.rebind()` et `Registry.lookup()`. Le code final utilise `InitialContext.rebind()` côté serveur et `InitialContext.lookup()` côté client. `LocateRegistry.createRegistry(1099)` crée seulement le registre sous-jacent ; l'enregistrement de l'objet passe par JNDI.

Chaque `jndi.properties` indique `RegistryContextFactory` et `rmi://localhost:1099`. La copie du fichier vers `bin` le place à la racine du classpath : `new InitialContext()` le lit automatiquement. Le nom de service commun est `banque`.

## 7. Captures d'écran à faire

Le dossier `docs/screenshots/` est prêt, mais aucune capture n'a été fabriquée. Pendant une démonstration, prendre de vraies captures du serveur et du client pour chaque activité, du contenu des deux JAR (`jar tf`), puis du serveur et du client bancaires avec JNDI. Montrer les quatre réponses bancaires dans la capture du client. Ajouter ensuite les images réelles au README.

## 8. Tests effectués

- Compilation des quatre projets avec `javac` : réussie.
- Contenu des deux JAR vérifié avec `jar tf` : uniquement les classes demandées.
- Activité 5-1 : serveur lancé, recherche et appel distant réussis ; 500 × 3 = 1500.0. Le client sans serveur affiche une erreur de connexion.
- Activité 5-2 RMI direct : création et consultation du compte 101, refus du doublon, compte 999 introuvable.
- Activité 5-2 JNDI : mêmes quatre cas réussis après `rebind` et `lookup` JNDI. Le serveur restait actif après l'appel client.

Les captures d'écran et la démonstration manuelle dans l'IDE restent à faire.

## 9. Synthèse

J'ai utilisé une interface distante pour définir les appels possibles, `UnicastRemoteObject` pour exposer l'implémentation et le registre pour retrouver le service. Les JAR clients ne partagent que les contrats et la classe `Compte`, transférée par sérialisation. JNDI fournit une autre API pour publier et rechercher l'objet RMI.

## 10. Dépôt

Nom prévu du dépôt GitHub : `TP5-DAR2026`. Après ajout des captures, publier le projet avec autorisation, puis déposer le lien du dépôt sur l'ENT.
