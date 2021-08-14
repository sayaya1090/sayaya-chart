package net.sayaya.ui.chart;

import com.google.gwt.core.client.*;
import elemental2.dom.*;
import net.sayaya.ui.DrawerElement;
import net.sayaya.ui.IconElement;
import net.sayaya.ui.TopBarElement;
import net.sayaya.ui.chart.column.ColumnBuilder;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import static org.jboss.elemento.Elements.*;

public class Test implements EntryPoint {
	private final HtmlContentBuilder<HTMLDivElement> content = div();
	@Override
	public void onModuleLoad() {
		LayoutTest();
		TestSheet();
		TestMergeCell();
	}

	void LayoutTest() {
		TopBarElement.TopBarButton menu = TopBarElement.buttonNavigation("menu");
		TopBarElement top = TopBarElement.topBarFixed()
						   .add(TopBarElement.section().add(menu).title("Test Top Bar"))
						   .add(TopBarElement.section().right().add(TopBarElement.buttonAction("file_download")))
						   .target(content);
		HtmlContentBuilder<HTMLDivElement> div = div().add(top).add(content);
		DrawerElement drawer = DrawerElement.drawer().header(DrawerElement.header()
													 .title(label().add("Mail"))
													 .subtitle(label().add("AAA")))
							  .content(DrawerElement.content()
											 .header("Mail")
											 .divider()
											 .add(DrawerElement.item().icon(IconElement.icon("inbox")).text("Inbox").activate(true))
											 .add(DrawerElement.item().icon(IconElement.icon("star")).text("Star").activate(true))
											 .add(DrawerElement.item().icon(IconElement.icon("send")).text("Sent Main")))
							  .target(div);
		Elements.body().add(drawer);
		Elements.body().add(div);
		menu.onClick(evt->drawer.toggle());
	}

	private void TestSheet() {
		SheetElement sheetElement = SheetElement.builder()
				.columns(
						ColumnBuilder.string("A").name("A").pattern("^a$").colorConditional("red").colorConditionalBackground("yellow").color("blue").build(),
						ColumnBuilder.string("B").build(),
						ColumnBuilder.checkbox("C").build()
				).build();
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
						ColumnBuilder.string("A").name("A").pattern("^a$").colorConditional("red").colorConditionalBackground("yellow").color("blue").build(),
						ColumnBuilder.string("B").build(),
						ColumnBuilder.checkbox("C").build()
				).mergeCells(new MergeCell[]{new MergeCell().col(1).row(0).colspan(2).rowspan(3)})
				.build();
		SheetElementSelectableSingle.header(sheetElement);
		content.add(sheetElement);
		sheetElement.values(
				new Data("1").put("A", "FFF"),
				new Data("2").put("A", "a"),
				new Data("3"));
	}
}
