# Progetto di Programmazione per Dispositivi Mobili, Università di Milano, Bicocca
Gloria Invernizzi - Matricola: 9...
Elisabetta Locatelli - Matricola: 914621
Sara Maggioni - Matricola: 9...
Beatrice Pomi - Matricola: 9...  

## BinGo, il tuo gesto verde in un click!
Applicazione Android progettata come supporto al corretto smaltimento dei rifiuti. 
L'app fornisce informazioni dettagliate su come differenziare i rifiuti e offre funzionalità 
interattive per rendere il processo di smaltimento 
più divertente e coinvolgente.    

## Caratteristiche principali
- **Guida alla differenziazione dei rifiuti**: Fornisce informazioni dettagliate su come 
  smaltire correttamente vari tipi di rifiuti, identificando i vari prodotti tramite codice a barre.
- **Funzionalità di gioco**: Include un sistema di punteggio e sfide per incentivare 
  gli utenti a smaltire correttamente i rifiuti. --> ELIMINATA??
- **Notifiche e promemoria**: Invia notifiche per ricordare agli utenti di smaltire i rifiuti 
  nei giorni previsti, impostati manualmente dall'utente nel calendario.
- **Interfaccia user-friendly**: Design intuitivo e facile da usare per tutte le età.
- **Supporto multilingue**: Disponibile in più lingue per raggiungere un pubblico più ampio.
- **Integrazione con servizi locali**: Collegamento con i servizi di raccolta rifiuti locali per 
  informazioni specifiche sulla raccolta. --> NON POSSIBILE :(
- **Modalità offline**: Accesso alle informazioni anche senza connessione internet?????
- **Feedback degli utenti**: Possibilità per gli utenti di fornire feedback e suggerimenti per 
  migliorare l'app --> NON IMPLEMENTATO PER ORA POSSIAMO PENSARE DI AGGIUNGERLA IN SEGUITO.
- **Accessibilità**: Progettata per essere accessibile a persone con disabilità --> DA VALUTARE 
- EVENTUALI SUPPORTI, PROBABILMENTE DA ELIMINARE. Es: integrazione con assistenti vocali.
- **Modalità famiglia**: Funzionalità per coinvolgere tutta la famiglia nel processo di 
  smaltimento dei rifiuti. POSSIBILITA' DI CREARE UN GRUPPO FAMILIA IN MODO CHE IL CALENDARIO SIA 
  CONDIVISO A TUTTI I MEMBRI.
- **Supporto per codici a barre**: Scansione dei codici a barre dei prodotti per ottenere 
- informazioni sul corretto smaltimento o inserimento manuale.
- **Personalizzazione**: Opzioni per personalizzare l'aspetto e le funzionalità
  dell'app in base alle preferenze dell'utente --> DA VALUTARE.
- **Integrazione con mappe**: Visualizzazione delle aree di raccolta rifiuti più vicine
  tramite mappe interattive (API Google Maps)--> DA VALUTARE.
- **Modalità notturna**: Tema scuro per un utilizzo più confortevole di notte.

## Installazione
Per installare l'app PDM-BinGo, segui questi passaggi:
1. Scarica il file APK dall'[link di download](#).
2. Abilita l'installazione da fonti sconosciute nelle impostazioni del tuo dispositivo Android.
4. Apri il file APK scaricato e segui le istruzioni per completare l'installazione.
5. Avvia l'app e inizia a utilizzare PDM-BinGo!

## Scelte implementative
- Linguaggio di programmazione: Java/(Kotlin)??
- Ambiente di sviluppo: Android Studio
- Database: SQLite/ROOM/Firebase
- Librerie: ZXing per la scansione dei codici a barre, Retrofit per le chiamate API, 
  Material Design per l'interfaccia utente??
- Architettura: MVVM/MVC??
- Testing: JUnit, Espresso ...?
- Strumenti di versionamento: Git/GitHub
- Gestione delle dipendenze: Gradle/Maven??

## Folder del progetto
- `app/src/main/java/com/pdm_bingo/`: Contiene il codice sorgente
- `app/src/main/res/`: Contiene le risorse dell'app (layout, immagini, stringhe, ecc.)
- `app/src/main/AndroidManifest.xml`: File di manifest dell'app
- `build.gradle`: File di configurazione di Gradle
- `README.md`: Documentazione del progetto    
- `LICENSE`: Licenza del progetto
- `.gitignore`: File per escludere file e cartelle dal versionamento Git
- `docs/`: Documentazione aggiuntiva del progetto
- `tests/`: Test unitari e di integrazione
- `scripts/`: Script utili per la gestione del progetto
- `assets/`: Risorse aggiuntive come font e file di configurazione
- `libs/`: Librerie esterne utilizzate nel progetto

## Architettura del progetto --> seguiamo questa logica?
L'architettura del progetto PDM-BinGo segue il pattern MVVM (Model-View-ViewModel) per
separare le responsabilità e facilitare la manutenzione del codice.
La separazione di logica di business, logica di presentazione e interfaccia utente permette di 
migliorare la testabilità e la scalabilità dell'applicazione.
Le principali componenti dell'architettura sono:
- **Model**: Gestisce i dati dell'applicazione, inclusi i modelli di dati e le operazioni di 
  accesso al database.
- **View**: Rappresenta l'interfaccia utente dell'applicazione, inclusi layout XML e componenti UI.
- **ViewModel**: Funziona come intermediario tra il Model e la View, gestendo la logica di 
  presentazione e l'interazione con l'utente.
- **Repository**: Gestisce le operazioni di accesso ai dati, fornendo un'interfaccia 
  unificata per il ViewModel.
- **Service**: Gestisce le operazioni di rete e le chiamate API per recuperare dati esterni.
- **Utilità**: Contiene classi di utilità e helper per operazioni comuni.
- **Testing**: Include test unitari e di integrazione per garantire la qualità del codice.
- **Build e Distribuzione**: Configurazione di Gradle per la compilazione e la distribuzione 
  dell'applicazione.
- **Documentazione**: Documentazione del codice e delle funzionalità dell'applicazione.





