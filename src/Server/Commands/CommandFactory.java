package Server.Commands;

public class CommandFactory {
    public static Command getCommand(String commandName) {
        switch (commandName.toLowerCase()) {
            case "list":
                return new ListCommand();
            case "cd":
                return new CdCommand();
            case "download":
                return new DownloadCommand();
            default:
                return null;
        }
    }
}
