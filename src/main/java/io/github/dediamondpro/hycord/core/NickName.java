package io.github.dediamondpro.hycord.core;

public class NickName {
    public String name;
    public String nick;
    public NickName(String name, String nick) {
        this.name = name;
        this.nick = nick;
    }

    @Override
    public String toString() {
        return name + "," + nick;
    }
}
