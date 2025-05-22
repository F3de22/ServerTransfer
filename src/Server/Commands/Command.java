package Server.Commands;

import java.io.File;

import Server.ClientHandler;

public interface Command {
    File execute(ClientHandler handler, String[] args);
}
