package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;
/*这个是Branch类，创建他的时候需要传入创建的名字，以及他所指向的commit的哈希值，用save方法可以将他保存在/gitlet/branches/中，
load可以取出*/
public class Branch implements Serializable {
    private String name;
    private String headCommitHash;
    public static final File BRANCHES_DIR = join(Repository.GITLET_DIR, "branches");
    public Branch (String name,String headCommitHash){
        this.headCommitHash=headCommitHash;
        this.name=name;
    }
    public String getName(){
        return this.name;
    }
    public String getHeadCommitHash(){
        return this.headCommitHash;
    }
    //改变当前分支指向的commit
    public void changeCommit(String Hash){
        headCommitHash=Hash;
    }
    public void save(){
        File saveBranch=Utils.join(BRANCHES_DIR,this.name);
        Utils.writeObject(saveBranch,this);
    }
    public static Branch load(String name){
        File saveBranch=Utils.join(BRANCHES_DIR,name);
        return Utils.readObject(saveBranch,Branch.class);
    }
}
