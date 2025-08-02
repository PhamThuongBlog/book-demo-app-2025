package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.json.JSONArray;
import org.json.JSONObject;

import service.AppointmentAPI;
import utils.DateLabelFormatter;
import utils.MessageUtils;

public class MainFrame extends JFrame {
	private JTable appointmentTable;
	private DefaultTableModel tableModel;
	private Timer reminderTimer;

	private String translateStatus(String status) {
		return switch (status) {
		case "SCHEDULED" -> "Đã lên lịch";
		case "CONFIRMED" -> "Đã xác nhận";
		case "CANCELLED" -> "Đã hủy";
		case "MISSED" -> "Đã lỡ";
		case "COMPLETED" -> "Đã hoàn thành";
		default -> "Không rõ";
		};
	}

	private JDatePickerImpl createDatePicker() {
		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Hôm nay");
		p.put("text.month", "Tháng");
		p.put("text.year", "Năm");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		return new JDatePickerImpl(datePanel, new DateLabelFormatter());
	}

	private JSpinner createTimeSpinner() {
		JSpinner spinner = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
		spinner.setEditor(editor);
		return spinner;
	}

	private Date combineDateTime(JDatePickerImpl datePicker, JSpinner timeSpinner) {
		Calendar date = Calendar.getInstance();
		date.setTime((Date) datePicker.getModel().getValue());
		Calendar time = Calendar.getInstance();
		time.setTime((Date) timeSpinner.getValue());
		date.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		return date.getTime();
	}

	public MainFrame() {
		setTitle("Quản lý cuộc hẹn");
		setSize(1100, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		String[] columns = { "ID", "Tiêu đề", "Bắt đầu", "Kết thúc", "Địa điểm", "Ghi chú", "Trạng thái" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		appointmentTable = new JTable(tableModel);
		appointmentTable.setAutoCreateRowSorter(true);
		JScrollPane scrollPane = new JScrollPane(appointmentTable);
		add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		JButton refreshButton = new JButton("Làm mới");
		refreshButton.addActionListener(e -> refreshAppointments());
		JButton addButton = new JButton("Thêm");
		addButton.addActionListener(e -> showAddAppointmentDialog());
		JButton editButton = new JButton("Sửa");
		editButton.addActionListener(e -> {
			int selectedRow = appointmentTable.getSelectedRow();
			if (selectedRow >= 0) {
				showEditAppointmentDialog(selectedRow);
			} else {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn cuộc hẹn để sửa", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
			}
		});

		JButton deleteButton = new JButton("Xóa");
		deleteButton.addActionListener(e -> deleteSelectedAppointment());
		JButton locationButton = new JButton("Quản lý địa điểm");
		locationButton.addActionListener(e -> new LocationManagerFrame().setVisible(true));
		JButton statsButton = new JButton("Thống kê");
		statsButton.addActionListener(e -> new AppointmentStatsFrame().setVisible(true));
		JButton requestButton = new JButton("Yêu cầu hẹn");
		requestButton.addActionListener(e -> showAppointmentRequestDialog());
		JButton pendingRequestButton = new JButton("Xử lý yêu cầu");
		pendingRequestButton.addActionListener(e -> showPendingRequestsDialog());
		buttonPanel.add(pendingRequestButton);
		JButton searchButton = new JButton("Tìm kiếm");
		searchButton.addActionListener(e -> showSearchDialog());
		JButton markCompletedButton = new JButton("Đánh dấu đã hoàn thành");
		markCompletedButton.addActionListener(e -> markAppointmentAsCompleted());

		buttonPanel.add(refreshButton);
		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(locationButton);
		buttonPanel.add(statsButton);
		buttonPanel.add(requestButton);
		buttonPanel.add(searchButton);
		buttonPanel.add(markCompletedButton);
		add(buttonPanel, BorderLayout.NORTH);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		JButton logoutButton = new JButton("Đăng xuất");
		logoutButton.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận",
					JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				reminderTimer.stop();
				dispose();
				SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
			}
		});
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		leftPanel.add(logoutButton);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		bottomPanel.add(leftPanel, BorderLayout.WEST);
		add(bottomPanel, BorderLayout.SOUTH);

