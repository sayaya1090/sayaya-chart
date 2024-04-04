package net.sayaya.ui.chart;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import net.sayaya.ui.chart.column.ColumnBuilder;
import net.sayaya.ui.dom.MdSelectOptionElement;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.Date;
import java.util.function.Function;

import static org.jboss.elemento.Elements.div;

public class Test implements EntryPoint {
	private final HTMLContainerBuilder<HTMLDivElement> content = div();
	@Override
	public void onModuleLoad() {
		LayoutTest();
		TestSheet();
		TestMergeCell();
		TestColumnNumber();
		TestColumnDate();
		TestColumnText();
		TestColumnLink();
		TestColumnDropDown();
		TestColumnChip();
		TestMouseEvent();
	}

	void LayoutTest() {
		// TopBarElement.TopBarButton menu = TopBarElement.buttonNavigation("menu");
		var div = div().add(content);
		Elements.body().add(div);
	}

	private void TestSheet() {
		SheetElement sheetElement = SheetElement.builder()
				.columns(
						ColumnBuilder.string("A").name("A").pattern("^a$").than("red", "yellow").color("blue").build(),
						ColumnBuilder.number("B").format(NumberFormat.getFormat("0.00")).build(),
						ColumnBuilder.checkbox("C").isTrue().than("red", "yellow").isFalse().than("blue", "green").build())
				.stretchH("all")
				.build();
		content.add(sheetElement);
		var k = Data.create("1").put("A", "FFF");
		k.onValueChange(d->k.put("B", "44444.345345345"));
		sheetElement.values(
				k,
				Data.create("2").put("A", "a"),
				Data.create("3"));
		SheetElementSelectableMulti wrapper = SheetElementSelectableMulti.wrap(sheetElement);
		Scheduler.get().scheduleFixedDelay(()->{
			sheetElement.values(
					Data.create("1").put("A", "FFF"),
					Data.create("2").put("A", "b"),
					Data.create("3"));
			return false;
		}, 5000);
	}

