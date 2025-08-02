package ui;

import javax.swing.*;

import service.AppointmentAPI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
	private JTextField usernameField;
	private JPasswordField passwordField;

	public LoginFrame() {
		setTitle("Đăng nhập hệ thống");
		setSize(400, 250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		JLabel titleLabel = new JLabel("QUẢN LÝ CUỘC HẸN", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
		panel.add(titleLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		panel.add(new JLabel("Tên đăng nhập:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		usernameField = new JTextField(15);
		panel.add(usernameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(new JLabel("Mật khẩu:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		passwordField = new JPasswordField(15);
		panel.add(passwordField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

		JButton loginButton = new JButton("Đăng nhập");
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (AppointmentAPI.login(usernameField.getText(), new String(passwordField.getPassword()))) {
						System.out.println("Login successful!");
						dispose();
						new MainFrame().setVisible(true);
					} else {
						System.out.println("Login failed - incorrect credentials");
						JOptionPane.showMessageDialog(LoginFrame.this, "Sai tên đăng nhập hoặc mật khẩu", "Lỗi",
								JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception ex) {
					System.out.println("Login error: " + ex.getMessage());
					ex.printStackTrace();
					JOptionPane.showMessageDialog(LoginFrame.this, "Lỗi kết nối: " + ex.getMessage(), "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonPanel.add(loginButton);
		JButton registerButton = new JButton("Đăng ký");
		registerButton.addActionListener(e -> showRegisterDialog());
		buttonPanel.add(registerButton);
		panel.add(buttonPanel, gbc);
		add(panel);
	}

	private void showRegisterDialog() {
		JDialog dialog = new JDialog(this, "Đăng ký tài khoản", true);
		dialog.setSize(400, 350);
		dialog.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		JTextField usernameField = new JTextField(15);
		JTextField emailField = new JTextField(15);
		JPasswordField passwordField = new JPasswordField(15);
		JPasswordField confirmPasswordField = new JPasswordField(15);

		gbc.gridx = 0;
		gbc.gridy = 0;
		dialog.add(new JLabel("Tên đăng nhập:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		dialog.add(usernameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		dialog.add(new JLabel("Email:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		dialog.add(emailField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		dialog.add(new JLabel("Mật khẩu:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		dialog.add(passwordField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		dialog.add(new JLabel("Xác nhận mật khẩu:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		dialog.add(confirmPasswordField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		JButton submitButton = new JButton("Đăng ký");
		submitButton.addActionListener(e -> {
			try {
				if (!new String(passwordField.getPassword()).equals(new String(confirmPasswordField.getPassword()))) {
					JOptionPane.showMessageDialog(dialog, "Mật khẩu xác nhận không khớp!", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (AppointmentAPI.register(usernameField.getText(), emailField.getText(),
						new String(passwordField.getPassword()), new String(confirmPasswordField.getPassword()))) {
					JOptionPane.showMessageDialog(dialog, "Đăng ký thành công.");
					dialog.dispose();
				} else {
					JOptionPane.showMessageDialog(dialog, "Đăng ký không thành công", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		});
		dialog.add(submitButton, gbc);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}
}