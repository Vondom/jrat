package org.shiftone.jrat.provider.tree.ui.trace;


import org.shiftone.jrat.core.MethodKey;
import org.shiftone.jrat.provider.tree.ui.TraceTreeNode;
import org.shiftone.jrat.ui.util.DotIcon;
import org.shiftone.jrat.util.log.Logger;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;


/**
 * Class StackTreeCellRenderer
 *
 * @author jeff@shiftone.org (Jeff Drost)
 */
public class StackTreeCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {

    private static final Logger LOG = Logger.getLogger(StackTreeCellRenderer.class);
    private PercentColorLookup colorLookup = new PercentColorLookup();
    private static Icon ICON_ROOT = new DotIcon(8, Color.darkGray);
    private static Icon ICON_ROOT2 = new DotIcon(8, Color.lightGray);
    private TraceTreeNode treeNode = null;
    private DecimalFormat pctDecimalFormat = new DecimalFormat("#,###.#'%'");
    private DecimalFormat msDecimalFormat = new DecimalFormat("#,###,###.##'ms'");

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {

        this.treeNode = (TraceTreeNode) value;
        this.hasFocus = hasFocus;
        this.selected = sel;

        double w = treeNode.getPctOfAvgParentDuration();
        Icon icon = null;

        if (treeNode.isRootNode()) {
            icon = ICON_ROOT;
        } else if (treeNode.getDepth() == 1) {
            icon = ICON_ROOT2;
        } else {
            icon = colorLookup.getIcon(w);
        }

        setText(nodeText(sel));

        if (selected) {
            setForeground(Color.white);
        } else {
            setForeground(Color.black);
        }

        if (!tree.isEnabled()) {
            setEnabled(false);
            setDisabledIcon(icon);
        } else {
            setEnabled(true);
            setIcon(icon);
        }

        setComponentOrientation(tree.getComponentOrientation());

        return this;
    }


    public String nodeText(boolean selected) {

        String result;

        if (treeNode.isRootNode()) {
            result = "Root";
        } else {
            MethodKey methodKey = treeNode.getMethodKey();
            String methodName = methodKey.getMethodName();

            if (treeNode.getDepth() == 1) {
                Double avg = treeNode.getAverageDuration();

                result = methodName + ((avg == null)
                        ? " - never exited"
                        : (" - " + msDecimalFormat.format(treeNode.getAverageDuration())));
            } else {
                double pct = treeNode.getPctOfAvgRootDuration();

                if (pct > 0.09) {
                    result = methodName + " - " + pctDecimalFormat.format(pct);
                } else {
                    result = methodName;
                }
            }
        }

        return result;    // + " " + treeNode.getMaxDepth();
    }
}