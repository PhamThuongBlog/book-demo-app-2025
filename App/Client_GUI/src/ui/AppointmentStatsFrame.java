package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.json.JSONObject;

import service.AppointmentAPI;
import utils.DateLabelFormatter;

public class AppointmentStatsFrame extends JFrame {
	public AppointmentStatsFrame() {
		setTitle("Thống kê cuộc hẹn");
		setSize(400, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel(new GridLayout(5, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		try {
			JSONObject stats = AppointmentAPI.getAppointmentStats();

			panel.add(new JLabel("Tổng số cuộc hẹn: " + stats.getInt("totalAppointments")));
			panel.add(new JLabel("Cuộc hẹn đã hoàn thành: " + stats.getInt("completedAppointments")));
			panel.add(new JLabel("Cuộc hẹn đã lỡ: " + stats.getInt("missedAppointments")));
			panel.add(new JLabel("Cuộc hẹn đã hủy: " + stats.getInt("cancelledAppointments")));

			JButton rescheduleButton = new JButton("Đặt lại cuộc hẹn đã lỡ");
			rescheduleButton.addActionListener(e -> showRescheduleDialog());
			panel.add(rescheduleButton);

		} catch (Exception e) {
			panel.add(new JLabel("Lỗi khi tải thống kê: " + e.getMessage()));
		}

		add(panel);
	}

	private void showRescheduleDialog() {
		JDialog dialog = new JDialog(this, "Đặt lại cuộc hẹn đã lỡ", true);
		dialog.setSize(450, 500);
		dialog.setLayout(new BorderLayout(10, 10));

		final List<JSONObject> missedAppointments = new ArrayList<>();
		DefaultListModel<String> listModel = new DefaultListModel<>();
		JList<String> missedAppointmentsList = new JList<>(listModel);

		try {
			missedAppointments.addAll(AppointmentAPI.getMissedAppointments());

			if (missedAppointments.isEmpty()) {
				JOptionPane.showMessageDialog(dialog, "Không có cuộc hẹn nào đã lỡ!");
				dialog.dispose();
				return;
			}

			SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

			for (JSONObject appt : missedAppointments) {
				Date startTime = apiFormat.parse(appt.getString("startTime"));
				String formattedDate = displayFormat.format(startTime);
				listModel.addElement(appt.getString("title") + " - " + formattedDate);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(dialog, "Lỗi khi tải danh sách: " + e.getMessage());
			dialog.dispose();
			return;
		}

		JLabel countLabel = new JLabel("Tổng số cuộc hẹn đã lỡ: " + missedAppointments.size());
		countLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		dialog.add(countLabel, BorderLayout.NORTH);

		JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
		formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin đặt lại"));

		UtilDateModel startModel = new UtilDateModel();
		startModel.setValue(new Date());
		startModel.setSelected(true);
		Properties p = new Properties();
		p.put("text.today", "Hôm nay");
		p.put("text.month", "Tháng");
		p.put("text.year", "Năm");
		JDatePanelImpl startDatePanel = new JDatePanelImpl(startModel, p);
		JDatePickerImpl startDatePicker = new JDatePickerImpl(startDatePanel, new DateLabelFormatter());
		JSpinner startTimeSpinner = new JSpinner(new SpinnerDateModel());
		startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));

		UtilDateModel endModel = new UtilDateModel();
		endModel.setValue(new Date());
		endModel.setSelected(true);
		JDatePanelImpl endDatePanel = new JDatePanelImpl(endModel, p);
		JDatePickerImpl endDatePicker = new JDatePickerImpl(endDatePanel, new DateLabelFormatter());
		JSpinner endTimeSpinner = new JSpinner(new SpinnerDateModel());
		endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));

		formPanel.add(new JLabel("Ngày bắt đầu mới:"));
		formPanel.add(startDatePicker);
		formPanel.add(new JLabel("Giờ bắt đầu mới:"));
		formPanel.add(startTimeSpinner);
		formPanel.add(new JLabel("Ngày kết thúc mới:"));
		formPanel.add(endDatePicker);
		formPanel.add(new JLabel("Giờ kết thúc mới:"));
		formPanel.add(endTimeSpinner);

		JButton rescheduleButton = new JButton("Xác nhận đặt lại");
		rescheduleButton.addActionListener(e -> {
			int selectedIndex = missedAppointmentsList.getSelectedIndex();
			if (selectedIndex >= 0 && selectedIndex < missedAppointments.size()) {
				try {
					Date startDate = (Date) startDatePicker.getModel().getValue();
					Date endDate = (Date) endDatePicker.getModel().getValue();
					if (startDate == null || endDate == null) {
						JOptionPane.showMessageDialog(dialog, "Vui lòng chọn ngày hợp lệ", "Cảnh báo",
								JOptionPane.WARNING_MESSAGE);
						return;
					}

					Calendar startCal = Calendar.getInstance();
					startCal.setTime(startDate);
					Calendar startTimeCal = Calendar.getInstance();
					startTimeCal.setTime((Date) startTimeSpinner.getValue());
					startCal.set(Calendar.HOUR_OF_DAY, startTimeCal.get(Calendar.HOUR_OF_DAY));
					startCal.set(Calendar.MINUTE, startTimeCal.get(Calendar.MINUTE));
					startCal.set(Calendar.SECOND, 0);

					Calendar endCal = Calendar.getInstance();
					endCal.setTime(endDate);
					Calendar endTimeCal = Calendar.getInstance();
					endTimeCal.setTime((Date) endTimeSpinner.getValue());
					endCal.set(Calendar.HOUR_OF_DAY, endTimeCal.get(Calendar.HOUR_OF_DAY));
					endCal.set(Calendar.MINUTE, endTimeCal.get(Calendar.MINUTE));
					endCal.set(Calendar.SECOND, 0);

					Date newStart = startCal.getTime();
					Date newEnd = endCal.getTime();

					JSONObject selectedAppointment = missedAppointments.get(selectedIndex);
					SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					String formattedNewStart = apiFormat.format(newStart);
					String formattedNewEnd = apiFormat.format(newEnd);

					if (AppointmentAPI.rescheduleAppointment(selectedAppointment.getInt("id"), formattedNewStart,
							formattedNewEnd)) {
						JOptionPane.showMessageDialog(this, "Đặt lại thành công!");
						dialog.dispose();
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(dialog, "Vui lòng chọn cuộc hẹn hợp lệ", "Cảnh báo",
						JOptionPane.WARNING_MESSAGE);
			}
		});

		dialog.add(new JScrollPane(missedAppointmentsList), BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		bottomPanel.add(formPanel, BorderLayout.NORTH);
		bottomPanel.add(rescheduleButton, BorderLayout.SOUTH);

		dialog.add(bottomPanel, BorderLayout.SOUTH);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}
}