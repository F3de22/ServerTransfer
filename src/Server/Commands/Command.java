package Server.Commands;

import Server.ClientHandler;

public interface Command {
    void execute(ClientHandler handler, String[] args);
}
