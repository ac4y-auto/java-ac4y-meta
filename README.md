# Ac4y Java Projektek - Meta Dokumentáció

## Áttekintés

Ez a repository a központi dokumentációs és iránymutatási tár az **ac4y Java projektcsaládhoz**. Új AI session-ök innen tájékozódnak a munkamódszerekről, projekt struktúráról és fejlesztési folyamatokról.

## Projekt Családfa

Az ac4y Java projektek moduláris felépítésűek, GitHub Packages-en keresztül érhetők el:

```
ac4y-auto (GitHub Organization)
│
├── java-ac4y-utility (v1.0.0)          - Utility osztályok (String, Date, GUID, XML, JSON)
│   └── Független, nincs ac4y függősége
│
├── java-ac4y-base (v1.1.0)             - Process kezelés, exception handling, config
│   └── Függ: ac4y-utility v1.0.0
│
├── java-ac4y-database (v1.0.0)         - JDBC connection properties-ből
│   └── Függ: ac4y-base v1.0.0
│
├── java-ac4y-connection-pool (v1.0.0)  - JNDI connection pooling (Java EE)
│   └── Függ: ac4y-base v1.0.0
│
├── java-ac4y-context (v1.0.0)          - Context factory, multi-module DB
│   └── Függ: ac4y-base v1.0.0, ac4y-database v1.0.0
│
└── java-ac4y-meta (this repo)          - Dokumentáció, iránymutatások
```

## GitHub Packages Elérés

### Maven Settings

A `~/.m2/settings.xml` fájlban:

```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>ac4y</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

**Token scope:** `read:packages` (vagy `write:packages` publish-hoz)

### Projekt pom.xml

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/ac4y-auto/*</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>ac4y</groupId>
        <artifactId>ac4y-base</artifactId>
        <version>1.1.0</version>
    </dependency>
</dependencies>
```

## Szervezeti Struktúra

### ac4y-auto (Fejlesztési Organization)

**Cél:** Aktív fejlesztés, Claude hozzáféréssel

**Védelem:**
- Branch protection rules (force push tiltva)
- Repository deletion organization-level jóváhagyás
- Fine-grained GitHub token korlátozott jogosultságokkal

**Claude jogosultságok:**
- Repository létrehozás: ✅
- Kód írás/módosítás: ✅
- Release létrehozás: ✅
- Repository törlés: ❌ (organization approval kell)
- Force push: ❌ (branch protection)

### ac4y-safe (Backup Organization)

**Cél:** Automatikus backup, disaster recovery

**Védelem:**
- Csak GitHub Actions írhat ide
- Manuális hozzáférés csak vészhelyzet esetén
- Claude nincs hozzáférése

**Backup folyamat:**
- Automatikus mirror minden push-nál (GitHub Actions)
- `.github/workflows/backup-mirror.yml` minden repo-ban

## Fejlesztési Munkamódszer

### 1. Új Projekt Létrehozása

```bash
# ac4y-auto organization-ben
gh repo create ac4y-auto/java-ac4y-{modulename} --public

# Lokális inicializálás
cd java-ac4y-{modulename}
git init
# ... fejlesztés ...
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/ac4y-auto/java-ac4y-{modulename}.git
git push -u origin main
```

### 2. Projekt Struktúra Template

```
java-ac4y-{modulename}/
├── .github/
│   └── workflows/
│       ├── maven-publish.yml        # GitHub Packages deploy
│       └── backup-mirror.yml        # Backup to ac4y-safe
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ac4y/{modulename}/
│   │   └── resources/
│   └── test/
│       └── java/
├── pom.xml
├── README.md
├── ARCHITECTURE.md                  # Részletes dokumentáció AI agent-eknek
└── .gitignore
```

### 3. pom.xml Konvenciók

**GroupId:** `ac4y`
**ArtifactId:** `ac4y-{modulename}` (NINCS `java-` prefix a Maven artifact-ben!)
**Repository név:** `java-ac4y-{modulename}` (VAN `java-` prefix!)

**Példa:**
- GitHub repo: `java-ac4y-utility`
- Maven artifact: `ac4y-utility`
- GroupId: `ac4y`

```xml
<groupId>ac4y</groupId>
<artifactId>ac4y-{modulename}</artifactId>
<version>1.0.0</version>

<distributionManagement>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/ac4y-auto/java-ac4y-{modulename}</url>
    </repository>
</distributionManagement>
```

### 4. Verziózás

**Semantic Versioning:** MAJOR.MINOR.PATCH

- **MAJOR:** Breaking changes (v1.0.0 → v2.0.0)
- **MINOR:** Új funkciók, backward compatible (v1.0.0 → v1.1.0)
- **PATCH:** Bug fixes (v1.0.0 → v1.0.1)

**Verzió bump folyamat:**
1. Módosítsd a `pom.xml` verzióját
2. Commit: `git commit -m "Bump version to v1.1.0"`
3. Tag: `git tag v1.1.0`
4. Push: `git push && git push --tags`
5. Release: `gh release create v1.1.0 --title "v1.1.0" --notes "Release notes"`

### 5. GitHub Actions Workflows

#### maven-publish.yml

```yaml
name: Publish to GitHub Packages

on:
  release:
    types: [created]

jobs:
  publish:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
      - name: Publish package
        run: mvn --batch-mode deploy
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

#### backup-mirror.yml

```yaml
name: Backup to ac4y-safe

