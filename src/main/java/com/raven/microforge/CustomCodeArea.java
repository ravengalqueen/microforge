package com.raven.microforge;

import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.text.Font;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.model.StyleSpans;

import java.util.function.BiConsumer;

public class CustomCodeArea extends CodeArea {
    private final Font font;
    private final String styleClass;


    public CustomCodeArea(Font f, String s) {
        this.font = f;
        this.styleClass = s;
    }

    /* public CustomCodeArea setFont(Font f) { new CustomCodeArea(f, styleClass);
         return null;
     }*/
    public void setFont(Font f) {
        //  StyleSpans<CustomCodeArea> spans = this.getStyleSpans();
        //  this.setStyleSpans(spans.mapStyles(s -> s.setFont(f));
    }
}
