# Ghid Setup Laborator - Tool-uri Esențiale

## 1. Java SDK
**Versiune recomandată:** Java 17 sau 21 (LTS)

### Instalare:
- **Windows/macOS:** Descarcă de la [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) sau [OpenJDK](https://openjdk.org/)
- **Linux (Ubuntu/Debian):**
  ```bash
  sudo apt update
  sudo apt install openjdk-17-jdk
  ```

### Verificare instalare:
```bash
java -version
javac -version
```

## 2. IDE

### IntelliJ IDEA
- Descarcă [IntelliJ IDEA Community](https://www.jetbrains.com/idea/download/) (gratuit)
- Rulează installer-ul și urmează pașii

### NetBeans
- Descarcă de la [netbeans.apache.org](https://netbeans.apache.org/download/)
- Asigură-te că ai Java SDK instalat înainte

## 3. Git Client
### Instalare:
- **Windows:** Descarcă de la [git-scm.com](https://git-scm.com/)
- **macOS:** `brew install git` sau descarcă de pe site
- **Linux:** `sudo apt install git`

### Configurare inițială:
```bash
git config --global user.name "Numele Tau"
git config --global user.email "email@tau.com"
```

## 4. Cont GitHub
1. Accesează [github.com](https://github.com)
2. Creează cont nou
3. Verifică email-ul
4. **Opțional:** Configurează SSH key pentru acces mai facil

## 5. Docker (Opțional dar recomandat)
### Instalare:
- **Windows/macOS:** Descarcă [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- **Linux:**
  ```bash
  sudo apt update
  sudo apt install docker.io docker-compose
  sudo usermod -aG docker $USER
  ```

### Verificare:
```bash
docker --version
docker-compose --version
```

## Verificare Setup Final
Rulează următoarele comenzi pentru a verifica că totul funcționează:
```bash
java -version
git --version
docker --version (dacă ai instalat Docker)
```

## Note Importante
- **Tool-uri suplimentare** vor fi anunțate pe parcursul laboratoarelor
- Păstrează toate tool-urile **actualizate**
- În caz de probleme, verifică documentația oficială a fiecărui tool
- Pentru support, contactează echipa de laborator

