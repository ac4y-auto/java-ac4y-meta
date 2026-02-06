# Ac4y Java Projektek - Projekt Áttekintés

## Mi az Ac4y?

Az **Ac4y** egy moduláris Java keretrendszer, amely általános célú segédeszközöket és infrastruktúrát biztosít Java alkalmazásokhoz. A projekt legacy rendszerekből származik, most pedig modern moduláris architektúrával újjáépítésre kerül.

## Design Filozófia

### 1. Modularitás
Minden funkcionalitás saját Maven modulban, független verziókezeléssel.

### 2. Függőségi Hierarchia
Egyértelmű függőségi fa, ciklikus függőségek nélkül.

### 3. Convention over Configuration
Konvenciók követése csökkenti a konfigurációs overheadet.

### 4. Backward Compatibility
Minor verzió frissítések nem törnek backward compatibility-t.

### 5. AI-First Documentation
Dokumentáció elsősorban AI agent-ek számára optimalizálva, de emberek is használhatják.

## Modul Részletes Áttekintése

### java-ac4y-utility

**Verzió:** 1.0.0
**Függőségek:** Gson, JDOM2, JAXB (külső)
**Célja:** Alapvető utility funkciók

**Főbb komponensek:**
- String kezelés (ékezet eltávolítás, encoding, parsing)
- Dátum műveletek (konverzió, eltolás)
- GUID generálás
- XML műveletek (DOM, JAXB)
- JSON műveletek (Gson wrapper)

**Használati eset:**
- Bármely Java projekt, ami utility funkciókat igényel
- Nincs ac4y függőség, önállóan használható

**Példa:**
```java
StringHandler sh = new StringHandler();
String clean = sh.getSimpled("Ékezetes Szöveg!");

GUIDHandler gh = new GUIDHandler();
String uuid = gh.getGUID();
```

---

### java-ac4y-base

**Verzió:** 1.1.0
**Függőségek:** ac4y-utility v1.0.0
**Célja:** Keretrendszer mag komponensek

**Főbb komponensek:**
- `Ac4yProcess`: Template method pattern feldolgozáshoz
- `Ac4yProcessContext`: Context objektum external/internal adatokkal
- `Ac4yException`: Keretrendszer exception
- `ErrorHandler`: Egyszerű error logging
- `ExternalPropertyHandler`: Properties betöltés classpath-ról

**Használati eset:**
- Process-alapú feldolgozási logika
- Egységes kivételkezelés
- Konfiguráció betöltés

**Példa:**
```java
public class MyProcess extends Ac4yProcess {
    @Override
    public Object process(Object input) throws Ac4yException {
        // logika
        return output;
    }
}
```

**Változások v1.0.0 → v1.1.0:**
- Utility osztályok kikerültek ac4y-utility modulba
- ac4y-utility v1.0.0 függőség hozzáadva

---

### java-ac4y-database

**Verzió:** 1.0.0
**Függőségek:** ac4y-base v1.0.0
**Célja:** JDBC connection kezelés properties fájlokból

**Főbb komponensek:**
- `DBConnection`: Properties-alapú JDBC connection létrehozás
- `Ac4yDBAdapter`: Connection wrapper

**Használati eset:**
- Standalone Java alkalmazások
- Properties-alapú DB konfiguráció
- Direkt JDBC használat

**Példa:**
```java
DBConnection dbConn = new DBConnection("myapp.properties");
Connection conn = dbConn.getConnection();
// SQL műveletek
conn.close();
```

**Properties formátum:**
```properties
driver=com.mysql.cj.jdbc.Driver
connectionString=jdbc:mysql://localhost:3306/mydb
dbuser=root
dbpassword=secret
```

---

### java-ac4y-connection-pool

**Verzió:** 1.0.0
**Függőségek:** ac4y-base v1.0.0
**Célja:** JNDI DataSource connection pooling Java EE környezetben

**Főbb komponensek:**
- `DBConnection`: JNDI lookup és pooled connection kezelés

**Használati eset:**
- Java EE / Jakarta EE alkalmazások (Tomcat, JBoss, GlassFish)
- Application server által kezelt connection pool
- Servlet, JSP, EJB környezet

**Példa:**
```java
// Application server DataSource: java:comp/env/jdbc/ac4y
DBConnection dbConn = new DBConnection();
Connection conn = dbConn.getConnection();
// SQL műveletek
conn.close(); // Visszaadja a pool-nak!
```

**JNDI konfiguráció (Tomcat):**
```xml
<Resource name="jdbc/ac4y"
          type="javax.sql.DataSource"
          maxTotal="100"
          url="jdbc:mysql://localhost:3306/mydb"/>
```

---

### java-ac4y-context

**Verzió:** 1.0.0
**Függőségek:** ac4y-base v1.0.0, ac4y-database v1.0.0
**Célja:** Modulspecifikus DB connection factory

**Főbb komponensek:**
- `Ac4yContext`: Factory osztály modulonkénti DB connection-höz

**Használati eset:**
- Multi-module alkalmazások
- Modulonként különböző adatbázis
- Convention-based konfiguráció

**Példa:**
```java
Ac4yContext context = new Ac4yContext();

// inventory.properties alapján
DBConnection inventoryDB = context.getDBConnection("inventory", "InventoryService");

// userservice.properties alapján
DBConnection userDB = context.getDBConnection("userservice", "UserService");
```

**Konvenció:**
- Modul név: `inventory` → Properties: `inventory.properties`
- Modul név: `userservice` → Properties: `userservice.properties`

---

