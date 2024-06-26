package net.sayaya.ui.chart;

import com.google.gwt.core.client.Scheduler;
import elemental2.core.JsArray;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import jsinterop.annotations.*;
import jsinterop.base.Js;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.sayaya.ui.chart.function.AfterGetColumnHeaderRenderers;
import net.sayaya.ui.chart.function.AfterGetRowHeaderRenderers;
import net.sayaya.ui.chart.function.MouseEventHandler;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.HTMLElementBuilder;

import java.util.Arrays;

import static org.jboss.elemento.Elements.div;

public class SheetElement extends HTMLElementBuilder<HTMLDivElement> {
	public static SheetConfiguration builder() {
		return new SheetConfiguration();
	}
	private final Handsontable table;
	private final SheetConfiguration configuration;
	SheetElement(SheetConfiguration setting) {
		this(div(), setting);
	}
	private SheetElement(HTMLContainerBuilder<HTMLDivElement> e, SheetConfiguration setting) {
		super(e.element());
		this.configuration = setting;
		table = new Handsontable(e.element(), setting);
		Js.asPropertyMap(table).set("spreadsheet", this);
	}
	public Data[] values() {
		Data[] data = configuration.data;
		if(data == null) return new Data[]{};
		else return data;
	}
	public SheetElement values(Data... data) {
		configuration.data(data);
		Scheduler.get().scheduleDeferred(()->table.updateSettings(configuration));
		return that();
	}
	public SheetElement append(Data data) {
		configuration.append(data);
		Scheduler.get().scheduleDeferred(()->table.updateSettings(configuration));
		return that();
	}
	public SheetElement prepend(Data data) {
		configuration.prepend(data);
		Scheduler.get().scheduleDeferred(()->table.updateSettings(configuration));
		return that();
	}
	public SheetElement delete(String id) {
		configuration.delete(id);
		Scheduler.get().scheduleDeferred(()->table.updateSettings(configuration));
		return that();
	}
	public SheetElement refresh() {
		table.render();
		return this;
	}
	public SheetElement selectRow(int start) {
		table.selectRows(start);
		return this;
	}
	public SheetElement selectRows(int start, int end) {
		table.selectRows(start, end);
		return this;
	}
	public Column[] columns() {
		return table.getSettings().columns;
	}
	public Handsontable table() {
		return table;
	}
	SheetConfiguration configuration() {
		return configuration;
	}
	@Override
	public SheetElement that() {
		return this;
	}

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name="Handsontable")
	public final static class Handsontable {
		@JsProperty(name="spreadsheet")
		public SheetElement spreadsheet;
		@JsConstructor
		public Handsontable(Element element, SheetConfiguration setting) {}
		public native void render();
		public native void updateSettings(SheetConfiguration setting);
		public native SheetConfiguration getSettings();
		public native int countRows();
		public native int countCols();
		public native boolean selectColumns(int start, int end);
		public native boolean selectColumns(int start);
		public native boolean selectRows(int start, int end);
		public native boolean selectRows(int start);
		public native boolean selectCell(int row, int column);
		public native Element getCell(int row, int col, boolean topmost);
		public native void setDataAtCell(int row, int col, Object value);
		public native void alter(String action, int idex, int amount);
	}

	@JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
	@Setter(onMethod_={@JsOverlay, @JsIgnore})
	@Getter(onMethod_={@JsOverlay, @JsIgnore})
	@Accessors(fluent=true)
	public final static class SheetConfiguration {
		private Data[] data;
		private String stretchH;
		private Integer width;
		private Object height;
		private Integer minRows;
		private Integer maxRows;
		private Integer fixedRowsTop;
		private Integer fixedColumnsLeft;
		private Object rowHeaders;
		private Object rowHeaderWidth;
		private boolean manualRowResize;
		private boolean manualColumnResize;
		private boolean manualRowMove;
		private boolean manualColumnMove;
		private Boolean renderAllRows;
		private Double viewportColumnRenderingOffset;
		private Object contextMenu;
		private boolean autoRowSize;
		private boolean autoColSize;
		private Column[] columns;
		private Object colHeaders;
		private boolean formulas;
		private String preventOverflow;
		private boolean disableVisualSelection;
		private Change beforeChange;
		private Object rowHeights;
		private Object colWidths;
		private MergeCell[] mergeCells;
		// Events
		private AfterGetColumnHeaderRenderers afterGetColumnHeaderRenderers;
		private AfterGetRowHeaderRenderers afterGetRowHeaderRenderers;
		private MouseEventHandler afterOnCellMouseDown;
		private MouseEventHandler afterOnCellMouseOver;
		private MouseEventHandler afterOnCellMouseUp;
		private MouseEventHandler afterOnCellContextMenu;

		@JsConstructor
		public SheetConfiguration() {}
		@JsFunction
		interface HeaderRenderFn {
			String render(int n);
		}
		@JsFunction
		interface Change {
			boolean change(Object info, String source);
		}
		@JsOverlay
		public SheetConfiguration rowHeaders(boolean show) {
			this.rowHeaders = show;
			return this;
		}
		@JsOverlay
		public SheetConfiguration rowHeaders(HeaderRenderFn func) {
			this.rowHeaders = func;
			return this;
		}
		@JsOverlay
		public SheetConfiguration rowHeaderWidth(int rowHeaderWidth) {
			this.rowHeaderWidth = rowHeaderWidth + 0.0;
			return this;
		}
		@JsOverlay
		public SheetConfiguration rowHeaderWidth(int... rowHeaderWidth) {
			this.rowHeaderWidth = Arrays.stream(rowHeaderWidth).mapToDouble(i->i+0.0).toArray();
			return this;
		}
		@JsOverlay
		public SheetConfiguration columns(Column... columns) {
			this.columns = columns;
			colHeaders = (HeaderRenderFn) n->columns[n].headerRenderer().render(n).innerHTML;
			return this;
		}
		@JsOverlay
		public SheetConfiguration preventOverflowX() {
			this.preventOverflow = "horizontal";
			return this;
		}
		@JsOverlay
		public SheetConfiguration heightAuto() {
			this.height = "auto";
			return this;
		}
		@JsOverlay
		public SheetConfiguration append(Data item) {
			if(data == null) data = new Data[] {};
			JsArray.asJsArray(data).push(item);
			return this;
		}
		@JsOverlay
		public SheetConfiguration prepend(Data item) {
			if(data == null) data = new Data[] {};
			JsArray.asJsArray(data).unshift(item);
			return this;
		}
		@JsOverlay
		public SheetConfiguration delete(String id) {
			if(data == null) return this;
			var arr = JsArray.asJsArray(data);
			arr.forEach((d, i)->{
				if(id.equals(d.idx())) arr.splice(i, 1);
				return null;
			});
			return this;
		}
		@JsOverlay
		public SheetElement build() {
			return new SheetElement(this);
		}
	}
}


