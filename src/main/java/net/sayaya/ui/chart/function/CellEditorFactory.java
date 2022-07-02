package net.sayaya.ui.chart.function;

import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTableCellElement;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import net.sayaya.ui.chart.SheetElement;

public class CellEditorFactory {
	public native static CellEditorBase base(Object prop, CellEditorBaseImpl proxy)/*-{
		var CustomEditor = $wnd.Handsontable.editors.BaseEditor.prototype.extend();
		CustomEditor.prototype.prepare=function(r, c, p, t, v, e){
		    proxy.prepare(r, c, p, t, v, e);
		}
		CustomEditor.prototype.setValue=function(e){
		    proxy.setValue(e);
		}
		CustomEditor.prototype.getValue=function() {
		    return proxy.getValue();
		}
		CustomEditor.prototype.open=function(evt) {
            proxy.open(this, evt);
		}
		CustomEditor.prototype.close=function() {
		    proxy.close();
		}
		CustomEditor.prototype.focus=function() {
		    proxy.focus();
		}
		return new CustomEditor(prop);
	}-*/;
	public native static CellEditorText text(Object prop, CellEditorTextImpl proxy)/*-{
		console.log("A")
		var CustomEditorText = $wnd.Handsontable.editors.TextEditor.prototype.extend();
		CustomEditorText.prototype.createElements = function() {
			$wnd.Handsontable.editors.TextEditor.prototype.createElements.apply(this, arguments);
			this.TEXTAREA = proxy.createElement();
			this.TEXTAREA.className += "handsontableInput";
			this.textareaStyle = this.TEXTAREA.style;
			this.textareaStyle.width = 0;
			this.textareaStyle.height = 0;
			$wnd.Handsontable.dom.empty(this.TEXTAREA_PARENT);
			this.TEXTAREA_PARENT.appendChild(this.TEXTAREA);
		}
		console.log("B")
		CustomEditorText.prototype.setValue=function(value){
			proxy.prepare(this.row, this.col, this.prop, this.TEXTAREA, value, this.cellProperties);
		    $wnd.Handsontable.editors.TextEditor.prototype.setValue.apply(this, arguments);
		    proxy.setValue(value);
		}
		console.log("C")
		CustomEditorText.prototype.getValue=function() {
		    return proxy.toValue($wnd.Handsontable.editors.TextEditor.prototype.getValue.apply(this, arguments));
		}
		console.log("D")
		return new CustomEditorText(prop);
	}-*/;
	@JsType(isNative = true)
	public interface CellEditorBaseImpl {
		void prepare(int row, int col, String prop, HTMLTableCellElement td, String value, Object cell);
		void setValue(String stringifiedInitialValue);
		String getValue();
		void open(Object e, Event event);
		void close();
		void focus();
	}
	@JsType(isNative = true)
	public interface CellEditorTextImpl {
		Element createElement();
		void initialize(Element element);
		void setValue(String stringfiedInitialValue);
		void prepare(int row, int col, String prop, HTMLElement td, String value, Object cell);
		String toValue(String value);
	}
	@JsType(isNative = true, namespace="Handsontable.editors", name="BaseEditor")
	public static abstract class CellEditorBase implements CellEditor {
		@JsProperty(name="instance") public SheetElement.Handsontable table;
		@JsProperty public int row;
		@JsProperty public int col;
		@JsProperty public String prop;
		@JsProperty(name="TD") public HTMLTableCellElement cell;
		CellEditorBase(Object prop){}
	}
	@JsType(isNative = true, namespace="Handsontable.editors", name="TextEditor")
	public static abstract class CellEditorText implements CellEditor {
		@JsProperty(name="instance") public SheetElement.Handsontable table;
		@JsProperty public int row;
		@JsProperty public int col;
		@JsProperty public String prop;
		@JsProperty(name="TD") public HTMLTableCellElement cell;
		CellEditorText(Object prop){}
	}
}
