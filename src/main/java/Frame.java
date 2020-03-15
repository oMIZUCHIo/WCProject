import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * @Description
 * @Author Zhou
 * @Date 2020/3/13
 * @Version 1.0
 */
public class Frame extends Application {

    private String filePath;
    private boolean countChar;
    private boolean countWord;
    private boolean countLine;
    private boolean countComplex;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {

        primaryStage.setTitle("文件选择");
        primaryStage.setWidth(500);
        primaryStage.setHeight(250);

        Label fileLabel = new Label("文件选择");
        Button fileBtn = new Button("选择");
        final Label filePathLab = new Label();

        Label operateLabel = new Label("操作选择");
        final CheckBox cbC = new CheckBox("-c");
        final CheckBox cbW = new CheckBox("-w");
        final CheckBox cbL = new CheckBox("-l");
        final CheckBox cbA = new CheckBox("-a");

        Button OKBtn = new Button("确定");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(fileLabel, 0, 1);
        grid.add(fileBtn, 1, 1);
        grid.add(filePathLab, 2, 1);
        grid.add(operateLabel,0,2);
        grid.add(cbC,1,2);
        grid.add(cbW,2,2);
        grid.add(cbL,3,2);
        grid.add(cbA,4,2);
        grid.add(OKBtn,5,3);

        Group root = new Group();
        root.getChildren().add(grid);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        fileBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("files (*.txt,*.java,*.c,*.cpp,*.word)", "*.txt","*.java","*.c","*.cpp","*.word");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(primaryStage);
                if(file != null){
                    filePathLab.setText(file.getName());
                    filePath = file.getAbsolutePath();
                }
            }
        });

        cbC.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean old_val, Boolean new_val) {
                countChar = cbC.isSelected();
            }
        });
        cbW.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean old_val, Boolean new_val) {
               countWord = cbW.isSelected();
            }
        });
        cbL.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean old_val, Boolean new_val) {
                countLine = cbL.isSelected();
            }
        });
        cbA.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean old_val, Boolean new_val) {
               countComplex = cbA.isSelected();
            }
        });


        OKBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {

                Stage stage = new Stage();
                stage.setTitle("查询结果");
                stage.setHeight(500);
                stage.setWidth(250);
                Label result = new Label();

                if(filePath == null || "".equals(filePath)){
                    result.setText("文件不能为空");
                }else{
                    if(countChar || countWord || countLine || countComplex){
                        Parameter parameter = new Parameter(countChar,countWord,countLine,countComplex,filePath);
                        WCUtil wcUtil = new WCUtil();
                        result.setText(wcUtil.process(parameter));
                    }else{
                        result.setText("请选择至少一个指令");
                    }
                }
                BorderPane pane = new BorderPane();
                pane.setCenter(result);
                Scene scene = new Scene(pane);
                stage.setScene(scene);
                stage.show();
            }
        });

    }
}
