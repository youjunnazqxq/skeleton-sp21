package gitlet;

import jdk.jshell.execution.Util;

import java.io.File;
import java.io.Serializable;
import java.security.AllPermission;
import java.util.*;

public class Stage implements Serializable {
    private Map<String,String> stageForAdd;
    private Set<String> stageForRemove;
    private static final File STAGES_DIR =Utils.join(Repository.GITLET_DIR,"Stages");
    public Stage(){
        this.stageForAdd=new TreeMap<>();
        this.stageForRemove=new HashSet<>();
    }
    //添加暂存区的文件并将这个文件移除删除区
    public void add(String fileName,String blobHash){
        stageForAdd.put(fileName,blobHash);
        stageForRemove.remove(fileName);
    }
    //添加一个待删区的文件
    public void rm(String removeFile){
        stageForRemove.add(removeFile);
    }
    //commit命令的清除p操作
    public void clear(){
        stageForAdd.clear();;
        stageForRemove.clear();
    }
    public void save(){
        File SaveStage=Utils.join(STAGES_DIR,"stage");
        Utils.writeObject(SaveStage,this);
    }
    public static Stage load(){
        File stageFile= Utils.join(STAGES_DIR,"stage");
        if(stageFile.exists()){
            return Utils.readObject(stageFile,Stage.class);
        }else{
            return new Stage();
        }
    }
    //判断文件是否在暂存区域
    public boolean isExistInAdd(String fileName){
        return stageForAdd.containsKey(fileName);
    }
    //判断文件是否在待删区
    public boolean isExistInRemove(String fileName){
        return stageForRemove.contains(fileName);
    }
    //从待删区取出
    public void takeOutRemove(String fileName){
        stageForRemove.remove(fileName);
    }
    //从暂存区移除
    public void takeOutAdd(String fileName){
        stageForAdd.remove(fileName);
    }
    //获取暂存区和删除区
    public Map<String,String>getStageForAdd(){
        return this.stageForAdd;
    }
    public Set<String> getStageForRemove(){return this.stageForRemove;}
    //打印暂存区和删除区的文件名字
    public void print(){
        System.out.println("=== Staged Files ===");
        for(Map.Entry<String,String> entry :stageForAdd.entrySet()){
            System.out.println(entry.getKey());
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        List<String> allRemove =new ArrayList<>(stageForRemove);
        Collections.sort(allRemove);
        for(String tempRemove:allRemove){
            System.out.println(tempRemove);
        }
    }
    //返回是否和在暂存区的文件相同
    public boolean isEquel(String fileName,String hash){
        return stageForAdd.get(fileName).equals(hash);
    }

}
