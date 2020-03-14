import lombok.Data;

//对输入数据的简单封装
@Data
class Parameter {

    //计算字符数  -c
    private boolean countChar = false;

    //计算词数  -w
    private boolean countWord = false;

    //计算行数  -l
    private boolean countLine = false;

    //递归处理目录下符合条件的文件 -s
    private boolean recurrence = false;

    //返回更复杂的数据（代码行 / 空行 / 注释行）
    private boolean countComplex = false;

    //是否调用图形化界面
    private boolean frame = false;

    //文件路径
    private String filePath;

    //需匹配名字
    private String matchName;

    Parameter(boolean countChar, boolean countWord, boolean countLine, boolean countComplex, String filePath){
        this.countChar = countChar;
        this.countWord = countWord;
        this.countLine = countLine;
        this.countComplex = countComplex;
        this.filePath = filePath;
    }

    Parameter(){}
}
