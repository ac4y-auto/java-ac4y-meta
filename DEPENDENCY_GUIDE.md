# Függőségi Útmutató - Ac4y Java Projektek

## Gyors Referencia

```xml
<!-- Csak utility (String, Date, GUID, XML, JSON) -->
<dependency>
    <groupId>ac4y</groupId>
    <artifactId>ac4y-utility</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Process kezelés + utility -->
<dependency>
    <groupId>ac4y</groupId>
    <artifactId>ac4y-base</artifactId>
    <version>1.1.0</version>
</dependency>

<!-- DB connection properties-ből + base + utility -->
<dependency>
    <groupId>ac4y</groupId>
    <artifactId>ac4y-database</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- JNDI connection pooling + base + utility -->
<dependency>
    <groupId>ac4y</groupId>
    <artifactId>ac4y-connection-pool</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Multi-module context + database + base + utility -->
<dependency>
    <groupId>ac4y</groupId>
    <artifactId>ac4y-context</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Függőségi Fa

### ac4y-utility (1.0.0)
```
ac4y-utility
├── com.google.code.gson:gson:2.10.1
├── org.jdom:jdom2:2.0.6.1
├── jakarta.xml.bind:jakarta.xml.bind-api:2.3.3
└── org.glassfish.jaxb:jaxb-runtime:2.3.9
```

### ac4y-base (1.1.0)
```
ac4y-base
└── ac4y:ac4y-utility:1.0.0
    ├── com.google.code.gson:gson:2.10.1
    ├── org.jdom:jdom2:2.0.6.1
    ├── jakarta.xml.bind:jakarta.xml.bind-api:2.3.3
    └── org.glassfish.jaxb:jaxb-runtime:2.3.9
```

### ac4y-database (1.0.0)
```
ac4y-database
└── ac4y:ac4y-base:1.0.0
    └── ac4y:ac4y-utility:1.0.0
        ├── com.google.code.gson:gson:2.10.1
        ├── org.jdom:jdom2:2.0.6.1
        ├── jakarta.xml.bind:jakarta.xml.bind-api:2.3.3
        └── org.glassfish.jaxb:jaxb-runtime:2.3.9
```

### ac4y-connection-pool (1.0.0)
```
ac4y-connection-pool
└── ac4y:ac4y-base:1.0.0
    └── ac4y:ac4y-utility:1.0.0
        ├── (külső függőségek...)
```

### ac4y-context (1.0.0)
```
ac4y-context
├── ac4y:ac4y-base:1.0.0
│   └── ac4y:ac4y-utility:1.0.0
│       ├── (külső függőségek...)
└── ac4y:ac4y-database:1.0.0
    └── (már benne van fentről)
```

## Version Constraints

### Kompatibilitás Mátrix

| Projekt Modul | ac4y-utility | ac4y-base | ac4y-database |
|---------------|--------------|-----------|---------------|
| ac4y-utility | - | - | - |
| ac4y-base | 1.0.0 | - | - |
| ac4y-database | (tranzitív) | 1.0.0 | - |
| ac4y-connection-pool | (tranzitív) | 1.0.0 | - |
| ac4y-context | (tranzitív) | 1.0.0 | 1.0.0 |

### Ajánlott Verzió Kombinációk

**Stabil (Production-ready):**
```xml
<properties>
    <ac4y.utility.version>1.0.0</ac4y.utility.version>
    <ac4y.base.version>1.1.0</ac4y.base.version>
    <ac4y.database.version>1.0.0</ac4y.database.version>
    <ac4y.connection-pool.version>1.0.0</ac4y.connection-pool.version>
    <ac4y.context.version>1.0.0</ac4y.context.version>
</properties>
```

**Bleeding Edge (Latest):**
```xml
<!-- Mindig a legújabb minor verzió -->
<properties>
    <ac4y.utility.version>1.0.0</ac4y.utility.version>
    <ac4y.base.version>1.1.0</ac4y.base.version>
    <!-- ... stb -->
</properties>
```

## Dependency Management (BOM Pattern)

### Parent POM Használata

Ha több ac4y modult használsz, készíts egy parent POM-ot:

```xml
<!-- parent-pom.xml -->
<project>
    <groupId>com.mycompany</groupId>
    <artifactId>myapp-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <dependencyManagement>
        <dependencies>
            <!-- Ac4y modulok -->
            <dependency>
                <groupId>ac4y</groupId>
                <artifactId>ac4y-utility</artifactId>
                <version>1.0.0</version>
            </dependency>
            <dependency>
                <groupId>ac4y</groupId>
                <artifactId>ac4y-base</artifactId>
                <version>1.1.0</version>
            </dependency>
            <dependency>
                <groupId>ac4y</groupId>
                <artifactId>ac4y-database</artifactId>
                <version>1.0.0</version>
            </dependency>
            <!-- ... többi ... -->
        </dependencies>
    </dependencyManagement>
</project>
```

Majd a gyerek modulokban:

```xml
<!-- module-pom.xml -->
<project>
    <parent>
        <groupId>com.mycompany</groupId>
        <artifactId>myapp-parent</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>myapp-service</artifactId>

    <dependencies>
        <!-- Verzió a parent-ből jön -->
        <dependency>
            <groupId>ac4y</groupId>
            <artifactId>ac4y-database</artifactId>
        </dependency>
    </dependencies>
</project>
```

## Külső Függőségek

### Gson (JSON)

**Verzió:** 2.10.1
**Licensz:** Apache 2.0
**Használva:** ac4y-utility (GsonCap)

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

**Konfliktus kezelés:**
Ha a projektedben más Gson verzió van:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version> <!-- Kikényszerít specifikus verziót -->
        </dependency>
    </dependencies>
</dependencyManagement>
```

