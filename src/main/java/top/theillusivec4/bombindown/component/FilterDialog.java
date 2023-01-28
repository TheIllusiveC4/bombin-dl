package top.theillusivec4.bombindown.component;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import top.theillusivec4.bombindown.util.Filter;

public class FilterDialog extends JDialog {

  private final Filter filter = new Filter();

  private JLabel membershipLabel;
  private JComboBox<String> membershipBox;
  private JLabel fromDateLabel;
  private DatePicker fromDate;
  private JLabel untilDateLabel;
  private DatePicker untilDate;

  public FilterDialog(JFrame owner, String title) {
    super(owner, title);
    this.initComponents();
  }

  private void initComponents() {
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setBounds(0, 0, 400, 200);
    this.setLocationRelativeTo(null);
    this.setLayout(new GridBagLayout());

    GridBagConstraints gc;

    this.membershipLabel = new JLabel("Membership: ");
    gc = new GridBagConstraints();
    gc.anchor = GridBagConstraints.LINE_END;
    this.add(this.membershipLabel, gc);

    this.membershipBox = new JComboBox<>(new String[] {"-", "Free", "Premium"});
    this.membershipBox.setSelectedItem(filter.membership);
    this.membershipBox.addActionListener(
        e -> this.filter.membership = (String) this.membershipBox.getSelectedItem());
    gc = new GridBagConstraints();
    gc.gridx = 1;
    gc.insets = new Insets(0, 0, 5, 0);
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.membershipBox, gc);

    this.fromDateLabel = new JLabel("Start Date: ");
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.anchor = GridBagConstraints.LINE_END;
    this.add(this.fromDateLabel, gc);

    this.fromDate = new DatePicker();
    DatePickerSettings settings = new DatePickerSettings();
    this.fromDate.setSettings(settings);
    this.fromDate.addDateChangeListener(dateChangeEvent -> {
      LocalDate newDate = dateChangeEvent.getNewDate();

      if (newDate != null) {
        filter.fromDate = Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
      } else {
        filter.fromDate = null;
      }
    });
    settings.setVetoPolicy(localDate -> !localDate.isAfter(LocalDate.now()) &&
        (this.untilDate.getDate() == null || this.untilDate.getDate().isAfter(localDate)));
    gc = new GridBagConstraints();
    gc.gridy = 1;
    gc.gridx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    gc.insets = new Insets(0, 0, 5, 0);
    this.add(this.fromDate, gc);

    this.untilDateLabel = new JLabel("End Date: ");
    gc = new GridBagConstraints();
    gc.gridy = 2;
    gc.anchor = GridBagConstraints.LINE_END;
    this.add(this.untilDateLabel, gc);

    this.untilDate = new DatePicker();
    settings = new DatePickerSettings();
    this.untilDate.setSettings(settings);
    this.untilDate.addDateChangeListener(dateChangeEvent -> {
      LocalDate newDate = dateChangeEvent.getNewDate();

      if (newDate != null) {
        filter.untilDate = Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
      } else {
        filter.untilDate = null;
      }
    });
    settings.setVetoPolicy(localDate -> !localDate.isAfter(LocalDate.now()) &&
        (this.fromDate.getDate() == null || this.fromDate.getDate().isBefore(localDate)));
    gc = new GridBagConstraints();
    gc.gridy = 2;
    gc.gridx = 1;
    gc.anchor = GridBagConstraints.LINE_START;
    this.add(this.untilDate, gc);
  }

  public Filter getFilter() {
    return this.filter;
  }
}
