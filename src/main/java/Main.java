
public class Main {

    public static void main(String[] args) {

        WCUtil wcUtil = new WCUtil();
        Parameter parameter = new Parameter();

        if(args == null || args.length == 0){
            System.out.println("\n请输入指令");
            return ;
        }

        //最后一个路径为文件路径
        parameter.setFilePath(args[args.length - 1]);

        for (int i = 0; i < args.length - 1; i++) {
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
            if(parameter.isFrame()){
                Frame.main(args);
            }else{
                System.out.println(wcUtil.process(parameter));
            }
        }
    }
}
