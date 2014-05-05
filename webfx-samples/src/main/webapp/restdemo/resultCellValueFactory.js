var ReadOnlyObjectWrapper = Packages.javafx.beans.property.ReadOnlyObjectWrapper;

var MyCallbackListener = Java.extend(Java.type("javafx.util.Callback"));
var resultCellValueFactory = new MyCallbackListener() {
    call: function(param) {
        var resultTableColumn = param.getTableColumn();
        var row = param.getValue();

        return new ReadOnlyObjectWrapper(row.get(resultTableColumn.getId()));
    }
};

itemName.setCellValueFactory(resultCellValueFactory);
itemParentName.setCellValueFactory(resultCellValueFactory);
artistName.setCellValueFactory(resultCellValueFactory);
