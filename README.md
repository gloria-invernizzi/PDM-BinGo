# BinGo!

Progetto di Programmazione per Dispositivi Mobili, Università di Milano, Bicocca, 2025-2026.

**BinGo!** è un'applicazione Android progettata per semplificare la gestione dei rifiuti e 
promuovere il corretto riciclo. Attraverso funzionalità intuitive, aiuta gli utenti a identificare 
i materiali degli imballaggi e a restare aggiornati sul calendario della raccolta differenziata.

---

## BinGo! team: Gruppo EGSB
- **910243** - Invernizzi Gloria (Referente)
- **914621** - Locatelli Elisabetta
- **909535** - Maggioni Sara
- **914386** - Pomi Beatrice

---

## Funzionalità Principali

- **Scansione Codici a Barre**: Inquadra il codice a barre di un prodotto per scoprire 
immediatamente come smaltire correttamente ogni sua parte (plastica, carta, vetro, ecc.).
- **Calendario Raccolta**: Visualizza i giorni di ritiro dei rifiuti per la tua zona.
- **Notifiche Personalizzate**: Ricevi promemoria per non dimenticare di esporre i bidoni.
- **Gruppi Famiglia**: Crea o unisciti a un gruppo famiglia per condividere il calendario e la 
gestione dei rifiuti con i tuoi conviventi.
- **Gestione Profilo**: Personalizza il tuo profilo con foto, indirizzo e preferenze.
- **Preferiti**: Salva i prodotti scansionati più frequenti per una consultazione rapida.
- **Multi-lingua**: Supporto per Italiano, Inglese e Spagnolo.
- **Temi**: Possibilità di scegliere tra tema chiaro e tema scuro.

---

### Core & Architettura MVVM
- **Linguaggio**: Java
- **IDE**: Android Studio
- **Gestione Dipendenze**: Gradle
- **Controllo Versione**: Git
- **Architettura**: MVVM (Model-View-ViewModel) per una netta separazione tra logica di business e 
interfaccia utente.
- **Interfaccia Utente**: Sviluppata in **XML** utilizzando i componenti di **Material Design** e 
icone ufficiali.
- **Navigation Component**: Gestione fluida del flusso di navigazione tra i Fragment.

---

### Integrazioni & Librerie
- **Firebase**: 
  - **Authentication**: Gestione accessi via Email/Password e **Google Sign-In**.
  - **Firestore**: Database NoSQL per la sincronizzazione dei dati in tempo reale.
  - **Storage**: Archiviazione delle immagini del profilo degli utenti.
- **ML Kit & CameraX**: Per la scansione rapida e precisa dei codici a barre.
- **Glide**: Caricamento e caching efficiente delle immagini.
- **WorkManager**: Pianificazione delle notifiche di raccolta in background.
- **API Esterne**: Recupero dati sui prodotti tramite **Open Food Facts** e **Barcode Monster**.

---

## Layer Dati & Persistenza

L'app garantisce il funzionamento offline e la sincronizzazione cloud:
1. **Cache Locale**: Utilizza **Room Database** per memorizzare prodotti scansionati e impostazioni.
2. **Cloud Sync**: I dati del profilo e della famiglia sono sincronizzati su **Firebase Firestore**.
3. **Impostazioni**: Gestite tramite **SharedPreferences** (es. lingua, tema).

---

## Qualità del Software & Test

Il progetto è stato sviluppato seguendo standard di qualità elevati:
- **Test Unitari**: Test della logica di business nei ViewModel e nelle classi di utility.
- **Test Strumentali**: Test delle funzionalità core direttamente su dispositivi reali ed emulatori.
- **Compatibilità**: L'app è compatibile con **Android 8.0 (Oreo)** e versioni successive.

---

## Note per il Testing (Google Sign-In)
Per testare correttamente la funzionalità di **Google Sign-In** durante la revisione del codice:
- È necessario fornire la propria impronta digitale **SHA-1** del certificato di debug affinché 
venga registrata sulla console Firebase del progetto.
- In alternativa, è consigliabile utilizzare la versione **release** dell'applicazione (APK), dove 
l'autenticazione è già configurata e non richiede modifiche lato server.

 ---

*Progetto realizzato per il corso di Programmazione per Dispositivi Mobili.*
