import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Controller {
    public Button btnLoadFromFile;
    public Button btnProcess;

    public TextArea taCodeField;
    public TableView<TokenEl> tOperators;
    public TableView<TokenEl> tOperands;
    public TextField uniqueOperators;
    public TextField allOperators;
    public TextField uniqueOperands;
    public TextField allOperands;
    public TextField pScope;
    public TextField pLength;
    public TextField pDictionary;
    public TableColumn<TokenEl, Integer> j;
    public TableColumn<TokenEl, Integer> f1j;
    public TableColumn<TokenEl, String> operator;
    public TableColumn<TokenEl, Integer> i;
    public TableColumn<TokenEl, String> operand;
    public TableColumn<TokenEl, Integer> f2i;

    private File dlgToOpenFile(boolean isSave){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы (*.txt)", "*.txt"));
        return isSave ? fileChooser.showSaveDialog(View.Interface) : fileChooser.showOpenDialog(View.Interface);
    }

    @FXML
    private void btnLoadFromFileOnClick(MouseEvent e) {
        File selectedFile = dlgToOpenFile(false);

        if (selectedFile == null) return;
        Charset charset = StandardCharsets.UTF_8;
        StringBuilder fileInformation = new StringBuilder();
        try(BufferedReader reader = Files.newBufferedReader(selectedFile.toPath(), charset)) {
            String line;
            while ((line = reader.readLine()) != null) fileInformation.append(line).append('\n');
            taCodeField.setText(fileInformation.toString());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    @FXML
    private void btnCount(MouseEvent e){
        PythonTokenizer.tokenFlow =  PythonTokenizer.tokenize(taCodeField.getText());
        PythonTokenizer.count();
        int n1 = PythonTokenizer.operators.size();
        int n2 = PythonTokenizer.operands.size();
        int N1 = PythonTokenizer.getSum(PythonTokenizer.operators);
        int N2 = PythonTokenizer.getSum(PythonTokenizer.operands);
        int n = n1 + n2;
        int N = N1 + N2;
        long V = Math.round(N*Math.log10(n)/Math.log10(2));

        ObservableList<TokenEl> operatorsData = FXCollections.observableArrayList();
        ObservableList<TokenEl> operandsData = FXCollections.observableArrayList();

        PythonTokenizer.operators.forEach((key, value)-> operatorsData.add(new TokenEl(key, value)));
        TokenEl.counter = 1;
        PythonTokenizer.operands.forEach((key, value)-> operandsData.add(new TokenEl(key, value)));

        j.setCellValueFactory(new PropertyValueFactory<TokenEl, Integer>("id"));
        operator.setCellValueFactory(new PropertyValueFactory<TokenEl, String >("token"));
        f1j.setCellValueFactory(new PropertyValueFactory<TokenEl, Integer>("count"));

        i.setCellValueFactory(new PropertyValueFactory<TokenEl, Integer>("id"));
        operand.setCellValueFactory(new PropertyValueFactory<TokenEl, String >("token"));
        f2i.setCellValueFactory(new PropertyValueFactory<TokenEl, Integer>("count"));

        tOperators.setItems(operatorsData);
        tOperands.setItems(operandsData);

        uniqueOperators.setText(String.valueOf(n1));
        uniqueOperands.setText(String.valueOf(n2));

        allOperators.setText(String.valueOf(N1));
        allOperands.setText(String.valueOf(N2));

        pDictionary.setText(String.valueOf(n));
        pLength.setText(String.valueOf(N));
        pScope.setText(String.valueOf(V));

        TokenEl.counter = 1;
    }
}
