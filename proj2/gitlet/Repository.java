package gitlet;
import javax.sql.rowset.spi.SyncResolver;
import java.io.File;
import java.io.IOException;
import java.io.SyncFailedException;
import java.nio.file.Files;
import java.util.*;
import java.nio.charset.StandardCharsets;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository  {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    Stage stage;
    Head  head;
    public Repository(){
        Utils.isGitletExist();
        stage = Stage.load();
        head = Head.load();
    }
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = new File( ".gitlet");
    // 对象存储
    public static final File OBJECTS_DIR = Utils.join(GITLET_DIR, "objects");
    public static final File COMMITS_DIR = Utils.join(OBJECTS_DIR, "commits");
    public static final File BLOBS_DIR = Utils.join(OBJECTS_DIR, "blobs");
    //分支和引用
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    // 暂存区
    public static final File STAGES_DIR = join(GITLET_DIR, "Stages");
    //远程仓库
    public static final File REMOTES_DIR=join(GITLET_DIR,"remotes");

    /*init：
    * 初始化仓库
    * 创建初始commit
    * 创建初始master分支
    * 创建head指针指向master
    * 保存*/
    public  static  void init(){
        if(GITLET_DIR.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }else{
            COMMITS_DIR.mkdirs();
            BLOBS_DIR.mkdirs();
            BRANCHES_DIR.mkdirs();
            STAGES_DIR.mkdirs();
            REMOTES_DIR.mkdirs();
            Commit inintalCommit = new Commit();
            Stage initialStage = new Stage();
            initialStage.save();//保存
            inintalCommit.save();
            Branch masterBranch = new Branch("master", inintalCommit.getCommitHashId());//创建主分支
            Head head = new Head(masterBranch.getName());//将head指针指向分支
            masterBranch.save();
            head.save();
        }
    }
    /*add：
    * 对于对应的文件进行blob来找到其哈希值
    * 通过head->branch->commit来找到其中存储的哈希值
    * 如果文件在待删区中将他从待删区中移除
    * 如果两个一样那么将他从暂存区删除
    * 如果不一样那么将他加入进暂存区域（这一步add已经将他从删除区中移除）
    * 保存*/
    public  void add(String fileName){
        File fileToAdd = Utils.findFile(fileName);//找到该文件
        if(!fileToAdd.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }else{
            Branch currentBranch = Branch.load(head.getCurrentBranch());//获取当前分支
            Commit currentCommit = Commit.load(currentBranch.getHeadCommitHash());//获取分支指向的类
            Blob currentBlob = new Blob(fileToAdd);
            String currentBlobHash = currentBlob.get_blob_id();
            stage.takeOutRemove(fileName);//从待删区中移除
            //判读哈希值是否相等
            if(!currentCommit.isEqual(fileName,currentBlobHash)){
                stage.add(fileName,currentBlobHash);
                stage.save();
                currentBlob.save();
            }else{
                stage.takeOutAdd(fileName);//移除待存区
                stage.save();
            }
        }
    }
   /*rm
   * 检查是否有后缀
   * 检查检查仓库是否存在
   * 通过head-branch-commit来找到目前的commit
   * 如果即没有被暂存有没有被跟踪就打印错误消息
   * 将他从文件的暂存区中移除
   * 如果commit已经追踪那么将加入删除区并删除工作目录的那个文件
   */
    public  void rm(String fileName){
        File removeFile = Utils.findFile(fileName);
        Branch currentBranch = Branch.load(head.getCurrentBranch());
        Commit currentCommit = Commit.load(currentBranch.getHeadCommitHash());
        if(!stage.isExistInAdd(fileName)&&!currentCommit.isTracked(fileName)){
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if(stage.isExistInAdd(fileName)){
            stage.takeOutAdd(fileName);
        }
        if(currentCommit.isTracked(fileName)){
            stage.rm(fileName);
            Utils.restrictedDelete(removeFile);
        }
        stage.save();
    }
    /*commit
     *首先检查 信息是否有 暂存区和删除区是否有东西
     * 创建一个新的commit 继承父代的blob信息
     * 从stageAdd中检查与父代的信息，从stageRemove中检查与父代的信息
     * 将分支更新指向这个commit
     */
    public  void commit(String message){
        if(stage.getStageForAdd().isEmpty()&&stage.getStageForRemove().isEmpty()){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Branch currentBranch = Branch.load(head.getCurrentBranch());
        Commit parentCommit = Commit.load(currentBranch.getHeadCommitHash());
        Commit currentCommit = new Commit(message,parentCommit.getCommitHashId());
        currentBranch.changeCommit(currentCommit.getCommitHashId());
        head.save();
        stage.clear();
        currentCommit.save();
        currentBranch.save();
        stage.save();
    }
    /*log
    *利用递归沿着第一个父节点来打印commit节点信息直到为null
    */
    public void log(){
        Branch currentBranch = Branch.load(head.getCurrentBranch());
        Commit currentCommit = Commit.load(currentBranch.getHeadCommitHash());
        logHelper(currentCommit.getCommitHashId());
    }
    private void logHelper(String currentCommitHash){
        if(currentCommitHash == null){
            return;
        }
        Commit currentCommit=Commit.load(currentCommitHash);
        currentCommit.printCommit();
        logHelper(currentCommit.getFirstParentHash());
    }
    /*global-log*/
    public void globalLog(){
        List<String> allCommitHash = Utils.plainFilenamesIn(COMMITS_DIR);
        for(String currentCommitHash:allCommitHash){
            Commit tempCommit = Commit.load(currentCommitHash);
            tempCommit.printCommit();
        }
    }
    /*find*/
    public void find(String message){
        List<String> allCommitHash = Utils.plainFilenamesIn(COMMITS_DIR);
        boolean findIs = false;
        for(String currentCommitHash:allCommitHash){
            Commit currentCommit = Commit.load(currentCommitHash);
            if(currentCommit.getMessage().equals(message)){
                System.out.println(currentCommit.getCommitHashId());
                findIs = true;
            }
        }
        if(!findIs){
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }
    /*status*/
    public void status(){
        //打印分支的名字
        //获取当前主分支
        Branch currentBranch = Branch.load(head.getCurrentBranch());
        //获取branches目录下的所有分支
        List<String> allBranch = Utils.plainFilenamesIn(BRANCHES_DIR);
        System.out.println("=== Branches ===");
        Collections.sort(allBranch);
        for(String tempBranch:allBranch){
            if(currentBranch.getName().equals(tempBranch)){
                System.out.println("*"+tempBranch);
            }else{
                System.out.println(tempBranch);
            }
        }
        System.out.println();
        //打印暂存区和删除区
        stage.print();
        System.out.println();
        /*打印已修改但未暂存
        *1.在当前commit中被跟跟踪，但在工作区已经被修改但违背暂存 */
        System.out.println("=== Modifications Not Staged For Commit ===");
        Commit currentCommit = Commit.load(currentBranch.getHeadCommitHash());
        //获取当前工作区的文件
        List<String> inWorkFile = Utils.plainFilenamesIn(CWD);
        //收集被跟踪但没有暂存的文件
        Set<String> modiButNotCommit = new TreeSet<>();
        //遍历工作区的文件
        for(String workFileName:inWorkFile){
            File workFile=Utils.findFile(workFileName);
            //判断有没有被commit跟踪
            if(currentCommit.isTracked(workFileName)){
                //判断工作区的文件和commit跟踪的文件是否相同
                if(!currentCommit.isEqual(workFileName,Utils.getHash(workFile))){
                    if(!stage.isExistInAdd(workFileName)){
                        modiButNotCommit.add(workFileName+"(modified)");
                    }
                }
            }
        }
        /*在暂存区有但
        * 1 工作区的版本不一样
        * 2 在工作区中被删除了*/
        Map <String,String> fileInstage = stage.getStageForAdd();
        for(Map.Entry<String,String> entry:fileInstage.entrySet()){
            if(inWorkFile.contains(entry.getKey())){
                File currentFile = Utils.findFile(entry.getKey());
                if(!stage.isEquel(entry.getKey(),Utils.getHash(currentFile))){
                    modiButNotCommit.add(entry.getKey()+"(modified)");
                }
            }else{
                modiButNotCommit.add(entry.getKey()+"(deleted)");
            }
        }
        //未在删除区中，但在commit中被跟踪，其不在当前的工作目录中
        for(Map.Entry<String,String> entry:currentCommit.getBlob().entrySet()){
            if(!stage.isExistInRemove(entry.getKey())&&!inWorkFile.contains(entry.getKey())){
                modiButNotCommit.add(entry.getKey()+"(deleted)");
            }
        }
        for(String toPrint:modiButNotCommit){
            System.out.println(toPrint);
        }
        System.out.println();
        //获取未被跟踪的文件
        Set<String> unstrackedFile = new TreeSet<>();
        System.out.println("=== Untracked Files ===");
        for(String currentfileName:inWorkFile){
            if(!stage.isExistInAdd(currentfileName)){
                if(!currentCommit.isTracked(currentfileName)){
                    unstrackedFile.add(currentfileName);
                }
            }
        }
        for(String toPrint:unstrackedFile){
            System.out.println(toPrint);
        }
        System.out.println();
    }
    /*branch*/
    public void branch(String newBranchName){
        File newBranchFile = Utils.join(BRANCHES_DIR,newBranchName);
        if(newBranchFile.exists()){
            System.out.println("A branch with that name already exists.");
        }else{
            Branch currentBranch = Branch.load(head.getCurrentBranch());
            Branch newBranch = new Branch(newBranchName, currentBranch.getHeadCommitHash());
            newBranch.save();
        }
    }
    /*rm-branch*/
    public void rmBranch(String removeBranchName){
        File removeBranch = Utils.join(BRANCHES_DIR,removeBranchName);
        Branch currentBranch = Branch.load(head.getCurrentBranch());
        if(!removeBranch.exists()){
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if(removeBranchName.equals(currentBranch.getName())){
            System.out.println("Cannot remove the current branch.");
            return ;
        }
        removeBranch.delete();
    }
    /*checkout*/
    /*第一种情况，获取当前的commit，将其放在工作目录中，如果已经存在则替代他*/
    public void checkFileName(String fileName){
        Branch currentBranch = Branch.load(head.getCurrentBranch());
        Commit currentCommit = Commit.load(currentBranch.getHeadCommitHash());
        //判断文件是否存在，如果存在就找到对应的哈希值
        String blobHash;
        if(currentCommit.isTracked(fileName)){
             blobHash = currentCommit.getBlobHash(fileName);
        }else{
            System.out.println("File does not exist in that commit.");
            return;
        }
        File inWorkFile = Utils.join(CWD,fileName);
        Blob currentBlob = Blob.load(blobHash);
        Utils.writeContents(inWorkFile,currentBlob.get_content());
    }
    /*第二种情况，获取指定commit版本中的文件，然后保存到当前的目录中*/
    public void checkCommitId(String shortId,String fileName){
        String commitId = Utils.findFullCommited(shortId);
        if(commitId==null){
            System.out.println("No commit with that id exists.");
            return ;
        }else{
            Commit currentCommit = Commit.load(commitId);
            if(currentCommit.isTracked(fileName)){
                Blob currentFileBlob = Blob.load(currentCommit.getBlobHash(fileName));
                File inWorkFile = Utils.join(CWD,fileName);
                Utils.writeContents(inWorkFile,currentFileBlob.get_content());
            }else{
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
        }
    }
    /*第三种情况，传入的是分支*/
    public void checkBranchName(String branchName){
        if(!Utils.isBranchExist(branchName)){
            System.out.println("No such branch exists.");
            return ;
        }else{
            Branch currentBrach = Branch.load(head.getCurrentBranch());
            Branch targetBranch = Branch.load(branchName);
            Commit currentCommit = Commit.load(currentBrach.getHeadCommitHash());
            Commit targetCommit = Commit.load(targetBranch.getHeadCommitHash());
            if(currentBrach.getName().equals(branchName)){
                System.out.println("No need to checkout the current branch.");
            }else{
                /*获取当前工作目录的所有文件,先检测当前文件是否被跟踪（不在commit和stageAdd中）找到不在当前commit却在目标comit的文件来报错*/
                if(judgeNotInCurrentCommitButInTargerCommit(currentCommit,targetCommit)){
                    System.out.println("There is an untracked file in the way; delete it,or add and commit it first.");
                    return ;
                }
                //将目标commit加入到工作目录中
                targetCommit.putInWork();
                //删除在当前commit但不在目标commit的文件
                Utils.deleteFile(currentCommit,targetCommit);
                /*保存*/
                head.changeCurrentBranch(targetBranch.getName());
                head.save();
                stage.clear();
                stage.save();
            }

        }
    }
    //reset
    public void reset(String shortedHash){
        String targetCommitHash = Utils.findFullCommited(shortedHash);
        if(targetCommitHash == null){
            System.out.println("No commit with that id exists.");
            return ;
        }
        Branch currentBranch = Branch.load(head.getCurrentBranch());
        Commit currentCommit = Commit.load(currentBranch.getHeadCommitHash());
        Commit targetCommit = Commit.load(targetCommitHash);
        if(Utils.judgeNotInCurrentCommitButInTargerCommit(currentCommit,targetCommit)){
            System.out.println("There is an untracked file in the way; delete it,or add and commit it first.");
            return ;
        }
        //将目标commit加入到工作目录中
        targetCommit.putInWork();
        //删除在当前commit但不在目标commit的文件
        Utils.deleteFile(currentCommit,targetCommit);
        /*保存*/
        currentBranch.changeCommit(targetCommitHash);
        currentBranch.save();
        stage.clear();
        stage.save();
    }
    /*merge
    * 首先找到分离commit，设置一个set将当前分支的父节点加入其中（DFS），然后遍历父代的指针，（BFS）找到最新的分离点
    * 然后将三个commit中的文件全部存起来
    * 对于每一个文件都分三种情况套路
    * 三三相同 过
    * 三三不同 冲突
    * 两两相同 则s与另外两个之一有相同点，则不相同的那个保留，s与两位两个都不同，保留当前current
    * */
    public  void merge(String targetBranchName){
        //检查是否有还未提交的文件
        if(!stage.getStageForRemove().isEmpty()||!stage.getStageForAdd().isEmpty()){
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        //检查分支存在？
        if(!Utils.isBranchExist(targetBranchName)){
            System.out.println("A branch with that name does not exist.");
            return ;
        }
        Branch currentBranch = Branch.load(head.getCurrentBranch());
        Branch  targetBranch = Branch.load(targetBranchName);
        Commit currentCommit = Commit.load(currentBranch.getHeadCommitHash());
        Commit targetCommit = Commit.load(targetBranch.getHeadCommitHash());
        String splitCommitHash = null;
        //检查是否与自身合并
        if(targetBranchName.equals(currentBranch.getName())){
            System.out.println("Cannot merge a branch with itself.");
            return ;
        }
        //检查是否有未跟踪的文件
        if(Utils.judgeNotInCurrentCommitButInTargerCommit(currentCommit,targetCommit)){
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
        //获取当前current中所有的父代
        Set<String> allCurrentParentCommitHash = new HashSet<>();
        findParentHelp(currentCommit.getCommitHashId(),allCurrentParentCommitHash);
        //用BFS遍历targetCommit
        splitCommitHash=findParentHelpInBfS(allCurrentParentCommitHash,targetCommit.getCommitHashId());
        Commit spiltCommmit = Commit.load(splitCommitHash);
        //最简单的两种情况 当前targetcommit与splitcommit相同打印退出   currentcommit与其相同用checkout回到target
        if(targetCommit.getCommitHashId().equals(splitCommitHash)){
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if(currentCommit.getCommitHashId().equals(splitCommitHash)){
            checkBranchName(targetBranchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        //制作一份包含三个commit所有的文件名字的hashSet，以便后来比较
        Set<String> allFileName = new HashSet<>();
        targetCommit.putAllInSet(allFileName);
        currentCommit.putAllInSet(allFileName);
        spiltCommmit.putAllInSet(allFileName);
        boolean flag = false;
        for(String fileName:allFileName){
            String spiltHash = spiltCommmit.getBlobHash(fileName);
            String currentHash = currentCommit.getBlobHash(fileName);
            String targetHash = targetCommit.getBlobHash(fileName);
            if(Objects.equals(currentHash,targetHash)){
                continue;
            }else if(Objects.equals(spiltHash,currentHash)){
                if(targetHash != null){
                    File changeFile = Utils.join(CWD,fileName);
                    Blob changeBlob = Blob.load(targetHash);
                    Utils.writeContents(changeFile,changeBlob.get_content());
                    stage.add(fileName,targetHash);
                }else{
                    Utils.restrictedDelete(fileName);
                    stage.rm(fileName);
                }
            }else if(Objects.equals(spiltHash,targetHash)){
                continue;
            }else{
                flag=true;
                String content = "<<<<<<< HEAD\n";
                String contentFromCurrent;
                String contentFromTarget;
                if(currentHash == null){
                    contentFromCurrent = "";
                }else{
                    Blob currentBlob=Blob.load(currentHash);
                    contentFromCurrent = new String(currentBlob.get_content(),StandardCharsets.UTF_8);
                }
                content += contentFromCurrent;
                content += "=======\n";
                if(targetHash == null){
                    contentFromTarget = "";
                }else{
                    Blob targetBlob = Blob.load(targetHash);
                    byte[] contentFromTargetintwo = targetBlob.get_content();
                    contentFromTarget = new String(contentFromTargetintwo,StandardCharsets.UTF_8);
                }
                content += contentFromTarget;
                content += ">>>>>>>\n";
                File currentFile = Utils.join(CWD,fileName);
                Utils.writeContents(currentFile,content);
                add(fileName);
            }
            }
        stage.save();
        String message = "Merged "+targetBranch.getName()+" into "+currentBranch.getName()+".";
        Commit mergeCommit = new Commit(message,currentCommit.getCommitHashId(), targetCommit.getCommitHashId());
        currentBranch.changeCommit(mergeCommit.getCommitHashId());
        currentBranch.save();
        mergeCommit.save();
        stage.clear();
        stage.save();
        if(flag){
            System.out.println("Encountered a merge conflict.");
        }
    }
    //用BFS找到父代
    private String  findParentHelpInBfS(Set<String> allCommitHashInRemote,String currentHashID){
        Set<String> visetedHash = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(currentHashID);
        while(!queue.isEmpty()){
            String currentCommitHashFromQueue = queue.poll();
            if(allCommitHashInRemote.contains(currentCommitHashFromQueue)){
                return  currentCommitHashFromQueue;
            }
            if(visetedHash.contains(currentCommitHashFromQueue)){
                continue;
            }
            visetedHash.add(currentCommitHashFromQueue);
            Commit currentCommitFromQueue=Commit.load(currentCommitHashFromQueue);
            String firstParentCommitHash=currentCommitFromQueue.getFirstParentHash();
            if(firstParentCommitHash!=null){
                queue.add(firstParentCommitHash);
            }
            if(currentCommitFromQueue.isMergeCommit()){
                queue.add(currentCommitFromQueue.getSecondParentHash());
            }
        }
        return null;
    }
    //用DFS获取所有的父代
    private void findParentHelp(String currentCommitHash,Set<String> allCurrentParentCommitHash){
        if(currentCommitHash == null){
            return ;
        }
        if(allCurrentParentCommitHash.contains(currentCommitHash)){
            return;
        }
        allCurrentParentCommitHash.add(currentCommitHash);
        Commit currentCommit = Commit.load(currentCommitHash);
        findParentHelp(currentCommit.getFirstParentHash(),allCurrentParentCommitHash);
        if(currentCommit.isMergeCommit()){
            findParentHelp(currentCommit.getSecondParentHash(),allCurrentParentCommitHash);
        }

    }
    //add_remote
    public void addRemote(String name,String path){
        if(Utils.isRemoteExist(name)){
            System.out.println("A remote with that name already exists.");
            return;
        }
        String correntedPath = path.replace("/",File.separator);
        Remote newRemote=new Remote(name,correntedPath);
        newRemote.save();
    }
    //rm_remote
    public void rmRemote(String name){
        File remoteFile =Utils.join(REMOTES_DIR,name);
        if(!Utils.isRemoteExist(name)){
            System.out.println("A remote with that name does not exist.");
            return ;
        }else{
            File file =Utils.join(REMOTES_DIR,name);
            if(!remoteFile.delete()){
                System.out.println("Error: Failed to remove the remote configuration.");
            }
        }
    }
    public void fetch(String name, String branchName) {
        if (!Utils.isRemoteExist(name)) {
            System.out.println("Remote directory not found.");
            return;
        }
        Remote remote = Remote.load(name);

        // [FIXED] 正确解析远程仓库路径
        File remoteGitletDir = new File(remote.getPath());
        File remoteRepoDir = remoteGitletDir.getParentFile();

        if (!remoteGitletDir.isDirectory()) {
            System.out.println("Remote directory not found.");
            return;
        }

        Branch remoteBranch = Branch.loadFrom(remoteRepoDir, branchName);
        if (remoteBranch == null) {
            System.out.println("That remote does not have that branch.");
            return;
        }
        String remoteHeadCommitHash = remoteBranch.getHeadCommitHash();

        Set<String> remoteCommitHashes = new HashSet<>();
        findParentHelp(remoteHeadCommitHash, remoteCommitHashes, remoteRepoDir);

        for (String commitHash : remoteCommitHashes) {
            File localCommitFile = Utils.join(COMMITS_DIR, commitHash);
            if (localCommitFile.exists()) {
                continue;
            }

            File remoteCommitFile = Utils.join(remoteGitletDir, "objects", "commits", commitHash);
            try {
                Files.copy(remoteCommitFile.toPath(), localCommitFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            Commit copiedCommit = Commit.load(commitHash);
            for (String blobHash : copiedCommit.getBlob().values()) {
                File localBlobFile = Utils.join(BLOBS_DIR, blobHash);
                if (localBlobFile.exists()) {
                    continue;
                }
                File remoteBlobFile = Utils.join(remoteGitletDir, "objects", "blobs", blobHash);
                try {
                    Files.copy(remoteBlobFile.toPath(), localBlobFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        String localTrackingBranchName = name + "/" + branchName;
        Branch localTrackingBranch;
        if (isBranchExist(localTrackingBranchName)) {
            localTrackingBranch = Branch.load(localTrackingBranchName);
            localTrackingBranch.changeCommit(remoteHeadCommitHash);
        } else {
            localTrackingBranch = new Branch(localTrackingBranchName, remoteHeadCommitHash);
        }
        localTrackingBranch.save();
    }

    public void push(String remoteName, String targetBranchName) {
        if (!Utils.isRemoteExist(remoteName)) {
            System.out.println("Remote directory not found.");
            return;
        }
        Remote remote = Remote.load(remoteName);

        // [FIXED] 正确解析远程仓库路径
        File remoteGitletDir = new File(remote.getPath());
        File remoteRepoDir = remoteGitletDir.getParentFile();

        if (!remoteGitletDir.isDirectory()) {
            System.out.println("Remote directory not found.");
            return;
        }

        Branch localBranch = Branch.load(head.getCurrentBranch());
        String localHeadHash = localBranch.getHeadCommitHash();

        Branch remoteBranch = Branch.loadFrom(remoteRepoDir, targetBranchName);
        String remoteHeadHash = (remoteBranch != null) ? remoteBranch.getHeadCommitHash() : null;

        Set<String> localHistoryHashes = new HashSet<>();
        findParentHelp(localHeadHash, localHistoryHashes);

        if (remoteHeadHash != null && !localHistoryHashes.contains(remoteHeadHash)) {
            System.out.println("Please pull down remote changes before pushing.");
            return;
        }

        Set<String> commitsToPush = new HashSet<>();
        for (String commitHash : localHistoryHashes) {
            File remoteCommitFile = Utils.join(remoteGitletDir, "objects", "commits", commitHash);
            if (!remoteCommitFile.exists()) {
                commitsToPush.add(commitHash);
            }
        }

        for (String commitHash : commitsToPush) {
            File localCommitFile = Utils.join(COMMITS_DIR, commitHash);
            File remoteCommitFile = Utils.join(remoteGitletDir, "objects", "commits", commitHash);
            try {
                remoteCommitFile.getParentFile().mkdirs();
                Files.copy(localCommitFile.toPath(), remoteCommitFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            Commit commitToCopy = Commit.load(commitHash);
            for (String blobHash : commitToCopy.getBlob().values()) {
                File localBlobFile = Utils.join(BLOBS_DIR, blobHash);
                File remoteBlobFile = Utils.join(remoteGitletDir, "objects", "blobs", blobHash);
                if (!remoteBlobFile.exists()) {
                    try {
                        remoteBlobFile.getParentFile().mkdirs();
                        Files.copy(localBlobFile.toPath(), remoteBlobFile.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }

        Branch newOrUpdatedRemoteBranch = new Branch(targetBranchName, localHeadHash);
        File remoteBranchFile = Utils.join(remoteGitletDir, "branches", targetBranchName);
        Utils.writeObject(remoteBranchFile, newOrUpdatedRemoteBranch);
    }

    public void pull(String remoteName, String remoteBranchName) {
        String localTrackingBranchName = remoteName + "/" + remoteBranchName;
        fetch(remoteName, remoteBranchName);
        merge(localTrackingBranchName);
    }

    private void findParentHelp(String currentCommitHash, Set<String> allParentCommitHashes, File repoDir) {
        if (currentCommitHash == null || allParentCommitHashes.contains(currentCommitHash)) {
            return;
        }
        allParentCommitHashes.add(currentCommitHash);
        Commit currentCommit = Commit.loadFrom(repoDir, currentCommitHash);
        if (currentCommit == null) {
            return;
        }
        findParentHelp(currentCommit.getFirstParentHash(), allParentCommitHashes, repoDir);
        if (currentCommit.isMergeCommit()) {
            findParentHelp(currentCommit.getSecondParentHash(), allParentCommitHashes, repoDir);
        }
    }
    }