## Függőségi Gráf

```
ac4y-utility (1.0.0)
    ↑
    │
ac4y-base (1.1.0)
    ↑
    ├─────────────┬──────────────┐
    │             │              │
ac4y-database  ac4y-connection-pool  (közvetlenül is)
 (1.0.0)        (1.0.0)
    ↑
    │
ac4y-context (1.0.0)
```

**Magyarázat:**
- `ac4y-utility`: Független, senkitől sem függ
- `ac4y-base`: Függ ac4y-utility-től
- `ac4y-database`: Függ ac4y-base-től
- `ac4y-connection-pool`: Függ ac4y-base-től
- `ac4y-context`: Függ ac4y-base-től ÉS ac4y-database-től

## Mikor Melyik Modult Használd?

### Csak utility funkciók kellenek?
→ **ac4y-utility**

### Process-alapú feldolgozás, kivételkezelés?
→ **ac4y-base** (+ tranzitív ac4y-utility)

### Egyszerű DB connection properties-ből?
→ **ac4y-database** (+ tranzitív ac4y-base, ac4y-utility)

### Java EE connection pooling?
→ **ac4y-connection-pool** (+ tranzitív ac4y-base, ac4y-utility)

### Multi-module app, modulonként külön DB?
→ **ac4y-context** (+ tranzitív ac4y-database, ac4y-base, ac4y-utility)

## Verzió Kompatibilitás Mátrix

| Modul | v1.0.0 | v1.1.0 | v2.0.0 (future) |
|-------|--------|--------|-----------------|
| ac4y-utility | ✅ | ✅ | ⚠️ Breaking |
| ac4y-base | ✅ v1.0.0 | ✅ v1.1.0 | ⚠️ Breaking |
| ac4y-database | ✅ | ✅ (base v1.0.0) | ⚠️ |
| ac4y-connection-pool | ✅ | ✅ (base v1.0.0) | ⚠️ |
| ac4y-context | ✅ | ✅ (db v1.0.0) | ⚠️ |

**Megjegyzés:**
- ac4y-database, connection-pool, context még base v1.0.0-t használnak
- Következő release-ben bump-olni kell base v1.1.0-ra

## Jövőbeli Tervek

### Rövid távú (v1.x)
- [ ] ac4y-database, connection-pool, context bump base v1.1.0-ra
- [ ] Egységes test coverage 80%+
- [ ] Performance benchmarks
- [ ] Javadoc generálás

### Közép távú (v2.x)
- [ ] Java 17 támogatás
- [ ] Module-info.java (JPMS)
- [ ] Virtual threads support
- [ ] Reactive streams support

### Hosszú távú (v3.x)
- [ ] Spring Boot starter-ek
- [ ] Quarkus extension-ök
- [ ] Cloud-native features (observability, health checks)

## Migrációs Útmutatók

### v1.0.0 → v1.1.0 (ac4y-base)

**Breaking changes:** NINCS

**Új függőség:** ac4y-utility v1.0.0 (tranzitív)

**Teendő:**
```xml
<!-- Régi -->
<dependency>
    <groupId>ac4y</groupId>
    <artifactId>ac4y-base</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Új -->
<dependency>
    <groupId>ac4y</groupId>
    <artifactId>ac4y-base</artifactId>
    <version>1.1.0</version>
</dependency>
```

**Kód változás:** NINCS (backward compatible)

## Contribution Guidelines

### Új Feature Hozzáadása

1. **Válaszd ki a megfelelő modult**
   - Utility funkció? → ac4y-utility
   - Process/exception? → ac4y-base
   - Database? → ac4y-database, connection-pool, vagy context

2. **Branch létrehozás**
   ```bash
   git checkout -b feature/my-new-feature
   ```

3. **Fejlesztés**
   - Kód írás
   - Unit tesztek (min 80% coverage)
   - ARCHITECTURE.md frissítés

4. **PR létrehozás**
   ```bash
   gh pr create --title "Add feature X" --body "Description"
   ```

5. **Review és merge**

6. **Version bump és release**
   - Minor version bump (v1.0.0 → v1.1.0)
   - Git tag + GitHub release

### Bug Fix

1. **Issue nyitás** (ha nincs még)
2. **Branch:** `fix/issue-123-description`
3. **Patch version bump** (v1.0.0 → v1.0.1)
4. **Hotfix release** ha kritikus

## Support és Community

### Dokumentáció
- Minden modul `ARCHITECTURE.md` - részletes docs
- `README.md` - gyors áttekintés
- `java-ac4y-meta` - központi iránymutatás

### Issues
- GitHub Issues az adott repository-ban
- Template: Bug report, Feature request

### Kérdések
- GitHub Discussions (TODO: Beállítás)
- Stack Overflow tag: `ac4y` (TODO: Létrehozás)

## Teljesítmény

### Benchmarks (TODO)

| Művelet | Idő (ms) | Memória (MB) |
|---------|----------|--------------|
| GUID generation | TBD | TBD |
| String simplification | TBD | TBD |
| DB connection (direct) | TBD | TBD |
| DB connection (pooled) | TBD | TBD |

## Biztonság

### Vulnerability Scanning
- Dependabot enabled minden repo-ban
- Automata security updates

### Best Practices
- Soha ne commitolj jelszavakat
- Properties fájlok .gitignore-ba (environment-specific)
- GitHub Secrets használata CI/CD-ben

## Licenc

**TODO:** Licenc meghatározása (MIT, Apache 2.0, GPL?)

---

**Utolsó frissítés:** 2026-02-06
