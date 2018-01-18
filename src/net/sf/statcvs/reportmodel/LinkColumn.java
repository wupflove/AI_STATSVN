package net.sf.statcvs.reportmodel;

import java.util.ArrayList;
import java.util.List;

import net.sf.statcvs.renderer.TableCellRenderer;

public class LinkColumn extends Column {
    private final String title;
    private final List urls = new ArrayList();
    private final List labels = new ArrayList();
    private String total = null;

    public LinkColumn(final String title) {
        this.title = title;
    }

    public void setTotal(final String value) {
        this.total = value;
    }

    public void addValue(final String url, final String label) {
        if (url == null) {
            this.urls.add("");
        } else {
            this.urls.add(url);
        }
        this.labels.add(label);
    }

    public int getRows() {
        return urls.size();
    }

    public void renderHead(final TableCellRenderer renderer) {
        renderer.renderCell(title);
    }

    public void renderCell(final int rowIndex, final TableCellRenderer renderer) {
        final String url = (String) this.urls.get(rowIndex);
        renderer.renderLinkCell(("".equals(url) ? null : url), (String) this.labels.get(rowIndex));
    }

    public void renderTotal(final TableCellRenderer renderer) {
        if (total == null) {
            renderer.renderEmptyCell();
        } else {
            renderer.renderCell(total);
        }
    }
}
