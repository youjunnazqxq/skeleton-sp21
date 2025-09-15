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
        this.headCommitHash = headCommitHash;
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public String getHeadCommitHash(){
        return this.headCommitHash;
    }
    //改变当前分支指向的commit
    public void changeCommit(String Hash){
        headCommitHash = Hash;
    }
    public void save() {
        File saveBranch = Utils.join(BRANCHES_DIR, this.name);
        // 关键修复：创建父目录（支持多级目录，如R1）
        File parentDir = saveBranch.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // mkdirs() 会创建所有不存在的父目录
        }
        Utils.writeObject(saveBranch, this);
    }
    public static Branch load(String name){
        File saveBranch = Utils.join(BRANCHES_DIR,name);
        return Utils.readObject(saveBranch,Branch.class);
    }
    public static Branch loadFrom(File repoDir, String branchName) {
        File branchFile = Utils.join(repoDir, ".gitlet", "branches", branchName);
        if (!branchFile.exists()) {
            return null;
        }
        return Utils.readObject(branchFile, Branch.class);
    }
}
