package fr.aerwyn81.oreregen.commands;

public class ORCommand {
    private final Cmd cmdClass;
    private final String command;
    private final String permission;
    private final boolean isPlayerCommand;
    private final String[] args;

    public ORCommand(Object command) {
        this.cmdClass = (Cmd) command;
        this.command = cmdClass.getClass().getAnnotation(ORAnnotations.class).command();
        this.permission = cmdClass.getClass().getAnnotation(ORAnnotations.class).permission();
        this.isPlayerCommand = cmdClass.getClass().getAnnotation(ORAnnotations.class).isPlayerCommand();
        this.args = cmdClass.getClass().getAnnotation(ORAnnotations.class).args();
    }

    public Cmd getCmdClass() {
        return cmdClass;
    }

    public String getCommand() {
        return command;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isPlayerCommand() { return isPlayerCommand; }

    public String[] getArgs() { return args; }
}
