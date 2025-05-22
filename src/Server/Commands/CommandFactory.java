
package Server.Commands;

import java.io.BufferedWriter;
import java.io.File;

public class CommandFactory {
    public static Command getCommand(String commandLine, File currentDir, BufferedWriter out, boolean isAdmin) {
        String[] parts = commandLine.split(" ");
        String command = parts[0];

        switch (command.toLowerCase()) {
            case "list":
                return new ListCommand(currentDir, out);
            case "cd":
                if (parts.length > 1) {
                    return new CdCommand(currentDir, out, parts[1]);
                }
                break;
            case "download":
                if (parts.length > 1) {
                    return new DownloadCommand(currentDir, out, parts[1]);
                }
                break;
            case "upload":
                if (isAdmin && parts.length > 1) {
                    return new UploadCommand(currentDir, out, parts[1]);
                }
                break;
            case "delete":
                if (isAdmin && parts.length > 1) {
                    return new DeleteCommand(currentDir, out, parts[1]);
                }
                break;
        }
        return null;
    }
}
