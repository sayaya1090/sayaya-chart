package net.sayaya.ui.chart.column;

import elemental2.core.JsRegExp;
import elemental2.core.RegExpResult;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import net.sayaya.ui.ChipElement;
import net.sayaya.ui.ChipSetElement;
import net.sayaya.ui.TextFieldElement;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.Data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.jboss.elemento.Elements.span;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Accessors(fluent = true)
public class ColumnChip implements ColumnBuilder {
    private final static String SPLITTER = "(?<!\\\\),";
    private final String id;
    @Delegate(excludes = ColumnBuilder.class) private final ColumnBuilderDefaultHelper<ColumnChip> defaultHelper = new ColumnBuilderDefaultHelper<>(()->this);
    private final ColumnStyleDataChangeHelper<ColumnChip> dataChangeHelper = new ColumnStyleDataChangeHelper<>(()->this);
    @Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleColorHelper<ColumnChip> colorHelper = new ColumnStyleColorHelper<>(()->this);
    private final List<ColumnStyleColorConditionalHelper<ColumnChip>> colorConditionalHelpers = new LinkedList<>();
    @Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleAlignHelper<ColumnChip> alignHelper = new ColumnStyleAlignHelper<>(()->this);

    @Override
    public Column build() {
        Column column = defaultHelper.build().data(id);
        return column.readOnly(true).renderer((sheet, td, row, col, prop, value, ci)->{
            Data data = sheet.spreadsheet.values()[row];

            alignHelper.clear(td);
            colorHelper.clear(td);
            for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.clear(td);
            alignHelper.apply(td, row, prop, value);
            colorHelper.apply(td, row, prop, value);
            dataChangeHelper.apply(sheet, td, row, prop);
            for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.apply(td, row, prop, value);
            alignHelper.apply(td, row, prop, value);

            var elem = ChipSetElement.chips();
            if(value==null) value = "";
            var match = value.split(SPLITTER);
            var tokens = Arrays.stream(match).filter(k->k!=null && !k.isEmpty());
            if (readOnly()) tokens.map(ChipElement::chip).forEach(elem::add);
            else {
                tokens.map(token->ChipElement.check(token).removable()).forEach(elem::add);
                elem.element().append(TextFieldElement.textBox().filled().element());
            }

            /*
            ListElement<ListElement.SingleLineItem> list = ListElement.singleLineList().add(ListElement.singleLine().label(""));
            for(var item: this.list) list.add(ListElement.singleLine().label(item.value()));
            DropDownElement elem = DropDownElement.filled(list).select(value);*/
            elem.onValueChange(evt->{
                var v = evt.value();
                //data.put(id, v);
                colorHelper.clear(td);
                for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.clear(td);

                //colorHelper.apply(td, row, prop, v);
                dataChangeHelper.apply(sheet, td, row, prop);
                //for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.apply(td, row, prop, v);
            });
            elem.element().getElementsByClassName("mdc-line-ripple").asList().stream().map(e->(HTMLElement)e).forEach(e-> e.style.display = "none");
            elem.element().getElementsByClassName("mdc-select__anchor").asList().stream().map(e->(HTMLElement)e).findFirst().ifPresent(e-> e.style.height = CSSProperties.HeightUnionType.of("100%"));
            elem.element().getElementsByClassName("mdc-select__selected-text").asList().stream().map(e->(HTMLElement)e).findFirst().ifPresent(e->{
                e.style.color = "inherit";
                e.style.textAlign = "inherit";
            });
            td.innerHTML = "";
            td.style.padding = CSSProperties.PaddingUnionType.of("0");
            td.appendChild(elem.element());
            return td;
        }).headerRenderer(n->span().textContent(defaultHelper.name()).element());
    }
    public ColumnStyleColorConditionalHelper<ColumnChip> pattern(String pattern) {
        ColumnStyleColorConditionalHelper<ColumnChip> helper = new ColumnStyleColorConditionalHelper<>(pattern, ()->this);
        colorConditionalHelpers.add(helper);
        return helper;
    }
}
