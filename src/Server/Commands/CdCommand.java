package Server.Commands;

import Server.ClientHandler;

import java.io.File;

public class CdCommand implements Command {
    @Override
    public void execute(ClientHandler handler, String[] args) {
        if (args.length < 2) {
            handler.sendMessage("Utilizzo: cd <directory>");
            return;
        }
        String dirName = args[1];
        File current = handler.getCurrentDir();
        File newDir;
        if (dirName.equals("..")) {
            newDir = current.getParentFile();
            // Impedisce di uscire dalla cartella base
            if (newDir == null || !newDir.getAbsolutePath().startsWith(new File("server_files").getAbsolutePath())) {
                handler.sendMessage("Non puoi andare oltre la directory base.");
                return;
            }
        } else {
            newDir = new File(current, dirName);
            if (!newDir.exists() || !newDir.isDirectory()) {
                handler.sendMessage("Directory non trovata.");
                return;
            }
        }
        handler.setCurrentDir(newDir);
        handler.sendMessage("Directory corrente: " + newDir.getAbsolutePath());
    }
}
