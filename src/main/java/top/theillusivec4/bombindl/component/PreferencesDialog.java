/*
 * Copyright (C) 2023 C4
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.bombindl.component;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import top.theillusivec4.bombindl.BombinDownloader;
import top.theillusivec4.bombindl.data.UserPrefs;
import top.theillusivec4.bombindl.util.Constants;

public class PreferencesDialog extends JDialog {

  private final JFrame parent;

  private JLabel qualityLabel;
  private JComboBox<String> qualityBox;
  private JLabel directoryLabel;
  private JTextField directoryField;
  private JButton directoryButton;
  private JLabel formatLabel;
  private JLabel formatDescription;
  private JTextField formatField;
  private JLabel metadataLabel;
  private JCheckBox metadataBox;
  private JLabel imagesLabel;
  private JCheckBox imagesBox;
  private JLabel maxDownloadsLabel;
  private JSpinner maxDownloadsSpinner;

  private JButton save;
  private JButton cancel;

  public PreferencesDialog(JFrame parent) {
    super(parent, "Preferences");
    this.parent = parent;
    this.initComponents();
  }

  private void initComponents() {
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.setModal(true);
    this.setLayout(new GridBagLayout());
    ((JPanel) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

    GridBagConstraints gc = new GridBagConstraints();
    this.qualityLabel = new JLabel("Download quality: ");
    this.qualityLabel.getAccessibleContext()
        .setAccessibleDescription("The quality to download when not specified.");
    gc.anchor = GridBagConstraints.LINE_END;
    this.add(this.qualityLabel, gc);

    this.qualityBox = new JComboBox<>(
        Arrays.stream(Constants.VideoQuality.values()).map(Constants.VideoQuality::getText)
            .toArray(String[]::new));
    this.qualityBox.setSelectedItem(UserPrefs.INSTANCE.getQuality().getText());
    gc = new GridBagConstraints();
    gc.gridx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.qualityBox, gc);

    this.directoryLabel = new JLabel("Download to: ");
    this.directoryLabel.getAccessibleContext()
        .setAccessibleDescription("The default directory to download files to when not specified.");
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.anchor = GridBagConstraints.LINE_END;
    this.add(this.directoryLabel, gc);

    File directory = UserPrefs.INSTANCE.getDownloadDirectory();
    this.directoryField = new JTextField(directory.toString(), 30);
    gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.gridy = 1;
    gc.gridx = 1;
    gc.gridwidth = 3;
    gc.weightx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.directoryField, gc);

    this.directoryButton = new JButton("...");
    this.directoryButton.addActionListener(e -> {
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(directory);
      chooser.setDialogTitle("Select the download directory");
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      chooser.setAcceptAllFileFilterUsed(false);

      if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        this.directoryField.setText(chooser.getSelectedFile().getAbsolutePath());
      }
    });
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.gridx = 4;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.directoryButton, gc);

    this.formatDescription = new JLabel(
        "<html>Possible template fields: <br>" +
            " {guid} - The unique ID of the file<br>" +
            " {title} - The title of the file<br>" +
            " {year} - The year the file was published<br>" +
            " {month} - The month the file was published<br>" +
            " {day} - The day of the month the file was published</html>");
    this.formatDescription.setFont(this.formatDescription.getFont().deriveFont(Font.ITALIC));
    gc = new GridBagConstraints();
    gc.gridy = 2;
    gc.gridx = 1;
    gc.gridwidth = 4;
    gc.weightx = 1;
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.anchor = GridBagConstraints.LINE_END;
    gc.insets = new Insets(10, 0, 10, 0);
    this.add(this.formatDescription, gc);

    this.formatLabel = new JLabel("File output name template: ");
    gc = new GridBagConstraints();
    gc.gridy = 3;
    gc.anchor = GridBagConstraints.LINE_END;
    this.add(this.formatLabel, gc);

    this.formatField = new JTextField();
    this.formatField.setText(UserPrefs.INSTANCE.getFileOutputTemplate());
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.gridx = 1;
    gc.gridwidth = 3;
    gc.weightx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.formatField, gc);

    this.metadataLabel = new JLabel("Include metadata: ");
    this.metadataLabel.getAccessibleContext()
        .setAccessibleDescription(
            "Check to download available metadata along with videos and shows.");
    gc = new GridBagConstraints();
    gc.gridy = 4;
    gc.anchor = GridBagConstraints.LINE_END;
    this.add(this.metadataLabel, gc);

    this.metadataBox = new JCheckBox();
    this.metadataBox.setSelected(UserPrefs.INSTANCE.isIncludeMetadata());
    gc = new GridBagConstraints();
    gc.gridy = 4;
    gc.gridx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.metadataBox, gc);

    this.imagesLabel = new JLabel("Include images: ");
    this.imagesLabel.getAccessibleContext()
        .setAccessibleDescription(
            "Check to download thumbnails and logos along with videos and shows.");
    gc = new GridBagConstraints();
    gc.gridy = 5;
    gc.anchor = GridBagConstraints.LINE_END;
    this.add(this.imagesLabel, gc);

    this.imagesBox = new JCheckBox();
    this.imagesBox.setSelected(UserPrefs.INSTANCE.isIncludeMetadata());
    gc = new GridBagConstraints();
    gc.gridy = 5;
    gc.gridx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.imagesBox, gc);

    this.maxDownloadsLabel = new JLabel("Maximum simultaneous downloads: ");
    gc = new GridBagConstraints();
    gc.gridy = 6;
    gc.anchor = GridBagConstraints.LINE_END;
    this.add(this.maxDownloadsLabel, gc);
    SpinnerNumberModel model =
        new SpinnerNumberModel(UserPrefs.INSTANCE.getMaxDownloads(), 1, 20, 1);
    this.maxDownloadsSpinner = new JSpinner(model);
    gc = new GridBagConstraints();
    gc.gridx = 1;
    gc.gridy = 6;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.maxDownloadsSpinner, gc);

    this.save = new JButton("Save");
    this.save.addActionListener(e -> {
      UserPrefs.INSTANCE.setFileOutputTemplate(this.formatField.getText());
      UserPrefs.INSTANCE.setQuality(
          Constants.VideoQuality.of((String) this.qualityBox.getSelectedItem()));
      UserPrefs.INSTANCE.setDownloadDirectory(new File(this.directoryField.getText()));
      int max = model.getNumber().intValue();
      UserPrefs.INSTANCE.setMaxDownloads(max);
      BombinDownloader.setMaxDownloads(max);
      UserPrefs.INSTANCE.setIncludeImages(this.imagesBox.isSelected());
      UserPrefs.INSTANCE.setIncludeMetadata(this.metadataBox.isSelected());
      UserPrefs.INSTANCE.save();
      this.dispose();
    });
    gc = new GridBagConstraints();
    gc.gridy = 8;
    gc.gridx = 3;
    gc.anchor = GridBagConstraints.LINE_END;
    gc.insets = new Insets(20, 0, 10, 0);
    this.add(this.save, gc);

    this.cancel = new JButton("Cancel");
    this.cancel.addActionListener(e -> this.dispose());
    gc.gridx = 5;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.cancel, gc);

    this.pack();
    this.setLocationRelativeTo(null);
  }
}