	private void TestMergeCell() {
		SheetElement sheetElement = SheetElement.builder()
				.columns(
						ColumnBuilder.string("A").name("A").pattern("^a$").than("red", "yellow").color("blue").build(),
						ColumnBuilder.string("B").build(),
						ColumnBuilder.checkbox("C").build()
				).mergeCells(new MergeCell[]{new MergeCell().col(1).row(0).colspan(2).rowspan(3)})
				.stretchH("all")
				.build();
		SheetElementSelectableSingle.header(sheetElement);
		content.add(sheetElement);
		sheetElement.values(
				Data.create("1").put("A", "FFF"),
				Data.create("2").put("A", "a"),
				Data.create("3"));
	}
	private void TestColumnNumber() {
		SheetElement sheetElement = SheetElement.builder()
				.columns(
						ColumnBuilder.string("A").name("A").pattern("^a$").than("red", "yellow").color("blue").build(),
						ColumnBuilder.number("B").format(NumberFormat.getFormat("0.00")).horizontal("right").lt(100).than("red", "yellow").build(),
						ColumnBuilder.number("C").readOnly(true).format(NumberFormat.getFormat("0.00")).horizontal("right").gt(100).than("red", "yellow").build())
				.stretchH("all")
				.build();
		SheetElementSelectableSingle.header(sheetElement);
		content.add(sheetElement);
		sheetElement.values(
				Data.create("1").put("A", "FFF"),
				Data.create("2").put("A", "a"),
				Data.create("3"));
	}
	private void TestColumnDate() {
		SheetElement sheetElement = SheetElement.builder()
				.columns(
						ColumnBuilder.string("A").name("A").pattern("^a$").than("red", "yellow").color("blue").build(),
						ColumnBuilder.date("B").width(300).format(DateTimeFormat.getFormat("MMM-dd-yyyy")).horizontal("center").lt(new Date()).than("red", "yellow").build(),
						ColumnBuilder.date("C").readOnly(true).format(DateTimeFormat.getFormat("yy-MMM")).horizontal("center").gt(new Date()).than("red", "yellow").build())
				.stretchH("all")
				.build();
		SheetElementSelectableSingle.header(sheetElement);
		content.add(sheetElement);
		sheetElement.values(
				Data.create("1").put("A", "FFF"),
				Data.create("2").put("A", "a"),
				Data.create("3"));
	}
	private void TestColumnText() {
		SheetElement sheetElement = SheetElement.builder()
				.columns(
						ColumnBuilder.string("A").name("A").pattern("^a$").than("red","yellow").color("blue").build(),
						ColumnBuilder.text("B", 30, 100).horizontal("right").pattern("^a").than("red", "yellow").build(),
						ColumnBuilder.checkbox("C").build())
				.stretchH("all")
				.build();
		SheetElementSelectableSingle.header(sheetElement);
		content.add(sheetElement);
		sheetElement.values(
				Data.create("1").put("A", "FFF"),
				Data.create("2").put("A", "a"),
				Data.create("3"));
	}
	private void TestColumnLink() {
		Function<Data, String> m = data->"https://google.co.kr";
		SheetElement sheetElement = SheetElement.builder()
				.columns(
						ColumnBuilder.string("A").name("A").pattern("^a$").than("red","yellow").color("blue").build(),
						ColumnBuilder.link("B", m).target("self").pattern("^a").than("red", "yellow").build(),
						ColumnBuilder.checkbox("C").build())
				.stretchH("all")
				.build();
		SheetElementSelectableSingle.header(sheetElement);
		content.add(sheetElement);
		sheetElement.values(
				Data.create("1").put("A", "FFF").put("B", "GOOGLE"),
				Data.create("2").put("A", "a"),
				Data.create("3"));
	}
	private final MdSelectOptionElement dropdownItem(String label) {
		MdSelectOptionElement item = new MdSelectOptionElement();
		item.value = label;
		item.textContent = label;
		return item;
	}
	private void TestColumnDropDown() {
		SheetElement sheetElement = SheetElement.builder()
				.columns(
						ColumnBuilder.string("A").name("A").pattern("^a$").than("red","yellow").color("blue").build(),
						ColumnBuilder.text("B", 30, 100).horizontal("right").pattern("^a").than("red", "yellow").build(),
						ColumnBuilder.dropdown("C",
								dropdownItem("A"),
								dropdownItem("B"),
								dropdownItem("C"),
								dropdownItem("D")).name("C").build())
				.stretchH("all")
				.build();
		SheetElementSelectableSingle.header(sheetElement);
		content.add(sheetElement);
		sheetElement.values(
				Data.create("1").put("A", "FFF"),
				Data.create("2").put("A", "a"),
				Data.create("3"));
	}
	private void TestColumnChip() {
		SheetElement sheetElement = SheetElement.builder()
				.columns(
						ColumnBuilder.text("tmp", 100, 100).build(),
						ColumnBuilder.chip("A").width(9).name("A").pattern("^a$").than("red","yellow").color("blue").build(),
						ColumnBuilder.chip("B").width(3).vertical("center").horizontal("right").pattern("ccc").than("red","yellow").vertical("center").readOnly(true).build(),
						ColumnBuilder.chip("C").width(3).vertical("bottom").horizontal("left").build())
				.stretchH("all")
				.build();
		SheetElementSelectableSingle.header(sheetElement);
		content.add(sheetElement);
		sheetElement.values(
				Data.create("1").put("A", "FFF").put("C", "FFF,ddd,eee,ccc=f44,ddd\\,eeew"),
				Data.create("2").put("A", "a").put("B", "FFF,ddd,eee,ccc=f44,ddd\\,eeew"),
				Data.create("3"));
	}
	private void TestMouseEvent() {
		SheetElement sheetElement = SheetElement.builder()
												.columns(
														ColumnBuilder.string("A").name("A").pattern("^a$").than("red", "yellow").color("blue").build(),
														ColumnBuilder.string("B").build(),
														ColumnBuilder.checkbox("C").build()
												).mergeCells(new MergeCell[]{new MergeCell().col(1).row(0).colspan(2).rowspan(3)})
												.stretchH("all").afterOnCellContextMenu((evt, coords, td)->{
					DomGlobal.console.log(evt.button + ", " + coords.row() + ", " + coords.col() + ", " + td);
				}).afterOnCellMouseDown((evt, coords, td)->{
					DomGlobal.console.log(evt.button + ", " + coords.row() + ", " + coords.col() + ", " + td);
				}).build();
		SheetElementSelectableSingle.header(sheetElement);
		content.add(sheetElement);
		sheetElement.values(
				Data.create("1").put("A", "FFF"),
				Data.create("2").put("A", "a"),
				Data.create("3"));
	}
}
