package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;

public class Remote implements Serializable {
    public static final File REMOTES_DIR=join(Repository.GITLET_DIR,"remotes");
    String name;
    String path;
    public Remote(String name, String path) {
        this.name = name;
        this.path = path;
    }
    //获取别名
    public String getName(){
        return this.name;
    }
    //获得仓库路径
    public String getPath(){return this.path;}
    public void save(){
        File remote=Utils.join(REMOTES_DIR,name);
        Utils.writeObject(remote,this);
    }
    public static Remote load(String otherName){
        File remote=Utils.join(REMOTES_DIR,otherName);
        return Utils.readObject(remote,Remote.class);
    }
}
