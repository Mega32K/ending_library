package com.mega.endinglib.api.server;

import com.mega.endinglib.server.ServerTaskManager;
import com.mega.endinglib.util.java.Args;

public abstract class ServerTaskInstance {

    private boolean isRemoved;

    private final Args args;

    public ServerTaskInstance(Args args) {
        this.args = args;
    }
    public boolean isRemoved() {
        return this.isRemoved;
    }
    public void setRemoved(boolean flag) {
        if (!flag && isRemoved) {
            this.addToManager();
        }
        this.isRemoved = flag;
    }
    public abstract void update(Args args);
    public Args getArgs() {
        return this.args;
    }
    public void addToManager() {
        ServerTaskManager.toAdd.add(this);
    }
    public void onRemove() {}
}
