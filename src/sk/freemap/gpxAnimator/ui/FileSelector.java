/*
 *  Copyright 2013 Martin Ždila, Freemap Slovakia
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package sk.freemap.gpxAnimator.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class FileSelector extends JPanel {
	private static final long serialVersionUID = 3157365691996396016L;
	
	private final JTextField fileTextField;

	private final JButton btnNewButton;
	
	private JFileChooser fileChooser;

	public enum Type {
		OPEN, SAVE
	}

	/**
	 * Create the panel.
	 */
	public FileSelector() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		fileTextField = new JTextField();
		fileTextField.setMaximumSize(new Dimension(2147483647, 21));
		fileTextField.setPreferredSize(new Dimension(55, 21));
		add(fileTextField);
		fileTextField.setColumns(10);
		
		fileTextField.getDocument().addDocumentListener(new DocumentListener() {
			String oldText = fileTextField.getText();
			
			@Override
			public void removeUpdate(final DocumentEvent e) {
				fire();
			}
			
			@Override
			public void insertUpdate(final DocumentEvent e) {
				fire();
			}
			
			@Override
			public void changedUpdate(final DocumentEvent e) {
				fire();
			}
			
			private void fire() {
				firePropertyChange("filename", oldText, oldText = fileTextField.getText());
			}
		});
		
		final Component rigidArea = Box.createRigidArea(new Dimension(5, 0));
		add(rigidArea);
		
		btnNewButton = new JButton("Browse");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (fileChooser == null) {
					fileChooser = new JFileChooser();
				}
				
				final Preferences prefs = Preferences.userRoot().node("app");
				final String lastCwd = prefs.get(MainFrame.PREF_LAST_CWD, null);
				
				final String text = fileTextField.getText();
				if (text.isEmpty()) {
					fileChooser.setCurrentDirectory(new File(lastCwd == null ? System.getProperty("user.dir") : lastCwd));
					fileChooser.setSelectedFile(new File(""));
				} else {
					final File file = new File(text);
					fileChooser.setCurrentDirectory(file.getParentFile() == null ? new File(System.getProperty("user.dir")) : file.getParentFile());
					fileChooser.setSelectedFile(file);
				}
				
				final Type type = configure(fileChooser);
				if ((type == Type.OPEN ? fileChooser.showOpenDialog(FileSelector.this) : fileChooser.showSaveDialog(FileSelector.this)) == JFileChooser.APPROVE_OPTION) {
					prefs.put(MainFrame.PREF_LAST_CWD, fileChooser.getSelectedFile().getParent());
					setFilename(transformFilename(fileChooser.getSelectedFile().toString()));
				}
			}
		});
		add(btnNewButton);

	}

	protected abstract Type configure(final JFileChooser gpxFileChooser);

	protected String transformFilename(final String filename) {
		return filename;
	}

	public String getFilename() {
		return fileTextField.getText();
	}
	
	public void setFilename(final String filename) {
		fileTextField.setText(filename);
	}
	
	@Override
	public void setToolTipText(final String text) {
		fileTextField.setToolTipText(text);
		btnNewButton.setToolTipText(text);
	}
	
	@Override
	public String getToolTipText() {
		return fileTextField.getToolTipText();
	}

}