on:
  push:
    branches:
      - main
      - master

jobs:
  backup:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
        with:
          fetch-depth: 0
      - name: Mirror to ac4y-safe
        env:
          BACKUP_TOKEN: ${{ secrets.BACKUP_TOKEN }}
        run: |
          REPO_NAME=$(basename $GITHUB_REPOSITORY)
          git remote add backup https://x-access-token:${BACKUP_TOKEN}@github.com/ac4y-safe/${REPO_NAME}.git
          git push backup --all --force
          git push backup --tags --force
```

**Organization secret:** `BACKUP_TOKEN` (ac4y-safe-hez írási jog)

### 6. Commit Konvenciók

```
<type>: <subject>

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>
```

**Types:**
- `feat`: Új funkció
- `fix`: Bug javítás
- `refactor`: Kód refaktorálás
- `docs`: Dokumentáció
- `test`: Tesztek
- `chore`: Build, dependencies

**Példák:**
```
feat: Add GUID generation utility

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>
```

```
refactor: Extract utility classes to separate module

Bump version to v1.1.0

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>
```

### 7. Pull Request Workflow

```bash
# Új branch
git checkout -b feature/new-feature

# Fejlesztés
git add .
git commit -m "feat: Implement new feature"

# Push
git push -u origin feature/new-feature

# PR létrehozás
gh pr create --title "Add new feature" --body "$(cat <<'EOF'
## Summary
- Implement feature X
- Add tests for feature X

## Test plan
- [ ] Unit tests pass
- [ ] Integration tests pass

🤖 Generated with Claude Code
EOF
)"
```

### 8. Dokumentáció Követelmények

Minden projektben **kötelező:**

1. **README.md** - Gyors áttekintés, használati példák
2. **ARCHITECTURE.md** - Részletes dokumentáció AI agent-eknek:
   - Komponens leírások
   - Használati minták
   - Gyors döntési fa
   - Token-hatékony összefoglaló
   - Troubleshooting

**ARCHITECTURE.md sablon:** Lásd `templates/ARCHITECTURE_TEMPLATE.md`

## AI Agent Munkamódszer

### Session Kezdés

1. **Olvasd el ezt a README-t** teljes egészében
2. **Nézd meg a projekt családfát** - értsd meg a függőségeket
3. **Ellenőrizd a GitHub token-t** (`~/.m2/settings.xml`)
4. **Ha új modult hozol létre:**
   - Kövesd a projekt struktúra template-et
   - Állítsd be a GitHub Actions workflow-kat
   - Készíts ARCHITECTURE.md-t

### Munkavégzés Közben

1. **Git műveletek:**
   - Commit-ok használják a konvenciót
   - Co-Authored-By mindig legyen
   - Ne force push (védett)
   - Ne töröld a repo-t (védett)

2. **Maven műveletek:**
   - Tesztek futtatása release előtt
   - Version bump dokumentálva
   - GitHub Packages publish release-en keresztül

3. **Dokumentáció:**
   - Kódváltozás = dokumentáció frissítés
   - ARCHITECTURE.md naprakész marad
   - Használati példák aktualizálása

### Troubleshooting

**Problem: 401 Unauthorized from GitHub Packages**
- Ellenőrizd: `~/.m2/settings.xml` token
- Token scope: `read:packages`

**Problem: Cannot push to repository**
- Branch protection? → PR-en keresztül
- Force push? → Tilos, új commit kell

**Problem: Backup workflow failed**
- `BACKUP_TOKEN` secret beállítva?
- ac4y-safe repo létezik?

## Kísérletek és Playground

A `playground/` könyvtár használható:
- Proof of concept-ekhez
- Új feature teszteléséhez
- Integrációs példákhoz

Ezek **NEM** kerülnek be a release-ekbe, csak referencia célra.

## Kapcsolódó Dokumentumok

- `PROJECT_OVERVIEW.md` - Projekt áttekintés
- `DEPENDENCY_GUIDE.md` - Függőségi útmutató
- `MIGRATION_GUIDE.md` - Migrációs útmutató régi verzióról
- `templates/` - Projekt templat-ek
- `examples/` - Használati példák
- `playground/` - Kísérleti kódok

## Gyors Hivatkozások

### Repositories
- [java-ac4y-utility](https://github.com/ac4y-auto/java-ac4y-utility)
- [java-ac4y-base](https://github.com/ac4y-auto/java-ac4y-base)
- [java-ac4y-database](https://github.com/ac4y-auto/java-ac4y-database)
- [java-ac4y-connection-pool](https://github.com/ac4y-auto/java-ac4y-connection-pool)
- [java-ac4y-context](https://github.com/ac4y-auto/java-ac4y-context)

### GitHub Organizations
- [ac4y-auto](https://github.com/ac4y-auto) - Fejlesztési organization
- [ac4y-safe](https://github.com/ac4y-safe) - Backup organization

### Maven Central (GitHub Packages)
- https://maven.pkg.github.com/ac4y-auto/*

## Licenc

Minden ac4y projekt nyílt forráskódú. (TODO: Konkrét licenc meghatározása)

## Kapcsolat

- GitHub Issues: Használd az adott projekt issue tracker-ét
- Dokumentáció frissítés: PR ide (java-ac4y-meta)

---

**Utolsó frissítés:** 2026-02-06
**Meta verzió:** 1.0.0
