package net.sayaya.ui.chart;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import elemental2.dom.*;
import net.sayaya.ui.DrawerElement;
import net.sayaya.ui.IconElement;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.TopBarElement;
import net.sayaya.ui.chart.column.ColumnBuilder;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.Date;

import static org.jboss.elemento.Elements.*;

public class Test implements EntryPoint {
	private final HtmlContentBuilder<HTMLDivElement> content = div();
	@Override
	public void onModuleLoad() {
		LayoutTest();
		TestSheet();
		TestMergeCell();
		TestColumnNumber();
		TestColumnDate();
		TestColumnText();
		TestColumnDropDown();
	}

	void LayoutTest() {
		TopBarElement.TopBarButton menu = TopBarElement.buttonNavigation("menu");
		HtmlContentBuilder<HTMLDivElement> div = div().add(content);
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
		SheetElementSelectableSingle.header(sheetElement);
		content.add(sheetElement);
		sheetElement.values(
				new Data("1").put("A", "FFF"),
				new Data("2").put("A", "a"),
				new Data("3"));
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
				new Data("1").put("A", "FFF"),
				new Data("2").put("A", "a"),
				new Data("3"));
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
				new Data("1").put("A", "FFF"),
				new Data("2").put("A", "a"),
				new Data("3"));
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
				new Data("1").put("A", "FFF"),
				new Data("2").put("A", "a"),
				new Data("3"));
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
				new Data("1").put("A", "FFF"),
				new Data("2").put("A", "a"),
				new Data("3"));
	}
	private void TestColumnDropDown() {
		SheetElement sheetElement = SheetElement.builder()
				.columns(
						ColumnBuilder.string("A").name("A").pattern("^a$").than("red","yellow").color("blue").build(),
						ColumnBuilder.text("B", 30, 100).horizontal("right").pattern("^a").than("red", "yellow").build(),
						ColumnBuilder.dropdown("C", ListElement.singleLineList()
								.add(ListElement.singleLine().label("A"))
								.add(ListElement.singleLine().label("B"))
								.add(ListElement.singleLine().label("C"))
								.add(ListElement.singleLine().label("D"))).build())
				.stretchH("all")
				.build();
		SheetElementSelectableSingle.header(sheetElement);
		content.add(sheetElement);
		sheetElement.values(
				new Data("1").put("A", "FFF"),
				new Data("2").put("A", "a"),
				new Data("3"));
	}
}
