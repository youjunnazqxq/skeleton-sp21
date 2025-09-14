package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.join;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** The message of this Commit. */
    private String message;
    private Date time;
    private String firstParentHash;
    private String secondParentHash;
    private Map<String,String> blobHash ;
    private String commitId;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File OBJECTS_DIR = Utils.join(GITLET_DIR, "objects");
    public static final File COMMITS_DIR = Utils.join(OBJECTS_DIR, "commits");
    public static final File STAGES_DIR = join(GITLET_DIR, "Stages");
    //用来构建第一个commit提交
    public Commit(){
        this.message="initial commit";
        this.time=new Date(0);
        this.firstParentHash=null;
        this.blobHash=new TreeMap<>();
        this.commitId=getCommitHash();
    }
    //用来构建常态的提交方式
    public Commit (String message,String firstParentHash) {
        this.message = message;
        this.firstParentHash = firstParentHash;
        this.blobHash = new TreeMap<>();
        this.time = new Date();
        //继承父代的blob哈希表
        if (firstParentHash != null) {
            Commit firstParentCommit = load(firstParentHash);
            Map<String, String> firstParentBlob = firstParentCommit.blobHash;
            if (firstParentBlob != null) {
                this.blobHash.putAll(firstParentBlob);
            }
        }
        this.secondParentHash=null;//TODO
        Stage stage = Stage.load();
        //从暂存区添加文件
        for(Map.Entry<String,String> entry:stage.getStageForAdd().entrySet()){
            this.blobHash.put(entry.getKey(),entry.getValue());
        }
        //从删除区删除文件
        for(String removeFile:stage.getStageForRemove()){
            this.blobHash.remove(removeFile);
        }
        this.commitId=getCommitHash();
    }
    //用于构建合并commit
    public Commit(String message,String firstParentHash,String secondParentHash){
        this.message = message;
        this.firstParentHash = firstParentHash;
        this.secondParentHash=secondParentHash;
        this.blobHash = new TreeMap<>();
        this.time = new Date();
        if (firstParentHash != null) {
            Commit firstParentCommit = load(firstParentHash);
            Map<String, String> firstParentBlob = firstParentCommit.getBlob();
            if (firstParentBlob != null) {
                this.blobHash.putAll(firstParentBlob);
            }
        }
        Stage stage = Stage.load();
        //从暂存区添加文件
        for(Map.Entry<String,String> entry:stage.getStageForAdd().entrySet()){
            this.blobHash.put(entry.getKey(),entry.getValue());
        }
        //从删除区删除文件
        for(String removeFile:stage.getStageForRemove()){
            this.blobHash.remove(removeFile);
        }
        this.commitId=getCommitHash();
    }


    //判断文件是否发生变化
    public boolean is_equal(String fileName,String blobHash){
        String tempHash=this.blobHash.get(fileName);
        if(tempHash==null){
            return false;
        }
        return tempHash.equals(blobHash);
    }
    //判断文件是否被跟踪(是否保存在blob哈希表里)
    public boolean isTracked(String fileName){
        return blobHash.containsKey(fileName);
    }
    //获取commit的哈希值
    private String getCommitHash(){
        List<Object> contentsToHash= new ArrayList<>();
        contentsToHash.add(this.message);
        contentsToHash.add(this.time.toString());
        if(this.firstParentHash!=null){
            contentsToHash.add(firstParentHash);
        }
        if(this.secondParentHash!=null){
            contentsToHash.add(secondParentHash);
        }
        contentsToHash.add(blobHash.toString());
        return Utils.sha1(contentsToHash);
    }
    //从外界获取commit的哈希值
    public String getCommitHashId(){
        return this.commitId;
    }
    //序列化保存
    public void save(){
        File saveCommit=Utils.join(COMMITS_DIR,this.commitId);
        Utils.writeObject(saveCommit,this);
    }
    //反序列话读取
    public static Commit load(String commitHash){
        File saveCommit=Utils.join(COMMITS_DIR,commitHash);
        if(!saveCommit.exists()){
            throw new IllegalArgumentException("No commit with that id exist");
        }
        return Utils.readObject(saveCommit,Commit.class);
    }
    //获取存储的整个blob哈希值
    public Map<String,String> getBlob(){
        return blobHash;
    }
    //打印commit的信息
    public void printCommit(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        String formattedDate = sdf.format(this.time);
        System.out.println("===");
        System.out.println("commit "+this.commitId);
        if(this.secondParentHash!=null){

        }
        System.out.println("Date: "+formattedDate);
        System.out.println(this.message);
        System.out.println();
    }
    //获取第一个父代的哈希值
    public String getFirstParentHash(){
        return this.firstParentHash;
    }
    //获取第二个父代的节点
    public String getSecondParentHash(){
        return this.secondParentHash;
    }
    //获取接受的信息
    public String getMessage(){
        return this.message;
    }
    //根据传入的FileName获取blobHash中存储的文件
    public String getBlobHash(String fileName){
        return blobHash.get(fileName);
    }
    //将所存在commit中的blob放在工作目录中
    public void putInWork(){
        for(Map.Entry<String,String> entry:this.blobHash.entrySet()){
            String curretrnBlobHash=entry.getValue();
            Blob currentBlob=Blob.load(curretrnBlobHash);
            File addWorkFile=Utils.join(CWD,entry.getKey());
            Utils.writeContents(addWorkFile,currentBlob.get_content());
        }
    }
    //判断是否为合并节点
    public boolean isMergeCommit(){
        if(secondParentHash==null){
            return false;
        }else{
            return true;
        }
    }
    //获取所有的blob文件名字放入待办清单中
    public void putAllInSet(Set<String> allFileName){
        for(Map.Entry<String,String> entry:blobHash.entrySet()){
            allFileName.add(entry.getKey());
        }
    }

}
