# 📧 Prog3Progetto - Servizio di Email Locale

## 📌 Descrizione
Questo progetto è un'applicazione **Java** per la gestione delle email, con un'architettura **client-server**.
Le email vengono memorizzate in un file locale (`emails.dat`) invece di un database.
L'applicazione include sia una **parte server** che gestisce le email, sia una **interfaccia client** sviluppata con **JavaFX** per la composizione e la gestione delle email.
Il client si connette al server tramite **socket TCP**, inviando e ricevendo oggetti serializzati.

## 🚀 Tecnologie Utilizzate
- **Java 17+** (consigliato)
- **Maven** (per la gestione delle dipendenze e build)
- **JavaFX** (per l'interfaccia grafica)
- **Socket TCP** (per la comunicazione client-server)

## 📂 Struttura del Progetto
```
Prog3Progetto/
│── src/
│   ├── main/java/prog3/prog3progetto/
│   │   ├── MainApp.java          # Punto di ingresso dell'applicazione
│   │   ├── ServerApp.java        # Avvio del server per la gestione delle email
│   │   ├── Server.java           # Logica del server email
│   │   ├── Email.java            # Modello dati per le email
│   │   ├── EmailController.java  # Gestione della casella email lato client
│   │   ├── ComposeController.java # Interfaccia per la composizione delle email
│   │   ├── MailboxController.java # Gestione della casella di posta
│   │   ├── StartController.java  # Gestisce la schermata iniziale e verifica l'email dell'utente con il server
│   │   ├── SessionStore.java     # Gestione delle sessioni utente
│   │   ├── DeleteEmailsRequest.java # Classe per eliminazione email lato server
│── emails.dat                      # Database locale delle email
│── pom.xml                         # Configurazione di Maven
│── .gitignore                      # File di configurazione per Git
```

## 🔧 Installazione e Utilizzo
### 1️⃣ Requisiti
Assicurati di avere installati:
- [Java 17+](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- [Apache Maven](https://maven.apache.org/install.html)

### 2️⃣ Clonare il repository
```sh
git clone <repo-url>
cd Prog3Progetto
```

### 3️⃣ Compilazione ed esecuzione
#### Avviare il **Server**
```sh
mvn clean install
mvn exec:java -Dexec.mainClass="prog3.prog3progetto.ServerApp"
```

#### Avviare il **Client**
```sh
mvn exec:java -Dexec.mainClass="prog3.prog3progetto.MainApp"
```

## 🛠️ Flusso di funzionamento
1. **Avvio del server** (`ServerApp.java`) per la gestione delle email.
   - Il server ascolta sulla porta **12345** e attende connessioni dai client.
2. **Avvio dell'interfaccia client** (`MainApp.java`).
3. **L'utente inserisce la propria email nella schermata di login** (`StartController.java`).
   - Il client invia la richiesta al server per verificare se l'email è registrata.
   - Se l'email è valida, viene caricata la casella di posta.
4. **Composizione di un'email** tramite GUI (`ComposeController.java`).
   - L'utente scrive il messaggio e lo invia tramite socket TCP al server.
5. **Invio dell'email** al server (`Server.java`).
   - Il server memorizza il messaggio nel file `emails.dat` e invia una conferma al client.
6. **Gestione della casella di posta** tramite `MailboxController.java`.
   - Il client recupera la lista delle email salvate.
7. **Eliminazione di email** inviata come richiesta al server (`DeleteEmailsRequest.java`).
   - Il server aggiorna `emails.dat` rimuovendo i messaggi eliminati.

## ✅ Funzionalità
✔️ Composizione e invio di email 
✔️ Organizzazione della casella di posta 
✔️ Comunicazione client-server basata su socket 
✔️ Interfaccia grafica con JavaFX 
✔️ Persistenza delle email su file locale (`emails.dat`) 
✔️ Gestione delle sessioni utente

## 🔍 Dettagli Tecnici
- **Comunicazione Client-Server:**
  - Il client e il server comunicano tramite **socket TCP** sulla porta **12345**.
  - Gli oggetti vengono inviati in formato **serializzato** (`ObjectOutputStream` e `ObjectInputStream`).
- **Gestione della persistenza:**
  - Il file `emails.dat` contiene la lista delle email in formato serializzato.
  - Ogni email è un oggetto della classe `Email.java`.
- **Architettura:**
  - Il client è basato su **JavaFX** per la GUI e utilizza `SessionStore.java` per gestire lo stato dell'utente.
  - Il server è un'applicazione standalone che gestisce la ricezione e il salvataggio delle email.

## 🤝 Contributi
Se vuoi contribuire:
1. Fai un **fork** del progetto  
2. Crea un nuovo **branch** (`feature/nome-feature`)  
3. Fai una **pull request**  

## 📜 Licenza
Questo progetto è distribuito sotto la licenza **MIT**.

---

📩 **Autore:** Bongiovanni Francesco, Aimo Simone
🌍 **GitHub:** (https://github.com/Francesco-Bongio)

