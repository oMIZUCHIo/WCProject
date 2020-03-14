import lombok.Data;

//各文件的查询结果封装
@Data
class Count {

    //文件名
    private String fileName;

    //字符数
    private Integer charNum;

    //词数
    private Integer WordNum;

    //行数
    private Integer lineNum;

    //代码行数
    private Integer CodeLineNum;

    //空行数
    private Integer emptyLineNum;

    //注释行数
    private Integer noteLineNum;

    Count(){}

    Count(int charNum, int wordNum, int lineNum){
        this.charNum = charNum;
        this.WordNum = wordNum;
        this.lineNum = lineNum;
    }
}