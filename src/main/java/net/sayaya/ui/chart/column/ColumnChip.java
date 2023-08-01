package net.sayaya.ui.chart.column;

import com.google.gwt.core.client.Scheduler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import net.sayaya.ui.ChipElement;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.Data;
import org.jboss.elemento.EventType;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jboss.elemento.Elements.*;

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
            // for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.apply(td, row, prop, value);
            // alignHelper.apply(td, row, prop, value);

            var elem = div().style("display: flex; flex-direction: row; flex-wrap: wrap; gap: 8px; margin: 4px; align-items: center;");
            if(value==null) value = "";
            var match = value.split(SPLITTER);
            var tokens = Arrays.stream(match).filter(k->k!=null && !k.isEmpty()).map(text->{
                if(text.contains("\\,")) return text.replace("\\,", ",");
                return text;

            });
            var chipStyle = "background-color: transparent; border: 1px solid #AAA; border-radius: 8px;";
            if (readOnly()) tokens.map(ChipElement::chip).peek(e->e.style(chipStyle)).forEach(e->elem.add(e));
            else {
                tokens.map(token->ChipElement.check(token).removable()).peek(e->e.style(chipStyle)).forEach(e->elem.add(e));
                var input = input("text").style("background: transparent; border: none; outline: none;");
                input.on(EventType.click, evt->input.element().focus());
                input.on(EventType.keydown, evt->{
                    evt.stopPropagation();
                    if("Enter".equalsIgnoreCase(evt.key)) Scheduler.get().scheduleDeferred(()->td.getElementsByTagName("input").asList().get(0).focus());
                });
                input.on(EventType.change, evt->{
                    var text = input.element().value;
                    if(text.contains(",")) text = text.replace(",", "\\,");
                    String nextValue = Stream.concat(Arrays.stream(match), Stream.of(text)).collect(Collectors.joining(","));
                    sheet.setDataAtCell(row, col, nextValue);
                });
                elem.add(input);
            }
            td.innerHTML = "";
            td.append(elem.element());
            return td;
        }).headerRenderer(n->span().textContent(defaultHelper.name()).element());
    }
    public ColumnStyleColorConditionalHelper<ColumnChip> pattern(String pattern) {
        ColumnStyleColorConditionalHelper<ColumnChip> helper = new ColumnStyleColorConditionalHelper<>(pattern, ()->this);
        colorConditionalHelpers.add(helper);
        return helper;
    }
}
