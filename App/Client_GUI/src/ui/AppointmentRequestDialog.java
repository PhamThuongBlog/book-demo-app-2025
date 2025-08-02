package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import org.json.JSONObject;
import service.AppointmentAPI;

public class AppointmentRequestDialog extends JDialog {
	private DefaultListModel<String> listModel;
	private JList<String> requestsList;
	private List<JSONObject> pendingRequests;

	public AppointmentRequestDialog(JFrame parent, List<JSONObject> pendingRequests) {
		super(parent, "Yêu cầu cuộc hẹn", true);
		this.pendingRequests = pendingRequests;
		setSize(500, 400);
		setLocationRelativeTo(parent);
		initUI();
	}

	private void initUI() {
		listModel = new DefaultListModel<>();
		requestsList = new JList<>(listModel);
		requestsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		populateRequests();
		JScrollPane scrollPane = new JScrollPane(requestsList);
		JButton acceptButton = new JButton("Đồng ý");
		JButton rejectButton = new JButton("Từ chối");
		acceptButton.addActionListener(e -> handleResponse(true));
		rejectButton.addActionListener(e -> handleResponse(false));
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		buttonPanel.add(acceptButton);
		buttonPanel.add(rejectButton);
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
	}

	private void populateRequests() {
		listModel.clear();
		for (JSONObject request : pendingRequests) {
			String sender = request.optString("senderUsername");
			String title = request.optString("title");
			String time = request.optString("startTime", "Không có thời gian");
			String display = String.format("Từ: %s | Tiêu đề: %s | Thời gian: %s", sender, title, time);
			listModel.addElement(display);
		}
	}

	private void handleResponse(boolean accept) {
		int selectedIndex = requestsList.getSelectedIndex();
		if (selectedIndex < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu cần xử lý");
			return;
		}

		JSONObject selectedRequest = pendingRequests.get(selectedIndex);
		int requestId = selectedRequest.getInt("id");

		try {
			boolean success = AppointmentAPI.respondToAppointmentRequest(requestId, accept);
			if (success) {
				JOptionPane.showMessageDialog(this, accept ? "✅ Đã chấp nhận cuộc hẹn." : "🚫 Đã từ chối cuộc hẹn.");
				pendingRequests.remove(selectedIndex);
				listModel.remove(selectedIndex);

				if (pendingRequests.isEmpty()) {
					dispose();
				}
			} else {
				JOptionPane.showMessageDialog(this, "❌ Không thể xử lý yêu cầu.");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
		}
	}
}
