# Middle-earth Battle

**Middle-earth Battle** is a JavaFX desktop application developed as a project for the course **Programiranje u Javi 2**.  
The application simulates a turn-based battle between two factions, **Gondor** and **Mordor**, using a custom game engine, JavaFX GUI, XML configuration, serialization, TCP sockets, RMI/JNDI communication and Reflection API documentation generation.

## Features

- Turn-based battle simulation between two players
- JavaFX graphical user interface created with FXML
- MVC-style project structure with controllers, models, services and resources
- Custom game engine for attack logic, unit selection and battle state management
- Unit hierarchy with multiple unit types:
  - Commander
  - Infantry
  - Ranged
  - Cavalry
  - Monster
- JavaFX properties and observable lists for automatic UI updates
- Asynchronous game loading and background processing
- TCP socket communication for sending battle moves
- RMI and JNDI implementation for lobby registration and in-game chat
- Game save/load system using Java serialization
- XML configuration loading with SAX
- XML configuration writing with DOM
- Source code documentation generation using Reflection API
- Background music and image resources

## Technologies Used

- Java 21
- JavaFX 21
- Maven
- FXML
- CSS
- TCP sockets
- RMI
- JNDI
- SAX
- DOM
- Java Serialization
- Reflection API
- Multithreading
- CompletableFuture
- ExecutorService

## Project Structure

```text
src/main/java/com/marija/middleearthbattle
├── async
├── audio
├── config
├── documentation
├── engine
├── model
├── move
├── network
├── rmi
├── save
└── ui
```

## How to Run

Clone the repository:

```bash
git clone https://github.com/j4cksparr0w/middle-earth-battle.git
cd middle-earth-battle
```

Run the application with Maven Wrapper:

```bash
./mvnw clean javafx:run
```

On Windows:

```bash
mvnw.cmd clean javafx:run
```

## Network Demo

By default, the application uses `localhost` for local testing.

For a two-device demo, start the client with:

```bash
./mvnw javafx:run -Dmiddleearth.remote.host=HOST_IP_ADDRESS
```

On Windows:

```bash
mvnw.cmd javafx:run -Dmiddleearth.remote.host=HOST_IP_ADDRESS
```

## Recommended Demo Flow

1. Start the application.
2. Click **Host Game** to open the Gondor player window.
3. Click **Join Game** to open the Mordor player window.
4. Send a chat message to demonstrate RMI/JNDI communication.
5. Play several attacks to demonstrate TCP move sending.
6. Save the game.
7. Load the saved game.
8. Generate documentation to demonstrate Reflection API usage.
9. Open or edit the XML configuration to demonstrate SAX/DOM functionality.

## Configuration

The game configuration is stored in:

```text
config/game-config.xml
```

This file contains unit data and can be loaded dynamically by the application.

## Save Files

Saved games are stored in the local `saves` directory when the application is running.  
The `saves` directory is ignored by Git because save files are generated locally.

## Author

**j4cksparr0w**

## License

This project was created for educational purposes.
