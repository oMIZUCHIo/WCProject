import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class WCUtil {

    /**
     * @description 对外调用接口
     * @param parameter 请求参数
     */
    String process(Parameter parameter){

        //对输入参数进行预处理，判断 参数是否合法，是否含有通配符
        String errormsg = judgeFilePath(parameter);
        if(errormsg != null)
            return errormsg;

        //各文件的查询结果集合
        List<Count> countList = new ArrayList<>();

        //查询文件相应信息
        searchFiles(parameter,countList);

        if(countList.size() == 0)
            return "\n未有符合条件的文件";

        StringBuilder stringBuffer = new StringBuilder();

        for(Count count : countList){

            stringBuffer.append("\n文件名：").append(count.getFileName());

            if(parameter.isCountChar()){
                stringBuffer.append("\n字符数：").append(count.getCharNum());
            }
            if(parameter.isCountWord()){
                stringBuffer.append("\n词数：").append(count.getWordNum());
            }
            if(parameter.isCountLine()){
                stringBuffer.append("\n行数：").append(count.getLineNum());
            }
            if(parameter.isCountComplex()){
                stringBuffer.append("\n代码行数：").append(count.getCodeLineNum());
                stringBuffer.append("\n空行数：").append(count.getEmptyLineNum());
                stringBuffer.append("\n注释行数：").append(count.getNoteLineNum());
            }

            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }

    /**
     * @description 查询文件
     * @param parameter 命令与文件路径信息封装
     */
    private void searchFiles(Parameter parameter, List<Count> countList){

        File file = new File(parameter.getFilePath());

        if (file.isFile() && file.exists()) { //为文件

            //当输入文件名不含通配符 或 含通配符且文件名匹配时才查询
            if(parameter.getMatchName() == null ||
                    (parameter.getMatchName() != null && compareName(file.getName(),parameter.getMatchName()))){

                //获取基础信息
                Count count = countingSimply(file.getPath());
                count.setFileName(file.getName());

                //当含有 -s 命令时额外添加 代码行数 等信息
                if(parameter.isCountComplex()){
                    toComplexCount(count,countingComplex(file.getPath()));
                }

                countList.add(count);
            }

        } else if (! file.exists()){

            System.out.println("\n文件不存在");

        } else if(file.isDirectory() && !parameter.isRecurrence()){

            if(parameter.getMatchName() == null){
                System.out.println("\n输入文件夹时，请加入-s命令进行递归查询");
            }else{
                System.out.println("\n输入文件含通配符时，请加入-s命令对目录进行递归查询");
            }

        }else if (file.isDirectory() && parameter.isRecurrence()) {   //为文件夹且需要递归时

            File[] files = file.listFiles();  //获取文件列表

            if(files != null && files.length != 0) {

                for (File f : files) {

                    if (f.isDirectory()) {   //为文件夹且需要递归

                        //递归查询子文件
                        parameter.setFilePath(f.getPath());
                        searchFiles(parameter,countList);

                    } else if (f.isFile()) {  //为文件

                        //当输入文件名不含通配符 或 含通配符且文件名匹配时才查询
                        if(parameter.getMatchName() == null ||
                                (parameter.getMatchName() != null && compareName(f.getName(),parameter.getMatchName()))) {

                            //获取基础信息
                            Count count = countingSimply(f.getPath());
                            count.setFileName(f.getName());

                            //当含有 -s 命令时额外添加 代码行数 等信息
                            if(parameter.isCountComplex()){
                                toComplexCount(count,countingComplex(f.getPath()));
                            }
                            countList.add(count);
                        }
                    }
                }
            }
        }
    }

    /**
     * @description 获取字符数，词数，行数信息
     * @param filePath 文件路径
     * @return Count 结果封装
     */
    private Count countingSimply(String filePath){

        int charNum = 0;
        int wordNum = 0;
        int lineNum = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            boolean flag = false;  //为false表示 一个单词的结束
            //IO逐行读取
            while ((line = reader.readLine()) != null) {

                ++ lineNum;

                //逐个获取字符
                for (int i = 0; i < line.length(); i++) {

                    char c = line.charAt(i);

                    //跳过空格类字符
                    if (c == ' ' || c == '\n' || c == '\t' || c == '\r') {
                       continue;
                    }
                    ++ charNum;

                    //如果字符为  空格、换行等 则为一个单词的结束
                    if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z')) {

                        flag = false;

                    //当flag为false时，表示上一个单词的结束，也代表新单词的开始
                    }else if (!flag) {

                        flag = true;
                        ++ wordNum;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("IO执行出错：" + e.getMessage());
        }
        return new Count(charNum,wordNum,lineNum);
    }

    /**
     * @description 查询代码行数，空行数，注释行数
     * @param filePath 文件路径
     */
    private Count countingComplex(String filePath){

        //单行注释               在单字符后的注释（多字符后的注释算做代码行，注释行的前提是 不是代码行）
        String singleLineNote01 = "(\\s*)([{};]?)(\\s*)(/{2})(.*)";
        /*单行注释*/             //在单字符后的注释（多字符后的注释算做代码行，注释行的前提是 不是代码行）
        String singleLineNote02 = "(\\s*)([{};]?)(\\s*)(/\\*)(.*)(\\*/)(\\s*)";
        //   多行注释开头   /*
        String muiltNoteStart = "(\\s*)(/\\*)(.*)";
        //   多行注释结尾   */
        String muiltNoteEnd = "(\\s*)(\\*/)(.*)";

        Count count = new Count();
        int codeLineNum = 0;
        int emptyLineNum = 0;
        int noteLineNum = 0;

        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            String line = null;

            while ((line = reader.readLine()) != null) {

                int notBlankNum = 0; // 判断非空格类字符数量

                //如果当行匹配注释行，则注释行加一并跳过后面操作，防止影响代码行和空行的计算
                if (line.matches(singleLineNote01) || line.matches(singleLineNote02)) {
                    // 单行注释统计
                    ++ noteLineNum;
                    continue;
                }
                // 多行注释统计
                if (line.matches(muiltNoteStart)) {
                    //第一行  /*  算做注释行
                    while (!line.matches(muiltNoteEnd)){
                        ++ noteLineNum;
                        line = reader.readLine();
                    }
                    //最后一行   */  也算做注释行
                    ++ noteLineNum;
                    continue;
                }
                //逐个获取字符，此时已确定此行不是注释行，再根据字符数量判断是否为代码行或空行
                for (int i = 0; i < line.length(); i++) {

                    char c = line.charAt(i);
                    ++ notBlankNum;

                    if (c == ' ' || c == '\n' || c == '\t' || c == '\r') {
                        -- notBlankNum;  //减去多计算的非空格类数
                    }
                }
                if(notBlankNum > 1){
                    ++ codeLineNum;
                }else{
                    ++ emptyLineNum;
                }
            }
            reader.close();
            count.setCodeLineNum(codeLineNum);
            count.setEmptyLineNum(emptyLineNum);
            count.setNoteLineNum(noteLineNum);
        } catch (IOException e) {
            System.out.println("IO执行出错：" + e.getMessage());
        }
        return count;
    }

    /**
     * @description 参数判断
     */
    private String judgeFilePath(Parameter parameter) {

        if(parameter.getFilePath() == null){
            return "\n文件路径不能为空";
        }else if(!(parameter.isCountChar() || parameter.isCountWord() || parameter.isCountLine()
                    || parameter.isCountComplex() || parameter.isFrame())){
            return "\n请输入 -c -w -l -a -x 中至少一个命令";
        }
        //获取目录路径
        String[] paths = parameter.getFilePath().split("\\\\");

        String fileName = paths[paths.length - 1];

        boolean flag = false;

        //先判断文件名中是否含有通配符（因为文件夹名中不含?,*字符，所以若含有通配符则为文件类型）
        for(int i = 0 ; i < fileName.length() ; i ++){
            if(fileName.toCharArray()[i] == '*' || fileName.toCharArray()[i] == '?'){
                flag = true;
            }
        }
        if(flag){

            //含有 ？, * 即为文件类型
            StringBuilder sb = new StringBuilder();

            for(int i = 0 ; i < paths.length - 1 ; i ++) {
                if (i == paths.length - 2) {
                    sb.append(paths[i]);
                } else {
                    sb.append(paths[i]).append("\\");
                }
            }
            //判断目录合法性
            File dirFile = new File(sb.toString());
            if(!dirFile.exists() || (dirFile.exists() && !dirFile.isDirectory())){
                return "\n文件路径出错";
            }
            //文件路径改为目录路径
            parameter.setFilePath(sb.toString());
            //设置需匹配的文件名
            parameter.setMatchName(fileName);

            return null;
        }
        File file = new File(parameter.getFilePath());

        //文件名中不含有通配符时再判断文件是否存在，防止通配符对文件存在判断造成影响
        if(!file.exists()){
            return "\n文件或文件夹不存在";
        }
        return null;
    }

    /**
     * @description 文件名是否匹配
     * @param fileName 实际文件名
     * @param matchName 含通配符的文件名
     */
    private boolean compareName(String fileName, String matchName){

        matchName = matchName.replaceAll("\\?","(.?)").replaceAll("\\*","(.*)");

        matchName = "^" + matchName + "$";

        return fileName.matches(matchName);
    }

    private void toComplexCount(Count simpleCount, Count complexCount){
        simpleCount.setCodeLineNum(complexCount.getCodeLineNum());
        simpleCount.setEmptyLineNum(complexCount.getEmptyLineNum());
        simpleCount.setNoteLineNum(complexCount.getNoteLineNum());
    }
}
