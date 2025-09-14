package gitlet;
import java.io.File;
import java.io.Serializable;
import static gitlet.Repository.GITLET_DIR;
public class Head implements Serializable {
    private String currentBranch;
    public static final File HEAD_FILE = Utils.join(GITLET_DIR, "HEAD");
    public Head(String initialBranchName){
        this.currentBranch=initialBranchName;
    }
    public String getCurrentBranch(){
        return currentBranch;
    }
    public void changeCurrentBranch(String currentName){
        this.currentBranch=currentName;
    }
    public void save(){
        Utils.writeObject(HEAD_FILE,this);
    }
    public static  Head load(){
        return Utils.readObject(HEAD_FILE,Head.class);
    }
}