### JDOM2 (XML)

**Verzió:** 2.0.6.1
**Licensz:** Apache-style
**Használva:** ac4y-utility (XMLHandler alternatíva)

```xml
<dependency>
    <groupId>org.jdom</groupId>
    <artifactId>jdom2</artifactId>
    <version>2.0.6.1</version>
</dependency>
```

### JAXB (XML Binding)

**Verziók:**
- jakarta.xml.bind-api: 2.3.3
- jaxb-runtime: 2.3.9

**Licensz:** Eclipse Distribution License
**Használva:** ac4y-utility (JaxbCap)

```xml
<dependency>
    <groupId>jakarta.xml.bind</groupId>
    <artifactId>jakarta.xml.bind-api</artifactId>
    <version>2.3.3</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>2.3.9</version>
</dependency>
```

**Megjegyzés:** Java 11+ környezetben JAXB nincs beépítve, explicit függőség kell.

## JDBC Driver-ek

Az ac4y modulok **NEM** tartalmaznak JDBC driver-eket. Ezeket az alkalmazásnak kell biztosítania.

### MySQL

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
    <scope>runtime</scope>
</dependency>
```

### PostgreSQL

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
    <scope>runtime</scope>
</dependency>
```

### Oracle

```xml
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>21.9.0.0</version>
    <scope>runtime</scope>
</dependency>
```

### H2 (Testing)

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.2.220</version>
    <scope>test</scope>
</dependency>
```

## Scope-ok

### Compile Scope (default)

```xml
<dependency>
    <groupId>ac4y</groupId>
    <artifactId>ac4y-base</artifactId>
    <version>1.1.0</version>
    <!-- scope nincs megadva = compile -->
</dependency>
```

Használat: Normál alkalmazáskód

### Runtime Scope

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
    <scope>runtime</scope>
</dependency>
```

Használat: JDBC driver-ek, csak futási időben kellenek

### Test Scope

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
```

Használat: Unit tesztek

### Provided Scope

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
    <scope>provided</scope>
</dependency>
```

Használat: Java EE környezetben az application server biztosítja

## Dependency Exclusions

Ha tranzitív függőséget ki akarsz zárni:

```xml
<dependency>
    <groupId>ac4y</groupId>
    <artifactId>ac4y-utility</artifactId>
    <version>1.0.0</version>
    <exclusions>
        <exclusion>
            <groupId>org.jdom</groupId>
            <artifactId>jdom2</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

**Figyelem:** Csak akkor csináld, ha biztosan nem használod az adott funkciót!

## Dependency Analysis

### Használt Függőségek Listázása

```bash
mvn dependency:tree
```

**Kimenet példa:**
```
[INFO] com.mycompany:myapp:jar:1.0.0
[INFO] \- ac4y:ac4y-database:jar:1.0.0:compile
[INFO]    \- ac4y:ac4y-base:jar:1.0.0:compile
[INFO]       \- ac4y:ac4y-utility:jar:1.0.0:compile
[INFO]          +- com.google.code.gson:gson:jar:2.10.1:compile
[INFO]          +- org.jdom:jdom2:jar:2.0.6.1:compile
[INFO]          +- jakarta.xml.bind:jakarta.xml.bind-api:jar:2.3.3:compile
[INFO]          \- org.glassfish.jaxb:jaxb-runtime:jar:2.3.9:compile
```

### Dependency Conflict Ellenőrzés

```bash
mvn dependency:analyze
```

**Figyelmeztet:**
- Használt de deklarálatlan függőségekre
- Deklarált de nem használt függőségekre

### Frissíthető Verziók

```bash
mvn versions:display-dependency-updates
```

## Gyakori Problémák

### Problem: NoClassDefFoundError

**Ok:** Hiányzó tranzitív függőség

**Megoldás:**
```bash
mvn dependency:tree | grep -i "missing-class"
```

Majd explicit add hozzá:
```xml
<dependency>
    <groupId>missing-group</groupId>
    <artifactId>missing-artifact</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Problem: ClassCastException / MethodNotFoundError

**Ok:** Verzió konfliktus (két különböző verzió ugyanabból a library-ből)

**Megoldás:**
```bash
mvn dependency:tree -Dverbose
```

Kényszeríts specifikus verziót dependency management-ben.

### Problem: Circular Dependency

**Ok:** A→B→A függőség

**Ac4y esetén:** NINCS ciklikus függőség!

**Hierarchia:**
```
utility → base → database → context
         ↓
         connection-pool
```

## Best Practices

1. **Mindig használj dependency management-et** multi-module projektekben
2. **Explicit verziószámok** minden függőségnél
3. **Minimal dependencies** - csak ami kell
4. **Scope-ok** helyesen beállítva (runtime, test, provided)
5. **Dependency analysis** rendszeres futtatása
6. **Version updates** óvatosan, teszteléssel
7. **Exclusions** csak indokolt esetben

## Version Update Checklist

Amikor ac4y verziót frissítesz:

- [ ] Ellenőrizd a CHANGELOG-ot breaking changes-ért
- [ ] Futtass `mvn dependency:tree` - ellenőrizd a tranzitív függőségeket
- [ ] Unit tesztek futtatása
- [ ] Integration tesztek futtatása
- [ ] Performance regression teszt (ha van)
- [ ] Staging environment tesztelés
- [ ] Production deployment

## Hasznos Maven Parancsok

```bash
# Dependency fa
mvn dependency:tree

# Dependency analysis
mvn dependency:analyze

# Effective POM (összes örökölt konfig)
mvn help:effective-pom

# Update ellenőrzés
mvn versions:display-dependency-updates

# Dependency copy (offline használatra)
mvn dependency:copy-dependencies
```

---

**Utolsó frissítés:** 2026-02-06
