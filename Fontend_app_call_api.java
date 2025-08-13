/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fontend_app_call_api;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author FPTSHOP
 */
public class Fontend_app_call_api extends JFrame {

private final UserService userService = new UserService();
    private final UserTableModel tableModel = new UserTableModel(Collections.emptyList());

    private JTextField searchField;
    private JTable table;
    private JLabel pageInfo;
    private JComboBox<Integer> pageSizeBox;
    private JButton firstBtn, prevBtn, nextBtn, lastBtn;

    // Dữ liệu & phân trang
    private List<User> allUsers = new ArrayList<>();
    private List<User> filteredUsers = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 5;
    private int totalPages = 1;

    public Fontend_app_call_api() {
        setTitle("User Info App - JSONPlaceholder (Swing)");
        setSize(900, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== Top: Load + Search =====
        JButton loadBtn = new JButton("Load Users");
        loadBtn.addActionListener(this::onLoadUsers);

        searchField = new JTextField(18);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> {
            applyFilter();
            goToPage(1);
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(loadBtn);
        top.add(new JLabel("Search by Name:"));
        top.add(searchField);
        top.add(searchBtn);

        // ===== Center: Table =====
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // ===== Bottom: Pagination controls =====
        firstBtn = new JButton("<< First");
        prevBtn  = new JButton("< Prev");
        nextBtn  = new JButton("Next >");
        lastBtn  = new JButton("Last >>");

        firstBtn.addActionListener(e -> goToPage(1));
        prevBtn.addActionListener(e -> goToPage(currentPage - 1));
        nextBtn.addActionListener(e -> goToPage(currentPage + 1));
        lastBtn.addActionListener(e -> goToPage(totalPages));

        pageInfo = new JLabel("Page 0 / 0");

        pageSizeBox = new JComboBox<>(new Integer[]{5, 10, 20, 50});
        pageSizeBox.setSelectedItem(5);
        pageSizeBox.addActionListener(e -> {
            pageSize = (Integer) pageSizeBox.getSelectedItem();
            goToPage(1);
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(firstBtn);
        bottom.add(prevBtn);
        bottom.add(pageInfo);
        bottom.add(nextBtn);
        bottom.add(lastBtn);
        bottom.add(new JLabel(" | Page size:"));
        bottom.add(pageSizeBox);

        // ===== Layout =====
        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void onLoadUsers(ActionEvent e) {
        // Tải dữ liệu
        allUsers = userService.fetchUsers();
        filteredUsers = new ArrayList<>(allUsers);
        goToPage(1);
    }

    private void applyFilter() {
        String kw = searchField.getText().trim().toLowerCase();
        if (kw.isEmpty()) {
            filteredUsers = new ArrayList<>(allUsers);
        } else {
            filteredUsers = allUsers.stream()
                    .filter(u -> u.getName().toLowerCase().contains(kw))
                    .collect(Collectors.toList());
        }
        updatePaginationMeta();
    }

    private void updatePaginationMeta() {
        int totalItems = filteredUsers.size();
        totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) pageSize));
        currentPage = Math.min(currentPage, totalPages);
        currentPage = Math.max(currentPage, 1);
    }

    private void goToPage(int page) {
        if (filteredUsers == null || filteredUsers.isEmpty()) {
            tableModel.setUsers(Collections.emptyList());
            pageInfo.setText("Page 0 / 0 (0 items)");
            setNavEnabled(false);
            return;
        }

        currentPage = Math.max(1, Math.min(page, totalPages));
        int from = (currentPage - 1) * pageSize;
        int to   = Math.min(from + pageSize, filteredUsers.size());
        List<User> pageData = filteredUsers.subList(from, to);

        tableModel.setUsers(pageData);
        pageInfo.setText(String.format(
                "Page %d / %d  (items %d–%d of %d)",
                currentPage, totalPages, from + 1, to, filteredUsers.size()
        ));

        setNavEnabled(true);
        firstBtn.setEnabled(currentPage > 1);
        prevBtn.setEnabled(currentPage > 1);
        nextBtn.setEnabled(currentPage < totalPages);
        lastBtn.setEnabled(currentPage < totalPages);
    }

    private void setNavEnabled(boolean enabled) {
        firstBtn.setEnabled(enabled);
        prevBtn.setEnabled(enabled);
        nextBtn.setEnabled(enabled);
        lastBtn.setEnabled(enabled);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Fontend_app_call_api().setVisible(true));
    }
}
