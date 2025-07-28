# Server Transfer - Java File Transfer System (Client-Server)
Project for UNIFI(University of Florence) Software Engineering exam
## Introduction
A **multithreaded Java client-server** application that allows users to navigate server directories, authenticate, and download/upload files. It supports concurrent clients, role-based permissions (User/Admin), and uses software design patterns such as **Singleton**, **Factory & Command**, and **Observer**.

## Features 
- 🔐 **User authentication** using a local `credentials.txt` file
- 📁 **Directory navigation** on the server side
- ⬇️ **Download files** from the server
- ⬆️ **Upload files** (Admins only)
- 🗑️ **Delete files** (Admins only)
- 👁️ **Observer Pattern** for Users actions logging
- 🧠 **Design Patterns Used**:
  - **Singleton** – for server instance
  - **Factory & Command** – to manage command creation
  - **Observer** – to log downloads
 
## 🛠️ How to Run

### ✅ Requirements
- Java 11 or above
- Maven required

### 🖥️ Start the Server

1. Open the project in IntelliJ
2. Run the `Server.java` class:
   - This starts listening on port `12345`
3. The server creates:
   - A `server_files` folder (if not present)
   - A `credentials.txt` file for user management

### 🧑‍💻 Start the Client

1. Run the `Client.java` class
2. When prompted:
   - Choose a download folder
   - Login or register
3. Use the command-line interface to:
   - Navigate folders (`cd`)
   - List files (`list`)
   - Download (`download <filename>`)
   - Upload / delete (if Admin)
