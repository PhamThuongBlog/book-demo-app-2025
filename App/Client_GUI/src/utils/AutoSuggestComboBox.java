package utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;
import java.util.stream.Collectors;

public class AutoSuggestComboBox extends JComboBox<String> {
	private final List<String> allItems;
	private final JTextField editor;

	public AutoSuggestComboBox(List<String> items) {
		super(items.toArray(new String[0]));
		this.setEditable(true);
		this.allItems = items;
		this.editor = (JTextField) this.getEditor().getEditorComponent();
		editor.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				filter();
			}

			public void removeUpdate(DocumentEvent e) {
				filter();
			}

			public void changedUpdate(DocumentEvent e) {
			}

			private void filter() {
				SwingUtilities.invokeLater(() -> {
					String input = editor.getText();
					List<String> filtered = allItems.stream()
							.filter(item -> item.toLowerCase().contains(input.toLowerCase()))
							.collect(Collectors.toList());
					setEditable(false);
					removeAllItems();
					for (String match : filtered) {
						addItem(match);
					}
					setEditable(true);
					setPopupVisible(true);
					editor.setText(input);
				});
			}
		});
	}

	public String getSelectedValue() {
		return editor.getText().trim();
	}
}