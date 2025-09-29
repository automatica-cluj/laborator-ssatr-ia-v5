# Ghid de Onboarding - Laborator SSATR
## Managementul proiectelor prin GitHub

### Introducere

Bun venit la laboratorul SSATR!

**GitHub va fi platforma centrală** pentru toate activitățile de laborator. 

### Obiective și Așteptări

La finalul acestui setup, veți avea:
-  Un repository GitHub personal pentru laborator
-  Structura inițială de proiect configurată
-  Înțelegerea workflow-ului de dezvoltare cu GitHub
-  Pregătirea pentru utilizarea GitHub Issues și Pull Requests

### Workflow-ul de Dezvoltare

În cadrul laboratoarelor, veți folosi următorul workflow standardizat:

1. **Issues** - Pentru fiecare temă/exercițiu, veți crea un issue care descrie sarcina
2. **Branch-uri** - Veți lucra pe branch-uri separate pentru fiecare issue
3. **Pull Requests** - Fiecare soluție va fi propusă prin Pull Request
4. **Code Review** - Codul va fi revizuit înainte de merge
5. **Merge în Main** - După aprobare, codul va fi integrat în branch-ul principal

###  Pașii de Setup Inițial

#### Pasul 1: Crearea Repository-ului

1. **Accesați GitHub** și autentificați-vă în cont
2. **Creați un repository nou:**
   - Numele: `ssatr-lab-ia-[numeleprenumele]` (ex: `ssatr-lab-ia-ioanpopescu`)
   - Descrierea: "Repository pentru laboratorul SSATR - [Anul Academic]"
   - Setați repository-ul ca **Public**
   - ✅ Bifați "Add a README file"
   - ✅ Bifați "Add .gitignore" și selectați **Java**

#### Pasul 2: Configurarea README-ului Inițial

Editați fișierul `README.md` cu următoarea structură:

```markdown
# Laborator SSATR - [Numele și Prenumele]

## Despre
Repository pentru lucrarile de laborator din cadrul cursului Structuri Software pentru Aplicații de Timp Real.

```

###  Workflow pentru Fiecare Laborator

#### 1. Crearea unui Issue

Pentru fiecare laborator/exercițiu:
- Accesați tab-ul **Issues**
- Creați un issue nou cu titlul: `Lab [X] - [Descrierea scurtă]`
- Adăugați o descriere detaliată a cerințelor (veti primi aceste cerinte la inceputul laboratorului)
- Assignați issue-ul la voi înșivă
- Adăugați label-uri relevante (ex: `lab`, `enhancement`, `bug`)

#### 2. Crearea unui Branch din Interfața Web GitHub

1. **În fereastra issue-ului** pe care tocmai l-ați creat
2. Pe partea dreaptă, în secțiunea "Development"
3. Faceți click pe **"Create a branch"**
4. GitHub va crea automat un branch legat de issue cu numele `[numărul-issue]-lab-x-descrierea-scurta`
5. Confirmați crearea branch-ului

#### 3. Clonarea Repository-ului (Prima Dată)

```bash
# Clonarea repository-ului (doar prima dată)
git clone https://github.com/username/ssatr-ia-lab-numele.git
cd ssatr-ia-lab-numele
```

#### 4. Sincronizarea și Checkout pe Branch-ul de Lucru

```bash
# Vizualizarea tuturor branch-urilor remote
git fetch origin
git branch -a

# Checkout pe branch-ul corespunzător lab-ului/issue-ului
git checkout [numele-branch-ului-creat-pe-github]
# Ex: git checkout 1-lab-0-onboarding-setup
```

#### 5. Efectuarea Lucrării

Pentru acest onboarding (Lab 0):
```bash
# Crearea directorului pentru lab
mkdir lab-0
cd lab-0

# Copierea fișierului de onboarding în director
# (copiați manual fișierul onboarding-guide.md în directorul lab-0)
```

Adauga in acest folder un proiect de tip "hello world" java.

#### 6. Commit și Push Modificărilor

```bash
# Adăugarea modificărilor
git add .

# Commit cu mesaj descriptiv
git commit -m "Lab 0: Add onboarding guide

- Create lab-0 directory structure
- Add onboarding guide documentation 
- Create sample java project
- Setup initial project structure

Closes #[numărul-issue]"

# Push modificările pe branch-ul remote
git push origin [numele-branch-ului]
```

#### 7. Crearea Pull Request-ului din Interfața Web

1. **Accesați repository-ul pe GitHub**
2. Veți vedea un banner cu opțiunea **"Compare & pull request"**
3. Faceți click pe buton
4. Completați:
   - **Titlu**: descriptiv și clar (ex: "Lab 0: Add onboarding documentation")
   - **Descriere**: explicați ce schimbări ați făcut
   - **Link către issue**: `Closes #[numărul-issue]`
5. Faceți click pe **"Create pull request"**

#### 8. Review și Merge

- Așteptați feedback-ul (de la instructor sau colegi)
- Implementați sugestiile primite dacă este necesar
- Faceți click pe **"Merge pull request"**
- Confirmați merge-ul cu **"Confirm merge"**
- **IMPORTANT**: Ștergeți branch-ul după merge - click pe **"Delete branch"**

#### 9. Sincronizarea Locală după Merge

```bash
# Revenirea pe branch-ul main
git checkout main

# Actualizarea branch-ului main cu modificările de pe remote
git pull origin main

# Verificarea că modificările au fost integrate
git log --oneline -5
```

#### 10. Curățarea Branch-urilor Locale (Opțional)

```bash
# Ștergeți branch-ul local dacă nu mai este necesar
git branch -d [numele-branch-ului-local]

# Curățarea referințelor către branch-urile remote șterse
git remote prune origin
```


###  Verificarea Setup-ului

Pentru a confirma că ați configurat totul corect:

- [ ] Repository-ul există și este public
- [ ] README.md conține informațiile cerute
- [ ] .gitignore este configurat pentru Java
- [ ] Ați creat primul issue pentru Lab01
- [ ] Ați făcut primul commit pe un branch separat
- [ ] Ați creat primul Pull Request

