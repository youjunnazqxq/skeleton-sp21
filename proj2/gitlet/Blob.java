package gitlet;

import java.io.File;
import java.io.Serializable;
/* 这个类是Bolb类
* 生出传入：一个文件
* 保存目录.gitlet/object/blobs
* 用save方法保存
* */
public class Blob implements Serializable {
    public static final File OBJECTS_DIR = Utils.join(Repository.GITLET_DIR, "objects");
    public  static final File BLOBS_FOLDER=Utils.join(OBJECTS_DIR,"blobs");
    private byte[] content;
    private String blob_id;
    public Blob(File current_file){
        content=Utils.readContents(current_file);
        blob_id=Utils.sha1(content);
    }
    public String get_blob_id(){
        return blob_id;
    }
    public byte[] get_content(){
        return content;
    }
    public void save(){
        File save_blob=Utils.join(BLOBS_FOLDER,blob_id);
        Utils.writeObject(save_blob,this);
    }
    public static Blob load(String name){
        File Blob_file=Utils.join(BLOBS_FOLDER,name);
        return Utils.readObject(Blob_file,Blob.class);
    }
}
