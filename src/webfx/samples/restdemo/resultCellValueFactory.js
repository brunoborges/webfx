/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var ReadOnlyObjectWrapper = Packages.javafx.beans.property.ReadOnlyObjectWrapper;
var Callback = Packages.javafx.util.Callback;

var rcvfObject = new Object();
rcvfObject.call = function(param) {
        var resultTableColumn = param.getTableColumn();
        var row = param.getValue();

        return new ReadOnlyObjectWrapper(row.get(resultTableColumn.getId()));
};

var resultCellValueFactory = new JavaAdapter(Callback, rcvfObject);

itemName.setCellValueFactory(resultCellValueFactory);
itemParentName.setCellValueFactory(resultCellValueFactory);
artistName.setCellValueFactory(resultCellValueFactory);