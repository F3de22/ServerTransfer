package Server.Commands;

import Server.ClientHandler;

import java.io.File;

public class ListCommand implements Command {
    public void execute(ClientHandler handler, String[] args) {
        File dir = handler.getCurrentDir();
        File[] files = dir.listFiles();
        if (files != null) {
            StringBuilder sb = new StringBuilder();
            for (File file : files) {
                sb.append(file.getName());
                if (file.isDirectory()) {
                    sb.append(" [DIR]");
                }
                sb.append("\n");
            }
            handler.sendMessage(sb.toString());
        } else {
            handler.sendMessage("Impossibile accedere alla directory.");
        }
    }
}
