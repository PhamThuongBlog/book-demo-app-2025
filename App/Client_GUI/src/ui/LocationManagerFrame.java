package ui;

import javax.swing.*;

import service.AppointmentAPI;

import java.awt.*;
import java.util.List;

public class LocationManagerFrame extends JFrame {
	private JList<String> locationList;
	private DefaultListModel<String> listModel;
	private JButton addButton, editButton, deleteButton;

	public LocationManagerFrame() {
		setTitle("Quản lý địa điểm");
		setSize(400, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		listModel = new DefaultListModel<>();
		locationList = new JList<>(listModel);
		JScrollPane scrollPane = new JScrollPane(locationList);
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

		addButton = new JButton("Thêm địa điểm");
		addButton.addActionListener(e -> showAddLocationDialog());
		editButton = new JButton("Sửa");
		editButton.addActionListener(e -> {
			int selectedIndex = locationList.getSelectedIndex();
			if (selectedIndex >= 0) {
				showEditLocationDialog(selectedIndex);
			} else {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn địa điểm để sửa");
			}
		});

		deleteButton = new JButton("Xóa");
		deleteButton.addActionListener(e -> {
			int selectedIndex = locationList.getSelectedIndex();
			if (selectedIndex >= 0) {
				deleteLocation(selectedIndex);
			} else {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn địa điểm để xóa");
			}
		});
		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		loadLocations();
	}

	private void loadLocations() {
		try {
			List<String> locations = AppointmentAPI.getLocations();
			listModel.clear();
			for (String location : locations) {
				listModel.addElement(location);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách địa điểm: " + e.getMessage());
		}
	}

	private void showAddLocationDialog() {
		JDialog dialog = new JDialog(this, "Thêm địa điểm mới", true);
		dialog.setSize(300, 150);
		dialog.setLayout(new GridLayout(3, 1));
		JLabel label = new JLabel("Tên địa điểm:");
		JTextField locationField = new JTextField();
		JButton saveButton = new JButton("Lưu");
		saveButton.addActionListener(e -> {
			String location = locationField.getText().trim();
			if (!location.isEmpty()) {
				try {
					if (AppointmentAPI.addLocation(location)) {
						loadLocations();
						dialog.dispose();
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm địa điểm: " + ex.getMessage());
				}
			} else {
				JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên địa điểm");
			}
		});
		dialog.add(label);
		dialog.add(locationField);
		dialog.add(saveButton);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void showEditLocationDialog(int index) {
		String oldLocation = listModel.getElementAt(index);
		JDialog dialog = new JDialog(this, "Sửa địa điểm", true);
		dialog.setSize(300, 150);
		dialog.setLayout(new GridLayout(3, 1));
		JLabel label = new JLabel("Tên địa điểm:");
		JTextField locationField = new JTextField(oldLocation);
		JButton saveButton = new JButton("Lưu");
		saveButton.addActionListener(e -> {
			String newLocation = locationField.getText().trim();
			if (!newLocation.isEmpty()) {
				try {
					if (AppointmentAPI.updateLocation(oldLocation, newLocation)) {
						loadLocations();
						dialog.dispose();
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(dialog, "Lỗi khi sửa địa điểm: " + ex.getMessage());
				}
			} else {
				JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên địa điểm");
			}
		});
		dialog.add(label);
		dialog.add(locationField);
		dialog.add(saveButton);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void deleteLocation(int index) {
		String location = listModel.getElementAt(index);
		int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa địa điểm '" + location + "'?",
				"Xác nhận xóa", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			try {
				if (AppointmentAPI.deleteLocation(location)) {
					loadLocations();
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Lỗi khi xóa địa điểm: " + e.getMessage());
			}
		}
	}
}