		refreshAppointments();
		reminderTimer = new Timer(60000, e -> checkReminders());
		reminderTimer.start();
	}

	private void markAppointmentAsCompleted() {
		int selectedRow = appointmentTable.getSelectedRow();
		if (selectedRow >= 0) {
			long appointmentId = (long) tableModel.getValueAt(selectedRow, 0);
			int confirm = JOptionPane.showConfirmDialog(this,
					"Bạn có chắc chắn muốn đánh dấu cuộc hẹn này đã hoàn thành?", "Xác nhận",
					JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				try {
					boolean success = AppointmentAPI.markAsCompleted(appointmentId);
					if (success) {
						MessageUtils.showSuccess(this, "Đã đánh dấu cuộc hẹn là hoàn thành.");
						refreshAppointments();
					} else {
						MessageUtils.showError(this, "Không thể đánh dấu cuộc hẹn.");
					}
				} catch (Exception ex) {
					MessageUtils.showError(this, "Lỗi: " + ex.getMessage());
				}
			}
		} else {
			MessageUtils.showError(this, "Vui lòng chọn một cuộc hẹn.");
		}
	}

	private void showAppointmentRequestDialog() {
		JDialog dialog = new JDialog(this, "Tạo yêu cầu hẹn", true);
		dialog.setSize(500, 500);
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 6, 6, 6);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		dialog.add(new JLabel("Người nhận (username):"), gbc);
		gbc.gridx = 1;
		JTextField receiverField = new JTextField(20);
		dialog.add(receiverField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		dialog.add(new JLabel("Tiêu đề:"), gbc);
		gbc.gridx = 1;
		JTextField titleField = new JTextField(20);
		dialog.add(titleField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		dialog.add(new JLabel("Thời gian bắt đầu:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JDatePickerImpl startDatePicker = createDatePicker();
		JSpinner startTimeSpinner = createTimeSpinner();
		startPanel.add(startDatePicker);
		startPanel.add(startTimeSpinner);
		dialog.add(startPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		dialog.add(new JLabel("Thời gian kết thúc:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JDatePickerImpl endDatePicker = createDatePicker();
		JSpinner endTimeSpinner = createTimeSpinner();
		endPanel.add(endDatePicker);
		endPanel.add(endTimeSpinner);
		dialog.add(endPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		dialog.add(new JLabel("Địa điểm:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 4;
		JComboBox<String> locationComboBox = new JComboBox<>();
		locationComboBox.setEditable(true);
		try {
			List<String> locations = AppointmentAPI.getLocations();
			for (String loc : locations) {
				locationComboBox.addItem(loc);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(dialog, "Không thể tải danh sách địa điểm: " + ex.getMessage());
		}
		dialog.add(locationComboBox, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		dialog.add(new JLabel("Mô tả:"), gbc);
		gbc.gridx = 1;
		JTextArea descriptionArea = new JTextArea(3, 20);
		descriptionArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(descriptionArea);
		dialog.add(scrollPane, gbc);

		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		JButton sendButton = new JButton("Gửi yêu cầu");
		sendButton.addActionListener(e -> {
			try {
				String receiver = receiverField.getText().trim();
				String title = titleField.getText().trim();
				Date startTime = combineDateTime(startDatePicker, startTimeSpinner);
				Date endTime = combineDateTime(endDatePicker, endTimeSpinner);

				if (receiver.isEmpty()) {
					JOptionPane.showMessageDialog(dialog, "Vui lòng nhập username người nhận", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (title.isEmpty()) {
					JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tiêu đề", "Lỗi", JOptionPane.ERROR_MESSAGE);
					return;
				}

				String location = (String) locationComboBox.getSelectedItem();
				String description = descriptionArea.getText().trim();

				SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				String formattedStart = apiFormat.format(startTime);
				String formattedEnd = apiFormat.format(endTime);

				boolean success = AppointmentAPI.sendAppointmentRequest(receiver, title, formattedStart, formattedEnd,
						location, description);

				if (success) {
					JOptionPane.showMessageDialog(dialog, "Đã gửi yêu cầu hẹn thành công!", "Thành công",
							JOptionPane.INFORMATION_MESSAGE);
					dialog.dispose();
				} else {
					JOptionPane.showMessageDialog(dialog, "Yêu cầu hẹn không được gửi. Vui lòng thử lại.", "Thất bại",
							JOptionPane.WARNING_MESSAGE);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				SwingUtilities.invokeLater(() -> {
					JOptionPane.showMessageDialog(dialog, "Lỗi khi gửi yêu cầu: " + ex.getMessage(), "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				});
			}
		});
		dialog.add(sendButton, gbc);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void showPendingRequestsDialog() {
		try {
			List<JSONObject> pendingRequests = AppointmentAPI.getPendingRequests();

			if (pendingRequests.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Không có yêu cầu cuộc hẹn nào đang chờ.");
			} else {
				new AppointmentRequestDialog(this, pendingRequests).setVisible(true);
				refreshAppointments();
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi khi tải yêu cầu cuộc hẹn: " + ex.getMessage());
		}
	}

	private void refreshAppointments() {
		try {
			JSONArray appointments = AppointmentAPI.getAppointments();
			tableModel.setRowCount(0);
			SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
			for (int i = 0; i < appointments.length(); i++) {
				JSONObject appt = appointments.getJSONObject(i);
				Date start = apiFormat.parse(appt.getString("startTime"));
				Date end = apiFormat.parse(appt.getString("endTime"));
				String rawStatus = appt.optString("status", "UNKNOWN");
				String translatedStatus = translateStatus(rawStatus);

				tableModel.addRow(new Object[] { appt.getLong("id"), appt.getString("title"),
						displayFormat.format(start), displayFormat.format(end), appt.optString("location", ""),
						appt.optString("description", ""), translatedStatus });

			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showAddAppointmentDialog() {
		JDialog dialog = new JDialog(this, "Thêm cuộc hẹn mới", true);
		dialog.setSize(500, 400);
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		dialog.add(new JLabel("Tiêu đề:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		JTextField titleField = new JTextField(20);
		dialog.add(titleField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		dialog.add(new JLabel("Thời gian bắt đầu:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JDatePickerImpl startDatePicker = createDatePicker();
		JSpinner startTimeSpinner = createTimeSpinner();
		startPanel.add(startDatePicker);
		startPanel.add(startTimeSpinner);
		dialog.add(startPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		dialog.add(new JLabel("Thời gian kết thúc:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JDatePickerImpl endDatePicker = createDatePicker();
		JSpinner endTimeSpinner = createTimeSpinner();
		endPanel.add(endDatePicker);
		endPanel.add(endTimeSpinner);
		dialog.add(endPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		dialog.add(new JLabel("Địa điểm:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		JComboBox<String> locationComboBox = new JComboBox<>();
		locationComboBox.setEditable(true);
		try {
			List<String> locations = AppointmentAPI.getLocations();
			for (String loc : locations) {
				locationComboBox.addItem(loc);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(dialog, "Không thể tải danh sách địa điểm: " + ex.getMessage());
		}
		dialog.add(locationComboBox, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		dialog.add(new JLabel("Ghi chú:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 4;
		JTextArea notesArea = new JTextArea(3, 20);
		notesArea.setLineWrap(true);
		JScrollPane notesScroll = new JScrollPane(notesArea);
		dialog.add(notesScroll, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		JButton saveButton = new JButton("Lưu");
		saveButton.addActionListener(e -> {
			try {
				String title = titleField.getText();
				Date startTime = combineDateTime(startDatePicker, startTimeSpinner);
				Date endTime = combineDateTime(endDatePicker, endTimeSpinner);
				String location = (String) locationComboBox.getSelectedItem();
				String notes = notesArea.getText();
				if (title.isEmpty()) {
					JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tiêu đề");
					return;
				}

				SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				boolean success = AppointmentAPI.addAppointment(title, apiFormat.format(startTime),
						apiFormat.format(endTime), location, notes);
				if (success) {
					refreshAppointments();
					dialog.dispose();
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm cuộc hẹn: " + ex.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		dialog.add(saveButton, gbc);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	@Override
	public void dispose() {
		reminderTimer.stop();
		super.dispose();
	}

	private void deleteSelectedAppointment() {
		int selectedRow = appointmentTable.getSelectedRow();
		if (selectedRow >= 0) {
			long appointmentId = (long) tableModel.getValueAt(selectedRow, 0);
			if (MessageUtils.confirm(this, "Bạn có chắc muốn xóa cuộc hẹn này?")) {
				try {
					if (AppointmentAPI.deleteAppointment(appointmentId)) {
						MessageUtils.showSuccess(this, "Xóa cuộc hẹn thành công");
						refreshAppointments();
					} else {
						MessageUtils.showError(this, "Xóa cuộc hẹn không thành công");
					}
				} catch (Exception ex) {
					MessageUtils.showError(this, "Lỗi khi xóa cuộc hẹn: " + ex.getMessage());
				}
			}
		} else {
			MessageUtils.showError(this, "Vui lòng chọn cuộc hẹn cần xóa");
		}
	}

	private void showEditAppointmentDialog(int rowIndex) {
		long appointmentId = (long) tableModel.getValueAt(rowIndex, 0);
		String title = (String) tableModel.getValueAt(rowIndex, 1);
		String startTime = (String) tableModel.getValueAt(rowIndex, 2);
		String endTime = (String) tableModel.getValueAt(rowIndex, 3);
		String location = (String) tableModel.getValueAt(rowIndex, 4);
		String description = (String) tableModel.getValueAt(rowIndex, 5);
		JDialog dialog = new JDialog(this, "Sửa cuộc hẹn", true);
		dialog.setSize(500, 400);
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 0;
		gbc.gridy = 0;
		dialog.add(new JLabel("Tiêu đề:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JTextField titleField = new JTextField(title, 20);
		dialog.add(titleField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		dialog.add(new JLabel("Thời gian bắt đầu:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JDatePickerImpl startDatePicker = createDatePicker();
		JSpinner startTimeSpinner = createTimeSpinner();
		startPanel.add(startDatePicker);
		startPanel.add(startTimeSpinner);
		dialog.add(startPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		dialog.add(new JLabel("Thời gian kết thúc:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JDatePickerImpl endDatePicker = createDatePicker();
		JSpinner endTimeSpinner = createTimeSpinner();
		endPanel.add(endDatePicker);
		endPanel.add(endTimeSpinner);
		dialog.add(endPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		dialog.add(new JLabel("Địa điểm:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		JComboBox<String> locationComboBox = new JComboBox<>();
		locationComboBox.setEditable(true);
		try {
			List<String> locations = AppointmentAPI.getLocations();
			for (String loc : locations) {
				locationComboBox.addItem(loc);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(dialog, "Không thể tải danh sách địa điểm: " + ex.getMessage());
		}
		dialog.add(locationComboBox, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		dialog.add(new JLabel("Ghi chú:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.fill = GridBagConstraints.BOTH;
		JTextArea notesArea = new JTextArea(description, 5, 20);
		notesArea.setLineWrap(true);
		JScrollPane notesScroll = new JScrollPane(notesArea);
		dialog.add(notesScroll, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		JButton saveButton = new JButton("Lưu thay đổi");
		saveButton.addActionListener(e -> {
			try {
				SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

				Date startDate = (Date) startDatePicker.getModel().getValue();
				Date startTimeOnly = (Date) startTimeSpinner.getValue();
				Date endDate = (Date) endDatePicker.getModel().getValue();
				Date endTimeOnly = (Date) endTimeSpinner.getValue();

				Calendar startCal = Calendar.getInstance();
				startCal.setTime(startDate);
				Calendar timeCal = Calendar.getInstance();
				timeCal.setTime(startTimeOnly);
				startCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
				startCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
				startCal.set(Calendar.SECOND, 0);

				Calendar endCal = Calendar.getInstance();
				endCal.setTime(endDate);
				timeCal.setTime(endTimeOnly);
				endCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
				endCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
				endCal.set(Calendar.SECOND, 0);

				if (AppointmentAPI.updateAppointment(appointmentId, titleField.getText(),
						apiFormat.format(startCal.getTime()), apiFormat.format(endCal.getTime()),
						(String) locationComboBox.getSelectedItem(), notesArea.getText())) {
					MessageUtils.showSuccess(dialog, "Cập nhật cuộc hẹn thành công");
					dialog.dispose();
					refreshAppointments();
				} else {
					MessageUtils.showError(dialog, "Cập nhật không thành công");
				}
			} catch (Exception ex) {
				MessageUtils.showError(dialog, "Lỗi: " + ex.getMessage());
			}
		});

		dialog.add(saveButton, gbc);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void checkReminders() {
		try {
			List<JSONObject> upcomingAppointments = AppointmentAPI.getUpcomingAppointments();
			Date now = new Date();

			SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

			for (JSONObject appointment : upcomingAppointments) {
				Date startTime = apiFormat.parse(appointment.getString("startTime"));
				long diff = startTime.getTime() - now.getTime();
				long minutesLeft = diff / (60 * 1000);
				if (minutesLeft <= 30 && !appointment.getBoolean("reminderSent")) {
					String message = String.format(
							"Bạn có cuộc hẹn sắp tới:\n\n" + "Tiêu đề: %s\n" + "Thời gian: %s\n" + "Địa điểm: %s\n"
									+ "Ghi chú: %s",
							appointment.getString("title"), displayFormat.format(startTime),
							appointment.optString("location", ""), appointment.optString("description", ""));
					JOptionPane.showMessageDialog(this, message, "Nhắc nhở cuộc hẹn", JOptionPane.INFORMATION_MESSAGE);
					AppointmentAPI.markAsReminded(appointment.getInt("id"));
				}
			}
		} catch (Exception ex) {
			System.err.println("Lỗi kiểm tra nhắc nhở: " + ex.getMessage());
		}
	}

	private void showSearchDialog() {
		JDatePickerImpl searchDatePicker = createDatePicker();
		JDialog dialog = new JDialog(this, "Tìm kiếm cuộc hẹn", true);
		dialog.setSize(400, 200);
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 0;
		gbc.gridy = 0;
		dialog.add(new JLabel("Tìm theo tên:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JTextField nameField = new JTextField(15);
		dialog.add(nameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		dialog.add(new JLabel("Hoặc theo ngày:"), gbc);
		gbc.gridx = 1;
		dialog.add(searchDatePicker, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		JButton searchButton = new JButton("Tìm kiếm");
		searchButton.addActionListener(e -> {
			try {
				JSONArray results;
				if (!nameField.getText().isEmpty()) {
					results = AppointmentAPI.searchAppointmentsByTitle(nameField.getText());
				} else {
					Date selectedDate = (Date) searchDatePicker.getModel().getValue();
					SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					String dateStr = apiFormat.format(selectedDate);
					results = AppointmentAPI.searchAppointmentsByDate(dateStr);
				}
				tableModel.setRowCount(0);
				SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
				SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				for (int i = 0; i < results.length(); i++) {
					JSONObject appt = results.getJSONObject(i);
					Date start = apiFormat.parse(appt.getString("startTime"));
					Date end = apiFormat.parse(appt.getString("endTime"));

					tableModel.addRow(new Object[] { appt.getLong("id"), appt.getString("title"),
							dateFormat.format(start), dateFormat.format(end), appt.optString("location", ""),
							appt.optString("description", ""),
							appt.has("status") ? appt.getString("status") : "UNKNOWN" });
				}
				dialog.dispose();
			} catch (Exception ex) {
				MessageUtils.showError(dialog, "Lỗi tìm kiếm: " + ex.getMessage());
			}
		});
		dialog.add(searchButton, gbc);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}
}