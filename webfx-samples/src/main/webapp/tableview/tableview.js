var $webfx = {title: "TableView Example"};

var FXCollections = Packages.javafx.collections.FXCollections;
var ReadOnlyObjectWrapper = Packages.javafx.beans.property.ReadOnlyObjectWrapper;
var HashMap = Packages.java.util.HashMap;

var MyCallback = Java.extend(Java.type("javafx.util.Callback"));
var mapCellValueFactory = new MyCallback() {
    call: function(param) {
        var row = param.getValue();
        return new ReadOnlyObjectWrapper(row.get(param.getTableColumn().getId()));
    }
};

firstNameColumn.setCellValueFactory(mapCellValueFactory);
lastNameColumn.setCellValueFactory(mapCellValueFactory);
emailColumn.setCellValueFactory(mapCellValueFactory);

function createPerson(firstName, lastName, email) {
    var person = new HashMap();
    person.put("firstName", firstName);
    person.put("lastName", lastName);
    person.put("email", email);
    return person;
}

var data = FXCollections.observableArrayList();
data.add(createPerson("Jacob", "Smith", "jacob.smith@example.com"));
data.add(createPerson("Isabella", "Johnson", "isabella.johnson@example.com"));
data.add(createPerson("Ethan", "Williams", "ethan.williams@example.com"));
data.add(createPerson("Emma", "Jones", "emma.jones@example.com"));
data.add(createPerson("Michael", "Brown", "michael.brown@example.com"));

tableView.setItems(data);
tableView.getSortOrder().add(firstNameColumn);
tableView.sort();

function addPerson() {
    data.add(createPerson(
        firstNameField.getText(),
        lastNameField.getText(),
        emailField.getText()
    ));
    firstNameField.setText("");
    lastNameField.setText("");
    emailField.setText("");
}
