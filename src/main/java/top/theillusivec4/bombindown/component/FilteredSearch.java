package top.theillusivec4.bombindown.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import top.theillusivec4.bombindown.util.video.Filter;

public class FilteredSearch extends JPanel {

  private final List<BiConsumer<Filter, String>> listeners = new ArrayList<>();
  private final JFrame owner;

  private PlaceHolderTextField searchBar;
  private JButton filterButton;
  private FilterDialog filterDialog;

  public FilteredSearch(JFrame owner) {
    super();
    this.owner = owner;
    this.initComponents();
  }

  private void initComponents() {
    this.setLayout(new GridBagLayout());
    this.createSearchBar();
  }

  private void createSearchBar() {
    GridBagConstraints gc;

    this.filterDialog = new FilterDialog(this.owner, "Filter");

    this.filterButton = new JButton("Filter");
    this.filterButton.addActionListener(e -> this.filterDialog.setVisible(true));
    gc = new GridBagConstraints();
    gc.insets = new Insets(5, 0, 10, 5);
    this.add(this.filterButton, gc);

    this.searchBar = new PlaceHolderTextField("Search...", 25);
    this.searchBar.addActionListener(e -> {
      for (BiConsumer<Filter, String> listener : this.listeners) {
        listener.accept(this.filterDialog.getFilter(), this.searchBar.getText());
      }
    });
    gc = new GridBagConstraints();
    gc.insets = new Insets(5, 0, 10, 5);
    this.add(this.searchBar, gc);
  }

  public void addSearchListener(BiConsumer<Filter, String> listener) {
    this.listeners.add(listener);
  }
}
