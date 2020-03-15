import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        WCUtil wcUtil = new WCUtil();
        Parameter parameter = new Parameter();

        if(args == null || args.length == 0){
            System.out.println("\n请输入指令");
            Scanner scanner = new Scanner(System.in);
            args = scanner.nextLine().split("\\s+");
        }

        if(!args[0].equals("wc.exe")){
            System.out.println("\n当前只可运行wc.exe");
            return ;
        }

        //最后一个路径为文件路径
        String lastParam = args[args.length - 1];
        if(!lastParam.equals("-x")){
            parameter.setFilePath(lastParam);
        }else{
            parameter.setFrame(true);
        }

        for (int i = 1; i < args.length - 1; i++) {
            switch (args[i]) {
                case "-c": //计算 file.c 的字符数
                    parameter.setCountChar(true);
                    break;
                case "-w": //计算 file.c 的单词数
                    parameter.setCountWord(true);
                    break;
                case "-l": //计算 file.c 的行数
                    parameter.setCountLine(true);
                    break;
                case "-s": //使用递归处理目录下符合条件的文件
                    parameter.setRecurrence(true);
                    break;
                case "-a": //返回更复杂的数据（代码行 / 空行 / 注释行）
                    parameter.setCountComplex(true);
                    break;
                case "-x": //调用图形化界面
                    parameter.setFrame(true);
                    break;
                default:
                    System.out.println("\n指令出错");
            }
        }
        if(parameter.isFrame()){
            Frame.main(args);
        }else{
            System.out.println(wcUtil.process(parameter));
        }
    }
}
