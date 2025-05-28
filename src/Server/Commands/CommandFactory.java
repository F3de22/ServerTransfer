package Server.Commands;

import Server.ClientHandler;

import java.io.File;

public class CommandFactory {
    public static Command getCommand(String commandLine, File currentDir, boolean isAdmin) {
        String[] parts = commandLine.split(" ");
        String command = parts[0];

        switch (command.toLowerCase()) {
            case "list":
                return new ListCommand(currentDir);
            case "cd":
                if (parts.length > 1) {
                    return new CdCommand(currentDir, parts[1]);
                }
                break;
            case "download":
                if (parts.length > 1) {
                    return new DownloadCommand(currentDir, parts[1]);
                }
                break;
            case "upload":
                if (isAdmin && parts.length > 1) {
                    return new UploadCommand(currentDir, parts[1]);
                }
                break;
            case "delete":
                if (isAdmin && parts.length > 1) {
                    return new DeleteCommand(currentDir, parts[1]);
                }
                break;
            case "exit":
                break;
        }
        return null;
    }
}
