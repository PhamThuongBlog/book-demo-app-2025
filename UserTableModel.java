/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fontend_app_call_api;

import javax.swing.table.AbstractTableModel;
import java.util.List;

//custom model cho JTable
public class UserTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Name", "Username", "Email", "City"};
    private List<User> users;

    public UserTableModel(List<User> users) {
        this.users = users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return users.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        User u = users.get(rowIndex);
        switch (columnIndex) {
            case 0: return u.getId();
            case 1: return u.getName();
            case 2: return u.getUsername();
            case 3: return u.getEmail();
            case 4: return u.getCity();
            default: return null;
        }
    }
}

