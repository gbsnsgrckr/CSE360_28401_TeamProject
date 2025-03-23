module Team_TP3 {
	requires javafx.controls;
	requires java.sql;
	requires javafx.base;
	requires javafx.graphics;
	requires junit;
	
	opens application to javafx.graphics, javafx.fxml, javafx.base;
}
