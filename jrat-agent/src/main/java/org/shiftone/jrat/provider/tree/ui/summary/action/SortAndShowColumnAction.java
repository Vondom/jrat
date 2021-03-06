package org.shiftone.jrat.provider.tree.ui.summary.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SortOrder;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.shiftone.jrat.desktop.util.Table;

/**
 * @author (jeff@shiftone.org) Jeff Drost
 */
public class SortAndShowColumnAction extends AbstractAction {

    private final JXTable table;
    private final int index;

    public SortAndShowColumnAction(String string, JXTable table, Table.Column tableColumn) {
        super(string);
        this.table = table;
        this.index = tableColumn.getIndex();

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        TableColumnExt tableColumnExt = (TableColumnExt) table.getColumns(true).get(index);

        // not sure I want to do this
        tableColumnExt.setVisible(true);

        table.setSortOrder(tableColumnExt.getIdentifier(), SortOrder.DESCENDING);
        table.scrollRowToVisible(0);
    }
}
