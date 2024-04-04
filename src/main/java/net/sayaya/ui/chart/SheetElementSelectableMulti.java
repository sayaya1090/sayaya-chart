package net.sayaya.ui.chart;

import com.google.gwt.user.client.Random;
import elemental2.core.JsArray;
import elemental2.dom.*;
import net.sayaya.ui.chart.event.HasSelectionChangeHandlers;
import net.sayaya.ui.chart.function.ColumnHeaderRenderer;
import org.gwtproject.event.shared.HandlerRegistration;

import java.util.Arrays;

import static org.jboss.elemento.EventType.bind;

public interface SheetElementSelectableMulti extends HasSelectionChangeHandlers<Data[]> {
	Data[] value();
	@Override
	default Data[] selection() {
		return Arrays.stream(value()).filter(d->d.state() == Data.DataState.SELECTED).toArray(Data[]::new);
	}
	static SheetElementSelectableMulti wrap(SheetElement sheetElement) {
		SheetElementSelectableMulti wrapper = new SheetElementSelectableMulti() {
			@Override
			public HandlerRegistration onSelectionChange(SelectionChangeEventListener<Data[]> listener) {
				EventListener wrapper = evt->listener.handle(SelectionChangeEvent.event(evt, selection()));
				return bind(sheetElement.element(), "selection-change", wrapper);
			}
			@Override
			public Data[] value() {
				return sheetElement.values();
			}
		};
		SheetElementSelectableMulti.header(sheetElement);
		return wrapper;
	}
	static void header(SheetElement sheetElement) {
		SheetElement.SheetConfiguration config = sheetElement.configuration();
		config.afterGetColumnHeaderRenderers(renderers -> {
			ColumnHeaderRenderer defaultRenderer = renderers[0];
			ColumnHeaderRenderer proxy = (row, TH) -> {
				if (row == -1) {
					TH.innerHTML = "<div class='relative'><input class='select-all-header-checkbox' type='checkbox' style='vertical-align: middle; margin: 0px;'/></div>";
				} else defaultRenderer.accept(row, TH);
				return TH;
			};
			renderers[0] = proxy;
		}).afterGetRowHeaderRenderers(renderers->{
			Data[] data = config.data();
			String id = "Sheet-row-select-" + Random.nextInt();
			JsArray.asJsArray(renderers).push((row, TH)->{
				if (data[row] == null) return;
				boolean checked = data[row].state() == Data.DataState.SELECTED;
				TH.classList.add("row-header-checkbox");
				TH.style.cursor = "pointer";
				TH.innerHTML = "<input name='" + id + "' " +
									   "idx='" + data[row].idx() + "' " + (checked ? "checked " : "") +
									   "type='checkbox' style='vertical-align: middle; margin: 0px;'/>";
			});
		}).rowHeaderWidth(30);
		sheetElement.element().addEventListener("click", evt->{
			HTMLElement target = (HTMLElement) evt.target;
			if(target.classList.contains("row-header-checkbox")) {
				HTMLTableCellElement th = (HTMLTableCellElement)target;
				HTMLInputElement check = (HTMLInputElement) th.firstElementChild;
				String idx = check.getAttribute("idx");
				Arrays.stream(config.data()).filter(d->idx.equals(d.idx())).findAny().get().select(check.checked);
				sheetElement.element().dispatchEvent(new CustomEvent("selection-change"));
			} else if(target.parentElement!=null && target.parentElement.classList.contains("row-header-checkbox")) {
				HTMLInputElement check = (HTMLInputElement) target;
				String idx = check.getAttribute("idx");
				Arrays.stream(config.data()).filter(d->idx.equals(d.idx())).findAny().get().select(check.checked);
				sheetElement.element().dispatchEvent(new CustomEvent("selection-change"));
			} else if (target.classList.contains("select-all-header-checkbox")) {
				HTMLInputElement check = (HTMLInputElement) target;
				Arrays.stream(config.data()).forEach(d -> d.select(check.checked));
				sheetElement.element().getElementsByClassName("row-header-checkbox").asList().forEach(e2 -> ((HTMLInputElement) e2.firstElementChild).checked = check.checked);
				sheetElement.element().dispatchEvent(new CustomEvent("selection-change"));
			}
		});
	}
	@Override
	default HandlerRegistration onSelectionChange(EventTarget dom, SelectionChangeEventListener<Data[]> listener) {
		EventListener wrapper = evt->listener.handle(SelectionChangeEvent.event(evt, selection()));
		return bind(dom, "selection-change", wrapper);
	}
}
