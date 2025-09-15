package gitlet;

//import jdk.internal.jimage.ImageStrings;
import javax.management.loading.ClassLoaderRepository;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if(args.length==0){
            System.out.println("Please enter a command");
            return ;
        }
        String firstArg = args[0];
        if(args[0].equals("init")){
            Repository.init();
            return ;
        }
        Repository repository=new Repository();
        switch(firstArg) {
            case "add":
                // TODO: handle the `add [filename]` command
                if(args.length<2){
                  Utils.printErrorNoExist();
                }else{
                    repository.add(args[1]);
                }
                break;
            // TODO: FILL THE REST IN
            case "rm":
                if(args.length<2){
                    Utils.printErrorNoExist();
                }else{
                    repository.rm(args[1]);
                }
                break;
            case "commit":
                if(args.length<2||args[1].isEmpty()){
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }else{
                    repository.commit(args[1]);
                }
                break;
            case "log":
                repository.log();
                break;
            case "global-log":
                repository.globalLog();
                break;
            case "find":
                if(args.length<2){
                    Utils.printErrorNoExist();
                }else{
                    repository.find(args[1]);
                }
                break;
            case "status":
                repository.status();
                break;
            case "branch":
                if(args.length < 2 ){
                    Utils.printErrorNoExist();
                }else{
                    repository.branch(args[1]);
                }
                break;
            case "rm-branch":
                if(args.length < 2){
                    Utils.printErrorNoExist();
                }else{
                    repository.rmBranch(args[1]);
                }
                break;
            case "checkout":
                if (args.length == 3 && args[1].equals("--")) {
                    repository.checkFileName(args[2]);
                } else if (args.length == 4 && args[2].equals("--")) {
                    repository.checkCommitId(args[1], args[3]);
                } else if (args.length == 2) {
                    repository.checkBranchName(args[1]);
                } else {
                    Utils.printErrorNoExist();
                }
                break;
            case "reset":
                if(args.length<2){
                    Utils.printErrorNoExist();
                }else{
                    repository.reset(args[1]);
                }
                break;
            case "merge":
                if(args.length < 2){
                    Utils.printErrorNoExist();
                }else{
                    repository.merge(args[1]);
                }
                break;
            case "add-remote":
                if(args.length<3){
                    Utils.printErrorNoExist();
                }else{
                    repository.addRemote(args[1],args[2]);
                }
                break;
            case "rm-remote":
                if(args.length<2){
                    Utils.printErrorNoExist();
                }else{
                 repository.rmRemote(args[1]);
                }
                break;
            case "fetch":
                if(args.length<3){
                    Utils.printErrorNoExist();
                }else{
                    repository.fetch(args[1],args[2]);
                }
                break;
            case "push":
                if(args.length<3){
                    Utils.printErrorNoExist();
                }else{
                    repository.push(args[1],args[2]);
                }
                break;
            case "pull":
                if(args.length<3){
                    Utils.printErrorNoExist();;
                }else{
                    repository.pull(args[1],args[2]);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0); //
        }
    }
}
