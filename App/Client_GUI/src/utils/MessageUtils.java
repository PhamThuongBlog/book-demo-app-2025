package utils;

import java.awt.Component;

import javax.swing.*;

public class MessageUtils {
	public static void showError(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
	}

	public static void showSuccess(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
	}

	public static boolean confirm(Component parent, String message) {
		return JOptionPane.showConfirmDialog(parent, message, "Xác nhận",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
}